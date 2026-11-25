package com.gasolinera.model;

/**
 * Condiciones propias de cada función dentro de un periodo de asignación.
 * La jerarquía es cerrada: añadir una función nueva implica un nuevo record permitido,
 * sin modificar AsignacionFuncion ni las asignaciones históricas.
 */
public sealed interface DetalleFuncion
        permits DetalleCajero, DetalleExpendedor, DetalleResponsableTienda, DetalleJefeEstacion {
}
