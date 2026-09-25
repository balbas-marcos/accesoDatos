import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class PagoGestion implements IGestionDatos<Pago> {
    List<Pago> pagolist = new ArrayList<>();
    private String rutaArchivo;
    private ILeerEscribir manejador;

    public PagoGestion(String rutaArchivo, ILeerEscribir manejador) {
        this.rutaArchivo = rutaArchivo;
        this.manejador = manejador;
    }


    @Override
    public void guardar(Pago pago) {

        pagolist.add(pago);
        manejador.escribir(rutaArchivo, pagolist);

    }

    @Override
    public List<Pago> cargarTodos() {
        List<Pago> texto = manejador.leer(rutaArchivo);
        texto.sort(Pago::compareTo);
        return texto.reversed();
    }

    @Override
    public Pago leerporID(byte id) {
        for (Pago p : pagolist) {
            if (p.getID() == id) {
                return p;
            } else {
                System.out.println("Cliente con ID: " + id + " no encontrado");
            }
        }
        return null;
    }
}
