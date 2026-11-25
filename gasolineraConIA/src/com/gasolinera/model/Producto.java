package com.gasolinera.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Producto de catálogo. La modalidad es fija: no hay setter y la jerarquía es sealed,
 * por lo que un producto no puede pasar de ENVASADO a GRANEL ni al revés.
 */
public sealed abstract class Producto permits ProductoEnvasado, ProductoGranel {

    private final int id;
    private final String referenciaComercial;
    private String descripcion;
    private BigDecimal precioVenta;
    private EstadoProducto estado;
    private final Modalidad modalidad;

    protected Producto(int id, String referenciaComercial, String descripcion, BigDecimal precioVenta,
                       EstadoProducto estado, Modalidad modalidad) {
        this.id = Validaciones.positivo(id, "id");
        this.referenciaComercial = Validaciones.texto(referenciaComercial, "referencia_comercial");
        this.descripcion = Validaciones.texto(descripcion, "descripcion");
        this.modalidad = Objects.requireNonNull(modalidad, "modalidad es obligatoria");
        this.estado = Objects.requireNonNull(estado, "estado es obligatorio");
        this.precioVenta = validarPrecio(precioVenta);
    }

    private static BigDecimal validarPrecio(BigDecimal precio) {
        if (precio == null) {
            throw new IllegalArgumentException("El precio de venta es obligatorio");
        }
        BigDecimal normalizado = precio.setScale(2, RoundingMode.HALF_UP);
        if (normalizado.signum() <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor que 0");
        }
        return normalizado;
    }

    public abstract void validarCantidad(BigDecimal cantidad);

    /** Valida la cantidad según la modalidad y calcula el importe con HALF_UP. */
    public final Importe calcularImporte(BigDecimal cantidad) {
        validarCantidad(cantidad);
        return Importe.de(cantidad, precioVenta);
    }

    public final void cambiarPrecio(BigDecimal nuevoPrecio) {
        this.precioVenta = validarPrecio(nuevoPrecio);
    }

    public final void cambiarDescripcion(String descripcion) {
        this.descripcion = Validaciones.texto(descripcion, "descripcion");
    }

    public final void cambiarEstado(EstadoProducto estado) {
        this.estado = Objects.requireNonNull(estado, "estado es obligatorio");
    }

    public int getId() {
        return id;
    }

    public String getReferenciaComercial() {
        return referenciaComercial;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public EstadoProducto getEstado() {
        return estado;
    }

    public Modalidad getModalidad() {
        return modalidad;
    }

    @Override
    public String toString() {
        return referenciaComercial + " [" + modalidad + "] " + descripcion + " · " + precioVenta;
    }
}
