package com.gasolinera.persistence;

import com.gasolinera.common.ILeerEscribir;
import com.gasolinera.model.Pago;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ManejarCSVPago implements ILeerEscribir<Pago> {
    String ruta;

    public ManejarCSVPago(String ruta) {
        this.ruta = ruta;
    }

    @Override
    public void guardar(Pago pago) {
        Path directorio = Path.of(ruta);
        if (Files.notExists(directorio)) {
            try {
                Files.createDirectories(directorio);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Path archivo = directorio.resolve("pagos.csv");
        if (Files.notExists(archivo)) {
            try {
                Files.createFile(archivo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        try (BufferedWriter out = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            out.write(toCSV(pago));
            out.newLine();
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    @Override
    public List<Pago> cargarTodos() {
        List<Pago> pagos = new ArrayList<>();
        Path directorio = Path.of(ruta);
        Path archivo = directorio.resolve("pagos.csv");
        try(BufferedReader in = Files.newBufferedReader(archivo)){
            String linea = in.readLine();
            while (linea != null){
                if(!linea.isBlank()){
                    String[] datos = linea.split(",");
                    byte ID = Byte.parseByte(datos[0].trim());
                    byte ID_cliente = Byte.parseByte(datos[1].trim());
                    LocalDate fecha = LocalDate.parse(datos[2].trim());
                    Double importe = Double.parseDouble(datos[3].trim());
                    Double cantidad = Double.parseDouble(datos[4].trim());
                    String combustible = datos[5];

                    pagos.add(new Pago(ID, ID_cliente, fecha, importe, cantidad, combustible));
                }
                linea = in.readLine();
            }
        }catch (IOException e){
            System.out.println("ERROR: " + e.getMessage());
        }
        return pagos;
    }

    @Override
    public <T> void ultimoID(T elemento) {

    }


    public String toCSV(Pago pago) {
        return pago.getID() + ", " +
                pago.getID_cliente() + ", " +
                pago.getFecha() + ", " +
                pago.getImporte() + ", " +
                pago.getLitros() + ", " +
                pago.getCombustible();
    }
}
