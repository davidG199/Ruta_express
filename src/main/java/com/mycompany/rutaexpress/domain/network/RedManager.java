package com.mycompany.rutaexpress.domain.network;

import com.mycompany.rutaexpress.estructures.graphs.GrafoRed;
import com.mycompany.rutaexpress.estructures.lists.List;
import com.mycompany.rutaexpress.estructures.lists.Node;
import com.mycompany.rutaexpress.files.GestorArchivos;
import com.mycompany.rutaexpress.files.GestorNotificaciones;

/**
 * Gestiona la red de sedes, las vías y la bodega de cada sede.
 */
public class RedManager {
    private GrafoRed red;
    private final GestorArchivos archivos;
    private final GestorNotificaciones notificador;
    private String ultimoError;

    public RedManager() {
        this(new GestorArchivos(), new GestorNotificaciones());
    }

    public RedManager(GestorArchivos archivos, GestorNotificaciones notificador) {
        this.archivos = archivos == null ? new GestorArchivos() : archivos;
        this.notificador = notificador == null ? new GestorNotificaciones() : notificador;
        this.red = new GrafoRed();
        this.ultimoError = "";
    }

    public int cargarRed(String ruta) {
        red = new GrafoRed();
        String[] lineas = archivos.leerLineas(ruta);
        for (String linea : lineas) {
            String[] columnas = linea.split(";", -1);
            if (columnas.length == 0) {
                continue;
            }
            try {
                if (columnas[0].equalsIgnoreCase("SEDE") && columnas.length >= 9) {
                    String[] datos = new String[8];
                    System.arraycopy(columnas, 1, datos, 0, 8);
                    registrarSede(datos);
                } else if (columnas[0].equalsIgnoreCase("VIA") && columnas.length >= 8) {
                    String[] datos = new String[7];
                    System.arraycopy(columnas, 1, datos, 0, 7);
                    registrarVia(datos);
                }
            } catch (Exception e) {
                registrarError(ruta, "Línea inválida: " + linea);
            }
        }
        return red.getCantidadSedes();
    }

    public boolean registrarSede(Sede sede) {
        ultimoError = "";
        if (sede == null || sede.getCodigo() == null || sede.getCodigo().trim().isEmpty()) {
            ultimoError = "La sede y su código son obligatorios.";
            return false;
        }
        if (red.buscarSede(sede.getCodigo()) != null) {
            ultimoError = "Ya existe la sede " + sede.getCodigo() + ".";
            return false;
        }
        red.agregarSede(sede);
        return true;
    }

    public boolean registrarSede(String[] datos) {
        if (datos == null || datos.length < 8 || datos[0].trim().isEmpty()) {
            ultimoError = "Faltan datos de la sede.";
            return false;
        }
        try {
            Sede sede = new Sede(datos[0].trim(), datos[1].trim(), datos[2].trim(),
                    datos[3].trim(), datos[4].trim(), datos[5].trim(),
                    datos[6].trim(), numero(datos[7]));
            return registrarSede(sede);
        } catch (NumberFormatException e) {
            ultimoError = "Datos numéricos inválidos en la sede.";
            return false;
        }
    }

    public boolean registrarVia(Via via) {
        ultimoError = "";
        if (via == null) {
            ultimoError = "La vía es obligatoria.";
            return false;
        }
        if (!red.agregarVia(via.getOrigen(), via.getDestino(), via)) {
            ultimoError = "No se pudo registrar la vía.";
            return false;
        }
        return true;
    }

    public boolean registrarVia(String[] datos) {
        if (datos == null || datos.length < 7) {
            ultimoError = "Faltan datos de la vía.";
            return false;
        }
        try {
            Via via = new Via(datos[0].trim(), datos[1].trim(), datos[2].trim(),
                    datos[3].trim(), numero(datos[4]), numero(datos[5]), numero(datos[6]));
            return registrarVia(via);
        } catch (NumberFormatException e) {
            ultimoError = "Datos numéricos inválidos en la vía.";
            return false;
        }
    }

    public boolean cambiarEstadoVia(String origen, String destino, String estado) {
        return red.cambiarEstadoVia(origen, destino, estado);
    }

    public String cambiarEstadoViaConNotificacion(String origen, String destino, String estado) {
        if (!cambiarEstadoVia(origen, destino, estado)) {
            return "No se encontró la vía " + origen + " - " + destino + ".";
        }
        StringBuilder resultado = new StringBuilder("Vía " + origen + " - " + destino
                + " cambiada a " + estado + ".");
        if (estado != null && estado.equalsIgnoreCase("CERRADA")) {
            int notificadas = notificarSedesAisladas();
            resultado.append("\nSedes aisladas notificadas: ").append(notificadas).append(".");
        }
        return resultado.toString();
    }

    public int notificarSedesAisladas() {
        List aisladas = red.sedesAisladas();
        int contador = 0;
        for (Node nodo = aisladas.getHead(); nodo != null; nodo = nodo.getLink()) {
            Sede sede = (Sede) nodo.getData();
            contador++;
            notificador.notificarSede(sede.getCorreoCoordinador(),
                    "Sede aislada por cierre de vía",
                    "La sede " + sede.getCodigo() + " (" + sede.getNombre()
                    + ") quedó aislada de la red principal.");
        }
        return contador;
    }

    public boolean existeCamino(String origen, String destino) {
        return red.existeCamino(origen, destino);
    }

    public Ruta calcularRutaMasCorta(String origen, String destino) {
        return red.rutaMasCorta(origen, destino);
    }

    public String consultarMejorRuta(String origen, String destino) {
        Ruta ruta = red.rutaMasCorta(origen, destino);
        if (ruta == null) {
            return "No existe una ruta con vías HABILITADAS entre "
                    + origen + " y " + destino + ".";
        }
        return ruta.toString();
    }

    public String mostrarRed() {
        return red.mostrarRed();
    }

    public List sedesAisladasLista() {
        return red.sedesAisladas();
    }

    public String mostrarSedesAisladas() {
        List aisladas = red.sedesAisladas();
        if (aisladas.isEmpty()) {
            return "No hay sedes aisladas.";
        }
        StringBuilder resultado = new StringBuilder();
        for (Node nodo = aisladas.getHead(); nodo != null; nodo = nodo.getLink()) {
            resultado.append(nodo.getData()).append("\n");
        }
        return "Sedes aisladas de la red principal:\n" + resultado;
    }

    public String verBodega(String codigoSede) {
        Sede sede = red.buscarSede(codigoSede);
        if (sede == null) {
            return "No existe la sede " + codigoSede + ".";
        }
        StringBuilder resultado = new StringBuilder();
        resultado.append("BODEGA ").append(sede.getCodigo()).append(" - ")
                .append(sede.getNombre()).append("\n");
        resultado.append("Ocupación: ").append(String.format("%.1f", sede.calcularOcupacion()))
                .append("% (").append(String.format("%.1f", sede.pesoAlmacenado()))
                .append(" de ").append(String.format("%.1f", sede.getCapacidadBodegaKg()))
                .append(" kg)\n");
        if (sede.calcularOcupacion() > 80) {
            resultado.append("ALERTA: la bodega supera el 80 % de ocupación.\n");
        }
        resultado.append("Paquetes en cola: ").append(sede.getDespacho().getTamanio()).append("\n");
        resultado.append("Vías:\n").append(red.mostrarViasDeSede(sede.getCodigo())).append("\n");
        resultado.append("Cola de despacho:\n").append(sede.recorrerDespacho());
        return resultado.toString();
    }

    public Sede buscarSede(String codigo) { return red.buscarSede(codigo); }
    public List listarSedes() { return red.getSedes(); }
    public int cantidadSedes() { return red.getCantidadSedes(); }
    public int cantidadVias() { return red.getCantidadVias(); }
    public GrafoRed getRed() { return red; }
    public String getUltimoError() { return ultimoError; }

    private double numero(String valor) {
        return Double.parseDouble(valor.trim().replace(",", "."));
    }

    private void registrarError(String archivo, String mensaje) {
        notificador.registrarFallo(archivo, mensaje);
    }
}
