package com.mycompany.rutaexpress.domain.config;

import com.mycompany.rutaexpress.files.GestorArchivos;
import com.mycompany.rutaexpress.files.GestorNotificaciones;

/**
 * Centraliza la configuración general de la aplicación.
 */
public class ConfiguracionManager {
    private final GestorArchivos archivos;
    private final GestorNotificaciones notificador;
    private double precioGalon;

    public ConfiguracionManager() {
        this(new GestorArchivos(), new GestorNotificaciones());
    }

    public ConfiguracionManager(GestorArchivos archivos, GestorNotificaciones notificador) {
        this.archivos = archivos == null ? new GestorArchivos() : archivos;
        this.notificador = notificador == null ? new GestorNotificaciones() : notificador;
        this.precioGalon = 15800;
    }

    public void cargar() {
        notificador.cargarConfiguracion();
        String[] lineas = archivos.leerLineas("config.txt");
        for (String linea : lineas) {
            String[] partes = linea.split("=", 2);
            if (partes.length != 2) {
                continue;
            }
            if (partes[0].trim().equalsIgnoreCase("precioGalon")) {
                try {
                    double valor = Double.parseDouble(partes[1].trim().replace(",", "."));
                    if (valor > 0) {
                        precioGalon = valor;
                    }
                } catch (NumberFormatException e) {
                    notificador.registrarFallo("config.txt", "Precio de galón inválido");
                }
            }
        }
    }

    public double getPrecioGalon() {
        return precioGalon;
    }

    public void setPrecioGalon(double precioGalon) {
        if (precioGalon > 0) {
            this.precioGalon = precioGalon;
        }
    }

    public GestorArchivos getArchivos() {
        return archivos;
    }

    public GestorNotificaciones getNotificador() {
        return notificador;
    }
}
