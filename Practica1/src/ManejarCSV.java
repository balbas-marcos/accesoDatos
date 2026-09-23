import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManejarCSV implements ILeerEscribir {

    public ManejarCSV() {
    }


    public int ultimoID(String archivo) {
        List<String> lineas = this.leer(archivo);

        if (!lineas.isEmpty()) {
            String ultimaLinea = lineas.get(lineas.size() - 1);
            String[] dato_id = ultimaLinea.split(";");
            return Integer.parseInt(dato_id[0]);
        }
        return 0;
    }


    @Override
    public void escribir(String ruta, List<Cliente> clientes) {
        Path archivo = Path.of(ruta);
        try (BufferedWriter out = Files.newBufferedWriter(archivo)) {
           for(Cliente c : clientes) {
               out.write(clienteToCSV(c));
           }
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    @Override
    public List<String> leer(String ruta) {
        List<String> leido = new ArrayList<>();
        Path archivo = Path.of(ruta);
        try (BufferedReader in = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)){
            String linea;
            while((linea = in.readLine()) != null) {
                leido.add(linea);
            }
            return leido;
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return null;
    }

    public String clienteToCSV(Cliente c) {

        return c.getID() + "," +
                c.getNombre() + "," +
                c.getTelefono() + "," +
                c.getFecha() + "," +
                c.getMatricula() + ";";
    }
}
