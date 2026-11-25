package com.gasolinera.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Relación N:M proveedor-producto con la referencia interna del proveedor. */
public final class AcuerdoSuministro {

    private final int id;
    private final int proveedorId;
    private final int productoId;
    private final String referenciaProveedor;
    private BigDecimal precioCompra;
    private int plazoEntregaDias;

    public AcuerdoSuministro(int id, int proveedorId, int productoId, String referenciaProveedor,
                             BigDecimal precioCompra, int plazoEntregaDias) {
        this.id = Validaciones.positivo(id, "id");
        this.proveedorId = Validaciones.positivo(proveedorId, "proveedor_id");
        this.productoId = Validaciones.positivo(productoId, "producto_id");
        this.referenciaProveedor = Validaciones.texto(referenciaProveedor, "referencia_proveedor");
        this.plazoEntregaDias = Validaciones.noNegativo(plazoEntregaDias, "plazo_entrega_dias");
        this.precioCompra = validarPrecio(precioCompra);
    }

    private static BigDecimal validarPrecio(BigDecimal precio) {
        if (precio == null) {
            throw new IllegalArgumentException("El precio de compra es obligatorio");
        }
        BigDecimal normalizado = precio.setScale(2, RoundingMode.HALF_UP);
        if (normalizado.signum() <= 0) {
            throw new IllegalArgumentException("El precio de compra debe ser > 0");
        }
        return normalizado;
    }

    public int getId() {
        return id;
    }

    public int getProveedorId() {
        return proveedorId;
    }

    public int getProductoId() {
        return productoId;
    }

    public String getReferenciaProveedor() {
        return referenciaProveedor;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public int getPlazoEntregaDias() {
        return plazoEntregaDias;
    }

    public void cambiarCondiciones(BigDecimal precioCompra, int plazoEntregaDias) {
        this.precioCompra = validarPrecio(precioCompra);
        this.plazoEntregaDias = Validaciones.noNegativo(plazoEntregaDias, "plazo_entrega_dias");
    }
}
