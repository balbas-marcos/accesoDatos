import java.util.List;

public class PagoGestion implements ILeerEscribir<Pago> {
    ILeerEscribir manejador;

    public PagoGestion(ILeerEscribir manejador) {
        this.manejador = manejador;
    }

    @Override
    public void guardar(Pago pago) {
        manejador.guardar(pago);
    }

    @Override
    public List<Pago> cargarTodos() {
        return manejador.cargarTodos();
    }

    @Override
    public <T> void ultimoID(T elemento) {

    }

    public byte ultimoID() {
        int id_max = 0;
        List<Cliente> clientes = manejador.cargarTodos();
        for (Cliente c : clientes) {
            if (c.getID() >= id_max) {
                id_max = c.getID();
            }
        }
        return (byte) id_max;
    }
}
