import java.util.Date;

public class Cliente {
    int ID = 0;
    String nombre;
    String telefono;
    Date fecha;
    String matricula;


    public Cliente( String nombre, String telefono, Date fecha, String matricula) {
        ID = ID+1;
        this.nombre = nombre;
        this.telefono = telefono;
        this.fecha = fecha;
        this.matricula = matricula.toUpperCase();
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}

