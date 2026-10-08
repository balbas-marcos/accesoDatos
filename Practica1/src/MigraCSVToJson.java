import java.util.ArrayList;
import java.util.List;

public class MigraCSVToJson {

    private ManejarCSVCliente manejarCSVCliente;
    private ManejarJsonCliente manejarJsonCliente;
    private ManejarCSVPago manejarCSVPago;
    private ManejarJsonPago manejarJsonPago;


    public MigraCSVToJson() {
        this.manejarCSVCliente = new ManejarCSVCliente();
        this.manejarJsonCliente = new ManejarJsonCliente();
        this.manejarCSVPago = new ManejarCSVPago();
        this.manejarJsonPago = new ManejarJsonPago();
    }

    public void migrarClientes(String rutaCSV, String rutaJson) {
        List<Cliente> clientes_CSV = manejarCSVCliente.leer(rutaCSV);
        List<Cliente> clientes_Json = manejarJsonCliente.leer(rutaJson);
        List<Cliente> clientes_agregar = new ArrayList<>();
        for (Cliente c1 : clientes_CSV) {
            boolean existe = false;
            for (Cliente c2 : clientes_Json) {
                if (c1.getID() == c2.getID()) {
                    existe = true;
                    break;
                }
            }
            if(!existe){
                clientes_agregar.add(c1);
            }
        }

        if (clientes_agregar != null && !clientes_agregar.isEmpty()) {
            manejarJsonCliente.escribir(rutaJson, clientes_agregar);
        } else {
            System.out.println("No hay clientes en el archivo CSV para migrar");
        }
    }



    public void migrarPagos(String rutaCSV, String rutaJson) {
        List<Pago> pagos_CSV = manejarCSVPago.leer(rutaCSV);
        List<Pago> pagos_Json = manejarJsonPago.leer(rutaJson);
        List<Pago> pagos_agregar = new ArrayList<>();
        for (Pago p1 : pagos_CSV) {
            boolean existe = false;
            for (Pago p2 : pagos_Json) {
                if (p1.getID() == p2.getID()) {
                    existe = true;
                    break;
                }
            }
            if(!existe){
                pagos_agregar.add(p1);
            }
        }

        if (pagos_agregar != null && !pagos_agregar.isEmpty()) {
            manejarJsonPago.escribir(rutaJson, pagos_agregar);
        } else {
            System.out.println("No hay pagos en el archivo CSV para migrar");
        }
    }
}
