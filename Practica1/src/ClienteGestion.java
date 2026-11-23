import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClienteGestion implements ILeerEscribir<Cliente>{

    ILeerEscribir manejador;

    public ClienteGestion(ILeerEscribir manejador){
        this.manejador = manejador;
    }

    @Override
    public void guardar(Cliente cliente) {
        manejador.guardar(cliente);
    }

    @Override
    public List<Cliente> cargarTodos() {
        return manejador.cargarTodos();
    }

    @Override
    public <T> void ultimoID(T elemento) {

    }

    public byte ultimoID() {
        int id_max = 0;
        List<Cliente> clientes = manejador.cargarTodos();
        for(Cliente c :clientes){
            if(c.getID() >= id_max){
                id_max = c.getID();
            }
        }
        return (byte) id_max;
    }


    public List<Cliente> buscar(String texto_buscado) {
        List<Cliente> lineas = this.cargarTodos();
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
