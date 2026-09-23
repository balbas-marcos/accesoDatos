import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClienteGestion implements IGestionDatos<Cliente> {
    List<Cliente> clientesList = new ArrayList<>();
    private String rutaArchivo;
    private ILeerEscribir manejador;

    public ClienteGestion(String rutaArchivo, ILeerEscribir manejador) {
        this.rutaArchivo = rutaArchivo;
        this.manejador = manejador;

    }

    public int ultimoID() {
        List<String> lineas = manejador.leer(rutaArchivo);

        if (!lineas.isEmpty()) {
            String ultimaLinea = lineas.get(lineas.size() - 1);
            String[] dato_id = ultimaLinea.split(";");
            return Integer.parseInt(dato_id[0]);
        } else {
            return 0;
        }
    }

    @Override
    public void guardar(Cliente cliente) {
        boolean existe = false;
        for (Cliente c : clientesList) {
            if (c.getMatricula().equalsIgnoreCase(cliente.getMatricula())) {
                existe = true;
                break;
            }
        }
        if (!existe) {
            manejador.escribir(rutaArchivo, clientesList);
        } else {
            System.out.println("ERROR: Esa matricula ya esta registrada");
        }
    }

    @Override
    public List<Cliente> cargarTodos() {
        List<String> lineas = manejador.leer(rutaArchivo);
        for (String linea : lineas) {
            if (!linea.isBlank()) {
                //por si alguna linea en blanco saltarla
                String[] datos = linea.split(";");
                String id = datos[0];
                String nombre = datos[1];
                String telefono = datos[2];
                LocalDate fecha = LocalDate.parse(datos[3]);
                String matricula = datos[4];
                clientesList.add(new Cliente(Integer.parseInt(id), nombre, telefono, fecha, matricula));
            }
        }
        return clientesList;

    }

    @Override
    public Cliente leerporID(byte id) {
        for (Cliente c : clientesList) {
            if (c.getID() == id) {
                return c;
            } else {
                System.out.println("Cliente con ID: " + id + " no encontrado");
            }
        }
        return null;
    }


}
