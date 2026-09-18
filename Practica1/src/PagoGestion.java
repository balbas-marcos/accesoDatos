import java.util.ArrayList;
import java.util.List;

public class PagoGestion implements IGestionDatos<Pago> {
    List<Pago> pagolist = new ArrayList<>();

    public PagoGestion() {

    }

    @Override
    public void guardar(Pago pago) {
        pagolist.add(pago);
    }

    @Override
    public List<Pago> cargarTodos() {
        return pagolist;
    }

    @Override
    public Pago leerporID(byte id) {
        for (Pago p : pagolist){
            if (p.getID() == id){
                return p;
            }else{
                System.out.println("Cliente con ID: "+id+" no encontrado");
            }
        }
        return null;
    }
}
