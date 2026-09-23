import java.util.List;

public interface ILeerEscribir {
    void escribir(String ruta, List<Cliente> elementos);
    List<String> leer(String ruta);
}
