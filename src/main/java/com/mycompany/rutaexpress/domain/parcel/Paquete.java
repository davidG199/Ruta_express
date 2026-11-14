package com.mycompany.rutaexpress.domain.parcel;

import com.mycompany.rutaexpress.estructures.lists.List;
import com.mycompany.rutaexpress.estructures.lists.Node;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Paquete {
    private String numeroGuia;
    private String documentoRemitente;
    private String nombreDestinatario;
    private String telefonoDestinatario;
    private String correoDestinatario;
    private String direccionEntrega;
    private String sedeOrigen;
    private String sedeDestino;
    private double pesoKg;
    private double largoCm;
    private double anchoCm;
    private double altoCm;
    private double valorDeclarado;
    private double costoEnvio;
    private String tipoServicio;
    private String estado;
    private boolean fragil;
    private LocalDateTime fechaAdmision;
    private LocalDate fechaMaximaEntrega;
    private final List historial;

    public Paquete(String numeroGuia, String documentoRemitente, String nombreDestinatario,
            String telefonoDestinatario, String correoDestinatario, String direccionEntrega,
            String sedeOrigen, String sedeDestino, double pesoKg, double largoCm,
            double anchoCm, double altoCm, double valorDeclarado, String tipoServicio,
            boolean fragil) {
        this.numeroGuia = numeroGuia;
        this.documentoRemitente = documentoRemitente;
        this.nombreDestinatario = nombreDestinatario;
        this.telefonoDestinatario = telefonoDestinatario;
        this.correoDestinatario = correoDestinatario;
        this.direccionEntrega = direccionEntrega;
        this.sedeOrigen = sedeOrigen;
        this.sedeDestino = sedeDestino;
        this.pesoKg = pesoKg;
        this.largoCm = largoCm;
        this.anchoCm = anchoCm;
        this.altoCm = altoCm;
        this.valorDeclarado = valorDeclarado;
        this.tipoServicio = tipoServicio;
        this.estado = "RADICADO";
        this.fragil = fragil;
        this.fechaAdmision = LocalDateTime.now();
        this.fechaMaximaEntrega = calcularFechaMaxima();
        this.historial = new List();
    }

    public double calcularPesoVolumetrico() {
        return (largoCm * anchoCm * altoCm) / 5000.0;
    }

    public double calcularPesoFacturable() {
        return Math.max(pesoKg, calcularPesoVolumetrico());
    }

    public double calcularCostoEnvio(double distanciaKm) {
        String tipo = tipoServicio == null ? "ESTANDAR"
                : tipoServicio.trim().toUpperCase().replace(" ", "_");
        double tarifaBase = switch (tipo) {
            case "EXPRESS" -> 15000;
            case "CONTRA_ENTREGA" -> 10000;
            default -> 8000;
        };
        double subtotal = tarifaBase + 1500 * calcularPesoFacturable()
                + 120 * Math.max(distanciaKm, 0) + 0.01 * valorDeclarado;
        if ("CONTRA_ENTREGA".equals(tipo)) {
            subtotal += 0.02 * valorDeclarado;
        }
        costoEnvio = fragil ? subtotal * 1.10 : subtotal;
        return costoEnvio;
    }

    public LocalDate calcularFechaMaxima() {
        String tipo = tipoServicio == null ? "ESTANDAR"
                : tipoServicio.trim().toUpperCase().replace(" ", "_");
        int dias = "EXPRESS".equals(tipo) ? 1 : 3;
        LocalDate base = fechaAdmision == null ? LocalDate.now() : fechaAdmision.toLocalDate();
        return base.plusDays(dias);
    }

    public boolean estaVencido(LocalDate fecha) {
        return fecha != null && fechaMaximaEntrega != null && fecha.isAfter(fechaMaximaEntrega);
    }

    public void agregarEvento(String codigoSede, String descripcion, String responsable) {
        historial.AddLast(new EventoPaquete(LocalDateTime.now(), codigoSede,
                descripcion, responsable));
    }

    public List getHistorial() {
        return historial;
    }

    public String historialTexto() {
        if (historial.isEmpty()) {
            return "Sin eventos registrados.";
        }
        StringBuilder resultado = new StringBuilder();
        Node actual = historial.getHead();
        while (actual != null) {
            resultado.append("- ").append(actual.getData()).append("\n");
            actual = actual.getLink();
        }
        return resultado.toString();
    }

    public String getGuia() {
        return numeroGuia;
    }

    public String getNumeroGuia() {
        return numeroGuia;
    }

    public void setNumeroGuia(String numeroGuia) {
        this.numeroGuia = numeroGuia;
    }

    public String getDocumentoRemitente() {
        return documentoRemitente;
    }

    public void setDocumentoRemitente(String documentoRemitente) {
        this.documentoRemitente = documentoRemitente;
    }

    public String getNombreDestinatario() {
        return nombreDestinatario;
    }

    public void setNombreDestinatario(String nombreDestinatario) {
        this.nombreDestinatario = nombreDestinatario;
    }

    public String getTelefonoDestinatario() {
        return telefonoDestinatario;
    }

    public void setTelefonoDestinatario(String telefonoDestinatario) {
        this.telefonoDestinatario = telefonoDestinatario;
    }

    public String getCorreoDestinatario() {
        return correoDestinatario;
    }

    public void setCorreoDestinatario(String correoDestinatario) {
        this.correoDestinatario = correoDestinatario;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public String getSedeOrigen() {
        return sedeOrigen;
    }

    public void setSedeOrigen(String sedeOrigen) {
        this.sedeOrigen = sedeOrigen;
    }

    public String getSedeDestino() {
        return sedeDestino;
    }

    public void setSedeDestino(String sedeDestino) {
        this.sedeDestino = sedeDestino;
    }

    public double getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(double pesoKg) {
        this.pesoKg = pesoKg;
    }

    public double getLargoCm() {
        return largoCm;
    }

    public void setLargoCm(double largoCm) {
        this.largoCm = largoCm;
    }

    public double getAnchoCm() {
        return anchoCm;
    }

    public void setAnchoCm(double anchoCm) {
        this.anchoCm = anchoCm;
    }

    public double getAltoCm() {
        return altoCm;
    }

    public void setAltoCm(double altoCm) {
        this.altoCm = altoCm;
    }

    public double getValorDeclarado() {
        return valorDeclarado;
    }

    public void setValorDeclarado(double valorDeclarado) {
        this.valorDeclarado = valorDeclarado;
    }

    public double getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(double costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
        this.fechaMaximaEntrega = calcularFechaMaxima();
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isFragil() {
        return fragil;
    }

    public void setFragil(boolean fragil) {
        this.fragil = fragil;
    }

    public LocalDateTime getFechaAdmision() {
        return fechaAdmision;
    }

    public void setFechaAdmision(LocalDateTime fechaAdmision) {
        this.fechaAdmision = fechaAdmision;
        this.fechaMaximaEntrega = calcularFechaMaxima();
    }

    public LocalDate getFechaMaximaEntrega() {
        return fechaMaximaEntrega;
    }

    public void setFechaMaximaEntrega(LocalDate fechaMaximaEntrega) {
        this.fechaMaximaEntrega = fechaMaximaEntrega;
    }

    @Override
    public String toString() {
        return "Guía: " + numeroGuia
                + "\nRemitente: " + documentoRemitente
                + "\nDestinatario: " + nombreDestinatario + " | Tel: " + telefonoDestinatario
                + "\nCorreo: " + correoDestinatario
                + "\nDirección: " + direccionEntrega
                + "\nRuta: " + sedeOrigen + " -> " + sedeDestino
                + "\nPeso: " + pesoKg + " kg | Volumétrico: "
                + String.format("%.2f", calcularPesoVolumetrico()) + " kg | Facturable: "
                + String.format("%.2f", calcularPesoFacturable()) + " kg"
                + "\nMedidas: " + largoCm + " x " + anchoCm + " x " + altoCm + " cm"
                + "\nValor declarado: $" + String.format("%.0f", valorDeclarado)
                + " | Costo envío: $" + String.format("%.0f", costoEnvio)
                + "\nServicio: " + tipoServicio + " | Frágil: " + (fragil ? "Sí" : "No")
                + "\nEstado: " + estado
                + "\nAdmisión: " + fechaAdmision
                + " | Fecha máxima: " + fechaMaximaEntrega;
    }
}
