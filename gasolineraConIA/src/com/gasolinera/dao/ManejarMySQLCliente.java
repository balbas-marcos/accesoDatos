package com.gasolinera.dao;

import com.gasolinera.common.ILeerEscribir;
import com.gasolinera.model.Cliente;

import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManejarMySQLCliente implements ILeerEscribir<Cliente> {

    @Override
    public void guardar(Cliente cliente) {
        String sqlInsert = "INSERT INTO clientes (nombre, telefono, matricula) VALUES (?, ?, ?)";

        try (Connection conexion = ManejarMySQL.conectar();
             PreparedStatement pstmt = conexion.prepareStatement(sqlInsert)) {

            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getTelefono());
            pstmt.setString(3, cliente.getMatricula());

            pstmt.executeUpdate();
            System.out.println("Cliente guardado en MySQL con éxito.");

        } catch (SQLException e) {
            System.err.println("ERROR al guardar el cliente en MySQL: " + e.getMessage());
        }
    }

    @Override
    public List<Cliente> cargarTodos() {
        List<Cliente> leidos = new ArrayList<>();
        String sql = "SELECT id, nombre, telefono, fecha, matricula FROM clientes";

        try (Connection conexion = ManejarMySQL.conectar();
             Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String telefono = rs.getString("telefono");
                String matricula = rs.getString("matricula");

                leidos.add(new Cliente((byte) id, nombre, telefono, matricula));
            }
        } catch (SQLException e) {
            System.err.println("ERROR al leer clientes de MySQL: " + e.getMessage());
        }
        return leidos;
    }

    @Override
    public <T> void ultimoID(T elemento) {

    }


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


    public String toString_personalizado(Cliente c) {
        return c.toString();
    }
}