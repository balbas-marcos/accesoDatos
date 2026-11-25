package com.gasolinera.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Línea de venta con precio unitario congelado e importe calculado con HALF_UP. */
public final class LineaVenta {

    private final int id;
    private final int numeroOrden;
    private final Producto producto;
    private final BigDecimal cantidad;
    private final BigDecimal precioUnitarioAplicado;
    private final Importe importe;

    public LineaVenta(int id, int numeroOrden, Producto producto, BigDecimal cantidad,
                      BigDecimal precioUnitarioAplicado) {
        this.id = Validaciones.positivo(id, "id");
        this.numeroOrden = Validaciones.positivo(numeroOrden, "numero_orden");
        this.producto = Objects.requireNonNull(producto, "producto es obligatorio");
        if (cantidad == null || cantidad.signum() <= 0) {
            throw new IllegalArgumentException("La cantidad de la línea debe ser positiva");
        }
        this.cantidad = cantidad;
        if (precioUnitarioAplicado == null) {
            throw new IllegalArgumentException("El precio unitario aplicado es obligatorio");
        }
        BigDecimal precio = precioUnitarioAplicado.setScale(2, RoundingMode.HALF_UP);
        if (precio.signum() <= 0) {
            throw new IllegalArgumentException("El precio unitario aplicado debe ser > 0");
        }
        this.precioUnitarioAplicado = precio;
        producto.validarCantidad(cantidad);
        this.importe = Importe.de(cantidad, this.precioUnitarioAplicado);
    }

    public int getId() {
        return id;
    }

    public int getNumeroOrden() {
        return numeroOrden;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getProductoId() {
        return producto.getId();
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitarioAplicado() {
        return precioUnitarioAplicado;
    }

    public Importe getImporte() {
        return importe;
    }

    @Override
    public String toString() {
        return numeroOrden + ". " + producto.getDescripcion() + " · " + cantidad
                + " × " + precioUnitarioAplicado + " = " + importe.valor();
    }
}
