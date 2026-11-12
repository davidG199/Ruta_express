package com.mycompany.rutaexpress.domain.network;

import com.mycompany.rutaexpress.estructures.graphs.GrafoRed;
import com.mycompany.rutaexpress.estructures.lists.List;

public class RedManager {
    private final GrafoRed red;
    private String ultimoError;

    public RedManager() {
        red = new GrafoRed();
        ultimoError = "";
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

    public boolean cambiarEstadoVia(String origen, String destino, String estado) {
        return red.cambiarEstadoVia(origen, destino, estado);
    }

    public boolean existeCamino(String origen, String destino) {
        return red.existeCamino(origen, destino);
    }

    public Ruta calcularRutaMasCorta(String origen, String destino) {
        return red.rutaMasCorta(origen, destino);
    }

    public Sede buscarSede(String codigo) { return red.buscarSede(codigo); }
    public List listarSedes() { return red.getSedes(); }
    public int cantidadSedes() { return red.getCantidadSedes(); }
    public int cantidadVias() { return red.getCantidadVias(); }
    public GrafoRed getRed() { return red; }
    public String getUltimoError() { return ultimoError; }
}
