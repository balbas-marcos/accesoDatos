import java.util.List;

public interface ILeerEscribir <T> {
    void guardar(T elemento);
    List<T> cargarTodos();
    <T> void ultimoID(T elemento);
}
