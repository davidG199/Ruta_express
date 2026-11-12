package com.mycompany.rutaexpress.estructures.graphs;

import com.mycompany.rutaexpress.domain.network.Ruta;
import com.mycompany.rutaexpress.domain.network.Sede;
import com.mycompany.rutaexpress.domain.network.Via;
import com.mycompany.rutaexpress.estructures.lists.List;
import com.mycompany.rutaexpress.estructures.lists.Node;

public class GrafoRed {
    private List sedes = new List();
    private List vias = new List();

    public void agregarSede(Sede sede) {
        sedes.AddLast(sede);
    }

    public Sede buscarSede(String codigo) {
        if (codigo == null) return null;
        for (Node node = sedes.getHead(); node != null; node = node.getLink()) {
            Sede sede = (Sede) node.getData();
            if (sede.getCodigo().equalsIgnoreCase(codigo.trim())) return sede;
        }
        return null;
    }

    public boolean agregarVia(String origen, String destino, Via via) {
        if (via == null || buscarSede(origen) == null || buscarSede(destino) == null
                || buscarVia(origen, destino) != null) return false;
        vias.AddLast(new Via(origen, destino, via.getTipoVia(), via.getEstado(),
                via.getDistanciaKm(), via.getTiempoMin(), via.getPeajes()));
        vias.AddLast(new Via(destino, origen, via.getTipoVia(), via.getEstado(),
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
        for (Node node = vias.getHead(); node != null; node = node.getLink()) {
            Via via = (Via) node.getData();
            if (via.getOrigen().equalsIgnoreCase(origen.trim())
                    && via.getDestino().equalsIgnoreCase(destino.trim())) return via;
        }
        return null;
    }

    public boolean existeCamino(String origen, String destino) {
        int n = sedes.Size();
        int inicio = indiceDe(origen);
        int fin = indiceDe(destino);
        if (inicio < 0 || fin < 0) return false;
        boolean[] visitados = new boolean[n];
        return existeCamino(inicio, fin, visitados);
    }

    private boolean existeCamino(int actual, int fin, boolean[] visitados) {
        if (actual == fin) return true;
        visitados[actual] = true;
        Sede sede = sedeEn(actual);
        for (Node node = vias.getHead(); node != null; node = node.getLink()) {
            Via via = (Via) node.getData();
            int vecino = indiceDe(via.getDestino());
            if (via.getOrigen().equalsIgnoreCase(sede.getCodigo())
                    && via.estaHabilitada() && vecino >= 0 && !visitados[vecino]
                    && existeCamino(vecino, fin, visitados)) return true;
        }
        return false;
    }

    public Ruta rutaMasCorta(String origen, String destino) {
        int n = sedes.Size();
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
            for (Node node = vias.getHead(); node != null; node = node.getLink()) {
                Via via = (Via) node.getData();
                if (!via.estaHabilitada()
                        || !via.getOrigen().equalsIgnoreCase(sedeEn(actual).getCodigo())) continue;
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
        ruta.agregarTramo(sedeEn(inicio).getCodigo(), 0, 0, 0);
        for (int i = cantidad - 1; i > 0; i--) {
            Via via = buscarVia(sedeEn(camino[i]).getCodigo(),
                    sedeEn(camino[i - 1]).getCodigo());
            ruta.agregarTramo(sedeEn(camino[i - 1]).getCodigo(),
                    via.getDistanciaKm(), via.getTiempoMin(), via.getPeajes());
        }
        return ruta;
    }

    private int indiceDe(String codigo) {
        if (codigo == null) return -1;
        for (int i = 0; i < sedes.Size(); i++) {
            if (sedeEn(i).getCodigo().equalsIgnoreCase(codigo.trim())) return i;
        }
        return -1;
    }

    private Sede sedeEn(int indice) {
        Node actual = sedes.getHead();
        for (int i = 0; actual != null; i++, actual = actual.getLink()) {
            if (i == indice) return (Sede) actual.getData();
        }
        return null;
    }

    public List getSedes() { return sedes; }
    public int getCantidadSedes() { return sedes.Size(); }
    public int getCantidadVias() { return vias.Size() / 2; }
}
