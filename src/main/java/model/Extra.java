package model;

public class Extra {

    private String nombre;

    public Extra(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Extra{" +
                "nombre='" + nombre + '\'' +
                '}';
    }
}
