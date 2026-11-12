package com.mycompany.rutaexpress.domain.vehicle;

public class Conductor {
    private String cedula;
    private String nombre;
    private String celular;
    private String correo;
    private String categoriaLicencia;

    public Conductor(String cedula, String nombre, String celular,
            String correo, String categoriaLicencia) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.celular = celular;
        this.correo = correo;
        this.categoriaLicencia = categoriaLicencia;
    }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getCategoriaLicencia() { return categoriaLicencia; }
    public void setCategoriaLicencia(String categoriaLicencia) { this.categoriaLicencia = categoriaLicencia; }
}
