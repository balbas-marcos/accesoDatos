import java.time.LocalDate;

public class Pago {
    int ID;
    int ID_cliente;
    LocalDate fecha;
    double importe;
    double litros;
    String combustible;

    public Pago(int ID_cliente, LocalDate fecha, double importe, double litros, String combustible) {
        if (combustible == null || combustible.isEmpty()) {
            System.out.println("Texto obligatorio, por ejemplo,\n" +
                    "Gasolina 95\n" +
                    "o\n" +
                    "Diésel");
            throw new IllegalArgumentException("ERROR al crear el pago, no combustible asignado");
        } else {
            this.combustible = combustible;
        }
        ID = ID + 1;
        this.ID_cliente = ID_cliente;

        if(fecha == null){
            this.fecha = LocalDate.now();
        }else{
            this.fecha = fecha;
        }
        this.fecha = fecha;
        this.importe = importe;
        this.litros = litros;

    }
}
