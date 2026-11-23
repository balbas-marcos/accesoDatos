import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManejarJsonCliente implements ILeerEscribir<Cliente> {
    String ruta;

    public ManejarJsonCliente(String ruta) {
        this.ruta = ruta;
    }

    @Override
    public void guardar(Cliente cliente) {
        List<Cliente> clientes = new ArrayList<>();
        Path directorio = Path.of(ruta);
        if (Files.notExists(directorio)) {
            try {
                Files.createDirectories(directorio);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Path archivo = directorio.resolve("clientes.json");
        if (Files.notExists(archivo)) {
            try {
                Files.createFile(archivo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                if (Files.size(archivo) > 0) {
                    clientes.addAll(cargarTodos());
                }
            } catch (IOException e) {
                System.out.println("Error al comprobar tamaño de archivo: " + e.getMessage());
            }
        }
        clientes.add(cliente);


        try (BufferedWriter out = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            int contador = 0;
            out.write("{");
            out.newLine();
            out.write("\t\"clientes\":");
            out.newLine();
            out.write("\t[");
            out.newLine();
            for (int i = 0; i < clientes.size(); i++) {
                out.write("\t\t" + toJson(clientes.get(i)));
                if (i < clientes.size() - 1) {
                    out.write(",");
                }
                out.newLine();
            }

            out.newLine();
            out.write("\t]");
            out.newLine();
            out.write("}");
            out.newLine();
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    @Override
    public List<Cliente> cargarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        Path directorio = Path.of(ruta);
        Path archivo = directorio.resolve("clientes.json");
        try (BufferedReader in = Files.newBufferedReader(archivo)) {
            String linea;
            in.readLine();
            in.readLine();
            in.readLine();
            while ((linea = in.readLine()) != null && !linea.trim().equals("]") && !linea.trim().equals("}")) {
                if (!linea.isBlank()) {
                    String[] datos = linea.split(",");

                    String[] datos_dividos = new String[4];

                    for (int i = 0; i < datos.length; i++) {
                        String[] datos_semidividios = datos[i].split(": ");
                        datos_dividos[i] = (datos_semidividios[1].replace("\"", "").replace("}", ""));


                    }

                    byte id = Byte.parseByte(datos_dividos[0].trim());
                    String nombre = datos_dividos[1].trim();
                    String telefono = datos_dividos[2].trim();
                    String matricula = datos_dividos[3].trim().replace("}", "");
                    clientes.add(new Cliente(id, nombre, telefono, matricula));

                }
            }
            in.readLine();
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return clientes;
    }

    @Override
    public <T> void ultimoID(T elemento) {

    }

    public String toJson(Cliente cliente) {
        return "{\"id\": " + cliente.getID() + ",\"nombre\": \"" + cliente.getNombre() + "\",\"telefono\": \"" + cliente.getTelefono() + "\", \"matricula\": \"" + cliente.getMatricula() + "\"}";
    }
}
