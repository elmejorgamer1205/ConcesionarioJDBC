package model;

import java.util.List;

public class Coche {
    private String matricula;
    private String maraca;
    private String modelo;
    private List<Extra> extras;
    private double precio;
    private int idPropietario;

    public Coche(String matricula, String maraca, String modelo, List<Extra> extras, double precio, int idPropietario) {
        this.matricula = matricula;
        this.maraca = maraca;
        this.modelo = modelo;
        this.extras = extras;
        this.precio = precio;
        this.idPropietario = idPropietario;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMaraca() {
        return maraca;
    }

    public void setMaraca(String maraca) {
        this.maraca = maraca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public List<Extra> getExtras() {
        return extras;
    }

    public void setExtras(List<Extra> extras) {
        this.extras = extras;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(int idPropietario) {
        this.idPropietario = idPropietario;
    }

    @Override
    public String toString() {
        return "Coche{" +
                "matricula='" + matricula + '\'' +
                ", maraca='" + maraca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", extras=" + extras +
                ", precio=" + precio +
                ", idPropietario=" + idPropietario +
                '}';
    }
}
