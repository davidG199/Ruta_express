package com.mycompany.rutaexpress.domain.pqr;

import java.time.LocalDateTime;

public class Pqr {
    private String radicado;
    private String documentoCliente;
    private String numeroGuia;
    private String tipo;
    private String estado;
    private String descripcion;
    private LocalDateTime fechaRadicacion;

    public Pqr(String radicado, String documentoCliente, String numeroGuia,
            String tipo, String descripcion, LocalDateTime fechaRadicacion) {
        this.radicado = radicado;
        this.documentoCliente = documentoCliente;
        this.numeroGuia = numeroGuia == null ? "" : numeroGuia;
        this.tipo = tipo;
        this.estado = "ABIERTA";
        this.descripcion = descripcion;
        this.fechaRadicacion = fechaRadicacion;
    }

    public void cerrar() {
        estado = "CERRADA";
    }

    public String getRadicado() { return radicado; }
    public void setRadicado(String radicado) { this.radicado = radicado; }
    public String getDocumentoCliente() { return documentoCliente; }
    public void setDocumentoCliente(String documentoCliente) { this.documentoCliente = documentoCliente; }
    public String getNumeroGuia() { return numeroGuia; }
    public void setNumeroGuia(String numeroGuia) { this.numeroGuia = numeroGuia; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFechaRadicacion() { return fechaRadicacion; }
    public void setFechaRadicacion(LocalDateTime fechaRadicacion) { this.fechaRadicacion = fechaRadicacion; }
}
