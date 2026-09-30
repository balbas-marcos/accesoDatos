import java.time.LocalDate;
import java.util.Locale;


public class Cliente implements Comparable<Cliente> {
    int ID = 0;
    String nombre;
    String telefono;
    LocalDate fecha;
    String matricula;


    public Cliente(int ID, String nombre, String telefono, LocalDate fecha, String matricula) {
        this.ID = ID;
        this.nombre = nombre.trim();
        this.telefono = telefono.trim();
        this.fecha = fecha;
        this.matricula = matricula.toUpperCase().trim();
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    @Override
    public int compareTo(Cliente otro) {
        int resultado = this.nombre.toLowerCase(Locale.ROOT).compareTo(otro.nombre.toLowerCase(Locale.ROOT));
        if (resultado == 0) {
            return Integer.compare(this.ID, otro.ID);
        }
        return resultado;
    }

    @Override
    public String toString() {
        return String.format("%-8d\t%-15s\t%-12s\t%-13s\t%-15s",
                ID, nombre, telefono, fecha, matricula);
    }

}

