import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ILeerEscribir manejador = new ManejarCSV();
        ClienteGestion gestionCliente = new ClienteGestion("src/archivosCSV/clientes.csv", manejador);
        PagoGestion gestionPago = new PagoGestion("src/pagos.csv", manejador);

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
                        Cliente cliente = altaCliente(gestionCliente, manejador);
                        gestionCliente.guardar(cliente);
                        break;
                    case 2:
                        for (Cliente c : gestionCliente.cargarTodos()) {
                            System.out.println(c);
                        }
                        break;
                    case 3:
                        System.out.println("Introduce el ID que buscas: ");
                        byte id_buscado = sc.nextByte();
                        System.out.println(gestionCliente.leerporID(id_buscado));
                        break;
                    case 4:
                        gestionPago.guardar(procesarPago(gestionCliente));
                        break;
                    case 5:
                        for (Pago p : gestionPago.cargarTodos()) {
                            System.out.println(p);
                        }
                }
            }catch (InputMismatchException e){
                System.out.println("ERROR: Introduzca un numero");
                sc.nextLine();
            }
        } while (opcion != 0);

    }


    public static Cliente altaCliente(ClienteGestion gestionCliente, ILeerEscribir manejador) {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Introduce tu nombre: ");
            String nombre = sc.next();
            System.out.print("Introduce tu telefono: ");
            String telefono = sc.next();
            System.out.print("Introduce tu fecha: ");
            String fechaString = sc.next();
            LocalDate fecha = LocalDate.parse(fechaString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            System.out.println("Introduce tu matricula: ");
            String matricula = sc.next();

            return new Cliente(gestionCliente.ultimoID() + 1, nombre, telefono, fecha, matricula);
        } catch (InputMismatchException e) {
            System.out.println("ERROR: " + e.getMessage());
            sc.nextLine();
        }
        return null;

    }

    public static Pago procesarPago(ClienteGestion gestion) {
        Scanner sc = new Scanner(System.in);
        boolean valido = false;
        int id_cliente = 0;
        LocalDate fecha = null;
        double importe = 0;
        double litros = 0;
        String combustible = null;
        do {
            try {
                System.out.println("Introduce el ID del cliente: ");
                id_cliente = sc.nextInt();
                for (Cliente c : gestion.cargarTodos()) {
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
        System.out.println("Introduce la fecha: ");
        String fechaString = sc.next();
        fecha = LocalDate.parse(fechaString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        System.out.println("Introudce el importe: ");
        importe = sc.nextDouble();

        System.out.println("Introduce los litros: ");
        litros = sc.nextDouble();

        System.out.println("Introduce el combustible: ");
        combustible = sc.next();

        return new Pago(id_cliente, fecha, importe, litros, combustible);
    }


}



