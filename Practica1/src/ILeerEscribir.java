import java.util.List;

public interface ILeerEscribir {
    void escribir(String ruta, String texto);
    List<String> leer(String ruta);
}
