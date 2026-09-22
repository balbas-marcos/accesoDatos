import java.util.ArrayList;
import java.util.List;

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
        String textoCSV = pago.getID_cliente() + ";" +
                pago.getFecha() + ";" +
                pago.getImporte() + ";" +
                pago.getLitros() + ";" +
                pago.getCombustible();

        manejador.escribir(rutaArchivo, textoCSV);
    }

    @Override
    public List<Pago> cargarTodos() {
        List<String> texto = manejador.leer(rutaArchivo);
        //falta saber como leerlo y añadirlo a la lista de pagoList asi luego podremos buscar por ID
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
