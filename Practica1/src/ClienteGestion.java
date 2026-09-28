import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClienteGestion implements IGestionDatos<Cliente> {
    List<Cliente> clientesList = new ArrayList<>();
    private String rutaArchivo;
    private ILeerEscribir manejador;

    public ClienteGestion(String rutaArchivo, ILeerEscribir manejador) {
        this.rutaArchivo = rutaArchivo;
        this.manejador = manejador;

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
            clientesList.add(cliente);
            System.out.println(cliente);
            manejador.escribir(rutaArchivo, clientesList);
            System.out.println("La ha mandado a manejarCSV");
        } else {
            System.out.println("ERROR: Esa matricula ya esta registrada");
        }
    }

    @Override
    public List<Cliente> cargarTodos() {
        List<Cliente> lineas = manejador.leer(rutaArchivo);
        lineas.sort(Cliente::compareTo);
        return lineas;

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


    public List<Cliente> buscar(String texto_buscado) {
        List<Cliente> lineas = manejador.leer(rutaArchivo);
        List<Cliente> buscados = new ArrayList<>();
        for(Cliente c : lineas){
            if(c.toString().toLowerCase(Locale.ROOT).contains(texto_buscado.toLowerCase(Locale.ROOT))){
                buscados.add(c);
            }
        }

        buscados.sort(Cliente::compareTo);
        return buscados;

    }


}
