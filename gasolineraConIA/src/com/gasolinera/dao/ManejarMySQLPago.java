package com.gasolinera.dao;

import com.gasolinera.common.ILeerEscribir;
import com.gasolinera.model.Pago;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManejarMySQLPago implements ILeerEscribir<Pago> {

    @Override
    public void guardar(Pago pago) {
        String sqlInsert = "INSERT INTO pagos (id_cliente, fecha, importe, litros, combustible) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ManejarMySQL.conectar();
             PreparedStatement pstmt = conexion.prepareStatement(sqlInsert)) {

            pstmt.setInt(1, pago.getID_cliente());
            pstmt.setString(2, pago.getFecha().toString());
            pstmt.setDouble(3, pago.getImporte());
            pstmt.setDouble(4, pago.getLitros());
            pstmt.setString(5, pago.getCombustible());

            pstmt.executeUpdate();
            System.out.println("Pago guardado en MySQL con éxito.");

        } catch (SQLException e) {
            System.err.println("ERROR al guardar el pago en MySQL: " + e.getMessage());
        }
    }


    @Override
    public List<Pago> cargarTodos() {
        List<Pago> leidos = new ArrayList<>();
        String sql = "SELECT id, id_cliente, fecha, importe, litros, combustible FROM pagos";

        try (Connection conexion = ManejarMySQL.conectar();
             Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int idCliente = rs.getInt("id_cliente");
                LocalDate fecha = LocalDate.parse(rs.getString("fecha"));
                double importe = rs.getDouble("importe");
                double litros = rs.getDouble("litros");
                String combustible = rs.getString("combustible");

                leidos.add(new Pago(id, idCliente, fecha, importe, litros, combustible));
            }
        } catch (SQLException e) {
            System.err.println("ERROR al leer pagos de MySQL: " + e.getMessage());
        }
        return leidos;
    }

    @Override
    public <T> void ultimoID(T elemento) {

    }


    public int ultimoID(String ruta) {
        String sql = "SELECT MAX(id) AS max_id FROM pagos";
        try (Connection conexion = ManejarMySQL.conectar();
             Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("max_id");
            }
        } catch (SQLException e) {
            System.err.println("ERROR al obtener el último ID de pagos: " + e.getMessage());
        }
        return 0;
    }


    public String toString_personalizado(Pago p) {
        return p.toString();
    }
}
