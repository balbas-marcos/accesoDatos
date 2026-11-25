package com.gasolinera.persistence;

import com.gasolinera.common.ILeerEscribir;
import com.gasolinera.model.Cliente;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class ManejarCSVCliente implements ILeerEscribir<Cliente> {
    String ruta;
    Path directorio;
    Path archivo;

    public ManejarCSVCliente(String ruta) {
        this.ruta = ruta;
    }

    @Override
    public void guardar(Cliente cliente) {
        Path directorio = Path.of(ruta);
        try {
            if (Files.notExists(directorio)) {
                Files.createDirectories(directorio);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Path archivo = directorio.resolve("clientes.csv");
        if (Files.notExists(archivo)) {
            try {
                Files.createFile(archivo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (BufferedWriter out = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            out.write(toCSV(cliente));
            out.newLine();
        } catch (IOException e) {
            System.out.println("ERROR:" + e.getMessage());
        }
    }

    @Override
    public List<Cliente> cargarTodos() {
        Path archivo = Path.of(ruta).resolve("clientes.csv");
        List<Cliente> clientes = new ArrayList<>();
        try (BufferedReader in = Files.newBufferedReader(archivo)) {
            String linea = in.readLine();
            while (linea != null) {
                if (!linea.isBlank()) {
                    String[] datos = linea.replace(";", "").split(",");
                    byte id = Byte.parseByte(datos[0]);
                    String nombre = datos[1].trim();
                    String telefono = datos[2].trim();
                    String matricula = datos[3].trim();

                    clientes.add(new Cliente(id,nombre, telefono, matricula));
                }
                linea = in.readLine();
            }
        } catch (IOException e) {
            System.out.println("ERROR:");
        }
        clientes.sort(Cliente::compareTo);
        return clientes;
    }

    @Override
    public <T> void ultimoID(T elemento) {

    }


    public String toCSV(Cliente cliente) {
        return cliente.getID() + "," + cliente.getNombre() + "," + cliente.getTelefono() + "," + cliente.getMatricula();
    }
}
