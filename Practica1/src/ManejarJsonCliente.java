import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManejarJsonCliente implements ILeerEscribir<Cliente> {
    String separador = ",";

    public ManejarJsonCliente() {
    }

    @Override
    public int ultimoID(String ruta) {
        List<Cliente> clientesActualizados = leer(ruta);
        if (clientesActualizados == null || clientesActualizados.isEmpty() || Files.notExists(Path.of(ruta))) {
            return 0;
        }
        Cliente ultimoCliente = clientesActualizados.get(clientesActualizados.size() - 1);
        return ultimoCliente.getID();
    }


    @Override
    public void escribir(String ruta, List<Cliente> clientes) {
        Path archivo = Path.of(ruta);
        try (BufferedWriter out = Files.newBufferedWriter(archivo, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
            for (Cliente c : clientes) {
                out.write(toString_personalizado(c));
                out.newLine();
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
                if (!linea.isBlank()) {
                    //por si alguna linea en blanco saltarla
                    String[] datos_enteros = linea.split(",");
                    String[] datos_semi_separados = new String[datos_enteros.length];
                    String[] datos = new String[datos_enteros.length];
                    //String[] datosFinal = new String[datos_enteros.length];
                    for (int i = 0; i < datos_enteros.length; i++) {
                        datos_semi_separados = datos_enteros[i].split(":");
                        datos = datos_semi_separados[1].split(", \"");

                        //System.out.println(datos_semi_separados[1]);
                        System.out.println(Arrays.toString(datos));
                    }

                    String id = datos[0];
                    String nombre = datos[1];
                    String telefono = datos[2];
                    //LocalDate fecha = LocalDate.parse(datos[3]);
                    LocalDate fecha = LocalDate.parse("2007-08-14");
                    String matricula = datos[3];
                    leido.add(new Cliente(Integer.parseInt(id), nombre, telefono, fecha, matricula));
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return leido;
    }

/*
    @Override
    public String toString_personalizado(Cliente c) {

        return c.getID() + separador +
                c.getNombre() + separador +
                c.getTelefono() + separador +
                c.getFecha() + separador +
                c.getMatricula();
    }

 */


    @Override
    public String toString_personalizado(Cliente c) {
        return "\t\t{\"id\": " + c.getID() + ",\"nombre\": \"" + c.getNombre() + "\",\"telefono\": \"" + c.getTelefono() + "\",\"fecha\": \"" + c.getFecha() + "\", \"matricula\": \"" + c.getMatricula() + "\"}";

    }
}
