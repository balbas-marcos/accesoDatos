package com.gasolinera.model;

import java.time.LocalDate;
import java.util.Objects;

public record DetalleExpendedor(String numeroAcreditacion, LocalDate fechaCaducidad) implements DetalleFuncion {

    public DetalleExpendedor {
        numeroAcreditacion = Validaciones.texto(numeroAcreditacion, "numero_acreditacion");
        Objects.requireNonNull(fechaCaducidad, "fecha_caducidad es obligatoria");
    }
}
