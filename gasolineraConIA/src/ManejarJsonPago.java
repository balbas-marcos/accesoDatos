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
    String ruta;

    public ManejarJsonPago(String ruta) {
        this.ruta = ruta;
    }

    @Override
    public void guardar(Pago pago) {
        List<Pago> pagos = new ArrayList<>();
        Path directorio = Path.of(ruta);
        if (Files.notExists(directorio)) {
            try {
                Files.createDirectories(directorio);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        Path archivo = directorio.resolve("pagos.json");
        if (Files.notExists(archivo)) {
            try {
                Files.createFile(archivo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            pagos.addAll(cargarTodos());
        }
        pagos.add(pago);


        try (BufferedWriter out = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            out.write("{");
            out.newLine();
            out.write("\t\"pagos\":");
            out.newLine();
            out.write("\t[");
            out.newLine();

            for (int i = 0; i < pagos.size(); i++) {
                out.write("\t\t" + toJson(pagos.get(i)));
                if (i < pagos.size() - 1) {
                    out.write(",");
                }
                out.newLine();
            }
            out.write("\t]");
            out.newLine();
            out.write("}");

        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    @Override
    public List<Pago> cargarTodos() {
        List<Pago> pagos = new ArrayList<>();
        Path directorio = Path.of(ruta);
        Path archivo = directorio.resolve("pagos.json");
        if(Files.notExists(directorio) || Files.notExists(directorio.resolve("pagos.json"))) {
            System.out.println("No se encontro el archivo");
            return null;
        }
        try (BufferedReader in = Files.newBufferedReader(archivo)){
            String linea;
            in.readLine();
            in.readLine();
            in.readLine();
            while((linea = in.readLine()) != null && !linea.trim().equals("}") && !linea.trim().equals("]")) {
                String[] datos = linea.split(",");
                String[] datos_divididos = new String[6];
                for(int i = 0; i < datos.length; i++){
                    String[] datos_semi_divididos = datos[i].split(": ");
                    datos_divididos[i] = datos_semi_divididos[1].replace("\"", "");

                }

                byte id = Byte.parseByte(datos_divididos[0].trim());
                byte id_cliente = Byte.parseByte(datos_divididos[1].trim());
                LocalDate fecha = LocalDate.parse(datos_divididos[2].trim());
                Double importe = Double.parseDouble(datos_divididos[3].trim());
                Double cantidad = Double.parseDouble(datos_divididos[4].trim());
                String combustible = datos_divididos[5].trim().replace("}", "");
                pagos.add(new Pago(id, id_cliente, fecha, importe, cantidad, combustible));

            }
            in.readLine();

        }catch (IOException e){
            System.out.println("ERROR: " + e.getMessage());
        }
        return pagos;
    }

    @Override
    public <T> void ultimoID(T elemento) {

    }

    public String toJson(Pago pago) {
        return "{\"id\": " + pago.getID() + ",\"nombre\": \"" + pago.getID_cliente() + "\",\"fecha\": \"" + pago.getFecha()+ "\",\"importe\": \"" + pago.getImporte()+ "\",\"cantidad\": \"" + pago.getLitros() + "\", \"combustible\": \"" + pago.getCombustible() + "\"}";
    }
}
