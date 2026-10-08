
D:.
│
├───.idea
│       .gitignore
│       misc.xml
│       modules.xml
│       vcs.xml
│       workspace.xml
│
└───src
    │   Cliente.java
    │   ClienteGestion.java
    │   IGestionDatos.java
    │   ILeerEscribir.java
    │   Main.java
    │   ManejarCSVCliente.java
    │   ManejarCSVPago.java
    │   ManejarJsonCliente.java
    │   ManejarJsonPago.java
    │   MigraCSVToJson.java
    │   Pago.java
    │   PagoGestion.java
    │
    ├───archivosCSV
    │       clientes.csv
    │       pagos.csv
    │
    └───archivosJson
            clientes.json
            pagos.json




He creado la clase MigrarCSVToJson

He tenido que añadir a las clases Cliente y Pago otro compareTo para ordendar el json en orden descendente por ID para que cuando tenga que leer el ultimo ID sea solo leer la primera linea del json y tenerlo:

 git diff 5ef48f95d43672b55cd6f017d092cccf01c4039b d28e56f6c191f1b2fb46eae550838b02047296f1



diff --git a/Practica1/src/Cliente.java b/Practica1/src/Cliente.java
index a2e4d13..45459e7 100644
--- a/Practica1/src/Cliente.java
+++ b/Practica1/src/Cliente.java
@@ -59,6 +59,13 @@ public class Cliente implements Comparable<Cliente> {
         this.matricula = matricula;
     }

+
+
+    public int compareID(Cliente otro) {
+        return Integer.compare(otro.ID, this.ID);
+    }
+


tambien para usar el json he cambiado dos lineas del main:
diff --git a/Practica1/src/Main.java b/Practica1/src/Main.java
index 4a1dd1c..ac7ca93 100644
--- a/Practica1/src/Main.java
+++ b/Practica1/src/Main.java

-        ILeerEscribir manejadorCliente = new ManejarCSVCliente();
-        ILeerEscribir manejadorPago = new ManejarCSVPago();
+        ILeerEscribir manejadorCliente = new ManejarJsonCliente();
+        ILeerEscribir manejadorPago = new ManejarJsonPago();


y luego al agregar el migrador lo he añadido al main:

+        MigraCSVToJson migrador = new MigraCSVToJson();
+        migrador.migrarClientes(rutaMigrarClienteCSV, rutaMigrarClienteJson);
+        migrador.migrarPagos(rutaMigrarPagoCSV, rutaMigrarPagoJson);



