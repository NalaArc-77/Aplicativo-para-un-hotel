package Modelo;
public class Habitacion {
    private String numero;
    private String tipo;
    private double precioXNoche;
    private String estado;
    private int numeroPiso;

    public Habitacion(){
        
    }

    public Habitacion(String numero, String tipo, double precioXNoche, String estado, int numeroPiso) {
        this.numero = numero;
        this.tipo = tipo;
        this.precioXNoche = precioXNoche;
        this.estado = estado;
        this.numeroPiso = numeroPiso;
    }
    
    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getPrecioXNoche() {
        return precioXNoche;
    }

    public void setPrecioXNoche(double precioXNoche) {
        this.precioXNoche = precioXNoche;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getNumeroPiso() {
        return numeroPiso;
    }

    public void setNumeroPiso(int numeroPiso) {
        this.numeroPiso = numeroPiso;
    }
    
    
    
}
