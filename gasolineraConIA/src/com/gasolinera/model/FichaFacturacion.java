package com.gasolinera.model;

/** Ficha de facturación 1:1 con una estación (serie y numeración de tickets). */
public final class FichaFacturacion {

    private final int id;
    private final int estacionId;
    private final String codigoConfiguracion;
    private final String serieTickets;
    private int siguienteNumero;
    private String textoPie;

    public FichaFacturacion(int id, int estacionId, String codigoConfiguracion, String serieTickets,
                            int siguienteNumero, String textoPie) {
        this.id = Validaciones.positivo(id, "id");
        this.estacionId = Validaciones.positivo(estacionId, "estacion_id");
        this.codigoConfiguracion = Validaciones.texto(codigoConfiguracion, "codigo_configuracion");
        this.serieTickets = Validaciones.texto(serieTickets, "serie_tickets");
        this.siguienteNumero = Validaciones.positivo(siguienteNumero, "siguiente_numero");
        this.textoPie = Validaciones.texto(textoPie, "texto_pie");
    }

    public int getId() {
        return id;
    }

    public int getEstacionId() {
        return estacionId;
    }

    public String getCodigoConfiguracion() {
        return codigoConfiguracion;
    }

    public String getSerieTickets() {
        return serieTickets;
    }

    public int getSiguienteNumero() {
        return siguienteNumero;
    }

    public String getTextoPie() {
        return textoPie;
    }

    /** Reserva y devuelve la próxima referencia de ticket. */
    public String reservarTicket() {
        return serieTickets + "-" + siguienteNumero++;
    }

    public void cambiarTextoPie(String textoPie) {
        this.textoPie = Validaciones.texto(textoPie, "texto_pie");
    }
}
