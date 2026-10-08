import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Array;
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
        Cliente ultimoCliente = clientesActualizados.get(0);
        return ultimoCliente.getID();
    }


    @Override
    public void escribir(String ruta, List<Cliente> clientes) {
        Path archivo = Path.of(ruta);
        clientes.sort(Cliente :: compareID);
        clientes.addAll(leer(ruta));
        try (BufferedWriter out = Files.newBufferedWriter(archivo, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            out.write("{");
            out.newLine();
            out.write("\t\"clientes\":");
            out.newLine();
            out.write("\t[");
            out.newLine();

            int contador = 0;
            for (Cliente c : clientes) {
                out.write(toString_personalizado(c));
                contador++;

                if (contador < clientes.size()) {
                    out.write(",");
                }
                out.newLine();

            }
            out.newLine();
            out.write("\t]");
            out.newLine();
            out.write("}");
            clientes.clear();

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
            in.readLine();
            in.readLine();
            in.readLine();
            while ((linea = in.readLine()) != null && !linea.trim().equals("]") && !linea.trim().equals("}")) {
                if (!linea.isBlank()) {
                    //por si alguna linea en blanco saltarla
                    String[] datos_enteros = linea.split(",");
                    String[] datos_separados = new String[datos_enteros.length];
                    List<String> datosList = new ArrayList<>();
                    for (int i = 0; i < datos_enteros.length; i++) {
                        datos_separados = datos_enteros[i].split(":");
                        datos_separados = datos_separados[1].split(", \"");
                        datosList.add(datos_separados[0]);
                        //System.out.println(datos_semi_separados[1]);
                    }
                    String[] datos_final = datosList.toArray(new String[5]);

                    String id = datos_final[0].trim();
                    String nombre = datos_final[1].replace("\"","").trim();
                    String telefono = datos_final[2].replace("\"","").trim();
                    LocalDate fecha = LocalDate.parse(datos_final[3].replace("\"","").trim());
                    String matricula = datos_final[4].replace("\"", "").replace("}", "").replace(",", "").trim();
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
