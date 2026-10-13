import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

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
        Scanner sc = new Scanner(System.in);
        List<Cliente> clientes_CSV = manejarCSVCliente.leer(rutaCSV);
        List<Cliente> clientes_agregar = new ArrayList<>();

        if (!Files.exists(Path.of(rutaJson))) {
            System.out.println("¿Quieres migrar el fichero Clietes.csv?(Si, No):");
            String respuesta = sc.next().toLowerCase(Locale.ROOT).trim();
            if (respuesta.equalsIgnoreCase("Si")) {
                for (Cliente c1 : clientes_CSV) {
                    clientes_agregar.add(c1);
                }
                manejarJsonCliente.escribir(rutaJson, clientes_agregar);
                System.out.println("Migración con éxito");
            }else{
                System.out.println("migracion cancelada");
            }

        } else {
            System.out.println("No hay clientes en el archivo CSV para migrar");
        }
    }


    public void migrarPagos(String rutaCSV, String rutaJson) {
        Scanner sc = new Scanner(System.in);

        List<Pago> pagos_CSV = manejarCSVPago.leer(rutaCSV);
        List<Pago> pagos_Json = manejarJsonPago.leer(rutaJson);
        List<Pago> pagos_agregar = new ArrayList<>();

        if (!Files.exists(Path.of(rutaJson))) {
            System.out.println("¿Quieres migrar el fichero Pagos.csv?(Si, No):");
            String respuesta = sc.next().toLowerCase(Locale.ROOT).trim();
            if (respuesta.equalsIgnoreCase("Si")) {
                for (Pago p1 : pagos_CSV) {
                    pagos_agregar.add(p1);
                }
                manejarJsonPago.escribir(rutaJson, pagos_agregar);
                System.out.println("Migración con éxito");

            } else {
                System.out.println("migracion cancelada");
            }

        }else {
            System.out.println("No hay pagos en el archivo CSV para migrar");
        }
    }
}
