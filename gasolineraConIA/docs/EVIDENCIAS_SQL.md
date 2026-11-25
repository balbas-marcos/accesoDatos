# Evidencias de ejecución y verificación SQL (Fase 1)

Este documento recoge las pruebas reales ejecutadas sobre los scripts de `gasolineraConIA/sql/`, el ajuste realizado por compatibilidad con el motor local, las salidas obtenidas y la matriz final de reglas garantizadas frente a las pendientes para la Fase 2.

---

## 1. Entorno de ejecución y orden aplicado

- **Servidor probado:** `127.0.0.1:3306` · **Versión:** `10.4.32-MariaDB` (usuario `root`, ya que en el puerto `3307` no había instancia levantada y el usuario `gasolinera` solo tenía permisos sobre el esquema antiguo `gasolinerasql`).
- **Orden estricto de ejecución (sin desactivar `FOREIGN_KEY_CHECKS`):**
  1. `sql/01_esquema.sql`
  2. `sql/02_triggers.sql`
  3. `sql/03_datos.sql`
  4. `sql/04_comprobaciones.sql`

Todos los scripts finalizaron con código de salida `0`.

---

## 2. Ajuste de compatibilidad detectado (`ERROR 1901` en MariaDB 10.4)

- **Problema observado:** Al ejecutar inicialmente `01_esquema.sql`, MariaDB rechazó la restricción `CHECK (supervisor_id IS NULL OR supervisor_id <> id)` en la tabla `empleados` con:
  ```text
  ERROR 1901 (HY000) at line 63: Function or expression 'AUTO_INCREMENT' cannot be used in the CHECK clause of `id`
  ```
- **Causa:** MariaDB 10.4 prohíbe referenciar una columna `AUTO_INCREMENT` (`id`) dentro de una restricción `CHECK` a nivel de tabla.
- **Solución aplicada:**
  1. Se retiró el `CHECK` de `empleados` en `01_esquema.sql` dejando documentado el motivo.
  2. Se añadieron dos triggers auxiliares en `02_triggers.sql` (`trg_empleados_no_autosup_ins` y `trg_empleados_no_autosup_upd`) que lanzan `SIGNAL SQLSTATE '45000'` si `NEW.supervisor_id = NEW.id`.
  3. Se actualizó `docs/DECISIONES_DISENO_SQL.md` para mantener coherencia entre el código y la memoria.
- **Resultado:** Compatible tanto con MySQL 8.0 como con MariaDB 10.4.

---

## 3. Resultados verificados de `04_comprobaciones.sql` (Parte A)

### 3.1. Carga de datos de ejemplo (`03_datos.sql`)
- **Estaciones, fichas 1:1 y surtidores:**
  - `EST-001` (`CFG-EST01`, serie `N26-`): 3 surtidores.
  - `EST-002` (`CFG-EST02`, serie `S26-`): 2 surtidores.
  - `EST-003` (`CFG-EST03`, serie `O26-`), `EST-004` (`CFG-EST04`, serie `E26-`) y `EST-005` (`CFG-EST05`, serie `C26-`): 1 surtidor cada una.
- **Clientes y tarjetas de fidelización:**
  - 6 clientes (`CLI-001` a `CLI-006`): `amrocs`, `alfredo`, `eustaquio` y `marta` tienen tarjeta asignada (`FID-2026-0001` a `FID-2026-0004`); `pepe` y `lucia` no tienen tarjeta (`NULL`).
  - 2 tarjetas emitidas en lote sin asignar (`FID-2026-0005` y `FID-2026-0006`, `cliente_id = NULL`).
- **Supervisión de empleados:**
  - `EMP-001` (Laura Gómez) y `EMP-005` (Elena Torres): sin supervisor (`NULL`).
  - `EMP-001` supervisa a 3 empleados (`EMP-002`, `EMP-003`, `EMP-004`); `EMP-005` supervisa a `EMP-006`.
- **Funciones del personal:**
  - `EMP-001`: simultáneamente `JEFE_ESTACION` (desde `2018-04-01`) y `CAJERO` (desde `2023-01-01`).
  - `EMP-002`: periodo cerrado de `CAJERO` (`2020-02-15` a `2022-12-31`), `EXPENDEDOR` activo (`2023-01-01`) y función `CAJERO` recuperada (`2025-03-01`).
  - `EMP-003` (`EXPENDEDOR`), `EMP-004` (`RESPONSABLE_TIENDA`), `EMP-005` (`JEFE_ESTACION`).
  - `EMP-006` (Javier Díaz): sin funciones asignadas (`NULL`).
- **Productos y proveedores:**
  - 6 productos: 3 `ENVASADO` (`PRD-ENV-001`, `PRD-ENV-002`, y `PRD-ENV-003` en estado `RETIRADO`) y 3 `GRANEL` (`PRD-GRA-001` con precisión `0.050`, `PRD-GRA-002` con `0.010`, `PRD-GRA-003` con `0.100`).
  - Todos los productos y proveedores cuentan con acuerdos activos (sin aislados).
- **Campañas promocionales:**
  - 5 campañas; `CMP-2026-05` tiene `0` inscritos; `CLI-001` está inscrito en 3 campañas y `CLI-006` en ninguna.
- **Incidencias:**
  - 5 incidencias; `INC-2026-005` figura `SIN ASIGNAR`, `EMP-002` es responsable de 2 (`INC-2026-001` y `INC-2026-002`) y otros empleados no tienen ninguna asignada.
- **Ventas, líneas, precios congelados, pagos y devoluciones:**
  - 5 ventas (4 `PAGADA`, 1 `PENDIENTE_PAGO` con pago parcial de `5.00` sobre `12.10`). `N26-000102` y `O26-000057` son anónimas (`ANÓNIMO`).
  - `N26-000101` suma `37.61` y tiene `total_pagado = 37.61` (cobrada con dos pagos: efectivo + tarjeta).
  - Tras actualizar en catálogo `PRD-ENV-001` (`1.25 -> 1.40`) y `PRD-ENV-002` (`14.90 -> 15.50`), las líneas 1 y 2 de `N26-000101` conservaron `precio_unitario_aplicado = 1.25` y `14.90`.
  - En devoluciones acumuladas: la línea `1` (`4.000` uds / `5.00`) acumula `3.000` uds / `3.75`; la línea `3` (`0.350` kg / `6.44`) devuelve `0.150` kg / `2.76`; la línea `6` (`2.000` uds / `5.00`) devuelve `1.000` ud / `2.50`; la línea `8` (`1.000` ud / `14.90`) devuelve `1.000` ud / `14.90`.

### 3.2. Verificación del trigger `trg_productos_cambio_precio` y `ROLLBACK`

| Prueba ejecutada | Resultado observado |
| :--- | :--- |
| Estado inicial tras `03_datos.sql` (2 cambios reales de precio) | `2` filas en `historial_precios` (producto `1`: `1.25 -> 1.40`, producto `2`: `14.90 -> 15.50`) |
| `UPDATE productos SET precio_venta = 1.40 WHERE id = 1` (mismo precio) | `filas_tras_mismo_precio = 2` (sin nueva fila) |
| `UPDATE productos SET descripcion = 'Agua mineral natural 1.5 L' WHERE id = 1` | `filas_tras_cambio_descripcion = 2` (sin nueva fila) |
| Dentro de `START TRANSACTION`: `UPDATE productos SET precio_venta = 19.95 WHERE id = 4` | `precio_durante_tx = 19.95`, `filas_historial_durante_tx = 3` |
| Tras ejecutar `ROLLBACK` | `precio_tras_rollback = 18.40`, `filas_historial_tras_rollback = 2` |

---

## 4. Resultados de las pruebas de error deliberado (Parte B)

Cada sentencia de la Parte B de `04_comprobaciones.sql` se ejecutó de forma individual contra `gasolinera_tienda`, siendo rechazada (`exit = 1`) con el error esperado:

1. **Estación con código duplicado (`uq_estaciones_codigo`):**
   `ERROR 1062 (23000): Duplicate entry 'EST-001' for key 'uq_estaciones_codigo'`
2. **Surtidor con número visible repetido en la misma estación (`uq_surtidores_estacion_num`):**
   `ERROR 1062 (23000): Duplicate entry '1-1' for key 'uq_surtidores_estacion_num'`
3. **Acuerdo proveedor-producto duplicado (`uq_acuerdos_prov_prod`):**
   `ERROR 1062 (23000): Duplicate entry '1-1' for key 'uq_acuerdos_prov_prod'`
4. **Inscripción duplicada de cliente en campaña (`uq_inscripciones_camp_cli`):**
   `ERROR 1062 (23000): Duplicate entry '1-1' for key 'uq_inscripciones_camp_cli'`
5. **Surtidor con `estacion_id` inexistente (`fk_surtidores_estacion`):**
   `ERROR 1452 (23000): Cannot add or update a child row: a foreign key constraint fails (\`gasolinera_tienda\`.\`surtidores\`, CONSTRAINT \`fk_surtidores_estacion\` FOREIGN KEY (\`estacion_id\`) REFERENCES \`estaciones\` (\`id\`))`
6. **Auto-supervisión en `empleados` (`trg_empleados_no_autosup_upd`):**
   `ERROR 1644 (45000): Un empleado no puede ser supervisor de sí mismo.`
7. **Producto `ENVASADO` con `precision_pesada` (`chk_productos_modalidad`):**
   `ERROR 4025 (23000): CONSTRAINT \`chk_productos_modalidad\` failed for \`gasolinera_tienda\`.\`productos\``
8. **Pago `TIENDA` con `litros` informados (`chk_pagos_tipo_coherencia`):**
   `ERROR 4025 (23000): CONSTRAINT \`chk_pagos_tipo_coherencia\` failed for \`gasolinera_tienda\`.\`pagos\``
9. **Campaña con `fecha_fin < fecha_inicio` (`chk_campanas_fechas`):**
   `ERROR 4025 (23000): CONSTRAINT \`chk_campanas_fechas\` failed for \`gasolinera_tienda\`.\`campanas_promocionales\``

---

## 5. Relación resumida de reglas garantizadas vs. pendientes en Fase 2

| Ámbito | Garantizadas actualmente (BD / Trigger) | Pendientes para Fase 2 (Java / Transacciones JDBC) |
| :--- | :--- | :--- |
| **Estaciones y surtidores** | PK técnica, unicidad de códigos/fichas, ficha 1:N/1:1 sin huérfanas, `UNIQUE(estacion_id, numero_visible)`, `numero_visible > 0` | Alta atómica estación + ficha + surtidor inicial; impedir borrar el último surtidor |
| **Personal y funciones** | Unicidad de número interno, DNI y acreditación; no auto-supervisión directa (trigger); `fecha_fin >= fecha_inicio` | Detección de ciclos indirectos de supervisión; no solapamiento de periodos de una misma función; alta conjunta en `asignaciones_funcion` + `detalles_*` |
| **Clientes y tarjetas** | Al menos un contacto (`telefono` o `email`), unicidad de código y tarjeta, máx. 1 tarjeta por cliente, fechas coherentes | Validaciones de negocio adicionales al asignar o dar de baja tarjetas |
| **Productos y proveedores** | `precio_venta > 0`, exclusividad `ENVASADO`/`GRANEL`, historial automático de cambios reales de precio con soporte `ROLLBACK`, `UNIQUE` de acuerdos | Inmutabilidad de `modalidad` tras el alta; alta conjunta proveedor/producto + primer acuerdo; impedir borrar último acuerdo |
| **Campañas** | Unicidad de código, `fecha_fin >= fecha_inicio`, sin inscripciones duplicadas | — |
| **Ventas, pagos y devoluciones** | Unicidad de ticket y orden de línea, precio congelado en `lineas_venta`, coherencia `REPOSTAJE`/`TIENDA` en `pagos` | Venta con ≥ 1 línea; cuadre `SUM(pagos) == total` al pasar a `PAGADA`; devoluciones solo en `PAGADA`, sin superar cantidad/importe acumulados (con bloqueo `FOR UPDATE`) y validando enteros / precisión de pesada |
