-- =============================================================================
-- 02_triggers.sql
-- Fase 1: Trigger automático para registrar cambios reales de precio de venta
-- Orden de ejecución: 2.º (después de 01_esquema.sql y antes de 03_datos.sql)
-- =============================================================================

USE gasolinera_tienda;

DROP TRIGGER IF EXISTS trg_productos_cambio_precio;

DELIMITER $$

CREATE TRIGGER trg_productos_cambio_precio
AFTER UPDATE ON productos
FOR EACH ROW
BEGIN
    -- Solo registra historial cuando cambia realmente el precio de venta.
    -- No se dispara en INSERT, ni al guardar el mismo precio, ni al modificar
    -- únicamente descripción o estado. Al ser tablas InnoDB, si la transacción
    -- hace ROLLBACK, esta inserción se revierte automáticamente junto al UPDATE.
    IF OLD.precio_venta <> NEW.precio_venta THEN
        INSERT INTO historial_precios (
            producto_id,
            precio_anterior,
            precio_nuevo,
            fecha_cambio
        ) VALUES (
            NEW.id,
            OLD.precio_venta,
            NEW.precio_venta,
            NOW()
        );
    END IF;
END$$

-- Triggers auxiliares de integridad: evitan la auto-supervisión directa en empleados
DROP TRIGGER IF EXISTS trg_empleados_no_autosup_ins$$
CREATE TRIGGER trg_empleados_no_autosup_ins
BEFORE INSERT ON empleados
FOR EACH ROW
BEGIN
    IF NEW.id > 0 AND NEW.supervisor_id IS NOT NULL AND NEW.supervisor_id = NEW.id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Un empleado no puede ser supervisor de sí mismo.';
    END IF;
END$$

DROP TRIGGER IF EXISTS trg_empleados_no_autosup_upd$$
CREATE TRIGGER trg_empleados_no_autosup_upd
BEFORE UPDATE ON empleados
FOR EACH ROW
BEGIN
    IF NEW.supervisor_id IS NOT NULL AND NEW.supervisor_id = NEW.id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Un empleado no puede ser supervisor de sí mismo.';
    END IF;
END$$

DELIMITER ;
