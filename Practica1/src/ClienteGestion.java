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


    @Override
    public void guardar(Cliente cliente){
        clientesList.add(cliente);
        String textoCSV = cliente.getID() + ";" +
                cliente.getNombre() + ";" +
                cliente.getTelefono() + ";" +
                cliente.getFecha() + ";" +
                cliente.getMatricula();

        manejador.escribir(rutaArchivo, textoCSV);
    }

    @Override
    public List<Cliente> cargarTodos() {
        List<String> texto = manejador.leer(rutaArchivo);
        //falta saber como leerlo y añadirlo a la lista de clienteList asi luego podremos buscar por ID
        return clientesList;

    }

    @Override
    public Cliente leerporID(byte id){
        for (Cliente c : clientesList){
            if (c.getID() == id){
                return c;
            }else{
                System.out.println("Cliente con ID: "+id+" no encontrado");
            }
        }
        return null;
    }



}
