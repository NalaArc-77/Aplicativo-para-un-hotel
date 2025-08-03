package Modelo;
public class Factura {
    private int idFactura;
    private int idReserva;
    private double totalPagar;
    private String fechaEmision;
    
    public Factura(){
        
    }

    public Factura(int idFactura, int idReserva, double totalPagar, String fechaEmision) {
        this.idFactura = idFactura;
        this.idReserva = idReserva;
        this.totalPagar = totalPagar;
        this.fechaEmision = fechaEmision;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public double getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(double totalPagar) {
        this.totalPagar = totalPagar;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
    
    
}
