package com.mycompany.rutaexpress;

import java.time.LocalDateTime;

public class NodoHistorial {
    private LocalDateTime fechaHora;
    private String codigoSede;
    private String descripcion;
    private String responsable;
    private NodoHistorial sig;

    public NodoHistorial(LocalDateTime fechaHora, String codigoSede,
            String descripcion, String responsable) {
        this.fechaHora = fechaHora;
        this.codigoSede = codigoSede;
        this.descripcion = descripcion;
        this.responsable = responsable;
        this.sig = null;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getCodigoSede() {
        return codigoSede;
    }

    public void setCodigoSede(String codigoSede) {
        this.codigoSede = codigoSede;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public NodoHistorial getSig() {
        return sig;
    }

    public void setSig(NodoHistorial sig) {
        this.sig = sig;
    }

    @Override
    public String toString() {
        return fechaHora + " | " + codigoSede + " | " + descripcion
                + " | Responsable: " + responsable;
    }
}
