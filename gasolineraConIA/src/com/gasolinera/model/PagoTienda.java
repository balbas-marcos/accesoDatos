package com.gasolinera.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/** Cobro de tienda, integrado con una venta. El pago de repostaje sigue siendo Pago (heredado). */
public final class PagoTienda {

    private final int id;
    private final int ventaId;
    private final Integer clienteId;
    private final LocalDateTime fechaHora;
    private final Importe importe;
    private final MedioPago medioPago;

    public PagoTienda(int id, int ventaId, Integer clienteId, LocalDateTime fechaHora,
                      Importe importe, MedioPago medioPago) {
        this.id = Validaciones.positivo(id, "id");
        this.ventaId = Validaciones.positivo(ventaId, "venta_id");
        if (clienteId != null) {
            Validaciones.positivo(clienteId, "cliente_id");
        }
        this.clienteId = clienteId;
        this.fechaHora = Objects.requireNonNull(fechaHora, "fecha_hora es obligatoria");
        if (importe == null || !importe.esPositivo()) {
            throw new IllegalArgumentException("El importe del pago debe ser positivo");
        }
        this.importe = importe;
        this.medioPago = Objects.requireNonNull(medioPago, "medio_pago es obligatorio");
    }

    public int getId() {
        return id;
    }

    public int getVentaId() {
        return ventaId;
    }

    public Optional<Integer> getClienteId() {
        return Optional.ofNullable(clienteId);
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public Importe getImporte() {
        return importe;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }
}
