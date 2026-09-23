import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;

public class ManejarCSV implements ILeerEscribir {

    public ManejarCSV() {
    }

    @Override
    public void escribir(String ruta, String texto) {
        try {
            Path archivo = Path.of(ruta);
            Files.writeString(
                    archivo,
                    texto + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    @Override
    public List<String> leer(String ruta) {
        Path archivo = Path.of(ruta);
        try {
            return Files.readAllLines(archivo, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return null;
    }
}
