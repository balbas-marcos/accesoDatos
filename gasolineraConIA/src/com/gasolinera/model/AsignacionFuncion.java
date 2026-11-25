package com.gasolinera.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/** Periodo en el que una persona ejerce una función, con sus condiciones propias. */
public final class AsignacionFuncion {

    private static final LocalDate FIN_ABIERTO = LocalDate.of(9999, 12, 31);

    private final int id;
    private final int empleadoId;
    private final int tipoFuncionId;
    private final LocalDate fechaInicio;
    private LocalDate fechaFin;
    private DetalleFuncion detalle;

    public AsignacionFuncion(int id, int empleadoId, int tipoFuncionId, LocalDate fechaInicio,
                             LocalDate fechaFin, DetalleFuncion detalle) {
        this.id = Validaciones.positivo(id, "id");
        this.empleadoId = Validaciones.positivo(empleadoId, "empleado_id");
        this.tipoFuncionId = Validaciones.positivo(tipoFuncionId, "tipo_funcion_id");
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "fecha_inicio es obligatoria");
        validarFechaFin(fechaFin);
        this.fechaFin = fechaFin;
        this.detalle = detalle;
    }

    private void validarFechaFin(LocalDate fechaFin) {
        if (fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("fecha_fin no puede ser anterior a fecha_inicio");
        }
    }

    public int getId() {
        return id;
    }

    public int getEmpleadoId() {
        return empleadoId;
    }

    public int getTipoFuncionId() {
        return tipoFuncionId;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public Optional<LocalDate> getFechaFin() {
        return Optional.ofNullable(fechaFin);
    }

    public Optional<DetalleFuncion> getDetalle() {
        return Optional.ofNullable(detalle);
    }

    /** Un periodo sin fecha_fin se considera abierto hasta FIN_ABIERTO. */
    public boolean vigenteEn(LocalDate fecha) {
        Objects.requireNonNull(fecha, "La fecha a consultar es obligatoria");
        LocalDate fin = fechaFin == null ? FIN_ABIERTO : fechaFin;
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fin);
    }

    /** Solo se consideran solapes entre la misma persona y la misma función. */
    public boolean solapaCon(AsignacionFuncion otra) {
        if (otra == null || otra.empleadoId != this.empleadoId || otra.tipoFuncionId != this.tipoFuncionId) {
            return false;
        }
        LocalDate finEsta = this.fechaFin == null ? FIN_ABIERTO : this.fechaFin;
        LocalDate finOtra = otra.fechaFin == null ? FIN_ABIERTO : otra.fechaFin;
        return !this.fechaInicio.isAfter(finOtra) && !otra.fechaInicio.isAfter(finEsta);
    }

    public void cerrar(LocalDate fechaFin) {
        validarFechaFin(fechaFin);
        this.fechaFin = fechaFin;
    }

    /** En Fase 2 el alta conjunta asignación + detalle se hará en una misma transacción. */
    public void asignarDetalle(DetalleFuncion detalle) {
        if (this.detalle != null) {
            throw new IllegalArgumentException("Esta asignación ya tiene un detalle de función");
        }
        this.detalle = Objects.requireNonNull(detalle, "El detalle de la función es obligatorio");
    }
}
