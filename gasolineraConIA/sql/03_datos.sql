-- =============================================================================
-- 03_datos.sql
-- Fase 1: Carga coherente de datos de ejemplo que cubren todos los escenarios
-- Orden de ejecución: 3.º (después de 01_esquema.sql y 02_triggers.sql)
-- =============================================================================

USE gasolinera_tienda;

-- -----------------------------------------------------------------------------
-- 1. ESTACIONES Y FICHAS DE FACTURACIÓN (5 estaciones, 1:1 con ficha)
-- -----------------------------------------------------------------------------
INSERT INTO estaciones (id, codigo_interno, nombre_comercial, direccion, telefono, fecha_apertura) VALUES
(1, 'EST-001', 'Estación Norte Castellana', 'Paseo de la Castellana 240, Madrid', '910112233', '2018-03-15'),
(2, 'EST-002', 'Estación Sur A-4', 'Autovía A-4 km 18, Pinto', '916223344', '2019-06-01'),
(3, 'EST-003', 'Estación Oeste M-50', 'Carretera M-50 km 62, Majadahonda', '916334455', '2020-09-10'),
(4, 'EST-004', 'Estación Este Alcalá', 'Av. de Madrid 88, Alcalá de Henares', '918445566', '2022-01-20'),
(5, 'EST-005', 'Estación Centro Vallecas', 'Av. de la Albufera 145, Madrid', '914556677', '2024-05-12');

INSERT INTO fichas_facturacion (id, estacion_id, codigo_configuracion, serie_tickets, siguiente_numero, texto_pie) VALUES
(1, 1, 'CFG-EST01', 'N26-', 105, 'Gracias por repostar en Estación Norte. Conserve su ticket.'),
(2, 2, 'CFG-EST02', 'S26-', 204, 'Estación Sur A-4 · Atención 24 horas.'),
(3, 3, 'CFG-EST03', 'O26-', 58,  'Estación Oeste M-50 · Tienda y lavadero.'),
(4, 4, 'CFG-EST04', 'E26-', 19,  'Estación Este Alcalá · Factura simplificada.'),
(5, 5, 'CFG-EST05', 'C26-', 12,  'Estación Centro Vallecas · Gracias por su visita.');

-- -----------------------------------------------------------------------------
-- 2. SURTIDORES (todas las estaciones >= 1; Estación 1 con 3 equipos)
-- -----------------------------------------------------------------------------
INSERT INTO surtidores (id, estacion_id, codigo_equipo, numero_visible, tipo_combustible, estado_servicio) VALUES
(1, 1, 'SURT-101', 1, 'Gasolina 95', 'OPERATIVO'),
(2, 1, 'SURT-102', 2, 'Gasolina 98', 'OPERATIVO'),
(3, 1, 'SURT-103', 3, 'Diesel',      'AVERIADO'),
(4, 2, 'SURT-201', 1, 'Gasolina 95', 'OPERATIVO'),
(5, 2, 'SURT-202', 2, 'Diesel',      'OPERATIVO'),
(6, 3, 'SURT-301', 1, 'Gasolina 95', 'OPERATIVO'),
(7, 4, 'SURT-401', 1, 'Diesel',      'OPERATIVO'),
(8, 5, 'SURT-501', 1, 'GLP',         'FUERA_DE_SERVICIO');

-- -----------------------------------------------------------------------------
-- 3. EMPLEADOS Y SUPERVISIÓN (sin supervisor, con supervisor, supervisa a varios)
-- -----------------------------------------------------------------------------
INSERT INTO empleados (id, numero_interno, documento_identidad, nombre, telefono, email, fecha_incorporacion, estacion_actual_id, supervisor_id) VALUES
(1, 'EMP-001', '12345678A', 'Laura Gómez Ruiz',      '600111222', 'laura.gomez@gasolinera.es',   '2018-04-01', 1, NULL),
(2, 'EMP-002', '23456789B', 'Carlos Martín Pérez',   '600222333', 'carlos.martin@gasolinera.es', '2020-02-15', 1, 1),
(3, 'EMP-003', '34567890C', 'Ana López Sánchez',     '600333444', 'ana.lopez@gasolinera.es',     '2021-07-01', 1, 1),
(4, 'EMP-004', '45678901D', 'David Navarro Gil',     '600444555', NULL,                          '2022-05-10', 2, 1),
(5, 'EMP-005', '56789012E', 'Elena Torres Vega',     '600555666', 'elena.torres@gasolinera.es',  '2019-06-15', 2, NULL),
(6, 'EMP-006', '67890123F', 'Javier Díaz Romero',    '600666777', 'javier.diaz@gasolinera.es',   '2026-01-10', 3, 5);

-- -----------------------------------------------------------------------------
-- 4. CATÁLOGO DE FUNCIONES Y ASIGNACIONES (simultáneas, sin función, recuperada)
-- -----------------------------------------------------------------------------
INSERT INTO tipos_funcion (id, codigo, nombre, descripcion, activo) VALUES
(1, 'CAJERO',             'Cajero',               'Cobro en tienda y gestión de devoluciones autorizadas.', TRUE),
(2, 'EXPENDEDOR',         'Expendedor',           'Atención en pista y manipulación de combustibles.',     TRUE),
(3, 'RESPONSABLE_TIENDA', 'Responsable de tienda','Gestión de surtido y compras con presupuesto.',        TRUE),
(4, 'JEFE_ESTACION',      'Jefe de estación',     'Dirección operativa y operaciones excepcionales.',     TRUE);

-- EMP-001 (Laura): varias funciones simultáneas (JEFE_ESTACION + CAJERO)
INSERT INTO asignaciones_funcion (id, empleado_id, tipo_funcion_id, fecha_inicio, fecha_fin) VALUES
(1, 1, 4, '2018-04-01', NULL),
(2, 1, 1, '2023-01-01', NULL),
-- EMP-002 (Carlos): fue CAJERO, lo dejó, pasó a EXPENDEDOR y recuperó CAJERO con nuevas condiciones
(3, 2, 1, '2020-02-15', '2022-12-31'),
(4, 2, 2, '2023-01-01', NULL),
(5, 2, 1, '2025-03-01', NULL),
-- EMP-003 (Ana): EXPENDEDOR
(6, 3, 2, '2021-07-01', NULL),
-- EMP-004 (David): RESPONSABLE_TIENDA
(7, 4, 3, '2022-05-10', NULL),
-- EMP-005 (Elena): JEFE_ESTACION
(8, 5, 4, '2019-06-15', NULL);
-- EMP-006 (Javier): sin funciones asignadas temporalmente

INSERT INTO detalles_jefe_estacion (id, asignacion_id, nivel_autorizacion) VALUES
(1, 1, 3),
(2, 8, 2);

INSERT INTO detalles_cajero (id, asignacion_id, limite_devoluciones) VALUES
(1, 2, 200.00),
(2, 3, 50.00),
(3, 5, 120.00);

INSERT INTO detalles_expendedor (id, asignacion_id, numero_acreditacion, fecha_caducidad) VALUES
(1, 4, 'EXP-2023-0041', '2028-12-31'),
(2, 6, 'EXP-2021-0018', '2027-07-01');

INSERT INTO detalles_responsable_tienda (id, asignacion_id, presupuesto_compras) VALUES
(1, 7, 3500.00);

-- -----------------------------------------------------------------------------
-- 5. CLIENTES Y TARJETAS DE FIDELIZACIÓN
-- (Se conservan los 4 clientes existentes de gasolinera + 2 nuevos de tienda)
-- -----------------------------------------------------------------------------
INSERT INTO clientes (id, codigo_cliente, nombre, telefono, email, direccion, matricula) VALUES
(1, 'CLI-001', 'amrocs',    '676424423', 'amrocs@correo.es',     NULL,                        '6677JKL'),
(2, 'CLI-002', 'alfredo',   '678958645', NULL,                   'C/ Mayor 12, Madrid',       '9877KJL'),
(3, 'CLI-003', 'pepe',      '456789431', 'pepe@correo.es',       NULL,                        '4548NST'),
(4, 'CLI-004', 'eustaquio', '678958746', NULL,                   NULL,                        '7898CDP'),
(5, 'CLI-005', 'marta',     NULL,        'marta.silva@correo.es','Av. Europa 30, Pinto',      NULL),
(6, 'CLI-006', 'lucia',     '611223344', 'lucia.perez@correo.es','C/ Real 5, Majadahonda',    NULL);

-- 4 tarjetas asignadas y 2 en lote sin asignar
INSERT INTO tarjetas_fidelizacion (id, numero_tarjeta, fecha_emision, cliente_id, fecha_asignacion) VALUES
(1, 'FID-2026-0001', '2026-01-05', 1,    '2026-01-10'),
(2, 'FID-2026-0002', '2026-01-05', 2,    '2026-01-12'),
(3, 'FID-2026-0003', '2026-01-05', 4,    '2026-02-01'),
(4, 'FID-2026-0004', '2026-01-05', 5,    '2026-02-15'),
(5, 'FID-2026-0005', '2026-03-01', NULL, NULL),
(6, 'FID-2026-0006', '2026-03-01', NULL, NULL);

-- -----------------------------------------------------------------------------
-- 6. PRODUCTOS (ENVASADO y GRANEL, incluyendo un retirado)
-- -----------------------------------------------------------------------------
INSERT INTO productos (id, referencia_comercial, descripcion, precio_venta, estado, modalidad, formato_envase, precision_pesada) VALUES
(1, 'PRD-ENV-001', 'Agua mineral 1.5 L',                 1.25, 'ACTIVO',   'ENVASADO', 'Botella 1.5L',       NULL),
(2, 'PRD-ENV-002', 'Aceite motor sintético 5W30 1 L',   14.90, 'ACTIVO',   'ENVASADO', 'Garrafa 1L',         NULL),
(3, 'PRD-ENV-003', 'Ambientador pino clásico (retirado)',2.50, 'RETIRADO', 'ENVASADO', 'Blíster 1 unidad',   NULL),
(4, 'PRD-GRA-001', 'Almendras tostadas a granel',       18.40, 'ACTIVO',   'GRANEL',   NULL,                 0.050),
(5, 'PRD-GRA-002', 'Gominolas surtidas a granel',        9.60, 'ACTIVO',   'GRANEL',   NULL,                 0.010),
(6, 'PRD-GRA-003', 'Pistachos tostados a granel',       21.00, 'ACTIVO',   'GRANEL',   NULL,                 0.100);

-- -----------------------------------------------------------------------------
-- 7. PROVEEDORES Y ACUERDOS DE SUMINISTRO
-- (Todos tienen >= 1 acuerdo; PROV-001 suministra 3; PRD-ENV-001 tiene 2 provs)
-- -----------------------------------------------------------------------------
INSERT INTO proveedores (id, codigo_interno, identificacion_fiscal, razon_social, telefono, email, direccion) VALUES
(1, 'PRV-001', 'B11111111', 'Distribuciones Ibéricas S.L.',     '911100200', 'pedidos@ibericas.es',    'Pol. Ind. Vallecas, Madrid'),
(2, 'PRV-002', 'B22222222', 'Bebidas y Snacks del Centro S.A.', '912200300', 'ventas@bebidascentro.es','Av. Industria 4, Getafe'),
(3, 'PRV-003', 'B33333333', 'Lubricantes Técnicos S.L.',        '913300400', 'comercial@lubritec.es',  'C/ Química 9, Coslada'),
(4, 'PRV-004', 'B44444444', 'Frutos Secos La Mancha S.L.',      '926400500', 'info@frutosmancha.es',   'Ctra. Toledo km 40'),
(5, 'PRV-005', 'B55555555', 'Dulces Artesanos Sur S.A.',        '915500600', NULL,                     'Pol. Cobo Calleja, Fuenlabrada');

INSERT INTO acuerdos_suministro (id, proveedor_id, producto_id, referencia_proveedor, precio_compra, plazo_entrega_dias) VALUES
(1, 1, 1, 'IB-AGUA-15',   0.55, 2),
(2, 2, 1, 'BC-AGU-1500',  0.58, 1),
(3, 1, 2, 'IB-ACE-5W30',  8.40, 3),
(4, 3, 2, 'LT-5W30-1L',   8.10, 2),
(5, 1, 3, 'IB-AMB-PINO',  1.10, 5),
(6, 4, 4, 'FM-ALM-GR',   11.20, 4),
(7, 5, 5, 'DA-GOM-GR',    4.80, 3),
(8, 4, 6, 'FM-PIS-GR',   13.50, 4);

-- -----------------------------------------------------------------------------
-- 8. CAMPAÑAS PROMOCIONALES E INSCRIPCIONES
-- (Campaña 5 sin inscritos; Cliente 6 sin campañas; múltiples en ambos sentidos)
-- -----------------------------------------------------------------------------
INSERT INTO campanas_promocionales (id, codigo_campana, nombre, descripcion, fecha_inicio, fecha_fin) VALUES
(1, 'CMP-2026-01', 'Primavera en Ruta',     'Ventajas de bienvenida en tienda para clientes registrados.', '2026-03-01', '2026-05-31'),
(2, 'CMP-2026-02', 'Club Café y Snacks',    'Campaña de degustación de productos a granel y bebidas.',   '2026-04-01', '2026-06-30'),
(3, 'CMP-2026-03', 'Verano Seguro',         'Revisión y promoción en aceites y accesorios de viaje.',     '2026-06-01', '2026-08-31'),
(4, 'CMP-2026-04', 'Vuelta al Trabajo',     'Campaña informativa de otoño para clientes habituales.',     '2026-09-01', '2026-10-31'),
(5, 'CMP-2026-05', 'Navidad Dulce (vacía)', 'Campaña futura preparada sin inscritos todavía.',           '2026-12-01', '2027-01-06');

INSERT INTO inscripciones_campana (id, campana_id, cliente_id, fecha_inscripcion) VALUES
(1, 1, 1, '2026-03-05'),
(2, 2, 1, '2026-04-02'),
(3, 3, 1, '2026-06-03'),
(4, 1, 2, '2026-03-10'),
(5, 2, 3, '2026-04-12'),
(6, 4, 5, '2026-09-05');
-- Cliente 6 (lucia) no participa en ninguna campaña. Campaña 5 no tiene inscritos.

-- -----------------------------------------------------------------------------
-- 9. VENTAS, LÍNEAS DE VENTA (>= 10 líneas) Y PAGOS
-- -----------------------------------------------------------------------------
INSERT INTO ventas (id, referencia_ticket, estacion_id, empleado_id, cliente_id, fecha_hora, estado, total) VALUES
(1, 'N26-000101', 1, 2, 1,    '2026-10-05 10:15:00', 'PAGADA',         37.61),
(2, 'N26-000102', 1, 1, NULL, '2026-10-06 12:30:00', 'PAGADA',          9.61),
(3, 'S26-000201', 2, 4, 2,    '2026-10-07 16:40:00', 'PAGADA',         18.65),
(4, 'S26-000202', 2, 5, 5,    '2026-10-08 18:10:00', 'PAGADA',         11.30),
(5, 'O26-000057', 3, 2, NULL, '2026-10-09 09:20:00', 'PENDIENTE_PAGO', 12.10);

INSERT INTO lineas_venta (id, venta_id, numero_orden, producto_id, cantidad, precio_unitario_aplicado, importe) VALUES
-- Venta 1 (37.61): 5.00 + 14.90 + 6.44 + 10.50 + 0.77 = 37.61
(1,  1, 1, 1, 4.000,  1.25,  5.00), -- 4 x 1.25 = 5.00
(2,  1, 2, 2, 1.000, 14.90, 14.90), -- 1 x 14.90 = 14.90
(3,  1, 3, 4, 0.350, 18.40,  6.44), -- 0.350 kg x 18.40 = 6.44
(4,  1, 4, 6, 0.500, 21.00, 10.50), -- 0.500 kg x 21.00 = 10.50
(5,  1, 5, 5, 0.080,  9.60,  0.77), -- 0.080 kg x 9.60 = 0.768 -> 0.77 (HALF_UP)
-- Venta 2 anónima (9.61): 5.00 + 4.61 = 9.61
(6,  2, 1, 3, 2.000,  2.50,  5.00), -- producto retirado vendido antes de su retirada
(7,  2, 2, 5, 0.480,  9.60,  4.61), -- 0.480 kg x 9.60 = 4.608 -> 4.61 (HALF_UP)
-- Venta 3 (18.65): 14.90 + 3.75 = 18.65
(8,  3, 1, 2, 1.000, 14.90, 14.90),
(9,  3, 2, 1, 3.000,  1.25,  3.75),
-- Venta 4 (11.30): 9.20 + 2.10 = 11.30
(10, 4, 1, 4, 0.500, 18.40,  9.20),
(11, 4, 2, 6, 0.100, 21.00,  2.10),
-- Venta 5 anónima pendiente de pago (12.10): 2.50 + 9.60 = 12.10
(12, 5, 1, 1, 2.000,  1.25,  2.50),
(13, 5, 2, 5, 1.000,  9.60,  9.60);

-- Pagos: 2 de repostaje heredados (compatibles con pagos.csv/json) + cobros de tienda
INSERT INTO pagos (id, tipo_operacion, cliente_id, venta_id, fecha, fecha_hora, importe, medio_pago, litros, combustible) VALUES
-- Pagos anteriores de repostaje (sin inventar fecha_hora ni medio_pago):
(1, 'REPOSTAJE', 1,    NULL, '2026-10-01', NULL,                  345.00, NULL,       34214.000, 'Diesel'),
(2, 'REPOSTAJE', 3,    NULL, '2026-10-01', NULL,                 3245.00, NULL,         123.000, 'Gasolina 98'),
-- Venta 1 (37.61) cobrada con DOS pagos (efectivo + tarjeta):
(3, 'TIENDA',    1,    1,    '2026-10-05', '2026-10-05 10:15:30',  20.00, 'EFECTIVO', NULL,   NULL),
(4, 'TIENDA',    1,    1,    '2026-10-05', '2026-10-05 10:15:45',  17.61, 'TARJETA',  NULL,   NULL),
-- Venta 2 (9.61) anónima:
(5, 'TIENDA',    NULL, 2,    '2026-10-06', '2026-10-06 12:30:20',   9.61, 'EFECTIVO', NULL,   NULL),
-- Venta 3 (18.65):
(6, 'TIENDA',    2,    3,    '2026-10-07', '2026-10-07 16:40:25',  18.65, 'TARJETA',  NULL,   NULL),
-- Venta 4 (11.30):
(7, 'TIENDA',    5,    4,    '2026-10-08', '2026-10-08 18:10:40',  11.30, 'TARJETA',  NULL,   NULL),
-- Venta 5 (12.10, PENDIENTE_PAGO) con pago parcial entregado:
(8, 'TIENDA',    NULL, 5,    '2026-10-09', '2026-10-09 09:20:30',   5.00, 'EFECTIVO', NULL,   NULL);

-- -----------------------------------------------------------------------------
-- 10. DEVOLUCIONES (parciales sucesivas sobre misma línea sin superar original)
-- -----------------------------------------------------------------------------
INSERT INTO devoluciones (id, linea_venta_id, empleado_id, cantidad, motivo, fecha_hora, importe_reintegrado, apta_reventa) VALUES
-- Dos devoluciones parciales sucesivas sobre línea 1 (4 uds a 1.25 = 5.00 -> 1 ud + 2 uds = 3 uds / 3.75):
(1, 1, 1, 1.000, 'Botella sobrante sin abrir',         '2026-10-05 11:00:00', 1.25, TRUE),
(2, 1, 2, 2.000, 'Cambio de compra del cliente',       '2026-10-05 18:30:00', 2.50, TRUE),
-- Otras devoluciones sobre ventas pagadas:
(3, 3, 1, 0.150, 'Peso superior al solicitado',        '2026-10-05 10:25:00', 2.76, FALSE),
(4, 6, 1, 1.000, 'Envase golpeado',                    '2026-10-06 13:10:00', 2.50, FALSE),
(5, 8, 5, 1.000, 'Error en viscosidad recomendada',    '2026-10-07 17:00:00', 14.90, TRUE);

-- -----------------------------------------------------------------------------
-- 11. INCIDENCIAS (una sin asignar, empleado sin incidencias, otro con varias)
-- -----------------------------------------------------------------------------
INSERT INTO incidencias (id, referencia, estacion_id, empleado_responsable_id, descripcion, fecha_hora_apertura, estado) VALUES
(1, 'INC-2026-001', 1, 2,    'Fallo en manguera del surtidor 3 (Diesel).',           '2026-10-02 08:30:00', 'EN_CURSO'),
(2, 'INC-2026-002', 1, 2,    'Atasco en impresora térmica de caja 1.',              '2026-10-04 14:15:00', 'RESUELTA'),
(3, 'INC-2026-003', 2, 5,    'Descuadre menor en apertura de turno de tarde.',      '2026-10-06 15:00:00', 'CERRADA'),
(4, 'INC-2026-004', 3, 3,    'Calibración pendiente de báscula de frutos secos.',   '2026-10-08 11:20:00', 'ABIERTA'),
(5, 'INC-2026-005', 5, NULL, 'Surtidor GLP fuera de servicio pendiente de técnico.','2026-10-09 09:00:00', 'ABIERTA');

-- -----------------------------------------------------------------------------
-- 12. CAMBIO REAL DE PRECIO POSTERIOR A LA VENTA (dispara el trigger)
-- Venta 1 y Venta 3 conservan su precio aplicado (1.25 y 14.90) intacto.
-- -----------------------------------------------------------------------------
UPDATE productos SET precio_venta = 1.40 WHERE id = 1;
UPDATE productos SET precio_venta = 15.50 WHERE id = 2;
