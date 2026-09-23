import java.time.LocalDate;


public class Cliente {
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
    public String toString() {
        return "Cliente{" +
                "ID=" + ID +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                ", fecha=" + fecha +
                ", matricula='" + matricula + '\'' +
                '}';
    }

}

