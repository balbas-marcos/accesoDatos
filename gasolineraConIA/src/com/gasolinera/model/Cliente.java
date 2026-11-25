package com.gasolinera.model;

import java.time.LocalDate;
import java.util.Locale;


public class Cliente implements Comparable<Cliente> {
    byte ID = 0;
    String nombre;
    String telefono;
    String matricula;


    public Cliente(byte ID, String nombre, String telefono, String matricula) {
        this.ID = ID;
        this.nombre = nombre.trim();
        this.telefono = telefono.trim();
        this.matricula = matricula;
    }


    public int getID() {
        return ID;
    }

    public void setID(byte ID) {
        this.ID = ID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }



    public int compareID(Cliente otro) {
        return Integer.compare(otro.ID, this.ID);
    }


    @Override
    public int compareTo(Cliente otro) {
        int resultado = this.nombre.toLowerCase(Locale.ROOT).compareTo(otro.nombre.toLowerCase(Locale.ROOT));
        if (resultado == 0) {
            return Integer.compare(this.ID, otro.ID);
        }
        return resultado;
    }

    @Override
    public String toString() {
        return String.format("%-8d\t%-15s\t%-12s\t%-13s\t%-15s",
                ID, nombre, telefono, matricula);
    }

}

