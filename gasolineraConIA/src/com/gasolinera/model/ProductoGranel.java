package com.gasolinera.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Producto a granel: cantidad decimal múltiplo de la precisión de pesada. */
public final class ProductoGranel extends Producto {

    private final BigDecimal precisionPesada;

    public ProductoGranel(int id, String referenciaComercial, String descripcion, BigDecimal precioVenta,
                          EstadoProducto estado, BigDecimal precisionPesada) {
        super(id, referenciaComercial, descripcion, precioVenta, estado, Modalidad.GRANEL);
        if (precisionPesada == null || precisionPesada.signum() <= 0 || precisionPesada.scale() > 3) {
            throw new IllegalArgumentException("GRANEL: la precisión de pesada debe ser > 0 y tener como máximo 3 decimales");
        }
        this.precisionPesada = precisionPesada;
    }

    public BigDecimal getPrecisionPesada() {
        return precisionPesada;
    }

    @Override
    public void validarCantidad(BigDecimal cantidad) {
        if (cantidad == null || cantidad.signum() <= 0 || cantidad.scale() > 3) {
            throw new IllegalArgumentException("GRANEL: la cantidad debe ser positiva y tener como máximo 3 decimales");
        }
        try {
            cantidad.divide(precisionPesada, 0, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException(
                    "GRANEL: la cantidad debe ser múltiplo de " + precisionPesada.toPlainString(), e);
        }
    }
}
