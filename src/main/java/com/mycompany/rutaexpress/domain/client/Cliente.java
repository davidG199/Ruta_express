package com.mycompany.rutaexpress.domain.client;

import com.mycompany.rutaexpress.estructures.lists.List;
import com.mycompany.rutaexpress.estructures.lists.Node;
import java.time.LocalDate;

public class Cliente {
    private String documento;
    private String tipoCliente;
    private String nombre;
    private String telefono;
    private String correo;
    private String direccion;
    private String municipio;
    private LocalDate fechaRegistro;
    private final List guias;

    public Cliente(String documento, String tipoCliente, String nombre,
            String telefono, String correo, String direccion, String municipio) {
        this.documento = documento;
        this.tipoCliente = tipoCliente;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.municipio = municipio;
        this.fechaRegistro = LocalDate.now();
        this.guias = new List();
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

    public void agregarGuia(String guia) {
        if (guia == null || guia.trim().isEmpty()) return;
        Node actual = guias.getHead();
        while (actual != null) {
            if (guia.trim().equalsIgnoreCase((String) actual.getData())) return;
            actual = actual.getLink();
        }
        guias.AddLast(guia.trim());
    }

    public List getGuias() {
        return guias;
    }

    @Override
    public String toString() {
        return "Documento: " + documento + " | Tipo: " + tipoCliente
                + " | Nombre: " + nombre + " | Teléfono: " + telefono
                + " | Correo: " + correo + " | Dirección: " + direccion
                + " | Municipio: " + municipio + " | Registro: " + fechaRegistro
                + " | Guías: " + guias.Size();
    }
}
