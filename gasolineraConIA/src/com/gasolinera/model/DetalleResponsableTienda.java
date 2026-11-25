package com.gasolinera.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record DetalleResponsableTienda(BigDecimal presupuestoCompras) implements DetalleFuncion {

    public DetalleResponsableTienda {
        if (presupuestoCompras == null || presupuestoCompras.signum() < 0) {
            throw new IllegalArgumentException("El presupuesto de compras no puede ser negativo");
        }
        presupuestoCompras = presupuestoCompras.setScale(2, RoundingMode.HALF_UP);
    }
}
