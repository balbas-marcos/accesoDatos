import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.time.LocalDate;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static String rutaClientes = "src/archivosJson/clientes.json";
    static String rutaPagos = "src/archivosJson/pagos.json";
    static String rutaMigrarClienteCSV = "src/archivosCSV/clientes.csv";
    static String rutaMigrarClienteJson = "src/archivosJson/clientes.json";
    static String rutaMigrarPagoCSV = "src/archivosCSV/pagos.csv";
    static String rutaMigrarPagoJson = "src/archivosJson/pagos.json";
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);


        ILeerEscribir manejadorCliente = new ManejarJsonCliente();
        ILeerEscribir manejadorPago = new ManejarJsonPago();
        ClienteGestion gestionCliente = new ClienteGestion(rutaClientes, manejadorCliente);
        PagoGestion gestionPago = new PagoGestion(rutaPagos, manejadorPago);

        MigraCSVToJson migrador = new MigraCSVToJson();
        migrador.migrarClientes(rutaMigrarClienteCSV, rutaMigrarClienteJson);
        migrador.migrarPagos(rutaMigrarPagoCSV, rutaMigrarPagoJson);

        byte opcion = -1;

        do {
            try {


                System.out.print("""
                        === GESTIÓN DE GASOLINERA ===
                        1. Dar de alta un cliente
                        2. Listar clientes
                        3. Buscar clientes
                        4. Procesar un pago de repostaje
                        5. Consultar pagos
                        0. Salir
                        Opción: """);
                opcion = sc.nextByte();
                switch (opcion) {
                    case 1:
                        Cliente cliente = altaCliente(gestionCliente, manejadorCliente);
                        gestionCliente.guardar(cliente);
                        break;
                    case 2:
                        System.out.printf("%-8s\t%-15s\t%-12s\t%-13s\t%s%n",
                                "ID", "NOMBRE", "TELEFONO", "FECHA", "MATRICULA");
                        for (Cliente c : gestionCliente.cargarTodos()) {
                            System.out.println(c.toString());
                        }
                        break;
                    case 3:
                        System.out.println("Introduce lo que buscas: ");
                        String buscado = sc.next();
                        for (Cliente c : gestionCliente.buscar(buscado)) {
                            System.out.println(c);
                        }
                        break;
                    case 4:
                        gestionPago.guardar(procesarPago(gestionCliente, gestionPago, manejadorPago));
                        break;
                    case 5:
                        System.out.printf("%-8s\t%-20s\t%-12s\t%-10s\t%-10s\t%s%n",
                                "ID", "NOMBRE", "FECHA", "IMPORTE", "LITROS", "COMBUSTIBLE");
                        for (Pago p : gestionPago.cargarTodos()) {
                            System.out.println(p.toString_consola(gestionCliente.cargarTodos()));
                        }
                }
            } catch (InputMismatchException e) {
                System.out.println("ERROR: Introduzca un numero");
                sc.nextLine();
            }
        } while (opcion != 0);

    }


    public static Cliente altaCliente(ClienteGestion gestionCliente, ILeerEscribir manejador) {
        Scanner sc = new Scanner(System.in);
        LocalDate fecha = null;
        String telefono = null;
        boolean valido = false;
        try {
            System.out.print("Introduce tu nombre: ");
            String nombre = sc.next().toLowerCase(Locale.ROOT).trim();
            do {
                try {
                    System.out.print("Introduce tu telefono: ");
                    telefono = sc.next().trim();
                    if (telefono.length() == 9) {
                        valido = true;
                    } else if (telefono.length() > 9) {
                        System.out.println("El numero no existe, demasiado largo");
                    }

                } catch (InputMismatchException e) {
                    System.out.println("ERROR: " + e.getMessage());
                    valido = false;

                }

            } while (!valido);
            valido = false;
            do {
                try {
                    System.out.print("Introduce tu fecha: ");
                    String fechaString = sc.next().trim();
                    fecha = LocalDate.parse(fechaString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    valido = true;

                } catch (DateTimeException e) {
                    System.out.println("ERROR: debe ser \'dd/MM/aaaa\'" + e.getMessage());
                    valido = false;
                }
            } while (!valido);
            System.out.println("Introduce tu matricula: ");
            String matricula = sc.next().trim();
            return new Cliente((manejador.ultimoID(rutaClientes) + 1), nombre, telefono, fecha, matricula);
        } catch (InputMismatchException e) {
            System.out.println("ERROR: " + e.getMessage());
            sc.nextLine().trim();
        }
        return null;

    }

    public static Pago procesarPago(ClienteGestion gestionCliente, PagoGestion gestionPago, ILeerEscribir manejador) {
        Scanner sc = new Scanner(System.in);
        boolean valido = false;
        int id_cliente = 0;
        LocalDate fecha = null;
        double importe = 0;
        double litros = 0;
        String combustible = null;
        do {
            try {
                System.out.println("CLIENTES DISPONIBLES");
                for (Cliente c : gestionCliente.cargarTodos()) {
                    System.out.println(c);
                }
                System.out.println("Introduce el ID del cliente: ");
                id_cliente = sc.nextInt();
                for (Cliente c : gestionCliente.cargarTodos()) {
                    if (id_cliente == c.getID()) {
                        valido = true;
                    }
                }
                if (!valido) {
                    System.out.println("No existe el cliente con ID: " + id_cliente);
                }
            } catch (InputMismatchException e) {
                System.out.println("ERROR: introduce un id: " + e.getMessage());
                sc.nextLine();
            }

        } while (!valido);
        sc.nextLine();
        valido = false;
        do {
            try {
                System.out.print("Introduce la fecha: ");
                String fechaString = sc.nextLine().trim();
                if (fechaString.isEmpty() || fechaString == null) {
                    fecha = LocalDate.now();
                    System.out.println("poniendo fecha de hoy(enter para la fecha de hoy)");
                    valido = true;
                } else {
                    fecha = LocalDate.parse(fechaString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    valido = true;
                }

            } catch (DateTimeException e) {
                System.out.println("ERROR: debe ser \'dd/MM/aaaa\'" + e.getMessage());
            }

        } while (!valido);

        valido = false;
        do {
            try {
                System.out.print("Introduce el importe: ");
                String importeString = sc.next().trim().replace(',', '.');
                importe = Double.parseDouble(importeString);
                valido = true;
            } catch (NumberFormatException e) {
                System.out.println("ERROR: El importe debe ser un número válido");
            }
        } while (!valido);
        valido = false;


        do {
            try {
                System.out.print("Introduce los litros: ");
                String litrosString = sc.next().trim().replace(',', '.');

                litros = Double.parseDouble(litrosString);
                valido = true;
            } catch (NumberFormatException e) {
                System.out.println("ERROR: los litros debe ser un número válido ");
            }
        } while (!valido);

        sc.nextLine();


        System.out.println("Introduce el combustible(Gasolina 95, 98, Diesel): ");
        combustible = sc.nextLine();


        return new Pago(manejador.ultimoID(rutaPagos) + 1, id_cliente, fecha, importe, litros, combustible);
    }


}



