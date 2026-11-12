package com.mycompany.rutaexpress;

public class NodoVia {
    private String origen;
    private String destino;
    private String tipoVia;
    private String estado;
    private double distanciaKm;
    private double tiempoMin;
    private double peajes;
    private NodoVia sig;

    public NodoVia(String origen, String destino, String tipoVia, String estado,
            double distanciaKm, double tiempoMin, double peajes) {
        this.origen = origen;
        this.destino = destino;
        this.tipoVia = tipoVia;
        this.estado = estado;
        this.distanciaKm = distanciaKm;
        this.tiempoMin = tiempoMin;
        this.peajes = peajes;
        this.sig = null;
    }

    public boolean estaHabilitada() {
        return estado != null && estado.equalsIgnoreCase("HABILITADA");
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getTipoVia() {
        return tipoVia;
    }

    public void setTipoVia(String tipoVia) {
        this.tipoVia = tipoVia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public double getTiempoMin() {
        return tiempoMin;
    }

    public void setTiempoMin(double tiempoMin) {
        this.tiempoMin = tiempoMin;
    }

    public double getPeajes() {
        return peajes;
    }

    public void setPeajes(double peajes) {
        this.peajes = peajes;
    }

    public NodoVia getSig() {
        return sig;
    }

    public void setSig(NodoVia sig) {
        this.sig = sig;
    }

    @Override
    public String toString() {
        return destino + " [" + tipoVia + ", " + estado + "] "
                + distanciaKm + " km, " + tiempoMin + " min, peajes $" + peajes;
    }
}
