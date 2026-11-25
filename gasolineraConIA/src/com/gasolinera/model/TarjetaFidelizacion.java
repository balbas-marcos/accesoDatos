package com.gasolinera.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/** Tarjeta con número único; puede estar emitida pero sin asignar (lote). */
public final class TarjetaFidelizacion {

    private final int id;
    private final String numeroTarjeta;
    private final LocalDate fechaEmision;
    private Integer clienteId;
    private LocalDate fechaAsignacion;

    public TarjetaFidelizacion(int id, String numeroTarjeta, LocalDate fechaEmision,
                               Integer clienteId, LocalDate fechaAsignacion) {
        this.id = Validaciones.positivo(id, "id");
        this.numeroTarjeta = Validaciones.texto(numeroTarjeta, "numero_tarjeta");
        this.fechaEmision = Objects.requireNonNull(fechaEmision, "fecha_emision es obligatoria");
        validarAsignacion(clienteId, fechaAsignacion);
        this.clienteId = clienteId;
        this.fechaAsignacion = fechaAsignacion;
    }

    private void validarAsignacion(Integer clienteId, LocalDate fechaAsignacion) {
        if (clienteId == null && fechaAsignacion == null) {
            return;
        }
        if (clienteId == null || fechaAsignacion == null) {
            throw new IllegalArgumentException("cliente_id y fecha_asignacion deben informarse a la vez");
        }
        Validaciones.positivo(clienteId, "cliente_id");
        if (fechaAsignacion.isBefore(fechaEmision)) {
            throw new IllegalArgumentException("fecha_asignacion no puede ser anterior a fecha_emision");
        }
    }

    public int getId() {
        return id;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public Optional<Integer> getClienteId() {
        return Optional.ofNullable(clienteId);
    }

    public Optional<LocalDate> getFechaAsignacion() {
        return Optional.ofNullable(fechaAsignacion);
    }

    public boolean estaAsignada() {
        return clienteId != null;
    }

    public void asignar(int clienteId, LocalDate fechaAsignacion) {
        validarAsignacion(clienteId, fechaAsignacion);
        this.clienteId = clienteId;
        this.fechaAsignacion = fechaAsignacion;
    }

    public void desasignar() {
        this.clienteId = null;
        this.fechaAsignacion = null;
    }
}
