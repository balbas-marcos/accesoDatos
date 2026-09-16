import java.util.ArrayList;
import java.util.List;

public class ClienteGestion implements IGestionDatos<Cliente> {
    List<Cliente> clientesList = new ArrayList<>();

    @Override
    public void guardar(Cliente cliente){
        clientesList.add(cliente);
    }

    @Override
    public List<Cliente> cargarTodos() {
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
