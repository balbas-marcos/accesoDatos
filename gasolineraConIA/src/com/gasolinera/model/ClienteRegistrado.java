package com.gasolinera.model;

import java.util.Optional;

/** Cliente de tienda/fidelización: requiere al menos un medio de contacto. */
public final class ClienteRegistrado {

    private final int id;
    private final String codigoCliente;
    private String nombre;
    private String telefono;
    private String email;
    private String direccion;
    private String matricula;

    public ClienteRegistrado(int id, String codigoCliente, String nombre, String telefono, String email,
                             String direccion, String matricula) {
        this.id = Validaciones.positivo(id, "id");
        this.codigoCliente = Validaciones.texto(codigoCliente, "codigo_cliente");
        this.nombre = Validaciones.texto(nombre, "nombre");
        validarContacto(telefono, email);
        this.telefono = Validaciones.textoOpcional(telefono, "telefono");
        this.email = Validaciones.textoOpcional(email, "email");
        this.direccion = Validaciones.textoOpcional(direccion, "direccion");
        this.matricula = Validaciones.textoOpcional(matricula, "matricula");
    }

    private static void validarContacto(String telefono, String email) {
        String t = Validaciones.textoOpcional(telefono, "telefono");
        String e = Validaciones.textoOpcional(email, "email");
        if (t == null && e == null) {
            throw new IllegalArgumentException("El cliente debe tener teléfono o email");
        }
    }

    public int getId() {
        return id;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public Optional<String> getTelefono() {
        return Optional.ofNullable(telefono);
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<String> getDireccion() {
        return Optional.ofNullable(direccion);
    }

    public Optional<String> getMatricula() {
        return Optional.ofNullable(matricula);
    }

    public void cambiarNombre(String nombre) {
        this.nombre = Validaciones.texto(nombre, "nombre");
    }

    public void cambiarContacto(String telefono, String email) {
        validarContacto(telefono, email);
        this.telefono = Validaciones.textoOpcional(telefono, "telefono");
        this.email = Validaciones.textoOpcional(email, "email");
    }

    public void cambiarDireccion(String direccion) {
        this.direccion = Validaciones.textoOpcional(direccion, "direccion");
    }

    public void cambiarMatricula(String matricula) {
        this.matricula = Validaciones.textoOpcional(matricula, "matricula");
    }
}
