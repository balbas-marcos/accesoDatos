# Decisiones del modelo Java (Fase 1)

Este documento se redacta **antes** de reorganizar los paquetes, crear las clases de dominio y compilar. Complementa a `DECISIONES_DISENO_SQL.md` (diseño relacional) y `EVIDENCIAS_SQL.md` (pruebas), y deja constancia de qué se cambia, qué se conserva y qué se difiere a la Fase 2.

---

## 1. Alcance

- Se organiza el código Java existente en paquetes bajo la raíz **`com.gasolinera`**.
- Se crea un dominio de ~35 tipos (entidades, `record`s, interfaces *sealed* y enums) que representa las reglas de `CONTEXT.md` **sin SQL, sin conexiones, sin ficheros, sin `Scanner` y sin `System.out`**.
- Se añade `DemoModalidades` en la capa de UI para demostrar `ENVASADO`, `GRANEL` y el redondeo `HALF_UP`.
- **No se implementa CRUD JDBC** ni se tocan las consultas, la URL de conexión ni la inicialización de tablas heredadas: `CONTEXT.md` lo prohibe expresamente en Fase 1.
- Se corrigen **únicamente dos fallos heredados**, los ubicados en `Cliente` y `Pago`. El resto de comportamientos heredados se documentan pero no se modifican.

---

## 2. Estructura de paquetes elegida y por qué

### 2.1. Mapa

```text
com.gasolinera
├── ui/          Main, DemoModalidades
├── service/     ClienteGestion, PagoGestion
├── model/       entidades, value objects, enums (sin dependencias de infraestructura)
├── persistence/ ManejarCSVCliente, ManejarCSVPago, ManejarJsonCliente,
│                ManejarJsonPago, MigraCSVToJson
├── dao/         ManejarMySQL, ManejarMySQLCliente, ManejarMySQLPago
└── common/      ILeerEscribir<T>, IGestionDatos<T>
```

Los directorios `src/archivosCSV` y `src/archivosJson` **no se mueven ni se renombran**: las clases de persistencia reciben rutas relativas (`"src/archivosCSV"`, `"src/archivosJson"`) que dependen del directorio de trabajo, no del paquete Java.

### 2.2. Por qué esta separación (responsabilidades)

| Paquete | Responsabilidad | Por qué está ahí |
|---|---|---|
| `ui` | Interacción con la persona: menú de `Main` y demostración por consola. | Es el único sitio autorizado para `Scanner` y `System.out`. `AGENTS.md` prohíbe mezclar `Scanner`/impresiones con SQL. `DemoModalidades` se añade aquí para no alterar el menú heredado. |
| `service` | Lógica de aplicación heredada (`ClienteGestion`, `PagoGestion`) y, en Fase 2, transacciones y reglas que abarcan varias filas. | Los `Gestion` actuales ya actuaban como capa intermedia entre el menú y los manejadores. Los movemos **sin renombrarlos ni cambiar su lógica** para no ampliar el alcance; el renombrado a `*Service` queda como propuesta futura. |
| `model` | Datos y reglas puras: entidades, `record`s inmutables, enums y validaciones de un solo objeto. | Es el corazón del sistema. No conoce a `dao`, `persistence`, `service` ni a clases de E/S. Así el mismo `Producto` sirve para CSV, JSON, JDBC o un test sin adaptarse. Cumple el requisito de “~15+ clases de dominio sin SQL ni conexiones”. |
| `persistence` | Lectura/escritura en **ficheros** CSV y JSON. | Aísla un formato volátil y frágil (parseo manual, separadores, comas) de la lógica de negocio y de la BD. Un cambio en el formato no debe obligar a tocar el dominio. |
| `dao` | Acceso a **MySQL** mediante JDBC (`Connection`, `PreparedStatement`, `ResultSet`). | Concentra todo `java.sql.*`. `ManejarMySQL` vive aquí como factoría de conexión/DDL inicial; en Fase 2 vendrán los DAOs del nuevo esquema `gasolinera_tienda`. |
| `common` | Contratos compartidos `ILeerEscribir<T>` e `IGestionDatos<T>`. | Evita que `service` importe de `persistence` o `dao` para conocer la interfaz. Reduce el acoplamiento: las implementaciones cumplen un contrato, no una clase concreta. |

**Regla de dependencias:** `ui → service → model` y `ui → persistence|dao → model/common`. El paquete `model` **no importa nada** de los otros paquetes del proyecto (y desde luego no importa `java.sql`, `java.nio.file` ni `java.util.Scanner`). El contrato `ILeerEscribir<T>` permanece en `common` con sus firmas originales, incluido el método genérico `<T> void ultimoID(T elemento)`, para no romper a las cinco clases que lo implementan.

### 2.3. Por qué `persistence` y `dao` se mantienen separados

Ambos implementan `ILeerEscribir<T>` y devuelven los mismos tipos de dominio, pero **resuelven problemas distintos**:

1. **Naturaleza del medio:** CSV/JSON son secuencias de texto con un formato propio; MySQL es un servicio transaccional con SQL, claves foráneas y `PreparedStatement`. Mezclarlos obligaría a condicionar cada consulta según el origen de los datos.
2. **Garantías distintas:** en ficheros no hay `ROLLBACK`, ni bloqueos, ni `UNIQUE` real; en BD sí. Las reglas de Fase 2 (alta conjunta, cuadre de pagos, devoluciones acumuladas con `SELECT ... FOR UPDATE`) solo pueden vivir en `dao`/`service`. Si `persistence` y `dao` estuvieran juntos, sería fácil “arreglar” una regla de BD con lógica de fichero o al revés.
3. **Compatibilidad heredada:** el menú actual elige el manejador JSON en tiempo de construcción (`new ManejarJsonCliente(...)`) sin saber si detrás hay fichero o MySQL. Mantener dos paquetes preserva ese patrón y permite seguir usando `ManejarMySQLCliente`/`ManejarMySQLPago` tal cual, sin que el dominio se entere.
4. **Rutas y despliegue:** los manejadores de fichero usan rutas relativas al directorio de trabajo; los de BD usan una URL JDBC. Separarlos evita arrastrar configuración de uno al otro.

### 2.4. Cómo convive `model` con el código heredado sin acoplarse

- `Cliente` y `Pago` **se trasladan** a `com.gasolinera.model` conservando nombre de clase, constructores, getters/setters, `Comparable`, `toString()` y tipos (`byte`, `double`). Sus únicas diferencias son el `package` y las dos correcciones del apartado 3.
- Los manejadores CSV/JSON/MySQL siguen construyendo esos mismos objetos con los mismos argumentos. Por tanto **no hay adaptadores ni conversiones nuevas**: el acoplamiento es únicamente de tipos de dominio, no de infraestructura.
- Las entidades nuevas (`Estacion`, `Producto`, `Venta`, …) no sustituyen a las heredadas: modelan el negocio de **tienda** descrito en `CONTEXT.md`. Para evitar confusiones se usan nombres explícitos (`ClienteRegistrado`, `PagoTienda`) y se documenta que la unificación con `Cliente`/`Pago` es trabajo de Fase 2, cuando haya DAOs reales.
- Ninguna clase de `model` llama a `ManejarMySQL`, a `Files`, a `DriverManager` ni imprime por consola. Las validaciones lanzan `IllegalArgumentException` con mensaje descriptivo; quien decide qué hacer con ese error es la capa superior.

---

## 3. Las dos únicas correcciones funcionales heredadas

| Clase | Antes | Después | Impacto |
|---|---|---|---|
| `model.Cliente` | El constructor recibe `matricula` pero no la asigna: `getMatricula()` devuelve siempre `null`. | Se añade `this.matricula = matricula;` | `ManejarCSVCliente.toCSV`, `ManejarJsonCliente.toJson` y `ManejarMySQLCliente.guardar` pasan a escribir el valor recibido en lugar de `null`. El orden de campos y las firmas no cambian. |
| `model.Pago` | Tras aplicar el fallback `this.fecha = LocalDate.now()` si `fecha == null`, una última línea `this.fecha = fecha;` vuelve a dejar `null`. | Se elimina esa última asignación. | Un `Pago` creado sin fecha conserva la fecha de hoy; los constructed con fecha explícita siguen igual. `toString()` y `toString_consola()` no cambian. |

`Pago` mantiene `double` en `importe`/`litros` y el mensaje por consola de su validación de combustible: cambiarlos rompería los tres manejadores heredados. El dinero **nuevo** usa `BigDecimal`.

---

## 4. Inventario del dominio (35 tipos en `model`)

| Ámbito | Tipos | Responsabilidad |
|---|---|---|
| Estaciones | `Estacion`, `FichaFacturacion`, `Surtidor`, `EstadoServicio` | Códigos no vacíos, número visible positivo, ficha referenciada por estación, estado de servicio. |
| Personal | `Empleado`, `TipoFuncion`, `AsignacionFuncion`, `DetalleFuncion`, `DetalleCajero`, `DetalleExpendedor`, `DetalleResponsableTienda`, `DetalleJefeEstacion` | Identidad, antigüedad, supervisor opcional, periodos de función y condiciones propias por periodo. |
| Clientes | `Cliente` (heredado), `ClienteRegistrado`, `TarjetaFidelizacion` | Contacto mínimo, tarjeta 0..1, fechas de emisión/asignación coherentes. |
| Productos y catálogo | `Producto`, `ProductoEnvasado`, `ProductoGranel`, `Modalidad`, `EstadoProducto`, `HistorialPrecio`, `Proveedor`, `AcuerdoSuministro`, `Campana`, `InscripcionCampana`, `Importe` | Modalidad fija, validación de cantidad, importe centralizado, histórico de precios, acuerdos N:M, campañas. |
| Ventas y pagos | `Venta`, `LineaVenta`, `EstadoVenta`, `Pago` (heredado), `PagoTienda`, `MedioPago`, `Devolucion` | Línea con precio congelado, total derivado, cuadre, devoluciones parciales. |
| Incidencias | `Incidencia`, `EstadoIncidencia` | Referencia, apertura, responsable opcional independiente de jerarquías. |

`Validaciones` es una ayuda **package-private** dentro de `model` (texto obligatorio/opcional y enters positivos); no forma parte de la API pública ni se cuenta entre las entidades.

---

## 5. Tipos, herencia y composición

- **Identidad:** `int` para los IDs técnicos nuevos (equivalente a `INT AUTO_INCREMENT`); `byte` solo en `Cliente`/`Pago` heredados por compatibilidad. Las referencias de negocio son `String` no vacío; su unicidad la garantiza la BD (`UNIQUE`), el dominio solo valida formato/no vacío.
- **Dinero y pesadas:** `BigDecimal`. `DECIMAL(10,2)` se normaliza a `scale = 2`; `DECIMAL(10,3)` a como máximo 3 decimales. Nunca `double`/`float` en el modelo nuevo.
- **Fechas:** `LocalDate` para `DATE`, `LocalDateTime` para `DATETIME`. `Main` y los manejadores heredados siguen usando `LocalDate`/`String` según su contrato.
- **Nulidad:** los campos se guardan nullable para facilitar el mapeo JDBC futuro, pero los getters de relaciones 0..1 devuelven `Optional<...>` (`email`, `supervisorId`, `clienteId`, `fechaFin`, `empleadoResponsableId`). Así la ausencia es explícita en la API sin alterar el mapeo.
- **Inmutabilidad:** `record` para value objects (`Detalle*`, `Importe`, `HistorialPrecio` si no necesita mutación) y para datos que no cambian. `record` da `equals`/`hashCode`/`toString` correctos y evita estados intermedios inválidos.
- **Herencia vs. composición:**
  - Se usa **herencia acotada** (`sealed`) solo donde hay una familia cerrada y estable: `Producto` → `ProductoEnvasado`/`ProductoGranel` y `DetalleFuncion` → cuatro detalles concretos. `sealed` impide subclases foráneas no validadas y hace exhaustivo el razonamiento.
  - Se usa **composición** para todo lo demás: `Venta` contiene `List<LineaVenta>`, `AsignacionFuncion` contiene un `DetalleFuncion`, `LineaVenta` referencia a `Producto`. No se crea una jerarquía de ventas ni de empleados por tipo de función: las funciones **cambian con el tiempo**, por lo que son datos, no subclases de `Empleado`.
- **`Optional` frente a `null` en constructs:** se prefiere `Optional` en la interfaz pública; dentro de las clases se conserva `null` donde el esquema SQL permite `NULL`.

---

## 6. Dinero y redondeo `HALF_UP`

Se centraliza en `record Importe(BigDecimal valor)`:

```java
public record Importe(BigDecimal valor) {
    public Importe {
        if (valor == null) throw new IllegalArgumentException("El importe es obligatorio");
        valor = valor.setScale(2, RoundingMode.HALF_UP);
    }
    public static Importe de(BigDecimal cantidad, BigDecimal precioUnitario) {
        return new Importe(cantidad.multiply(precioUnitario));
    }
}
```

- El redondeo se aplica **una sola vez por operación** (línea de venta o devolución), nunca acumulando `double`.
- `LineaVenta` congela `precioUnitarioAplicado` y calcula su importe con `Importe.de`; cambiar después `producto.precioVenta` no altera la línea, igual que garantiza el esquema con `precio_unitario_aplicado` en `lineas_venta`.
- `Venta.calcularTotal()` suma importes ya redondeados; es el equivalente en memoria de `ventas.total` (`DECIMAL(10,2)`).
- Ejemplo: `0,345 × 18,40 = 6,348 → 6,35` con `HALF_UP`.

---

## 7. Modalidades estables del producto

- `Producto` es `sealed abstract`, con `modalidad` **final y sin setter**. Solo existen `ProductoEnvasado` y `ProductoGranel`; por tanto la modalidad es inmutable tras el alta, tal como exige `DECISIONES_DISENO_SQL.md` §4.2.5.
- `cambiarPrecio(...)` y `cambiarEstado(...)` sí existen: el precio puede variar y el trigger SQL registrará el histórico cuando se persista; el estado `ACTIVO`/`RETIRADO` también es mutable.
- `validarCantidad(BigDecimal)` es abstracto y lo implementa cada subtipo:

| Modalidad | Regla | Ejemplo válido | Ejemplo rechazado |
|---|---|---|---|
| `ENVASADO` | Entero positivo (`stripTrailingZeros().scale() <= 0`) y `formatoEnvase` obligatorio. | `4.000 × 1,25 = 5,00` | `2,5` uds. |
| `GRANEL` | Decimal positivo, máximo 3 decimales y múltiplo exacto de `precisionPesada > 0` (`divide(..., UNNECESSARY)`). | `0,350 kg × 18,40 = 6,44` con precisión `0,050` | `0,155 kg` con precisión `0,050`. |

- `Producto.calcularImporte(cantidad)` valida primero y después delega en `Importe.de`. Así ventas y devoluciones comparten exactamente el mismo criterio.

---

## 8. Funciones cambiantes del personal

- `TipoFuncion` es un **catálogo de datos** (`codigo` único, `nombre`, `descripcion`, `activo`), no un enum cerrado: la empresa puede añadir funciones futuras sin tocar el código ni el histórico.
- Cada `AsignacionFuncion` es un periodo `(empleadoId, tipoFuncionId, fechaInicio, fechaFin)` con su propio `DetalleFuncion` 1:1. Los detalles son `record`s *sealed*: `DetalleCajero(límite de devoluciones)`, `DetalleExpendedor(nº acreditación + caducidad)`, `DetalleResponsableTienda(presupuesto)`, `DetalleJefeEstacion(nivel)`.
- Esto permite, exactamente como describe `CONTEXT.md`: empleados sin funciones, varias funciones a la vez y recuperar una función pasada creando **una nueva asignación** con nuevas condiciones, sin borrar el periodo anterior.
- El dominio ofrece `vigenteEn(LocalDate)` y `solapaCon(AsignacionFuncion)` (intervalos `[inicio, fin o '9999-12-31']`). La comprobación definitiva con bloqueo y el alta atómica asignación + detalle son responsabilidad del servicio JDBC de Fase 2.
- `Empleado.cambiarSupervisor(...)` rechaza la auto-supervisión. Los **ciclos indirectos** (`A→B→C→A`) se comprueban en Fase 2, como ya se acordó en `DECISIONES_DISENO_SQL.md` §4.2.3. La estación de cada `Venta` se copia en la propia venta, de modo que cambiar `empleado.estacionActualId` no reescribe ventas pasadas.

---

## 9. Ventas, pagos y devoluciones: qué hace el dominio y qué queda para Fase 2

**Dominio (sin transacciones):**
- `Venta` exige ticket y estación/empleado, mantiene ≥1 `LineaVenta`, prohíbe números de orden repetidos en el ticket, calcula el total y solo permite `marcarPagada` si el importe entregado cuadra exactamente.
- `PagoTienda` modela el cobro de tienda (venta, fecha-hora, medio de pago, cliente opcional). `Pago` heredado sigue siendo el cobro de repostaje con `double`.
- `Devolucion.sobre(linea, ...)` valida la modalidad con el producto de la línea, usa el **precio congelado** de la línea para el reintegro y ofrece `cantidadPendiente`/`importePendiente` frente a devoluciones previas para no exceder cantidad ni importe originales.

**Fase 2 (service + DAO, no implementado ahora):**
- Persistir la venta y sus líneas en una sola transacción.
- `SELECT ... FOR UPDATE` sobre línea/venta antes de devolver.
- Comprobar que la venta esté `PAGADA`, detectar solapes de funciones, ciclos de supervisión, altas conjuntas estación+ficha+surtidor y proveedor/producto+acuerdo, y cuadrar `SUM(pagos) = total` con precisión `DECIMAL`.

---

## 10. Matriz de responsabilidades

| Regla | Garantizada en BD/trigger | Responsabilidad del dominio | Pendiente en Fase 2 |
|---|---|---|---|
| PK autoincremental y `UNIQUE` de códigos | Sí | Formato no vacío | — |
| `precio > 0`, exclusividad `ENVASADO`/`GRANEL`, fechas de campaña/tarjeta | Sí (`CHECK`) | Validación temprana con mensaje claro | — |
| Historial solo si cambia el precio, con `ROLLBACK` | Sí (trigger `AFTER UPDATE`) | — | — |
| Modalidad inmutable | Parcial | `Producto` *sealed* sin setter | Refuerzo opcional con trigger |
| No auto-supervisión directa | Sí (trigger) | `Empleado.cambiarSupervisor` | — |
| ≥1 surtidor / ≥1 acuerdo, último no borrable | — | — | Sí (transacción) |
| Ciclos de supervisión y solape de funciones | — | Helpers `solapaCon`, `vigenteEn` | Sí (transacción + bloqueo) |
| Venta con ≥1 línea y cuadre de pagos | Parcial (`total > 0`) | `tieneLineas`, `puedeMarcarsePagada` | Sí |
| Devoluciones acumuladas y tope de importe | — | `validarDentroDe`, `Importe` `HALF_UP` | Sí (`FOR UPDATE`) |

---

## 11. Hallazgos heredados que NO se corrigen en Fase 1

Se dejan constancia para no confundirlos con las dos correcciones autorizadas:

1. `ManejarJsonCliente.cargarTodos()` declara `String[4]` pero las líneas de `src/archivosJson/clientes.json` tienen 5 campos (`fecha` y `matricula`): al leer esa muestra se produce `ArrayIndexOutOfBoundsException`. Los JSON **generados por el propio manejador** tienen 4 campos y se leen bien.
2. `ManejarCSVCliente` lee 4 columnas de un CSV de 5 (`id,nombre,telefono,fecha,matricula`), por lo que el campo `fecha` heredado se descarta y la matrícula original no se recupera.
3. `PagoGestion.ultimoID()` itera un `List<Cliente>` sobre el manejador de pagos; el menú que lo invoca puede fallar en tiempo de ejecución.
4. `MigraCSVToJson` usa rutas `src/archivosJSON` (mayúsculas) mientras el directorio real es `src/archivosJson`; en Windows funciona por insensibilidad de mayúsculas, en Linux no.
5. `Cliente.toString()` define cinco especificadores (`%-8d`, `%-15s`, `%-12s`, `%-13s`, `%-15s`) pero solo pasa cuatro argumentos (`ID`, `nombre`, `telefono`, `matricula`). Al invocarlo se lanza `MissingFormatArgumentException`. Se detectó durante la verificación de CSV; el menú heredado que lista clientes también se ve afectado. No se corrige porque no está entre las dos correcciones autorizadas.

Todos pertenecen a la capa de persistencia/servicio heredada. Corregirlos cambiaría formatos o firmas y queda fuera del permiso concedido.

---

## 12. Verificación prevista

1. Escribir este documento (hecho).
2. Crear los paquetes y mover los 16 archivos Java existentes: solo `package` + `import`, sin cambiar SQL, rutas ni formatos.
3. Aplicar las dos correcciones de `Cliente` y `Pago`.
4. Crear los tipos de dominio y `DemoModalidades`.
5. Compilar desde la raíz de `gasolineraConIA` con `javac -encoding UTF-8` (no hace falta el conector en tiempo de compilación porque el código solo usa JDBC estándar).
6. Ejecutar `DemoModalidades` y una comprobación aislada de CSV/JSON en un directorio temporal: escribir y releer `Cliente`/`Pago`, comprobar que la matrícula ya se conserva, que el fallback de fecha funciona y que el orden de campos de `toCSV`/`toJson` es el de siempre. No se ejecuta `Main` porque llama a `ManejarMySQL.inicializarBaseDatos()` y no hay driver MySQL en el classpath.
7. No hacer `git commit` ni `git push` hasta que se solicite expresamente.

**Resultado de la verificación ejecutada:** compilación correcta de 50 archivos Java (solo avisos `unchecked` del código heredado); `DemoModalidades` ejecutada con éxito; 14 comprobaciones CSV/JSON correctas en un directorio temporal. Los ficheros reales de `src/archivosCSV` y `src/archivosJson` no se modificaron. Durante la verificación apareció el hallazgo heredado n.º 5 del apartado 11, que se documenta y no se corrige.

---

## 13. Decisiones abiertas

- Raíz de paquetes: `com.gasolinera` (aceptada).
- `ManejarMySQL` se ubica en `dao` como factoría de conexión; moverlo a un paquete `db` propio es una alternativa si se prefiere una separación aún mayor.
- Los `Gestion` no se renombran en este paso; el cambio a `ClienteService`/`PagoService` se propone aparte.
- La unificación de `Cliente`/`Pago` heredados con `ClienteRegistrado`/`PagoTienda` se pospone a Fase 2, cuando existan DAOs del esquema `gasolinera_tienda`.
