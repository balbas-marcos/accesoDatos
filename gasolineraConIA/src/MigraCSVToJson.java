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
        this.manejarCSVCliente = new ManejarCSVCliente("src/archivosCSV/clientes.csv");
        this.manejarJsonCliente = new ManejarJsonCliente("src/archivosJSON/clientes.json");
        this.manejarCSVPago = new ManejarCSVPago("src/archivosCSV/pagos.csv");
        this.manejarJsonPago = new ManejarJsonPago("src/archivosJSON/pagos.json");
    }

    public void migrarClientes(String rutaCSV, String rutaJson) {
        Scanner sc = new Scanner(System.in);
        List<Cliente> clientes_CSV = manejarCSVCliente.cargarTodos();
        List<Cliente> clientes_agregar = new ArrayList<>();

        if (!Files.exists(Path.of(rutaJson))) {
            System.out.println("¿Quieres migrar el fichero Clietes.csv?(Si, No):");
            String respuesta = sc.next().toLowerCase(Locale.ROOT).trim();
            if (respuesta.equalsIgnoreCase("Si")) {
                for (Cliente c1 : clientes_CSV) {
                    manejarJsonCliente.guardar(c1);
                }
                System.out.println("Migración con éxito");
            } else {
                System.out.println("migracion cancelada");
            }

        } else {
            System.out.println("No hay clientes en el archivo CSV para migrar");
        }
    }


    public void migrarPagos(String rutaCSV, String rutaJson) {
        Scanner sc = new Scanner(System.in);

        List<Pago> pagos_CSV = manejarCSVPago.cargarTodos();
        List<Pago> pagos_Json = manejarJsonPago.cargarTodos();
        List<Pago> pagos_agregar = new ArrayList<>();

        if (!Files.exists(Path.of(rutaJson))) {
            System.out.println("¿Quieres migrar el fichero Pagos.csv?(Si, No):");
            String respuesta = sc.next().toLowerCase(Locale.ROOT).trim();
            if (respuesta.equalsIgnoreCase("Si")) {
                for (Pago p1 : pagos_CSV) {
                    manejarJsonPago.guardar(p1);
                }
                System.out.println("Migración con éxito");

            } else {
                System.out.println("migracion cancelada");
            }

        } else {
            System.out.println("No hay pagos en el archivo CSV para migrar");
        }
    }
}
