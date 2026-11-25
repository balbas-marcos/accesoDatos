package com.gasolinera.model;

/** Catálogo de funciones: permite altas futuras sin cambiar el código ni el histórico. */
public final class TipoFuncion {

    private final int id;
    private final String codigo;
    private String nombre;
    private String descripcion;
    private boolean activo;

    public TipoFuncion(int id, String codigo, String nombre, String descripcion, boolean activo) {
        this.id = Validaciones.positivo(id, "id");
        this.codigo = Validaciones.texto(codigo, "codigo");
        this.nombre = Validaciones.texto(nombre, "nombre");
        this.descripcion = Validaciones.texto(descripcion, "descripcion");
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }
}
