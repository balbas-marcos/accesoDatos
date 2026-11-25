-- =============================================================================
-- 01_esquema.sql
-- Fase 1: Creación de la base de datos, tablas, claves y restricciones
-- Motor: MySQL 8.0+ (InnoDB, utf8mb4)
-- Orden de ejecución: 1.º (sobre entorno limpio, sin desactivar FK)
-- =============================================================================

DROP DATABASE IF EXISTS gasolinera_tienda;
CREATE DATABASE gasolinera_tienda
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gasolinera_tienda;

-- -----------------------------------------------------------------------------
-- 1. ESTACIONES, FACTURACIÓN Y SURTIDORES
-- -----------------------------------------------------------------------------

CREATE TABLE estaciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_interno VARCHAR(20) NOT NULL,
    nombre_comercial VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    fecha_apertura DATE NOT NULL,
    CONSTRAINT uq_estaciones_codigo UNIQUE (codigo_interno)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE fichas_facturacion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    estacion_id INT NOT NULL,
    codigo_configuracion VARCHAR(30) NOT NULL,
    serie_tickets VARCHAR(20) NOT NULL,
    siguiente_numero INT NOT NULL DEFAULT 1,
    texto_pie VARCHAR(255) NOT NULL,
    CONSTRAINT uq_fichas_estacion UNIQUE (estacion_id),
    CONSTRAINT uq_fichas_codigo UNIQUE (codigo_configuracion),
    CONSTRAINT chk_fichas_siguiente_numero CHECK (siguiente_numero >= 1),
    CONSTRAINT fk_fichas_estacion FOREIGN KEY (estacion_id)
        REFERENCES estaciones (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE surtidores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    estacion_id INT NOT NULL,
    codigo_equipo VARCHAR(30) NOT NULL,
    numero_visible INT NOT NULL,
    tipo_combustible VARCHAR(50) NOT NULL,
    estado_servicio ENUM('OPERATIVO', 'AVERIADO', 'FUERA_DE_SERVICIO') NOT NULL DEFAULT 'OPERATIVO',
    CONSTRAINT uq_surtidores_codigo UNIQUE (codigo_equipo),
    CONSTRAINT uq_surtidores_estacion_num UNIQUE (estacion_id, numero_visible),
    CONSTRAINT chk_surtidores_num_visible CHECK (numero_visible > 0),
    CONSTRAINT fk_surtidores_estacion FOREIGN KEY (estacion_id)
        REFERENCES estaciones (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 2. PERSONAL, SUPERVISIÓN Y FUNCIONES
-- -----------------------------------------------------------------------------

CREATE TABLE empleados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_interno VARCHAR(20) NOT NULL,
    documento_identidad VARCHAR(20) NOT NULL,
    nombre VARCHAR(120) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    email VARCHAR(120) NULL,
    fecha_incorporacion DATE NOT NULL,
    estacion_actual_id INT NOT NULL,
    supervisor_id INT NULL,
    CONSTRAINT uq_empleados_numero UNIQUE (numero_interno),
    CONSTRAINT uq_empleados_documento UNIQUE (documento_identidad),
    -- Nota: la regla supervisor_id <> id se refuerza mediante trigger en 02_triggers.sql
    -- para garantizar compatibilidad tanto con MySQL 8.0 como con MariaDB 10.4 (Error 1901 sobre AUTO_INCREMENT).
    CONSTRAINT fk_empleados_estacion FOREIGN KEY (estacion_actual_id)
        REFERENCES estaciones (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_empleados_supervisor FOREIGN KEY (supervisor_id)
        REFERENCES empleados (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tipos_funcion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_tipos_funcion_codigo UNIQUE (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE asignaciones_funcion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    empleado_id INT NOT NULL,
    tipo_funcion_id INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NULL,
    CONSTRAINT chk_asignaciones_fechas CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio),
    CONSTRAINT fk_asignaciones_empleado FOREIGN KEY (empleado_id)
        REFERENCES empleados (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_asignaciones_tipo FOREIGN KEY (tipo_funcion_id)
        REFERENCES tipos_funcion (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE detalles_cajero (
    id INT AUTO_INCREMENT PRIMARY KEY,
    asignacion_id INT NOT NULL,
    limite_devoluciones DECIMAL(10, 2) NOT NULL,
    CONSTRAINT uq_det_cajero_asignacion UNIQUE (asignacion_id),
    CONSTRAINT chk_det_cajero_limite CHECK (limite_devoluciones >= 0),
    CONSTRAINT fk_det_cajero_asignacion FOREIGN KEY (asignacion_id)
        REFERENCES asignaciones_funcion (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE detalles_expendedor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    asignacion_id INT NOT NULL,
    numero_acreditacion VARCHAR(40) NOT NULL,
    fecha_caducidad DATE NOT NULL,
    CONSTRAINT uq_det_expendedor_asignacion UNIQUE (asignacion_id),
    CONSTRAINT uq_det_expendedor_acreditacion UNIQUE (numero_acreditacion),
    CONSTRAINT fk_det_expendedor_asignacion FOREIGN KEY (asignacion_id)
        REFERENCES asignaciones_funcion (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE detalles_responsable_tienda (
    id INT AUTO_INCREMENT PRIMARY KEY,
    asignacion_id INT NOT NULL,
    presupuesto_compras DECIMAL(10, 2) NOT NULL,
    CONSTRAINT uq_det_resp_tienda_asignacion UNIQUE (asignacion_id),
    CONSTRAINT chk_det_resp_tienda_presupuesto CHECK (presupuesto_compras >= 0),
    CONSTRAINT fk_det_resp_tienda_asignacion FOREIGN KEY (asignacion_id)
        REFERENCES asignaciones_funcion (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE detalles_jefe_estacion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    asignacion_id INT NOT NULL,
    nivel_autorizacion INT NOT NULL,
    CONSTRAINT uq_det_jefe_asignacion UNIQUE (asignacion_id),
    CONSTRAINT chk_det_jefe_nivel CHECK (nivel_autorizacion >= 1),
    CONSTRAINT fk_det_jefe_asignacion FOREIGN KEY (asignacion_id)
        REFERENCES asignaciones_funcion (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 3. CLIENTES Y TARJETAS DE FIDELIZACIÓN
-- -----------------------------------------------------------------------------

CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_cliente VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NULL,
    email VARCHAR(120) NULL,
    direccion VARCHAR(200) NULL,
    matricula VARCHAR(20) NULL,
    CONSTRAINT uq_clientes_codigo UNIQUE (codigo_cliente),
    CONSTRAINT uq_clientes_matricula UNIQUE (matricula),
    CONSTRAINT chk_clientes_contacto CHECK (telefono IS NOT NULL OR email IS NOT NULL)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tarjetas_fidelizacion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_tarjeta VARCHAR(30) NOT NULL,
    fecha_emision DATE NOT NULL,
    cliente_id INT NULL,
    fecha_asignacion DATE NULL,
    CONSTRAINT uq_tarjetas_numero UNIQUE (numero_tarjeta),
    CONSTRAINT uq_tarjetas_cliente UNIQUE (cliente_id),
    CONSTRAINT chk_tarjetas_asignacion CHECK (
        (cliente_id IS NULL AND fecha_asignacion IS NULL)
        OR
        (cliente_id IS NOT NULL AND fecha_asignacion IS NOT NULL AND fecha_asignacion >= fecha_emision)
    ),
    CONSTRAINT fk_tarjetas_cliente FOREIGN KEY (cliente_id)
        REFERENCES clientes (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 4. PRODUCTOS, HISTORIAL DE PRECIOS, PROVEEDORES Y CAMPAÑAS
-- -----------------------------------------------------------------------------

CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    referencia_comercial VARCHAR(30) NOT NULL,
    descripcion VARCHAR(200) NOT NULL,
    precio_venta DECIMAL(10, 2) NOT NULL,
    estado ENUM('ACTIVO', 'RETIRADO') NOT NULL DEFAULT 'ACTIVO',
    modalidad ENUM('ENVASADO', 'GRANEL') NOT NULL,
    formato_envase VARCHAR(60) NULL,
    precision_pesada DECIMAL(6, 3) NULL,
    CONSTRAINT uq_productos_referencia UNIQUE (referencia_comercial),
    CONSTRAINT chk_productos_precio CHECK (precio_venta > 0),
    CONSTRAINT chk_productos_modalidad CHECK (
        (modalidad = 'ENVASADO' AND formato_envase IS NOT NULL AND precision_pesada IS NULL)
        OR
        (modalidad = 'GRANEL' AND precision_pesada IS NOT NULL AND precision_pesada > 0 AND formato_envase IS NULL)
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE historial_precios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    producto_id INT NOT NULL,
    precio_anterior DECIMAL(10, 2) NOT NULL,
    precio_nuevo DECIMAL(10, 2) NOT NULL,
    fecha_cambio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_hist_precio_anterior CHECK (precio_anterior > 0),
    CONSTRAINT chk_hist_precio_nuevo CHECK (precio_nuevo > 0),
    CONSTRAINT chk_hist_precio_distinto CHECK (precio_anterior <> precio_nuevo),
    CONSTRAINT fk_hist_precios_producto FOREIGN KEY (producto_id)
        REFERENCES productos (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE proveedores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_interno VARCHAR(20) NOT NULL,
    identificacion_fiscal VARCHAR(20) NOT NULL,
    razon_social VARCHAR(150) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    email VARCHAR(120) NULL,
    direccion VARCHAR(200) NULL,
    CONSTRAINT uq_proveedores_codigo UNIQUE (codigo_interno),
    CONSTRAINT uq_proveedores_nif UNIQUE (identificacion_fiscal)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE acuerdos_suministro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    proveedor_id INT NOT NULL,
    producto_id INT NOT NULL,
    referencia_proveedor VARCHAR(40) NOT NULL,
    precio_compra DECIMAL(10, 2) NOT NULL,
    plazo_entrega_dias INT NOT NULL,
    CONSTRAINT uq_acuerdos_prov_prod UNIQUE (proveedor_id, producto_id),
    CONSTRAINT uq_acuerdos_prov_ref UNIQUE (proveedor_id, referencia_proveedor),
    CONSTRAINT chk_acuerdos_precio CHECK (precio_compra > 0),
    CONSTRAINT chk_acuerdos_plazo CHECK (plazo_entrega_dias >= 0),
    CONSTRAINT fk_acuerdos_proveedor FOREIGN KEY (proveedor_id)
        REFERENCES proveedores (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_acuerdos_producto FOREIGN KEY (producto_id)
        REFERENCES productos (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE campanas_promocionales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_campana VARCHAR(20) NOT NULL,
    nombre VARCHAR(120) NOT NULL,
    descripcion TEXT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    CONSTRAINT uq_campanas_codigo UNIQUE (codigo_campana),
    CONSTRAINT chk_campanas_fechas CHECK (fecha_fin >= fecha_inicio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE inscripciones_campana (
    id INT AUTO_INCREMENT PRIMARY KEY,
    campana_id INT NOT NULL,
    cliente_id INT NOT NULL,
    fecha_inscripcion DATE NOT NULL,
    CONSTRAINT uq_inscripciones_camp_cli UNIQUE (campana_id, cliente_id),
    CONSTRAINT fk_inscripciones_campana FOREIGN KEY (campana_id)
        REFERENCES campanas_promocionales (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_inscripciones_cliente FOREIGN KEY (cliente_id)
        REFERENCES clientes (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 5. VENTAS, LÍNEAS, PAGOS, DEVOLUCIONES E INCIDENCIAS
-- -----------------------------------------------------------------------------

CREATE TABLE ventas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    referencia_ticket VARCHAR(30) NOT NULL,
    estacion_id INT NOT NULL,
    empleado_id INT NOT NULL,
    cliente_id INT NULL,
    fecha_hora DATETIME NOT NULL,
    estado ENUM('PENDIENTE_PAGO', 'PAGADA', 'ANULADA') NOT NULL DEFAULT 'PENDIENTE_PAGO',
    total DECIMAL(10, 2) NOT NULL,
    CONSTRAINT uq_ventas_referencia UNIQUE (referencia_ticket),
    CONSTRAINT chk_ventas_total CHECK (total > 0),
    CONSTRAINT fk_ventas_estacion FOREIGN KEY (estacion_id)
        REFERENCES estaciones (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_ventas_empleado FOREIGN KEY (empleado_id)
        REFERENCES empleados (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_ventas_cliente FOREIGN KEY (cliente_id)
        REFERENCES clientes (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE lineas_venta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venta_id INT NOT NULL,
    numero_orden INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad DECIMAL(10, 3) NOT NULL,
    precio_unitario_aplicado DECIMAL(10, 2) NOT NULL,
    importe DECIMAL(10, 2) NOT NULL,
    CONSTRAINT uq_lineas_venta_orden UNIQUE (venta_id, numero_orden),
    CONSTRAINT chk_lineas_orden CHECK (numero_orden >= 1),
    CONSTRAINT chk_lineas_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_lineas_precio CHECK (precio_unitario_aplicado > 0),
    CONSTRAINT chk_lineas_importe CHECK (importe > 0),
    CONSTRAINT fk_lineas_venta FOREIGN KEY (venta_id)
        REFERENCES ventas (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_lineas_producto FOREIGN KEY (producto_id)
        REFERENCES productos (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE pagos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_operacion ENUM('REPOSTAJE', 'TIENDA') NOT NULL DEFAULT 'REPOSTAJE',
    cliente_id INT NULL,
    venta_id INT NULL,
    fecha DATE NOT NULL,
    fecha_hora DATETIME NULL,
    importe DECIMAL(10, 2) NOT NULL,
    medio_pago ENUM('EFECTIVO', 'TARJETA', 'OTRO') NULL,
    litros DECIMAL(10, 3) NULL,
    combustible VARCHAR(50) NULL,
    CONSTRAINT chk_pagos_importe CHECK (importe > 0),
    CONSTRAINT chk_pagos_tipo_coherencia CHECK (
        (
            tipo_operacion = 'REPOSTAJE'
            AND venta_id IS NULL
            AND cliente_id IS NOT NULL
            AND litros IS NOT NULL AND litros > 0
            AND combustible IS NOT NULL
            AND fecha_hora IS NULL
            AND medio_pago IS NULL
        )
        OR
        (
            tipo_operacion = 'TIENDA'
            AND venta_id IS NOT NULL
            AND fecha_hora IS NOT NULL
            AND medio_pago IS NOT NULL
            AND litros IS NULL
            AND combustible IS NULL
        )
    ),
    CONSTRAINT fk_pagos_cliente FOREIGN KEY (cliente_id)
        REFERENCES clientes (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_pagos_venta FOREIGN KEY (venta_id)
        REFERENCES ventas (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE devoluciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    linea_venta_id INT NOT NULL,
    empleado_id INT NOT NULL,
    cantidad DECIMAL(10, 3) NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    fecha_hora DATETIME NOT NULL,
    importe_reintegrado DECIMAL(10, 2) NOT NULL,
    apta_reventa BOOLEAN NOT NULL,
    CONSTRAINT chk_devoluciones_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_devoluciones_importe CHECK (importe_reintegrado > 0),
    CONSTRAINT fk_devoluciones_linea FOREIGN KEY (linea_venta_id)
        REFERENCES lineas_venta (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_devoluciones_empleado FOREIGN KEY (empleado_id)
        REFERENCES empleados (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE incidencias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    referencia VARCHAR(30) NOT NULL,
    estacion_id INT NOT NULL,
    empleado_responsable_id INT NULL,
    descripcion TEXT NOT NULL,
    fecha_hora_apertura DATETIME NOT NULL,
    estado ENUM('ABIERTA', 'EN_CURSO', 'RESUELTA', 'CERRADA') NOT NULL DEFAULT 'ABIERTA',
    CONSTRAINT uq_incidencias_referencia UNIQUE (referencia),
    CONSTRAINT fk_incidencias_estacion FOREIGN KEY (estacion_id)
        REFERENCES estaciones (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_incidencias_empleado FOREIGN KEY (empleado_responsable_id)
        REFERENCES empleados (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
