# CONTEXT.md — Gasolinera (Fase 1 · 2.º DAM · Acceso a Datos)

## 1. Objetivo y compatibilidad
Ampliamos el proyecto Java actual (`Gasolinera`, `Pagos`, persistencia en CSV y JSON, y conexión JDBC preparada) para gestionar tienda y actividad diaria en **MySQL (InnoDB)**[cite: 2].
- **Mantener CSV y JSON** funcionando sin romper datos ni comportamientos previos; justificar cualquier cambio y verificar su impacto[cite: 2].
- **Separación de capas**: dominio Java, lógica de aplicación y persistencia desacoplados[cite: 2].
- **En esta Fase 1 NO se implementan** consultas/CRUD JDBC desde Java, ni gestión detallada de depósitos, mangueras, suministros, repostajes nuevos, motor de descuentos o stock completo[cite: 2].

## 2. Reglas técnicas obligatorias
- **BD (MySQL InnoDB)**: todas las tablas (incluidas intermedias, líneas e históricos) usan **un único entero autoincremental como PK**[cite: 2]. Los códigos/referencias de negocio van en columnas con `UNIQUE` (simples o compuestas)[cite: 2]. Prohibido usar `FOREIGN_KEY_CHECKS = 0` para tapar fallos[cite: 2].
- **Trigger de precios**: registra cambios reales de `precio_venta` en historial solo cuando cambia el precio (`OLD <> NEW`); nunca en `INSERT`, ni al repetir precio o cambiar solo descripción; si la transacción hace `ROLLBACK`, el historial también se revierte[cite: 2].
- **Java**: ~15+ clases de dominio (sin contar DAOs, conexión, excepciones ni tests)[cite: 2]. Sin SQL ni conexiones dentro del dominio[cite: 2]. Tipos precisos (p. ej. `BigDecimal` para dinero/pesadas y enteros para unidades) con redondeo monetario común y validación/cálculo por modalidad[cite: 2].

## 3. Reglas del dominio
- **Estaciones, facturación y surtidores**: código interno único; ficha de facturación 1:1 obligatoria (alta conjunta sin estados incompletos); ≥1 surtidor por estación (código único global, número visible único por estación; no borrar el último)[cite: 2].
- **Personal, funciones y supervisión**: número/DNI únicos; conserva identidad y antigüedad ante cambios de estación (ventas pasadas no cambian de estación) o función[cite: 2]. Funciones (*cajero*, *expendedor*, *responsable de tienda*, *jefe de estación* + futuras) con fechas y condiciones propias: puede haber empleados sin funciones, con varias a la vez o retomando una función tras terminar un periodo, sin solapar periodos de la misma función en una persona[cite: 2]. Supervisión actual opcional (0..1 supervisor, varios subordinados; sin auto-supervisión ni ciclos)[cite: 2].
- **Clientes y tarjetas**: compras identificadas y anónimas[cite: 2]. Clientes registrados con código único y contacto (documentar qué campos pueden faltar)[cite: 2]. Tarjetas con número único, fechas de emisión/asignación; 0..1 tarjeta por cliente y tarjetas sin asignar permitidas[cite: 2].
- **Productos y proveedores**: referencia única, precio `> 0`, estado y modalidad fija (*envasado* en unidades enteras positivas y formato; *granel* decimal según precisión de pesada)[cite: 2]. Proveedores con código/CIF/razón social y acuerdos N:M con productos; todo proveedor y todo producto (incluso retirado) debe tener ≥1 acuerdo (sin altas incompletas ni dejar aislados)[cite: 2].
- **Campañas**: código único, `fecha_fin >= fecha_inicio`, inscripción N:M de clientes registrados (sin duplicar inscripción; no exige tarjeta)[cite: 2].
- **Ventas, pagos y devoluciones**: venta con ticket único, estación, empleado, cliente opcional y ≥1 línea (carrito vacío no es venta)[cite: 2]. Línea con número de orden único en el ticket, cantidad, precio unitario congelado e importe[cite: 2]. Pagos integrados con `Pagos` sin perder lo anterior; venta pagada cuando la suma de pagos cuadra con el importe[cite: 2]. Devoluciones sobre línea de venta pagada (parciales/sucesivas, respetando modalidad, precio original, redondeo sin pasarse del importe y aptitud de reventa; no superan cantidad comprada)[cite: 2].
- **Incidencias**: referencia única, estación, apertura, estado y responsable actual opcional (independiente de funciones y supervisión)[cite: 2].

## 4. Entregables clave de la Fase 1
- Modelo relacional y diccionario de datos en texto/tablas (**sin diagrama E-R**)[cite: 2].
- Scripts en carpeta ordenada: `01_esquema.sql`, `02_triggers.sql`, `03_datos.sql` (≥5 filas en tablas principales y ≥10 líneas de venta) y `04_comprobaciones.sql` (separando/comentando errores provocados)[cite: 2].
- Clasificación de reglas: **garantizadas en BD/trigger** vs. **pendientes/control en Fase 2** (altas conjuntas, último surtidor/acuerdo, ciclos de supervisión, solapamiento de funciones, devoluciones acumuladas, cuadre de pagos)[cite: 2].
- Código Java compilable con demostración de ambas modalidades y CSV/JSON intactos[cite: 2].
- `README.md` (sin credenciales personales), `AGENTS.md`, `CONTEXT.md`, justificación Java/MySQL, reglas y evidencias, 3 operaciones JDBC futuras y `AUDITORIA_IA.odt`[cite: 2].