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

public class ManejarJsonPago implements ILeerEscribir<Pago> {
    public ManejarJsonPago() {
    }

    @Override
    public int ultimoID(String ruta) {
        List<Pago> clientesActualizados = leer(ruta);

        if (clientesActualizados == null || clientesActualizados.isEmpty() || Files.notExists(Path.of(ruta))) {
            return 0;
        }
        Pago ultimoPago = clientesActualizados.get(clientesActualizados.size() - 1);
        return ultimoPago.getID();
    }


    @Override
    public void escribir(String ruta, List<Pago> pagos) {
        Path archivo = Path.of(ruta);


        try (BufferedWriter out = Files.newBufferedWriter(archivo, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
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
                    leido.add(new Pago(Integer.parseInt(datos_final[0].replace("\"", "").trim()), Integer.parseInt(datos_final[1].replace("\"","").trim()), LocalDate.parse(datos_final[2].replace("\"","").trim()), Double.parseDouble(datos_final[3].replace("\"","").trim()), Double.parseDouble(datos_final[4].replace("\"","").trim()), datos_final[5].replace("\"","").replace("}","").trim()));
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return leido;
    }


    @Override
    public String toString_personalizado(Pago p) {
        return "\t\t{\"id\": " + p.getID() + ",\"nombre\": \"" + p.getID_cliente() + "\",\"fecha\": \"" + p.getFecha() + "\",\"importe\": \"" + p.getImporte() + "\", \"litros\": \"" + p.getLitros() + "\", \"combustible\": \"" + p.getCombustible() + "\"}";
    }
}
