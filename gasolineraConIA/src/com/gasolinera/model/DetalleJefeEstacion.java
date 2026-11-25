package com.gasolinera.model;

public record DetalleJefeEstacion(int nivelAutorizacion) implements DetalleFuncion {

    public DetalleJefeEstacion {
        if (nivelAutorizacion < 1) {
            throw new IllegalArgumentException("El nivel de autorización debe ser al menos 1");
        }
    }
}
