package com.gasolinera.model;

import java.time.LocalDate;
import java.util.Objects;

/** Inscripción de un cliente registrado en una campaña (no requiere tarjeta). */
public final class InscripcionCampana {

    private final int id;
    private final int campanaId;
    private final int clienteId;
    private final LocalDate fechaInscripcion;

    public InscripcionCampana(int id, int campanaId, int clienteId, LocalDate fechaInscripcion) {
        this.id = Validaciones.positivo(id, "id");
        this.campanaId = Validaciones.positivo(campanaId, "campana_id");
        this.clienteId = Validaciones.positivo(clienteId, "cliente_id");
        this.fechaInscripcion = Objects.requireNonNull(fechaInscripcion, "fecha_inscripcion es obligatoria");
    }

    public int getId() {
        return id;
    }

    public int getCampanaId() {
        return campanaId;
    }

    public int getClienteId() {
        return clienteId;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }
}
