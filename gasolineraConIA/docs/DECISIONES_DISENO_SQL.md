# Decisiones de diseño relacional y SQL (Fase 1)

Este documento recoge las decisiones de diseño de la base de datos `gasolinera_tienda` en MySQL 8.0, el funcionamiento del trigger obligatorio de historial de precios y la clasificación de reglas de negocio entre restricciones de base de datos y controles pendientes para la Fase 2.

---

## 1. Decisiones generales de diseño relacional

### 1.1. Identidad técnica (`id`) frente a referencias de negocio
- **Decisión:** Todas las tablas —incluidas las tablas asociativas (`acuerdos_suministro`, `inscripciones_campana`), las asignaciones (`asignaciones_funcion` y sus subtablas de detalle), las líneas de venta (`lineas_venta`) y el historial (`historial_precios`)— tienen una clave primaria formada por un único entero autoincremental (`id INT AUTO_INCREMENT PRIMARY KEY`).
- **Por qué:**
  1. Cumple explícitamente el requisito del encargo.
  2. Desacopla la identidad técnica interna de las referencias comerciales o de negocio (`codigo_interno`, `numero_tarjeta`, `referencia_ticket`, `documento_identidad`, etc.). Si un código comercial necesitara corregirse en el futuro, no obligaría a actualizar claves primarias ni claves foráneas en cascada.
  3. Las referencias de negocio mantienen su unicidad mediante restricciones `UNIQUE` independientes (simples o compuestas).

### 1.2. Motor `InnoDB` y codificación `utf8mb4`
- **Decisión:** Todas las tablas se crean con `ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci`.
- **Por qué:**
  - `InnoDB` es obligatorio para soportar claves foráneas reales y transacciones ACID.
  - Garantiza que, si falla una operación o se ejecuta un `ROLLBACK`, tanto la modificación en `productos` como la inserción generada por el trigger en `historial_precios` se deshacen de forma atómica.

### 1.3. Tipos de datos y criterio de redondeo monetario
- **Decisión:**
  - Precios unitarios, importes, presupuestos y límites de devolución usan `DECIMAL(10,2)` (nunca `DOUBLE` ni `FLOAT`).
  - Cantidades, litros y pesos usan `DECIMAL(10,3)`, y la precisión de pesada en `productos` usa `DECIMAL(6,3)`.
  - Las fechas sin hora usan `DATE` y los instantes de operación usan `DATETIME`.
- **Por qué:**
  - Los tipos de coma flotante (`DOUBLE`) introducen errores binarios de redondeo incompatibles con cálculos de caja y devoluciones.
  - **Criterio de redondeo monetario:** dos decimales con redondeo estándar comercial `HALF_UP` (medio hacia arriba), aplicado al calcular el importe de cada línea (`cantidad * precio_unitario_aplicado`) y de cada devolución (`cantidad * precio_unitario_aplicado`).

### 1.4. Política de claves foráneas (`ON DELETE RESTRICT ON UPDATE RESTRICT`)
- **Decisión:** Todas las claves foráneas usan `ON DELETE RESTRICT ON UPDATE RESTRICT`.
- **Por qué:**
  1. **Trazabilidad histórica:** No se debe permitir borrar una estación, un empleado, un producto o una línea de venta si existen registros relacionados (ventas, devoluciones, historial de precios o asignaciones).
  2. **Claves técnicas inmutables:** Como todas las FK apuntan a un `id AUTO_INCREMENT`, ese valor técnico nunca debe cambiar; por tanto `ON UPDATE CASCADE` no aporta valor y ocultaría errores.
  3. **Compatibilidad con `CHECK` en MySQL 8.0:** MySQL 8.0 prohíbe definir restricciones `CHECK` sobre columnas involucradas en acciones referenciales como `ON DELETE SET NULL` o `ON UPDATE CASCADE` (Error 3823). Con `RESTRICT` es posible mantener `CHECK` nativos en `empleados.supervisor_id`, `tarjetas_fidelizacion.cliente_id` y `pagos`.

---

## 2. Decisiones por áreas del dominio

### 2.1. Estaciones, fichas de facturación (1:1) y surtidores
- **Relación 1:1 `estaciones` ↔ `fichas_facturacion`:**
  - Se modela colocando `estacion_id INT NOT NULL UNIQUE` como clave foránea en `fichas_facturacion`.
  - De este modo, una ficha no puede existir sin estación ni compartirse entre varias estaciones.
  - **Por qué no poner una FK mutua circular:** Una referencia circular entre `estaciones` y `fichas_facturacion` impediría insertar los datos en orden sin desactivar claves foráneas. En Fase 2, el alta conjunta indivisible se garantizará insertando ambas filas dentro de una misma transacción JDBC (`START TRANSACTION` ... `COMMIT`).
- **Surtidores:**
  - `UNIQUE(estacion_id, numero_visible)` impide repetir el número visible dentro de una misma estación, pero permite que la Estación 1 y la Estación 2 tengan ambas un surtidor número `1`.
  - `codigo_equipo` lleva `UNIQUE` global en toda la empresa.

### 2.2. Personal, supervisión y funciones extensibles
- **Adscripción actual vs. historial de ventas:**
  - `empleados.estacion_actual_id` refleja únicamente dónde trabaja hoy el empleado.
  - `ventas` guarda su propia columna `estacion_id`, de modo que cambiar a un empleado de estación nunca altera la estación registrada en sus ventas pasadas.
- **Supervisión:**
  - Autorreferencia nullable `empleados.supervisor_id -> empleados(id)`.
  - La prohibición de auto-supervisión directa (`supervisor_id <> id`) se implementa mediante triggers `BEFORE INSERT` / `BEFORE UPDATE` en `02_triggers.sql` (`SIGNAL SQLSTATE '45000'`), garantizando compatibilidad tanto en MySQL 8.0 como en MariaDB 10.4 (que rechaza referenciar columnas `AUTO_INCREMENT` dentro de un `CHECK` con Error 1901).
- **Funciones del personal (`tipos_funcion` + `asignaciones_funcion` + subtablas `detalles_*`):**
  - Se ha elegido el patrón de **tabla catálogo + tabla de asignaciones + subtablas 1:1 por tipo de función** (`detalles_cajero`, `detalles_expendedor`, `detalles_responsable_tienda`, `detalles_jefe_estacion`).
  - **Por qué este diseño y no una única tabla con muchas columnas `NULL`:**
    1. Evita mezclar atributos sin relación en una tabla llena de nulos.
    2. Permite que una persona ejerza **varias funciones simultáneamente**, esté **temporalmente sin funciones** (ninguna fila activa) o **recupere una función pasada** conservando las condiciones históricas de cada periodo (`fecha_inicio`, `fecha_fin`).
    3. Hace el modelo **extensible**: cuando la empresa añada una nueva función en el futuro, basta con insertar un nuevo registro en `tipos_funcion` y crear su nueva subtabla `detalles_<nueva_funcion>` sin modificar la estructura ni los datos históricos de `asignaciones_funcion`.

### 2.3. Clientes y tarjetas de fidelización
- **Criterio de contacto en `clientes`:**
  - `telefono`, `email` y `direccion` son opcionales individualmente (`NULL`), pero se exige `CHECK (telefono IS NOT NULL OR email IS NOT NULL)` para garantizar al menos un medio de contacto útil sin obligar a rellenar todos.
- **Relación 0..1 ↔ 0..1 con `tarjetas_fidelizacion`:**
  - `cliente_id INT NULL UNIQUE` en `tarjetas_fidelizacion` permite almacenar lotes de tarjetas sin titular (`cliente_id IS NULL`) e impide que dos tarjetas activas pertenezcan al mismo cliente.
  - El `CHECK` compuesto asegura que si `cliente_id` es `NULL`, `fecha_asignacion` también lo sea; y si está asignada, `fecha_asignacion >= fecha_emision`.

### 2.4. Productos (envasados vs. granel), proveedores y campañas
- **Modalidad fija en `productos`:**
  - Se utiliza tabla única con discriminador `modalidad ENUM('ENVASADO', 'GRANEL')` y una restricción `CHECK` excluyente:
    - Si es `ENVASADO`, `formato_envase` es obligatorio y `precision_pesada` debe ser `NULL`.
    - Si es `GRANEL`, `precision_pesada` es obligatoria (`> 0`) y `formato_envase` debe ser `NULL`.
- **Proveedores y acuerdos (`acuerdos_suministro`):**
  - `UNIQUE(proveedor_id, producto_id)` impide duplicar acuerdos entre el mismo proveedor y producto.
  - `UNIQUE(proveedor_id, referencia_proveedor)` evita que un proveedor asigne la misma referencia interna a dos productos distintos.
- **Campañas promocionales:**
  - `CHECK (fecha_fin >= fecha_inicio)` en `campanas_promocionales` y `UNIQUE(campana_id, cliente_id)` en `inscripciones_campana` para evitar inscripciones duplicadas.

### 2.5. Integración de `clientes` y `pagos` sin romper CSV ni JSON
- **Conservación de CSV y JSON:**
  - Las clases de persistencia en fichero (`ManejarCSVCliente`, `ManejarCSVPago`, `ManejarJsonCliente`, `ManejarJsonPago`) trabajan con los campos y constructores originales de `Cliente` y `Pago` y dependen de `ILeerEscribir<T>`. El diseño SQL **no obliga a cambiar el formato de los ficheros CSV/JSON**.
- **Integración en la tabla `clientes`:**
  - Se conserva `matricula` con `UNIQUE`, pero pasa a admitir `NULL` (`VARCHAR(20) NULL UNIQUE`), ya que en MySQL múltiples `NULL` no violan `UNIQUE`. Así conviven los clientes antiguos de repostaje (con matrícula) y los nuevos clientes de tienda (sin matrícula obligatoria).
- **Integración en la tabla `pagos` sin reinterpretar registros antiguos:**
  - Se introduce `tipo_operacion ENUM('REPOSTAJE', 'TIENDA')` junto a un `CHECK` de coherencia:
    - **`REPOSTAJE` (registros heredados y futuros repostajes):** mantiene `cliente_id` obligatorio, `fecha`, `importe`, `litros` y `combustible`; deja `venta_id`, `fecha_hora` y `medio_pago` a `NULL` para **no inventar ni transformar silenciosamente** una hora o un medio de pago que no existían en la práctica anterior.
    - **`TIENDA` (nuevos cobros de tienda):** requiere `venta_id`, `fecha_hora` y `medio_pago` (`EFECTIVO`, `TARJETA`, `OTRO`), permite `cliente_id = NULL` (compras anónimas) y exige `litros = NULL` y `combustible = NULL`.

### 2.6. Ventas, líneas, devoluciones e incidencias
- **Ventas y líneas:**
  - `ventas.cliente_id` es nullable para admitir compras anónimas.
  - `ventas.estado` (`PENDIENTE_PAGO`, `PAGADA`, `ANULADA`) permite distinguir cobros incompletos de ventas ya liquidadas.
  - `lineas_venta` guarda `precio_unitario_aplicado` e `importe` propios: los cambios posteriores de `productos.precio_venta` no alteran las ventas históricas.
- **Devoluciones:**
  - Cada registro en `devoluciones` apunta a `linea_venta_id` e incluye `importe_reintegrado` y `apta_reventa`. Los reintegros quedan completamente separados de la tabla `pagos` y la línea original permanece inalterada para trazabilidad.

---

## 3. Funcionamiento del trigger de `historial_precios`

El script `02_triggers.sql` define el trigger `trg_productos_cambio_precio` (`AFTER UPDATE ON productos FOR EACH ROW`):

```sql
IF OLD.precio_venta <> NEW.precio_venta THEN
    INSERT INTO historial_precios (producto_id, precio_anterior, precio_nuevo, fecha_cambio)
    VALUES (NEW.id, OLD.precio_venta, NEW.precio_venta, NOW());
END IF;
```

**Por qué cumple todas las condiciones del caso:**
1. **No se dispara al crear el producto:** al ser `AFTER UPDATE`, un `INSERT` inicial en `productos` no genera ninguna fila histórica.
2. **Solo registra cambios reales de precio:**
   - Si se ejecuta un `UPDATE` con el mismo valor (`precio_venta = 1.40` cuando ya valía `1.40`), la condición `OLD.precio_venta <> NEW.precio_venta` es falsa y no inserta nada.
   - Si se modifica únicamente `descripcion` o `estado`, `OLD.precio_venta` y `NEW.precio_venta` siguen siendo iguales, por lo que tampoco inserta nada.
3. **Independiente de Java:** funciona aunque el cambio se realice directamente desde una consola SQL o un gestor gráfico.
4. **Reversión automática en transacciones (`ROLLBACK`):** al ejecutarse dentro de la misma transacción `InnoDB` que el `UPDATE`, si se hace `ROLLBACK`, MySQL deshace tanto la actualización de `productos` como la inserción producida por el trigger en `historial_precios`.

---

## 4. Clasificación de reglas de negocio (garantizadas vs. pendientes en Fase 2)

### 4.1. Garantizadas directamente en MySQL (esquema + trigger)
- Clave primaria técnica única autoincremental en todas las tablas (`PK`).
- Unicidad de códigos internos, referencias comerciales, documentos, tarjetas, tickets e incidencias (`UNIQUE`).
- Unicidad de `numero_visible` por estación en `surtidores` (`UNIQUE(estacion_id, numero_visible)`).
- Relación `1` máxima en `fichas_facturacion` (sin fichas huérfanas ni compartidas entre estaciones).
- No duplicidad de acuerdos proveedor-producto, referencias de proveedor, inscripciones en campañas ni orden de línea en un ticket (`UNIQUE`).
- Prohibición de auto-supervisión directa (`trg_empleados_no_autosup_ins` / `trg_empleados_no_autosup_upd`).
- Coherencia de fechas en asignaciones de función, tarjetas de fidelización y campañas (`CHECK`).
- Precios, cantidades e importes positivos (`CHECK > 0`), exclusividad estricta entre modalidad `ENVASADO` y `GRANEL`, y coherencia de campos `REPOSTAJE`/`TIENDA` en `pagos`.
- Registro automático e íntegro de cambios reales de precio mediante el trigger `trg_productos_cambio_precio` (con soporte transaccional `ROLLBACK`).

### 4.2. Pendientes de control en la Fase 2 (capa de servicio JDBC y transacciones)
Estas reglas afectan a varias filas o a operaciones compuestas de varios pasos y **no deben resolverse forzando referencias circulares** en el esquema:

1. **Alta conjunta indivisible de estación + ficha + surtidor inicial (y prohibición de borrar el último surtidor):**
   - *Cómo se resolverá:* transacción JDBC (`setAutoCommit(false)`) que inserte `estaciones`, `fichas_facturacion` y al menos un `surtidores` antes de `commit()`. Al eliminar un surtidor, se comprobará con bloqueo que queden al menos 2 antes del `DELETE`.
2. **Alta conjunta de proveedor/producto con al menos un acuerdo y prohibición de dejar elementos aislados:**
   - *Cómo se resolverá:* transacción JDBC para crear un proveedor o producto junto con su primer `acuerdos_suministro`. Antes de borrar un acuerdo, se verificará que ni el proveedor ni el producto queden con `COUNT(*) = 0` acuerdos.
3. **Detección de ciclos indirectos de supervisión (`A → B → C → A`):**
   - *Cómo se resolverá:* antes de asignar `supervisor_id`, el servicio recorrerá la cadena ascendente de supervisores en Java (o mediante consulta `WITH RECURSIVE`) rechazando la operación si aparece el propio empleado.
4. **No solapamiento de periodos de una misma función y coherencia con su subtabla `detalles_*`:**
   - *Cómo se resolverá:* dentro de una transacción se comprobará que no existan filas en `asignaciones_funcion` para el mismo `(empleado_id, tipo_funcion_id)` cuyo intervalo `[fecha_inicio, COALESCE(fecha_fin, '9999-12-31')]` solape con el nuevo periodo, y se insertará en la misma transacción la subtabla `detalles_*` correspondiente al `tipo_funcion`.
5. **Inmutabilidad de la modalidad del producto (`ENVASADO` ↔ `GRANEL`):**
   - *Cómo se resolverá:* los métodos de actualización de catálogo en el DAO/servicio no permitirán modificar `modalidad` una vez creado el producto (opcionalmente reforzable con un trigger `BEFORE UPDATE` en fase posterior).
6. **Alta de venta con al menos una línea (evitar carritos vacíos) y cuadre de pagos al confirmar `PAGADA`:**
   - *Cómo se resolverá:* no se persistirá la venta hasta confirmar al menos 1 línea en la transacción de creación. Para cambiar el estado a `PAGADA`, el servicio verificará `SUM(pagos.importe) == ventas.total`.
7. **Devoluciones válidas (solo sobre ventas `PAGADA`, sin superar cantidad/importe acumulados y respetando modalidad):**
   - *Cómo se resolverá:* dentro de una transacción con bloqueo pesimista (`SELECT ... FOR UPDATE` sobre la línea/venta), se comprobará que la venta esté `PAGADA`, que la cantidad devuelta cumpla la modalidad (`ENVASADO` entero; `GRANEL` múltiplo de `precision_pesada`), y que la suma de cantidades e importes devueltos previos más la nueva devolución no supere la cantidad ni el importe originales de `lineas_venta` (ajustando el redondeo si fuera la última devolución posible para no exceder `lineas_venta.importe`).
