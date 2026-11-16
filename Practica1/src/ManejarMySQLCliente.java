import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManejarMySQLCliente implements ILeerEscribir<Cliente> {

    @Override
    public void escribir(String ruta, List<Cliente> clientes) {
        if (!clientes.isEmpty()) {
            Cliente ultimoCliente = clientes.get(clientes.size() - 1);
            String sqlInsert = "INSERT INTO clientes (nombre, telefono, fecha, matricula) VALUES (?, ?, ?, ?)";

            try (Connection conexion = ManejarMySQL.conectar();
                 PreparedStatement pstmt = conexion.prepareStatement(sqlInsert)) {

                pstmt.setString(1, ultimoCliente.getNombre());
                pstmt.setString(2, ultimoCliente.getTelefono());
                pstmt.setString(3, ultimoCliente.getFecha().toString());
                pstmt.setString(4, ultimoCliente.getMatricula());

                pstmt.executeUpdate();
                System.out.println("Cliente guardado en MySQL con éxito.");

            } catch (SQLException e) {
                System.err.println("ERROR al guardar el cliente en MySQL: " + e.getMessage());
            }
        }
    }

    @Override
    public List<Cliente> leer(String ruta) {
        List<Cliente> leidos = new ArrayList<>();
        String sql = "SELECT id, nombre, telefono, fecha, matricula FROM clientes";

        try (Connection conexion = ManejarMySQL.conectar();
             Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String telefono = rs.getString("telefono");
                LocalDate fecha = LocalDate.parse(rs.getString("fecha"));
                String matricula = rs.getString("matricula");

                leidos.add(new Cliente(id, nombre, telefono, fecha, matricula));
            }
        } catch (SQLException e) {
            System.err.println("ERROR al leer clientes de MySQL: " + e.getMessage());
        }
        return leidos;
    }

    @Override
    public int ultimoID(String ruta) {
        String sql = "SELECT MAX(id) AS max_id FROM clientes";
        try (Connection conexion = ManejarMySQL.conectar();
             Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("max_id");
            }
        } catch (SQLException e) {
            System.err.println("ERROR al obtener el último ID: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public String toString_personalizado(Cliente c) {
        return c.toString();
    }
}