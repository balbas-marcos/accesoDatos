package com.gasolinera.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record DetalleCajero(BigDecimal limiteDevoluciones) implements DetalleFuncion {

    public DetalleCajero {
        if (limiteDevoluciones == null || limiteDevoluciones.signum() < 0) {
            throw new IllegalArgumentException("El límite de devoluciones no puede ser negativo");
        }
        limiteDevoluciones = limiteDevoluciones.setScale(2, RoundingMode.HALF_UP);
    }
}
