package com.mycompany.rutaexpress.domain.pqr;

import com.mycompany.rutaexpress.estructures.queues.Cola;

public class PqrManager {
    private final Cola<Pqr> pqrs;
    private String ultimoError;

    public PqrManager() {
        pqrs = new Cola<>();
        ultimoError = "";
    }

    public boolean radicar(Pqr pqr) {
        ultimoError = "";
        if (pqr == null) {
            ultimoError = "La PQR es obligatoria.";
            return false;
        }
        if (pqr.getRadicado() == null || pqr.getRadicado().trim().isEmpty()) {
            ultimoError = "El radicado es obligatorio.";
            return false;
        }
        if (buscarPorRadicado(pqr.getRadicado()) != null) {
            ultimoError = "Ya existe la PQR " + pqr.getRadicado() + ".";
            return false;
        }
        pqrs.encolar(pqr);
        return true;
    }

    public Pqr atender() {
        Pqr pqr = pqrs.desencolar();
        if (pqr != null && "ABIERTA".equalsIgnoreCase(pqr.getEstado())) {
            pqrs.encolar(pqr);
        }
        return pqr;
    }

    public Pqr verFrente() { return pqrs.verFrente(); }

    public int contarAbiertasPorTipo(String tipo) {
        int contador = 0;
        var nodo = pqrs.getFrente();
        while (nodo != null) {
            Pqr pqr = nodo.getValor();
            if (pqr.getEstado().equalsIgnoreCase("ABIERTA")
                    && pqr.getTipo().equalsIgnoreCase(tipo)) contador++;
            nodo = nodo.getSiguiente();
        }
        return contador;
    }

    public String listar() {
        if (pqrs.estaVacia()) return "No hay PQR pendientes.";
        StringBuilder resultado = new StringBuilder();
        var nodo = pqrs.getFrente();
        int i = 1;
        while (nodo != null) {
            Pqr pqr = nodo.getValor();
            resultado.append(i++).append(". ").append(pqr.getRadicado()).append(" | ")
                    .append(pqr.getTipo()).append(" | ").append(pqr.getEstado())
                    .append(" | Cliente: ").append(pqr.getDocumentoCliente()).append("\n");
            nodo = nodo.getSiguiente();
        }
        return resultado.toString();
    }

    public Cola<Pqr> getPqrs() { return pqrs; }
    public String getUltimoError() { return ultimoError; }

    public Pqr buscarPorRadicado(String radicado) {
        if (radicado == null) return null;
        for (var nodo = pqrs.getFrente(); nodo != null; nodo = nodo.getSiguiente()) {
            if (nodo.getValor().getRadicado().equalsIgnoreCase(radicado.trim())) {
                return nodo.getValor();
            }
        }
        return null;
    }
}
