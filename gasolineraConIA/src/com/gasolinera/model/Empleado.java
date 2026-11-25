package com.gasolinera.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/** Empleado: su identidad no cambia aunque cambien su estación, función o supervisor. */
public final class Empleado {

    private final int id;
    private final String numeroInterno;
    private final String documentoIdentidad;
    private String nombre;
    private String telefono;
    private String email;
    private final LocalDate fechaIncorporacion;
    private int estacionActualId;
    private Integer supervisorId;

    public Empleado(int id, String numeroInterno, String documentoIdentidad, String nombre, String telefono,
                    String email, LocalDate fechaIncorporacion, int estacionActualId, Integer supervisorId) {
        this.id = Validaciones.positivo(id, "id");
        this.numeroInterno = Validaciones.texto(numeroInterno, "numero_interno");
        this.documentoIdentidad = Validaciones.texto(documentoIdentidad, "documento_identidad");
        this.nombre = Validaciones.texto(nombre, "nombre");
        this.telefono = Validaciones.texto(telefono, "telefono");
        this.email = Validaciones.textoOpcional(email, "email");
        this.fechaIncorporacion = Objects.requireNonNull(fechaIncorporacion, "fecha_incorporacion es obligatoria");
        this.estacionActualId = Validaciones.positivo(estacionActualId, "estacion_actual_id");
        cambiarSupervisor(supervisorId);
    }

    public int getId() {
        return id;
    }

    public String getNumeroInterno() {
        return numeroInterno;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public LocalDate getFechaIncorporacion() {
        return fechaIncorporacion;
    }

    public int getEstacionActualId() {
        return estacionActualId;
    }

    public Optional<Integer> getSupervisorId() {
        return Optional.ofNullable(supervisorId);
    }

    public void cambiarNombre(String nombre) {
        this.nombre = Validaciones.texto(nombre, "nombre");
    }

    public void cambiarTelefono(String telefono) {
        this.telefono = Validaciones.texto(telefono, "telefono");
    }

    public void cambiarEmail(String email) {
        this.email = Validaciones.textoOpcional(email, "email");
    }

    /** Cambiar de estación no altera la estación guardada en ventas anteriores. */
    public void cambiarEstacion(int estacionActualId) {
        this.estacionActualId = Validaciones.positivo(estacionActualId, "estacion_actual_id");
    }

    /** La auto-supervisión directa se rechaza aquí; los ciclos indirectos se comprueban en Fase 2. */
    public void cambiarSupervisor(Integer supervisorId) {
        if (supervisorId != null) {
            Validaciones.positivo(supervisorId, "supervisor_id");
            if (supervisorId.intValue() == this.id) {
                throw new IllegalArgumentException("Un empleado no puede ser supervisor de sí mismo");
            }
        }
        this.supervisorId = supervisorId;
    }

    @Override
    public String toString() {
        return numeroInterno + " - " + nombre;
    }
}
