package com.gasolinera.model;

import java.time.LocalDate;
import java.util.Objects;

/** Estación de servicio con su identidad de negocio y su ubicación. */
public final class Estacion {

    private final int id;
    private final String codigoInterno;
    private String nombreComercial;
    private String direccion;
    private String telefono;
    private final LocalDate fechaApertura;

    public Estacion(int id, String codigoInterno, String nombreComercial, String direccion,
                    String telefono, LocalDate fechaApertura) {
        this.id = Validaciones.positivo(id, "id");
        this.codigoInterno = Validaciones.texto(codigoInterno, "codigo_interno");
        this.nombreComercial = Validaciones.texto(nombreComercial, "nombre_comercial");
        this.direccion = Validaciones.texto(direccion, "direccion");
        this.telefono = Validaciones.texto(telefono, "telefono");
        this.fechaApertura = Objects.requireNonNull(fechaApertura, "fecha_apertura es obligatoria");
    }

    public int getId() {
        return id;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public LocalDate getFechaApertura() {
        return fechaApertura;
    }

    public void cambiarNombreComercial(String nombreComercial) {
        this.nombreComercial = Validaciones.texto(nombreComercial, "nombre_comercial");
    }

    public void cambiarDireccion(String direccion) {
        this.direccion = Validaciones.texto(direccion, "direccion");
    }

    public void cambiarTelefono(String telefono) {
        this.telefono = Validaciones.texto(telefono, "telefono");
    }

    @Override
    public String toString() {
        return codigoInterno + " - " + nombreComercial;
    }
}
