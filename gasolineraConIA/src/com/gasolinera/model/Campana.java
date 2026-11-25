package com.gasolinera.model;

import java.time.LocalDate;
import java.util.Objects;

/** Campaña promocional con intervalo de fechas válido. */
public final class Campana {

    private final int id;
    private final String codigoCampana;
    private String nombre;
    private String descripcion;
    private final LocalDate fechaInicio;
    private LocalDate fechaFin;

    public Campana(int id, String codigoCampana, String nombre, String descripcion,
                   LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = Validaciones.positivo(id, "id");
        this.codigoCampana = Validaciones.texto(codigoCampana, "codigo_campana");
        this.nombre = Validaciones.texto(nombre, "nombre");
        this.descripcion = Validaciones.texto(descripcion, "descripcion");
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "fecha_inicio es obligatoria");
        this.fechaFin = Objects.requireNonNull(fechaFin, "fecha_fin es obligatoria");
        validarFechaFin(fechaFin);
    }

    private void validarFechaFin(LocalDate fechaFin) {
        if (fechaFin == null || fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("fecha_fin no puede ser anterior a fecha_inicio");
        }
    }

    public int getId() {
        return id;
    }

    public String getCodigoCampana() {
        return codigoCampana;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public boolean vigenteEn(LocalDate fecha) {
        Objects.requireNonNull(fecha, "La fecha a consultar es obligatoria");
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    public void ampliarHasta(LocalDate fechaFin) {
        validarFechaFin(fechaFin);
        this.fechaFin = fechaFin;
    }
}
