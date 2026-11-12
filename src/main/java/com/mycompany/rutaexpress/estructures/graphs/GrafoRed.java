package com.mycompany.rutaexpress.estructures.graphs;

import com.mycompany.rutaexpress.domain.network.Ruta;
import com.mycompany.rutaexpress.domain.network.Sede;
import com.mycompany.rutaexpress.domain.network.Via;
import com.mycompany.rutaexpress.estructures.lists.ListaSimple;
import com.mycompany.rutaexpress.estructures.lists.Nodo;

public class GrafoRed {
    private ListaSimple<Sede> sedes = new ListaSimple<>();
    private ListaSimple<Via> vias = new ListaSimple<>();

    public void agregarSede(Sede sede) {
        sedes.agregar(sede);
    }

    public Sede buscarSede(String codigo) {
        if (codigo == null) return null;
        return sedes.buscar(sede -> sede.getCodigo().equalsIgnoreCase(codigo.trim()));
    }

    public boolean agregarVia(String origen, String destino, Via via) {
        if (via == null || buscarSede(origen) == null || buscarSede(destino) == null
                || buscarVia(origen, destino) != null) return false;
        vias.agregar(new Via(origen, destino, via.getTipoVia(), via.getEstado(),
                via.getDistanciaKm(), via.getTiempoMin(), via.getPeajes()));
        vias.agregar(new Via(destino, origen, via.getTipoVia(), via.getEstado(),
                via.getDistanciaKm(), via.getTiempoMin(), via.getPeajes()));
        return true;
    }

    public boolean cambiarEstadoVia(String origen, String destino, String estado) {
        Via ida = buscarVia(origen, destino);
        Via vuelta = buscarVia(destino, origen);
        if (ida == null || vuelta == null) return false;
        ida.setEstado(estado);
        vuelta.setEstado(estado);
        return true;
    }

    private Via buscarVia(String origen, String destino) {
        if (origen == null || destino == null) return null;
        for (Nodo<Via> nodo = vias.getPrimero(); nodo != null; nodo = nodo.getSiguiente()) {
            Via via = nodo.getValor();
            if (via.getOrigen().equalsIgnoreCase(origen.trim())
                    && via.getDestino().equalsIgnoreCase(destino.trim())) return via;
        }
        return null;
    }

    public boolean existeCamino(String origen, String destino) {
        int n = sedes.getTamanio();
        int inicio = indiceDe(origen);
        int fin = indiceDe(destino);
        if (inicio < 0 || fin < 0) return false;
        boolean[] visitados = new boolean[n];
        return existeCamino(inicio, fin, visitados);
    }

    private boolean existeCamino(int actual, int fin, boolean[] visitados) {
        if (actual == fin) return true;
        visitados[actual] = true;
        Sede sede = sedes.obtener(actual);
        for (Nodo<Via> nodo = vias.getPrimero(); nodo != null; nodo = nodo.getSiguiente()) {
            Via via = nodo.getValor();
            int vecino = indiceDe(via.getDestino());
            if (via.getOrigen().equalsIgnoreCase(sede.getCodigo())
                    && via.estaHabilitada() && vecino >= 0 && !visitados[vecino]
                    && existeCamino(vecino, fin, visitados)) return true;
        }
        return false;
    }

    public Ruta rutaMasCorta(String origen, String destino) {
        int n = sedes.getTamanio();
        int inicio = indiceDe(origen);
        int fin = indiceDe(destino);
        if (inicio < 0 || fin < 0) return null;
        double[] distancias = new double[n];
        boolean[] visitados = new boolean[n];
        int[] anteriores = new int[n];
        for (int i = 0; i < n; i++) {
            distancias[i] = Double.MAX_VALUE;
            anteriores[i] = -1;
        }
        distancias[inicio] = 0;
        for (int paso = 0; paso < n; paso++) {
            int actual = -1;
            double menor = Double.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (!visitados[i] && distancias[i] < menor) {
                    menor = distancias[i];
                    actual = i;
                }
            }
            if (actual < 0) break;
            visitados[actual] = true;
            for (Nodo<Via> nodo = vias.getPrimero(); nodo != null; nodo = nodo.getSiguiente()) {
                Via via = nodo.getValor();
                if (!via.estaHabilitada()
                        || !via.getOrigen().equalsIgnoreCase(sedes.obtener(actual).getCodigo())) continue;
                int vecino = indiceDe(via.getDestino());
                if (vecino >= 0 && distancias[actual] + via.getDistanciaKm() < distancias[vecino]) {
                    distancias[vecino] = distancias[actual] + via.getDistanciaKm();
                    anteriores[vecino] = actual;
                }
            }
        }
        if (distancias[fin] == Double.MAX_VALUE) return null;
        int[] camino = new int[n];
        int cantidad = 0;
        for (int actual = fin; actual != -1; actual = anteriores[actual]) camino[cantidad++] = actual;
        Ruta ruta = new Ruta();
        ruta.agregarTramo(sedes.obtener(inicio).getCodigo(), 0, 0, 0);
        for (int i = cantidad - 1; i > 0; i--) {
            Via via = buscarVia(sedes.obtener(camino[i]).getCodigo(),
                    sedes.obtener(camino[i - 1]).getCodigo());
            ruta.agregarTramo(sedes.obtener(camino[i - 1]).getCodigo(),
                    via.getDistanciaKm(), via.getTiempoMin(), via.getPeajes());
        }
        return ruta;
    }

    private int indiceDe(String codigo) {
        if (codigo == null) return -1;
        for (int i = 0; i < sedes.getTamanio(); i++) {
            if (sedes.obtener(i).getCodigo().equalsIgnoreCase(codigo.trim())) return i;
        }
        return -1;
    }

    public ListaSimple<Sede> getSedes() { return sedes; }
    public int getCantidadSedes() { return sedes.getTamanio(); }
    public int getCantidadVias() { return vias.getTamanio() / 2; }
}
