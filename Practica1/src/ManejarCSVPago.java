import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.*;

public class ManejarCSVPago implements ILeerEscribir<Pago> {
    String separador = ",";
    public ManejarCSVPago() {
    }

    @Override
    public int ultimoID(String ruta) {
        List<Pago> clientesActualizados = leer(ruta);

        if (clientesActualizados == null || clientesActualizados.isEmpty()|| Files.notExists(Path.of(ruta))) {
            return 0;
        }
        Pago ultimoPago = clientesActualizados.get(clientesActualizados.size() - 1);
        return ultimoPago.getID();
    }


    @Override
    public void escribir(String ruta, List<Pago> pagos) {
        Path archivo = Path.of(ruta);


        try (BufferedWriter out = Files.newBufferedWriter(archivo, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
            for (Pago p : pagos) {
                out.write(toString_personalizado(p));
                out.newLine();
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
                if (!linea.isBlank()) {
                    String[] datos = linea.split(separador);
                    leido.add(new Pago(Integer.parseInt(datos[0]), Integer.parseInt(datos[1]), LocalDate.parse(datos[2]), Double.parseDouble(datos[3]), Double.parseDouble(datos[4]), datos[5]));
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return leido;
    }


    @Override
    public String toString_personalizado(Pago p) {

        return p.getID() + separador +
                p.getID_cliente() + separador +
                p.getFecha() + separador +
                p.getImporte() + separador +
                p.getLitros() + separador +
                p.getCombustible();
    }

}
