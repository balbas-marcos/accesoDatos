import java.time.LocalDate;

public class Pago implements Comparable<Pago> {
    int ID;
    int ID_cliente;
    LocalDate fecha;
    double importe;
    double litros;
    String combustible;



    public Pago( int ID, int ID_cliente, LocalDate fecha, double importe, double litros, String combustible) {
        if (combustible == null || combustible.isEmpty()) {
            System.out.println("Texto obligatorio, por ejemplo,\n" +
                    "Gasolina 95\n" +
                    "o\n" +
                    "Diésel");
            throw new IllegalArgumentException("ERROR al crear el pago, no combustible asignado");
        } else {
            this.combustible = combustible;
        }


        this.ID = ID;
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


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID_cliente() {
        return ID_cliente;
    }

    public void setID_cliente(int ID_cliente) {
        this.ID_cliente = ID_cliente;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public double getLitros() {
        return litros;
    }

    public void setLitros(double litros) {
        this.litros = litros;
    }

    public String getCombustible() {
        return combustible;
    }

    public void setCombustible(String combustible) {
        this.combustible = combustible;
    }


    @Override
    public int compareTo(Pago otro) {
        int resultado = this.fecha.compareTo(otro.fecha);
        if (resultado == 0) {
            return this.ID;
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "Pago{" +
                "ID=" + ID +
                ", ID_cliente=" + ID_cliente +
                ", fecha=" + fecha +
                ", importe=" + importe +
                ", litros=" + litros +
                ", combustible='" + combustible + '\'' +
                '}';
    }
}
