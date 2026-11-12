package com.mycompany.rutaexpress;

import java.time.LocalDate;

public class NodoCliente {
    private String documento;
    private String tipoCliente;
    private String nombre;
    private String telefono;
    private String correo;
    private String direccion;
    private String municipio;
    private LocalDate fechaRegistro;
    private ListaSimpleGuias guias;
    private NodoCliente sig;

    public NodoCliente(String documento, String tipoCliente, String nombre,
            String telefono, String correo, String direccion, String municipio) {
        this.documento = documento;
        this.tipoCliente = tipoCliente;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.municipio = municipio;
        this.fechaRegistro = LocalDate.now();
        this.guias = new ListaSimpleGuias();
        this.sig = null;
    }

    public void agregarGuia(String guia) {
        guias.agregar(guia);
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public ListaSimpleGuias getGuias() {
        return guias;
    }

    public void setGuias(ListaSimpleGuias guias) {
        this.guias = guias;
    }

    public NodoCliente getSig() {
        return sig;
    }

    public void setSig(NodoCliente sig) {
        this.sig = sig;
    }

    public int totalGuias() {
        return guias.getTamanio();
    }

    @Override
    public String toString() {
        return "Documento: " + documento + " | Tipo: " + tipoCliente
                + " | Nombre: " + nombre + " | Teléfono: " + telefono
                + " | Correo: " + correo + " | Dirección: " + direccion
                + " | Municipio: " + municipio + " | Registro: " + fechaRegistro
                + " | Guías: " + guias.getTamanio();
    }
}
