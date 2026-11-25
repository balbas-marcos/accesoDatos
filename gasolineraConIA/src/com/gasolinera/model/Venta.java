package com.gasolinera.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Venta de tienda: ticket, estación y empleado propios, cliente opcional y al menos una línea. */
public final class Venta {

    private final int id;
    private final String referenciaTicket;
    private final int estacionId;
    private final int empleadoId;
    private final Integer clienteId;
    private final LocalDateTime fechaHora;
    private EstadoVenta estado;
    private final List<LineaVenta> lineas = new ArrayList<>();

    public Venta(int id, String referenciaTicket, int estacionId, int empleadoId, Integer clienteId,
                 LocalDateTime fechaHora) {
        this.id = Validaciones.positivo(id, "id");
        this.referenciaTicket = Validaciones.texto(referenciaTicket, "referencia_ticket");
        this.estacionId = Validaciones.positivo(estacionId, "estacion_id");
        this.empleadoId = Validaciones.positivo(empleadoId, "empleado_id");
        if (clienteId != null) {
            Validaciones.positivo(clienteId, "cliente_id");
        }
        this.clienteId = clienteId;
        this.fechaHora = Objects.requireNonNull(fechaHora, "fecha_hora es obligatoria");
        this.estado = EstadoVenta.PENDIENTE_PAGO;
    }

    public int getId() {
        return id;
    }

    public String getReferenciaTicket() {
        return referenciaTicket;
    }

    public int getEstacionId() {
        return estacionId;
    }

    public int getEmpleadoId() {
        return empleadoId;
    }

    public Optional<Integer> getClienteId() {
        return Optional.ofNullable(clienteId);
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public EstadoVenta getEstado() {
        return estado;
    }

    public List<LineaVenta> getLineas() {
        return List.copyOf(lineas);
    }

    public boolean tieneLineas() {
        return !lineas.isEmpty();
    }

    /** El número de orden es único dentro del ticket. */
    public void agregarLinea(LineaVenta linea) {
        Objects.requireNonNull(linea, "La línea es obligatoria");
        if (estado != EstadoVenta.PENDIENTE_PAGO) {
            throw new IllegalArgumentException("Solo se pueden añadir líneas a una venta pendiente de pago");
        }
        boolean repetido = lineas.stream().anyMatch(l -> l.getNumeroOrden() == linea.getNumeroOrden());
        if (repetido) {
            throw new IllegalArgumentException("El número de orden " + linea.getNumeroOrden() + " ya existe en el ticket");
        }
        lineas.add(linea);
    }

    /** Total en memoria equivalente a ventas.total; se recalcula desde los importes ya redondeados. */
    public Importe calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (LineaVenta linea : lineas) {
            total = total.add(linea.getImporte().valor());
        }
        return new Importe(total);
    }

    public boolean puedeMarcarsePagada(Importe totalPagado) {
        Objects.requireNonNull(totalPagado, "El total pagado es obligatorio");
        return tieneLineas()
                && estado == EstadoVenta.PENDIENTE_PAGO
                && totalPagado.valor().compareTo(calcularTotal().valor()) == 0;
    }

    public void marcarPagada(Importe totalPagado) {
        if (!puedeMarcarsePagada(totalPagado)) {
            throw new IllegalArgumentException("La venta no puede marcarse como pagada: carrito vacío, estado incorrecto o importes no cuadrados");
        }
        this.estado = EstadoVenta.PAGADA;
    }

    public void anular() {
        if (estado == EstadoVenta.PAGADA) {
            throw new IllegalArgumentException("Una venta pagada no puede anularse desde el dominio");
        }
        this.estado = EstadoVenta.ANULADA;
    }
}
