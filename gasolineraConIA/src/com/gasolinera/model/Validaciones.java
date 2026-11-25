package com.gasolinera.model;

/** Ayudas de validación de uso interno del dominio. Sin E/S, sin SQL. */
final class Validaciones {

    private Validaciones() {
    }

    static String texto(String valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio");
        }
        String recortado = valor.trim();
        if (recortado.isEmpty()) {
            throw new IllegalArgumentException("El campo " + campo + " no puede estar vacío");
        }
        return recortado;
    }

    static String textoOpcional(String valor, String campo) {
        return valor == null ? null : texto(valor, campo);
    }

    static int positivo(int valor, String campo) {
        if (valor <= 0) {
            throw new IllegalArgumentException("El campo " + campo + " debe ser positivo");
        }
        return valor;
    }

    static int noNegativo(int valor, String campo) {
        if (valor < 0) {
            throw new IllegalArgumentException("El campo " + campo + " no puede ser negativo");
        }
        return valor;
    }
}
