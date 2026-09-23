import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ManejarCSVPago implements ILeerEscribir<Pago> {

    public ManejarCSVPago() {
    }


    public int ultimoID(String archivo) {
        List<Pago> lineas = this.leer(archivo);

        if (!lineas.isEmpty()) {
            Pago ultimoPago = lineas.get(lineas.size() - 1);
            return ultimoPago.getID();
        }
        return 0;
    }


    @Override
    public void escribir(String ruta, List<Pago> pagos) {
        Path archivo = Path.of(ruta);
        try (BufferedWriter out = Files.newBufferedWriter(archivo)) {
           for(Pago p : pagos) {
               out.write(pagoToCSV(p));
           }
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    @Override
    public List<Pago> leer(String ruta) {
        List<Pago> leido = new ArrayList<>();
        Path archivo = Path.of(ruta);
        try (BufferedReader in = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)){
            String linea;
            while((linea = in.readLine()) != null) {
                for(Pago p : leido){
                    String[] datos = linea.split(",");
                    leido.add(new Pago(Integer.parseInt(datos[0]), Integer.parseInt(datos[1]),datos[2], datos[3], datos[4]));
                }

            }
            return leido;
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return null;
    }

    public String pagoToCSV(Pago p) {

        return p.getID() + "," +
                p.getID_cliente() + "," +
                p.getFecha() + "," +
                p.getImporte() + "," +
                p.getLitros() + ";";
    }

}
