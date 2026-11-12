package com.mycompany.rutaexpress.domain.parcel;

import com.mycompany.rutaexpress.estructures.trees.ArbolBinario;

public class PaqueteManager {
    private final ArbolBinario<Paquete> paquetes;
    private String ultimoError;

    public PaqueteManager() {
        paquetes = new ArbolBinario<>((a, b) -> a.getGuia().compareToIgnoreCase(b.getGuia()));
        ultimoError = "";
    }

    public boolean registrar(Paquete paquete) {
        ultimoError = "";
        if (paquete == null) {
            ultimoError = "El paquete es obligatorio.";
            return false;
        }
        if (paquete.getGuia() == null || paquete.getGuia().trim().isEmpty()) {
            ultimoError = "La guía es obligatoria.";
            return false;
        }
        if (buscarPorGuia(paquete.getGuia()) != null) {
            ultimoError = "Ya existe un paquete con guía " + paquete.getGuia() + ".";
            return false;
        }
        paquetes.insertar(paquete);
        return true;
    }

    public Paquete buscarPorGuia(String guia) {
        if (guia == null) return null;
        return paquetes.buscar(p -> p.getGuia().equalsIgnoreCase(guia.trim()));
    }

    public String listar() {
        StringBuilder resultado = new StringBuilder();
        paquetes.recorrerEnOrden(p -> resultado.append(p).append("\n\n"));
        return resultado.length() == 0 ? "No hay paquetes registrados." : resultado.toString();
    }

    public String listarPorEstado(String estado) {
        StringBuilder resultado = new StringBuilder();
        paquetes.recorrerEnOrden(p -> {
            if (p.getEstado().equalsIgnoreCase(estado)) {
                resultado.append(p.getGuia()).append(" | ").append(p.getEstado())
                        .append(" | ").append(p.getSedeOrigen()).append(" -> ")
                        .append(p.getSedeDestino()).append(" | $")
                        .append(String.format("%.0f", p.getCostoEnvio())).append("\n");
            }
        });
        return resultado.length() == 0 ? "No hay paquetes en estado " + estado + "." : resultado.toString();
    }
    public int cantidad() { return paquetes.cantidad(); }
    public int altura() { return paquetes.altura(); }
    public int contarHojas() { return paquetes.contarHojas(); }
    public String guiaMenor() { return extremo(true); }
    public String guiaMayor() { return extremo(false); }

    private String extremo(boolean menor) {
        final Paquete[] resultado = {null};
        paquetes.recorrerEnOrden(p -> {
            if (resultado[0] == null) resultado[0] = p;
            else if (!menor) resultado[0] = p;
        });
        return resultado[0] == null ? "" : resultado[0].getGuia();
    }
    public String getUltimoError() { return ultimoError; }
}
