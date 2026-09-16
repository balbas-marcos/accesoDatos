import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ClienteGestion gestion = new ClienteGestion();
        byte opcion = -1;
        do {
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
                    gestion.guardar(altaCliente());
                    break;
                case 2:
                    for (Cliente c : gestion.cargarTodos()){
                        System.out.println(c);
                    }
                    break;
                case 3:
                    System.out.println("Introduce el ID que buscas: ");
                    byte id_buscado = sc.nextByte();
                    System.out.println(gestion.leerporID(id_buscado));
            }
        } while (opcion != 0);

    }



    public static Cliente altaCliente(){
        Scanner sc = new Scanner(System.in);
        System.out.print("Introduce tu nombre: ");
        String nombre = sc.next();
        System.out.print("Introduce tu telefono: ");
        String telefono = sc.next();
        System.out.print("Introduce tu fecha: ");
        String fechaString = sc.next();
        LocalDate fecha = LocalDate.parse(fechaString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        System.out.print("Introduce tu matricula: ");
        String matricula = sc.nextLine();

        return new Cliente(nombre,telefono,fecha, matricula);

    }
}



