import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ManejarMySQL {

    public static Connection conectar() {
        Connection conexion = null;
        String url = "jdbc:mysql://localhost:3307/ra2_gasolinerasql";
        String usuario = "gasolinera";
        String clave = "1234";

        try {
            conexion = DriverManager.getConnection(url, usuario, clave);

            // hecho con IA para saber la version de cada cosa
            DatabaseMetaData datos = conexion.getMetaData();
            System.out.println("Gestor: " + datos.getDatabaseProductName());
            System.out.println("Versión del gestor: " + datos.getDatabaseProductVersion());
            System.out.println("Driver: " + datos.getDriverName());
            System.out.println("Conexión válida: " + conexion.isValid(2));

        } catch (SQLException e) {
            System.err.println("Error al conectar con MySQL: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
        }
        return conexion;
    }

    public static void inicializarBaseDatos() {
        String sqlClientes = "CREATE TABLE IF NOT EXISTS clientes (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nombre VARCHAR(100) NOT NULL, " +
                "telefono VARCHAR(20) NOT NULL, " +
                "fecha VARCHAR(20) NOT NULL, " +
                "matricula VARCHAR(20) UNIQUE NOT NULL" +
                ");";

        String sqlPagos = "CREATE TABLE IF NOT EXISTS pagos (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "id_cliente INT NOT NULL, " +
                "fecha VARCHAR(20) NOT NULL, " +
                "importe DOUBLE NOT NULL, " +
                "litros DOUBLE NOT NULL, " +
                "combustible VARCHAR(50) NOT NULL, " +
                "FOREIGN KEY (id_cliente) REFERENCES clientes(id)" +
                ");";

        try (Connection conexion = conectar();
             Statement stmt = conexion != null ? conexion.createStatement() : null) {

            if (conexion != null && stmt != null) {
                // se crean las tablas
                stmt.execute(sqlClientes);
                stmt.execute(sqlPagos);
                System.out.println("Estructura de la base de datos creada o verificada correctamente en MySQL.");
            }

        } catch (SQLException e) {
            System.err.println("ERROR al crear las tablas en la base de datos: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
        }
    }
}