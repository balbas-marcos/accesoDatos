import java.util.List;

public interface ILeerEscribir<T> {
    void escribir(String ruta, List<T> elementos);
    List<T> leer(String ruta);
    int ultimoID(List<T> elementos);
}
