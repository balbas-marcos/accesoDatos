import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManejarCSVCliente implements ILeerEscribir<Cliente> {

    public ManejarCSVCliente() {
    }


    public int ultimoID(String archivo) {
        List<Cliente> clientes = this.leer(archivo);

        if (clientes != null && !clientes.isEmpty()) {
            Cliente ultimoCliente = clientes.get(clientes.size() - 1);
            return ultimoCliente.getID();
        }
        return 0;
    }


    @Override
    public void escribir(String ruta, List<Cliente> clientes) {
        Path archivo = Path.of(ruta);
        try (BufferedWriter out = Files.newBufferedWriter(archivo)) {
            for (Cliente c : clientes) {
                out.write(clienteToCSV(c));
            }
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    @Override
    public List<Cliente> leer(String ruta) {
        List<Cliente> leido = new ArrayList<>();
        Path archivo = Path.of(ruta);
        try (BufferedReader in = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = in.readLine()) != null) {
                for (Cliente c : leido) {
                    if (!linea.isBlank()) {
                        //por si alguna linea en blanco saltarla
                        String[] datos = linea.split(",");
                        String id = datos[0];
                        String nombre = datos[1];
                        String telefono = datos[2];
                        LocalDate fecha = LocalDate.parse(datos[3]);
                        String matricula = datos[4];
                        leido.add(new Cliente(Integer.parseInt(id), nombre, telefono, fecha, matricula));
                    }
                }
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
