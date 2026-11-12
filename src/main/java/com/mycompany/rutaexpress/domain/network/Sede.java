package com.mycompany.rutaexpress.domain.network;

public class Sede {
    private String codigo;
    private String nombre;
    private String municipio;
    private String direccion;
    private String tipo;
    private String telefono;
    private String correoCoordinador;
    private double capacidadBodegaKg;

    public Sede(String codigo, String nombre, String municipio, String direccion,
            String tipo, String telefono, String correoCoordinador, double capacidadBodegaKg) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.municipio = municipio;
        this.direccion = direccion;
        this.tipo = tipo;
        this.telefono = telefono;
        this.correoCoordinador = correoCoordinador;
        this.capacidadBodegaKg = capacidadBodegaKg;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreoCoordinador() { return correoCoordinador; }
    public void setCorreoCoordinador(String correoCoordinador) { this.correoCoordinador = correoCoordinador; }
    public double getCapacidadBodegaKg() { return capacidadBodegaKg; }
    public void setCapacidadBodegaKg(double capacidadBodegaKg) { this.capacidadBodegaKg = capacidadBodegaKg; }
}
