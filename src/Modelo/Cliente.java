package Modelo;

public class Cliente {

    private String nombres;
    private String apellidos;
    private String numDocumento;
    private String tipoDocumento;
    private String telefono;

    public Cliente() {

    }

    public Cliente(String nombres, String apellidos, String documento, String tipo, String telefono) {

        this.nombres = nombres;
        this.apellidos = apellidos;
        this.numDocumento = documento;
        this.tipoDocumento = tipo;
        this.telefono = telefono;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNumDocumento() {
        return numDocumento;
    }

    public void setNumDocumento(String numDocumento) {
        this.numDocumento = numDocumento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

}
