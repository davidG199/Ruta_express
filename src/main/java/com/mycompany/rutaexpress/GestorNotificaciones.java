package com.mycompany.rutaexpress;

import java.time.LocalDateTime;

public class GestorNotificaciones {
    private String rutaConfig;
    private String correoEmisor;
    private String clave;
    private String correoCoordinador;
    private GestorArchivos archivos;

    public GestorNotificaciones(GestorArchivos archivos) {
        this.archivos = archivos;
        this.rutaConfig = "config.txt";
        this.correoEmisor = "";
        this.clave = "";
        this.correoCoordinador = "";
    }

    public void cargarConfiguracion() {
        String[] lineas = archivos.leerLineas(rutaConfig);
        for (String linea : lineas) {
            String[] partes = linea.split("=", 2);
            if (partes.length != 2) {
                continue;
            }
            String llave = partes[0].trim().toLowerCase();
            String valor = partes[1].trim();
            switch (llave) {
                case "correoemisor" -> correoEmisor = valor;
                case "clave", "clavecorreo" -> clave = valor;
                case "correocoordinador" -> correoCoordinador = valor;
                default -> {
                }
            }
        }
    }

    public boolean enviarCorreo(String destino, String asunto, String cuerpo) {
        if (correoEmisor.isEmpty() || clave.isEmpty()) {
            registrarFallo(destino, "Cuenta de correo no configurada en " + rutaConfig);
            return false;
        }
        if (destino == null || !destino.contains("@")) {
            registrarFallo(destino, "Correo destino inválido");
            return false;
        }

        String registro = "Fecha: " + LocalDateTime.now()
                + "\nDe: " + correoEmisor
                + "\nPara: " + destino
                + "\nAsunto: " + asunto
                + "\n" + cuerpo
                + "\n--------------------------------------------------";
        archivos.agregarLinea("notificaciones.log", registro);
        return true;
    }

    public void notificarSede(String destino, String asunto, String cuerpo) {
        if (destino == null || destino.trim().isEmpty()) {
            destino = correoCoordinador;
        }
        if (!enviarCorreo(destino, asunto, cuerpo)) {
            registrarFallo(destino, "No se pudo enviar la notificación: " + asunto);
        }
    }

    public void registrarFallo(String destino, String error) {
        archivos.agregarLinea("fallos.log", LocalDateTime.now()
                + " | Destino: " + (destino == null ? "(sin destino)" : destino)
                + " | Error: " + error);
    }

    public String getCorreoEmisor() {
        return correoEmisor;
    }

    public String getCorreoCoordinador() {
        return correoCoordinador;
    }

    public String getRutaConfig() {
        return rutaConfig;
    }

    public void setRutaConfig(String rutaConfig) {
        this.rutaConfig = rutaConfig;
    }
}
