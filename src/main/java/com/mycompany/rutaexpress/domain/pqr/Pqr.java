package com.mycompany.rutaexpress.domain.pqr;

import com.mycompany.rutaexpress.estructures.lists.List;
import com.mycompany.rutaexpress.estructures.lists.Node;

import java.time.LocalDateTime;

public class Pqr {
    private String radicado;
    private String documentoCliente;
    private String numeroGuia;
    private String tipo;
    private String estado;
    private String descripcion;
    private LocalDateTime fechaRadicacion;
    private final List seguimientos;

    public Pqr(String radicado, String documentoCliente, String numeroGuia,
            String tipo, String descripcion, LocalDateTime fechaRadicacion) {
        this.radicado = radicado;
        this.documentoCliente = documentoCliente;
        this.numeroGuia = numeroGuia == null ? "" : numeroGuia;
        this.tipo = tipo;
        this.estado = "ABIERTA";
        this.descripcion = descripcion;
        this.fechaRadicacion = fechaRadicacion;
        this.seguimientos = new List();
    }

    public void agregarSeguimiento(String funcionario, String nota) {
        seguimientos.AddLast(new Seguimiento(LocalDateTime.now(),
                funcionario == null ? "Funcionario" : funcionario,
                nota == null ? "" : nota));
    }

    public void cerrar(String respuesta) {
        if (respuesta != null && !respuesta.trim().isEmpty()) {
            agregarSeguimiento("Cierre", respuesta.trim());
        }
        estado = "CERRADA";
    }

    public void cerrar() {
        cerrar("");
    }

    public List getSeguimientos() {
        return seguimientos;
    }

    public String seguimientosTexto() {
        if (seguimientos.isEmpty()) {
            return "Sin seguimientos.";
        }
        StringBuilder resultado = new StringBuilder();
        Node actual = seguimientos.getHead();
        while (actual != null) {
            resultado.append("- ").append(actual.getData()).append("\n");
            actual = actual.getLink();
        }
        return resultado.toString();
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

    @Override
    public String toString() {
        return "Radicado: " + radicado + " | Tipo: " + tipo + " | Estado: " + estado
                + "\nCliente: " + documentoCliente + " | Guía: "
                + (numeroGuia.isEmpty() ? "(sin guía)" : numeroGuia)
                + "\nFecha: " + fechaRadicacion
                + "\nDescripción: " + descripcion
                + "\nSeguimientos:\n" + seguimientosTexto();
    }
}
