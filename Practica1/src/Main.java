import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        menu(); // mostramos el menu

    }


    public static void menu() {
        Scanner sc = new Scanner(System.in);
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

            }
        } while (opcion != 0);
    }
}
