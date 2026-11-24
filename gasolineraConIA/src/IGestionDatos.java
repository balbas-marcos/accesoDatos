    import java.util.List;

    public interface IGestionDatos <T>{
        void guardar(T elemento);
        List<T> cargarTodos();
        T leerporID(byte id);
    }
