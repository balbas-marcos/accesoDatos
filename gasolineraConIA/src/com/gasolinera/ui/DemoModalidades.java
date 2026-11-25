package com.gasolinera.ui;

import com.gasolinera.model.AsignacionFuncion;
import com.gasolinera.model.DetalleCajero;
import com.gasolinera.model.Devolucion;
import com.gasolinera.model.EstadoProducto;
import com.gasolinera.model.LineaVenta;
import com.gasolinera.model.Producto;
import com.gasolinera.model.ProductoEnvasado;
import com.gasolinera.model.ProductoGranel;
import com.gasolinera.model.Venta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Demostración de las dos modalidades y del redondeo HALF_UP. Sin Scanner y sin E/S. */
public final class DemoModalidades {

    private DemoModalidades() {
    }

    public static void main(String[] args) {
        LocalDateTime ahora = LocalDateTime.of(2026, 11, 25, 10, 30);

        Producto agua = new ProductoEnvasado(1, "PRD-ENV-001", "Agua mineral 1,5 L",
                new BigDecimal("1.25"), EstadoProducto.ACTIVO, "Botella 1,5 L");
        Producto aceitunas = new ProductoGranel(2, "PRD-GRA-001", "Aceitunas verdes",
                new BigDecimal("18.40"), EstadoProducto.ACTIVO, new BigDecimal("0.050"));
        Producto harina = new ProductoGranel(3, "PRD-GRA-002", "Harina a granel",
                new BigDecimal("1.00"), EstadoProducto.ACTIVO, new BigDecimal("0.005"));

        System.out.println("=== DEMO MODALIDADES Y HALF_UP ===");
        System.out.println("Envasado: " + agua);
        System.out.println("Granel:   " + aceitunas);

        Venta venta = new Venta(1, "N26-000901", 1, 1, null, ahora);
        LineaVenta lineaAgua = new LineaVenta(1, 1, agua, new BigDecimal("4.000"), new BigDecimal("1.25"));
        LineaVenta lineaGranel = new LineaVenta(2, 2, aceitunas, new BigDecimal("0.350"), new BigDecimal("18.40"));
        venta.agregarLinea(lineaAgua);
        venta.agregarLinea(lineaGranel);

        System.out.println("Línea 1 (ENVASADO): " + lineaAgua);
        System.out.println("Línea 2 (GRANEL):   " + lineaGranel);
        System.out.println("Total de la venta:  " + venta.calcularTotal().valor());

        System.out.println("HALF_UP 0,125 kg x 1,00 = "
                + harina.calcularImporte(new BigDecimal("0.125")).valor()
                + " (con HALF_EVEN sería 0,12)");

        rechazar("ENVASADO con 2,5 unidades", () -> agua.validarCantidad(new BigDecimal("2.5")));
        rechazar("GRANEL 0,155 kg con precisión 0,050",
                () -> aceitunas.validarCantidad(new BigDecimal("0.155")));

        venta.marcarPagada(venta.calcularTotal());
        System.out.println("Estado de la venta tras cuadrar el cobro: " + venta.getEstado());

        Devolucion devolucion = Devolucion.sobre(lineaGranel, 1, 1, new BigDecimal("0.150"),
                "Producto defectuoso", ahora, false);
        devolucion.validarDentroDe(lineaGranel, List.of());
        System.out.println("Devolución válida: " + devolucion.getCantidad() + " kg -> "
                + devolucion.getImporteReintegrado().valor());

        rechazar("Devolución que supera la cantidad pendiente", () -> {
            Devolucion exceso = Devolucion.sobre(lineaGranel, 2, 1, new BigDecimal("0.400"),
                    "Error de captura", ahora, true);
            exceso.validarDentroDe(lineaGranel, List.of(devolucion));
        });

        AsignacionFuncion cajeroAbierto = new AsignacionFuncion(1, 1, 2,
                LocalDate.of(2026, 1, 1), null, new DetalleCajero(new BigDecimal("50.00")));
        AsignacionFuncion cajeroPosterior = new AsignacionFuncion(2, 1, 2,
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 12, 31), new DetalleCajero(new BigDecimal("50.00")));
        System.out.println("Solape detectado entre periodos de CAJERO: "
                + cajeroAbierto.solapaCon(cajeroPosterior));

        System.out.println("=== FIN DE LA DEMO ===");
    }

    private static void rechazar(String descripcion, Runnable accion) {
        try {
            accion.run();
            System.out.println("ERROR: " + descripcion + " no fue rechazado");
        } catch (IllegalArgumentException e) {
            System.out.println("Rechazado (" + descripcion + "): " + e.getMessage());
        }
    }
}
