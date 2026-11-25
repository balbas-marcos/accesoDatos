package com.gasolinera.model;

import java.util.Objects;

/** Surtidor de una estación: código global, número visible por estación y estado de servicio. */
public final class Surtidor {

    private final int id;
    private final int estacionId;
    private final String codigoEquipo;
    private final int numeroVisible;
    private final String tipoCombustible;
    private EstadoServicio estadoServicio;

    public Surtidor(int id, int estacionId, String codigoEquipo, int numeroVisible,
                    String tipoCombustible, EstadoServicio estadoServicio) {
        this.id = Validaciones.positivo(id, "id");
        this.estacionId = Validaciones.positivo(estacionId, "estacion_id");
        this.codigoEquipo = Validaciones.texto(codigoEquipo, "codigo_equipo");
        this.numeroVisible = Validaciones.positivo(numeroVisible, "numero_visible");
        this.tipoCombustible = Validaciones.texto(tipoCombustible, "tipo_combustible");
        this.estadoServicio = Objects.requireNonNull(estadoServicio, "estado_servicio es obligatorio");
    }

    public int getId() {
        return id;
    }

    public int getEstacionId() {
        return estacionId;
    }

    public String getCodigoEquipo() {
        return codigoEquipo;
    }

    public int getNumeroVisible() {
        return numeroVisible;
    }

    public String getTipoCombustible() {
        return tipoCombustible;
    }

    public EstadoServicio getEstadoServicio() {
        return estadoServicio;
    }

    public void cambiarEstado(EstadoServicio estadoServicio) {
        this.estadoServicio = Objects.requireNonNull(estadoServicio, "estado_servicio es obligatorio");
    }
}
