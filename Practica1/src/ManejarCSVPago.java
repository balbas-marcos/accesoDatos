import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManejarCSVPago implements ILeerEscribir<Pago> {

    public ManejarCSVPago() {
    }

    @Override
    public int ultimoID(List<Pago> pagos) {
        if (pagos == null || pagos.isEmpty()) {
            return 0;
        }
        Pago ultimoPago = pagos.get(pagos.size() - 1);
        return ultimoPago.getID();
    }


    @Override
    public void escribir(String ruta, List<Pago> pagos) {
        Path archivo = Path.of(ruta);
        try (BufferedWriter out = Files.newBufferedWriter(archivo)) {
            for (Pago p : pagos) {
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
        try (BufferedReader in = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = in.readLine()) != null) {
                for (Pago p : leido) {
                    String[] datos = linea.split(",");
                    leido.add(new Pago(Integer.parseInt(datos[0]), Integer.parseInt(datos[1]), LocalDate.parse(datos[2]), Double.parseDouble(datos[3]), Double.parseDouble(datos[4]), datos[5]));
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
                p.getLitros();
    }

}
