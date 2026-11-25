package com.gasolinera.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

/** Registro de un cambio real de precio (equivalente a la fila del trigger SQL). */
public record HistorialPrecio(int id, int productoId, BigDecimal precioAnterior,
                              BigDecimal precioNuevo, LocalDateTime fechaCambio) {

    public HistorialPrecio {
        Validaciones.positivo(id, "id");
        Validaciones.positivo(productoId, "producto_id");
        if (precioAnterior == null || precioNuevo == null) {
            throw new IllegalArgumentException("Los precios del histórico son obligatorios");
        }
        precioAnterior = precioAnterior.setScale(2, RoundingMode.HALF_UP);
        precioNuevo = precioNuevo.setScale(2, RoundingMode.HALF_UP);
        if (precioAnterior.signum() <= 0 || precioNuevo.signum() <= 0) {
            throw new IllegalArgumentException("Los precios del histórico deben ser > 0");
        }
        if (precioAnterior.compareTo(precioNuevo) == 0) {
            throw new IllegalArgumentException("El histórico solo registra cambios reales de precio");
        }
        Objects.requireNonNull(fechaCambio, "fecha_cambio es obligatoria");
    }
}
