package com.gasolinera.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/** Devolución parcial sobre una línea de venta ya pagada; respeta modalidad, precio y topes. */
public final class Devolucion {

    private final int id;
    private final int lineaVentaId;
    private final int empleadoId;
    private final BigDecimal cantidad;
    private final String motivo;
    private final LocalDateTime fechaHora;
    private final Importe importeReintegrado;
    private final boolean aptaReventa;

    public Devolucion(int id, int lineaVentaId, int empleadoId, BigDecimal cantidad, String motivo,
                      LocalDateTime fechaHora, Importe importeReintegrado, boolean aptaReventa) {
        this.id = Validaciones.positivo(id, "id");
        this.lineaVentaId = Validaciones.positivo(lineaVentaId, "linea_venta_id");
        this.empleadoId = Validaciones.positivo(empleadoId, "empleado_id");
        if (cantidad == null || cantidad.signum() <= 0) {
            throw new IllegalArgumentException("La cantidad devuelta debe ser positiva");
        }
        this.cantidad = cantidad;
        this.motivo = Validaciones.texto(motivo, "motivo");
        this.fechaHora = Objects.requireNonNull(fechaHora, "fecha_hora es obligatoria");
        if (importeReintegrado == null || !importeReintegrado.esPositivo()) {
            throw new IllegalArgumentException("El importe reintegrado debe ser positivo");
        }
        this.importeReintegrado = importeReintegrado;
        this.aptaReventa = aptaReventa;
    }

    /** Calcula el reintegro con el precio congelado de la línea y valida su modalidad. */
    public static Devolucion sobre(LineaVenta linea, int id, int empleadoId, BigDecimal cantidad,
                                   String motivo, LocalDateTime fechaHora, boolean aptaReventa) {
        Objects.requireNonNull(linea, "La línea de venta es obligatoria");
        linea.getProducto().validarCantidad(cantidad);
        Importe reintegro = Importe.de(cantidad, linea.getPrecioUnitarioAplicado());
        return new Devolucion(id, linea.getId(), empleadoId, cantidad, motivo, fechaHora, reintegro, aptaReventa);
    }

    public static BigDecimal cantidadPendiente(LineaVenta linea, List<Devolucion> previas) {
        Objects.requireNonNull(linea, "La línea de venta es obligatoria");
        BigDecimal devuelta = BigDecimal.ZERO;
        if (previas != null) {
            for (Devolucion devolucion : previas) {
                if (devolucion.lineaVentaId == linea.getId()) {
                    devuelta = devuelta.add(devolucion.cantidad);
                }
            }
        }
        BigDecimal pendiente = linea.getCantidad().subtract(devuelta);
        return pendiente.signum() < 0 ? BigDecimal.ZERO : pendiente;
    }

    /** Última devolución: el importe disponible se limita al importe original de la línea. */
    public static Importe importePendiente(LineaVenta linea, List<Devolucion> previas) {
        Objects.requireNonNull(linea, "La línea de venta es obligatoria");
        BigDecimal reintegrado = BigDecimal.ZERO;
        if (previas != null) {
            for (Devolucion devolucion : previas) {
                if (devolucion.lineaVentaId == linea.getId()) {
                    reintegrado = reintegrado.add(devolucion.importeReintegrado.valor());
                }
            }
        }
        BigDecimal pendiente = linea.getImporte().valor().subtract(reintegrado);
        return new Importe(pendiente.signum() < 0 ? BigDecimal.ZERO : pendiente);
    }

    /** Comprobación pura; en Fase 2 se ejecutará dentro de una transacción con FOR UPDATE. */
    public void validarDentroDe(LineaVenta linea, List<Devolucion> previas) {
        Objects.requireNonNull(linea, "La línea de venta es obligatoria");
        if (linea.getId() != this.lineaVentaId) {
            throw new IllegalArgumentException("La devolución no corresponde a la línea indicada");
        }
        if (cantidad.compareTo(cantidadPendiente(linea, previas)) > 0) {
            throw new IllegalArgumentException("La cantidad devuelta supera la cantidad pendiente de la línea");
        }
        if (importeReintegrado.valor().compareTo(importePendiente(linea, previas).valor()) > 0) {
            throw new IllegalArgumentException("El importe reintegrado supera el importe pendiente de la línea");
        }
    }

    public int getId() {
        return id;
    }

    public int getLineaVentaId() {
        return lineaVentaId;
    }

    public int getEmpleadoId() {
        return empleadoId;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public Importe getImporteReintegrado() {
        return importeReintegrado;
    }

    public boolean isAptaReventa() {
        return aptaReventa;
    }
}
