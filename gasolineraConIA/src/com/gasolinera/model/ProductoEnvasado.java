package com.gasolinera.model;

import java.math.BigDecimal;

/** Producto envasado: se vende en unidades enteras positivas y tiene formato de envase. */
public final class ProductoEnvasado extends Producto {

    private final String formatoEnvase;

    public ProductoEnvasado(int id, String referenciaComercial, String descripcion, BigDecimal precioVenta,
                            EstadoProducto estado, String formatoEnvase) {
        super(id, referenciaComercial, descripcion, precioVenta, estado, Modalidad.ENVASADO);
        this.formatoEnvase = Validaciones.texto(formatoEnvase, "formato_envase");
    }

    public String getFormatoEnvase() {
        return formatoEnvase;
    }

    @Override
    public void validarCantidad(BigDecimal cantidad) {
        if (cantidad == null || cantidad.signum() <= 0 || cantidad.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("ENVASADO: la cantidad debe ser un número entero positivo");
        }
    }
}
