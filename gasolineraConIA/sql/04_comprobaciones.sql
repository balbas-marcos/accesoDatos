-- =============================================================================
-- 04_comprobaciones.sql
-- Fase 1: Verificación de reglas, escenarios, trigger y transacción ROLLBACK.
--         Las pruebas que provocan error deliberado están separadas al final
--         y comentadas para que el bloque de verificación principal termine OK.
-- Orden de ejecución: 4.º (después de 01_esquema.sql, 02_triggers.sql y 03_datos.sql)
-- =============================================================================

USE gasolinera_tienda;

-- =============================================================================
-- PARTE A: CONSULTAS DE VERIFICACIÓN DE ESCENARIOS Y TRIGGER (EJECUTABLES)
-- =============================================================================

-- 1. Estaciones con su ficha 1:1 y número de surtidores (Estación 1 tiene varios)
SELECT e.codigo_interno, e.nombre_comercial, f.codigo_configuracion, f.serie_tickets,
       COUNT(s.id) AS num_surtidores
FROM estaciones e
JOIN fichas_facturacion f ON f.estacion_id = e.id
LEFT JOIN surtidores s ON s.estacion_id = e.id
GROUP BY e.id, e.codigo_interno, e.nombre_comercial, f.codigo_configuracion, f.serie_tickets
ORDER BY e.id;

-- 2. Clientes con/sin tarjeta y tarjetas todavía sin asignar
SELECT c.codigo_cliente, c.nombre, t.numero_tarjeta, t.fecha_asignacion
FROM clientes c
LEFT JOIN tarjetas_fidelizacion t ON t.cliente_id = c.id
ORDER BY c.id;

SELECT numero_tarjeta, fecha_emision
FROM tarjetas_fidelizacion
WHERE cliente_id IS NULL;

-- 3. Supervisión de empleados (sin supervisor, con supervisor, supervisa a varios)
SELECT e.numero_interno, e.nombre, sup.nombre AS supervisor
FROM empleados e
LEFT JOIN empleados sup ON e.supervisor_id = sup.id
ORDER BY e.id;

-- 4. Funciones del personal (simultáneas, sin función, función recuperada)
SELECT e.numero_interno, e.nombre, tf.codigo AS funcion,
       af.fecha_inicio, af.fecha_fin
FROM empleados e
LEFT JOIN asignaciones_funcion af ON af.empleado_id = e.id
LEFT JOIN tipos_funcion tf ON tf.id = af.tipo_funcion_id
ORDER BY e.id, af.fecha_inicio;

-- 5. Productos de ambas modalidades y sus proveedores (sin elementos aislados)
SELECT p.referencia_comercial, p.modalidad, p.precio_venta, p.estado,
       p.formato_envase, p.precision_pesada,
       GROUP_CONCAT(pr.razon_social SEPARATOR ' | ') AS proveedores
FROM productos p
JOIN acuerdos_suministro a ON a.producto_id = p.id
JOIN proveedores pr ON pr.id = a.proveedor_id
GROUP BY p.id
ORDER BY p.id;

-- 6. Campañas y participación (campaña 5 sin inscritos, cliente 6 sin campañas)
SELECT c.codigo_campana, c.nombre, COUNT(ic.id) AS total_inscritos
FROM campanas_promocionales c
LEFT JOIN inscripciones_campana ic ON ic.campana_id = c.id
GROUP BY c.id
ORDER BY c.id;

-- 7. Incidencias (sin asignar, empleado sin incidencias, responsable de varias)
SELECT i.referencia, e.codigo_interno AS estacion,
       COALESCE(emp.nombre, 'SIN ASIGNAR') AS responsable, i.estado
FROM incidencias i
JOIN estaciones e ON e.id = i.estacion_id
LEFT JOIN empleados emp ON emp.id = i.empleado_responsable_id
ORDER BY i.id;

-- 8. Ventas, líneas (precios congelados) y pagos (repostaje vs tienda múltiple)
SELECT v.referencia_ticket, v.estado, v.total,
       COALESCE(c.nombre, 'ANÓNIMO') AS cliente,
       COALESCE(SUM(p.importe), 0.00) AS total_pagado
FROM ventas v
LEFT JOIN clientes c ON c.id = v.cliente_id
LEFT JOIN pagos p ON p.venta_id = v.id
GROUP BY v.id
ORDER BY v.id;

-- Comprobación de que las líneas de Venta 1 conservan 1.25 y 14.90 pese al cambio de precio a 1.40 y 15.50
SELECT v.referencia_ticket, lv.numero_orden, pr.referencia_comercial,
       lv.cantidad, lv.precio_unitario_aplicado, pr.precio_venta AS precio_catalogo_actual,
       lv.importe
FROM lineas_venta lv
JOIN ventas v ON v.id = lv.venta_id
JOIN productos pr ON pr.id = lv.producto_id
WHERE v.id = 1
ORDER BY lv.numero_orden;

-- 9. Devoluciones acumuladas por línea frente a la cantidad e importe comprados
SELECT lv.id AS linea_id, p.referencia_comercial,
       lv.cantidad AS cantidad_vendida, lv.importe AS importe_linea,
       COALESCE(SUM(d.cantidad), 0.000) AS cantidad_devuelta,
       COALESCE(SUM(d.importe_reintegrado), 0.00) AS importe_devuelto
FROM lineas_venta lv
JOIN productos p ON p.id = lv.producto_id
LEFT JOIN devoluciones d ON d.linea_venta_id = lv.id
GROUP BY lv.id
HAVING cantidad_devuelta > 0
ORDER BY lv.id;

-- -----------------------------------------------------------------------------
-- 10. COMPROBACIONES DEL TRIGGER DE PRECIOS (comportamiento + ROLLBACK)
-- -----------------------------------------------------------------------------

-- Estado previo del historial (debe mostrar las 2 filas creadas al final de 03_datos.sql)
SELECT id, producto_id, precio_anterior, precio_nuevo, fecha_cambio
FROM historial_precios
ORDER BY id;

-- 10a. Actualizar al MISMO precio -> NO debe generar nueva fila en historial_precios
UPDATE productos SET precio_venta = 1.40 WHERE id = 1;
SELECT COUNT(*) AS filas_tras_mismo_precio FROM historial_precios;

-- 10b. Actualizar solo la descripción -> NO debe generar nueva fila en historial_precios
UPDATE productos SET descripcion = 'Agua mineral natural 1.5 L' WHERE id = 1;
SELECT COUNT(*) AS filas_tras_cambio_descripcion FROM historial_precios;

-- 10c. Cambio real de precio dentro de una transacción que se deshace con ROLLBACK
-- Resultado esperado: dentro de la transacción se ve la fila temporal en historial_precios;
-- tras ROLLBACK desaparecen tanto el nuevo precio (vuelve a 18.40) como la entrada histórica.
START TRANSACTION;
UPDATE productos SET precio_venta = 19.95 WHERE id = 4;
SELECT id, referencia_comercial, precio_venta AS precio_durante_tx FROM productos WHERE id = 4;
SELECT COUNT(*) AS filas_historial_durante_tx FROM historial_precios;
ROLLBACK;

SELECT id, referencia_comercial, precio_venta AS precio_tras_rollback FROM productos WHERE id = 4;
SELECT COUNT(*) AS filas_historial_tras_rollback FROM historial_precios;


-- =============================================================================
-- PARTE B: PRUEBAS DE ERROR DELIBERADO Y LIMITACIONES DE FASE 1
-- Descomentar de una en una para comprobar el rechazo de MySQL.
-- =============================================================================

-- [ERROR ESPERADO 1 - Duplicate entry (UQ)] Referencia de negocio duplicada en estaciones:
-- INSERT INTO estaciones (codigo_interno, nombre_comercial, direccion, telefono, fecha_apertura)
-- VALUES ('EST-001', 'Duplicada', 'Calle Falsa 1', '910000000', '2026-01-01');

-- [ERROR ESPERADO 2 - Duplicate entry (UQ compuesta)] Número visible repetido en misma estación:
-- INSERT INTO surtidores (estacion_id, codigo_equipo, numero_visible, tipo_combustible, estado_servicio)
-- VALUES (1, 'SURT-999', 1, 'Diesel', 'OPERATIVO');

-- [ERROR ESPERADO 3 - Duplicate entry (UQ compuesta)] Acuerdo proveedor-producto repetido:
-- INSERT INTO acuerdos_suministro (proveedor_id, producto_id, referencia_proveedor, precio_compra, plazo_entrega_dias)
-- VALUES (1, 1, 'IB-DUP', 0.60, 2);

-- [ERROR ESPERADO 4 - Duplicate entry (UQ compuesta)] Cliente inscrito dos veces en misma campaña:
-- INSERT INTO inscripciones_campana (campana_id, cliente_id, fecha_inscripcion)
-- VALUES (1, 1, '2026-03-20');

-- [ERROR ESPERADO 5 - Cannot add or update a child row (FK)] Surtidor apuntando a estación inexistente:
-- INSERT INTO surtidores (estacion_id, codigo_equipo, numero_visible, tipo_combustible, estado_servicio)
-- VALUES (999, 'SURT-ERR', 1, 'Diesel', 'OPERATIVO');

-- [ERROR ESPERADO 6 - Check constraint violated] Auto-supervisión en empleados:
-- UPDATE empleados SET supervisor_id = 1 WHERE id = 1;

-- [ERROR ESPERADO 7 - Check constraint violated] Producto envasado con precision_pesada:
-- INSERT INTO productos (referencia_comercial, descripcion, precio_venta, estado, modalidad, formato_envase, precision_pesada)
-- VALUES ('PRD-ERR-1', 'Inválido', 2.00, 'ACTIVO', 'ENVASADO', 'Caja', 0.050);

-- [ERROR ESPERADO 8 - Check constraint violated] Pago de tienda con litros informados:
-- INSERT INTO pagos (tipo_operacion, cliente_id, venta_id, fecha, fecha_hora, importe, medio_pago, litros, combustible)
-- VALUES ('TIENDA', 1, 1, '2026-10-10', '2026-10-10 10:00:00', 5.00, 'EFECTIVO', 3.000, 'Diesel');

-- [ERROR ESPERADO 9 - Check constraint violated] Campaña con fecha_fin anterior a fecha_inicio:
-- INSERT INTO campanas_promocionales (codigo_campana, nombre, descripcion, fecha_inicio, fecha_fin)
-- VALUES ('CMP-ERR', 'Errónea', 'Fechas invertidas', '2026-06-10', '2026-06-01');

-- -----------------------------------------------------------------------------
-- NOTA EXPRESA SOBRE REGLAS PENDIENTES DE FASE 2 (NO RECHAZADAS AÚN EN SQL):
-- Las siguientes reglas requieren lógica transaccional o validaciones multi-fila
-- que se implementarán en la capa de servicio/JDBC en la Fase 2:
--   1. Impedir crear una estación sin su ficha de facturación o sin surtidor inicial.
--   2. Impedir borrar el último surtidor de una estación o el último acuerdo de proveedor/producto.
--   3. Detectar ciclos indirectos de supervisión (ej. emp 1 -> emp 2 -> emp 1).
--   4. Evitar periodos solapados de una misma función para un mismo empleado.
--   5. Impedir devoluciones sobre ventas PENDIENTE_PAGO o cuya suma supere la línea original.
--   6. Validar en Java/BD que la cantidad envasada sea entera y la de granel múltiplo de precision_pesada.
-- -----------------------------------------------------------------------------
