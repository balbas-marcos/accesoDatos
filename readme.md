He usado la version jdk: OpenJDK 21.0.12
El programa esta hecho de forma que tengo una clase Cliente y otra clase Pago en las que defino lo que tiene cada una
luego para gestionar los clientes y pagos tengo dos clases GestorCliente y GestorPago ambas implementan una interfaz en la que defino todos los metodos y cada una lo utiliza de una forma.
Por último como lo que se pide es poder escribir en CSV pero a la vez que si en un futuro quiero poner otro metodo de guardado no tenga que cambiar todo el programa lo que he hecho es crear una interfaz LeerEscribir en la que defino los metodos de escribir, leer y ultimoID este último es importante para cuando cierres el programa y lo vuelvas a abrir al crear un nuevo cliente o pago sea capaz de saber donde se quedó el último id. Para conectar todo lo que hago es tener ahora otras dos clases que son ManejarCSVPago y ManejarCSVCliente en estos implemento la interfaz y luego en el main las instancio pero le asigno la variable de tipo interfaz LeerEscribir lo que permite que pueda usar el metodo csv pero si en un futuro pongo otro solo tendria que crear dos manejadores uno cliente y otro pago utilizando la misma interfaz por ejemplo si quisiera json seria hacer esas dos clases nuevas y en el main hacer que LeerEscribir use la clase json.
Metiendonos especificamente en el CSV para guardarlo lo hago mediante comas creando un propio toString en los manejadores para establecer como debe de escribirse y luego ya solo sea escribirlo usando BufferedWriter.


Para el JSON he utilizado el formato estandar:
{
    "nombre:"
    [
        ....
    ]
}
los he separado en dos archivos uno para clientes y otro para pagos.

Para la migracion he creado la clase MigrarCSVToJson con dos metodos uno migrarClientes y otro migrarPagos en ambos se pasan dos argumentos el primero es la ruta del csv y el segundo la del json, es decir migrarCLientes tiene que tener la ruta del clientes.csv y luego la de clientes.json

Lo llamo directamente en el main al ejecutar el programa y para que no este siempre clonando lo que hay en csv al json porque se liaria he puesto en la clase MigrarCSVToJson que compruebe primero que el id de ese cliente no existe en el json para asi agregarle en caso de ya existir no tiene que migrar nada al json.