package com.gasolinera.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Valor monetario con dos decimales y redondeo comercial HALF_UP.
 * Es el único punto del dominio donde se redondea un importe.
 */
public record Importe(BigDecimal valor) {

    public static final int ESCALA = 2;

    public Importe {
        if (valor == null) {
            throw new IllegalArgumentException("El importe es obligatorio");
        }
        valor = valor.setScale(ESCALA, RoundingMode.HALF_UP);
    }

    public static Importe de(BigDecimal cantidad, BigDecimal precioUnitario) {
        if (cantidad == null) {
            throw new IllegalArgumentException("La cantidad es obligatoria");
        }
        if (precioUnitario == null) {
            throw new IllegalArgumentException("El precio unitario es obligatorio");
        }
        return new Importe(cantidad.multiply(precioUnitario));
    }

    public static Importe cero() {
        return new Importe(BigDecimal.ZERO);
    }

    public boolean esPositivo() {
        return valor.signum() > 0;
    }
}
