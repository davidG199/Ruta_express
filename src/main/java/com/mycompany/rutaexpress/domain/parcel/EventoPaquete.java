package com.mycompany.rutaexpress.domain.parcel;

import java.time.LocalDateTime;

/**
 * Evento del historial de un paquete.
 */
public class EventoPaquete {
    private final LocalDateTime fechaHora;
    private final String codigoSede;
    private final String descripcion;
    private final String responsable;

    public EventoPaquete(LocalDateTime fechaHora, String codigoSede,
            String descripcion, String responsable) {
        this.fechaHora = fechaHora;
        this.codigoSede = codigoSede == null ? "" : codigoSede;
        this.descripcion = descripcion == null ? "" : descripcion;
        this.responsable = responsable == null ? "Sistema" : responsable;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getCodigoSede() {
        return codigoSede;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getResponsable() {
        return responsable;
    }

    @Override
    public String toString() {
        return fechaHora + " | " + codigoSede + " | " + descripcion
                + " | Responsable: " + responsable;
    }
}
