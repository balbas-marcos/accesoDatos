package com.gasolinera.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/** Incidencia de estación; su responsable actual es independiente de funciones y supervisión. */
public final class Incidencia {

    private final int id;
    private final String referencia;
    private final int estacionId;
    private Integer empleadoResponsableId;
    private String descripcion;
    private final LocalDateTime fechaHoraApertura;
    private EstadoIncidencia estado;

    public Incidencia(int id, String referencia, int estacionId, Integer empleadoResponsableId,
                      String descripcion, LocalDateTime fechaHoraApertura) {
        this.id = Validaciones.positivo(id, "id");
        this.referencia = Validaciones.texto(referencia, "referencia");
        this.estacionId = Validaciones.positivo(estacionId, "estacion_id");
        if (empleadoResponsableId != null) {
            Validaciones.positivo(empleadoResponsableId, "empleado_responsable_id");
        }
        this.empleadoResponsableId = empleadoResponsableId;
        this.descripcion = Validaciones.texto(descripcion, "descripcion");
        this.fechaHoraApertura = Objects.requireNonNull(fechaHoraApertura, "fecha_hora_apertura es obligatoria");
        this.estado = EstadoIncidencia.ABIERTA;
    }

    public int getId() {
        return id;
    }

    public String getReferencia() {
        return referencia;
    }

    public int getEstacionId() {
        return estacionId;
    }

    public Optional<Integer> getEmpleadoResponsableId() {
        return Optional.ofNullable(empleadoResponsableId);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getFechaHoraApertura() {
        return fechaHoraApertura;
    }

    public EstadoIncidencia getEstado() {
        return estado;
    }

    public void cambiarResponsable(Integer empleadoResponsableId) {
        if (empleadoResponsableId != null) {
            Validaciones.positivo(empleadoResponsableId, "empleado_responsable_id");
        }
        this.empleadoResponsableId = empleadoResponsableId;
    }

    public void cambiarEstado(EstadoIncidencia estado) {
        this.estado = Objects.requireNonNull(estado, "estado es obligatorio");
    }
}
