package com.gasolinera.model;

import java.util.Optional;

/** Proveedor con identificación fiscal y datos de contacto opcionales. */
public final class Proveedor {

    private final int id;
    private final String codigoInterno;
    private final String identificacionFiscal;
    private String razonSocial;
    private String telefono;
    private String email;
    private String direccion;

    public Proveedor(int id, String codigoInterno, String identificacionFiscal, String razonSocial,
                     String telefono, String email, String direccion) {
        this.id = Validaciones.positivo(id, "id");
        this.codigoInterno = Validaciones.texto(codigoInterno, "codigo_interno");
        this.identificacionFiscal = Validaciones.texto(identificacionFiscal, "identificacion_fiscal");
        this.razonSocial = Validaciones.texto(razonSocial, "razon_social");
        this.telefono = Validaciones.texto(telefono, "telefono");
        this.email = Validaciones.textoOpcional(email, "email");
        this.direccion = Validaciones.textoOpcional(direccion, "direccion");
    }

    public int getId() {
        return id;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public String getIdentificacionFiscal() {
        return identificacionFiscal;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public String getTelefono() {
        return telefono;
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<String> getDireccion() {
        return Optional.ofNullable(direccion);
    }

    public void cambiarRazonSocial(String razonSocial) {
        this.razonSocial = Validaciones.texto(razonSocial, "razon_social");
    }

    public void cambiarContacto(String telefono, String email, String direccion) {
        this.telefono = Validaciones.texto(telefono, "telefono");
        this.email = Validaciones.textoOpcional(email, "email");
        this.direccion = Validaciones.textoOpcional(direccion, "direccion");
    }
}
