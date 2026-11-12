package com.mycompany.rutaexpress;

public class GrafoRed {
    private ListaSimpleSedes sedes;

    public GrafoRed() {
        sedes = new ListaSimpleSedes();
    }

    public void agregarSede(NodoSede s) {
        sedes.agregar(s);
    }

    public NodoSede buscarSede(String codigo) {
        return sedes.buscar(codigo);
    }

    public boolean agregarVia(String origen, String destino, NodoVia v) {
        NodoSede o = buscarSede(origen);
        NodoSede d = buscarSede(destino);
        if (o == null || d == null || v == null) {
            return false;
        }
        if (o.buscarVia(destino) != null) {
            return false;
        }
        NodoVia ida = new NodoVia(o.getCodigo(), d.getCodigo(), v.getTipoVia(),
                v.getEstado(), v.getDistanciaKm(), v.getTiempoMin(), v.getPeajes());
        NodoVia vuelta = new NodoVia(d.getCodigo(), o.getCodigo(), v.getTipoVia(),
                v.getEstado(), v.getDistanciaKm(), v.getTiempoMin(), v.getPeajes());
        o.agregarVia(ida);
        d.agregarVia(vuelta);
        return true;
    }

    public boolean cambiarEstadoVia(String origen, String destino, String estado) {
        NodoSede o = buscarSede(origen);
        NodoSede d = buscarSede(destino);
        if (o == null || d == null) {
            return false;
        }
        NodoVia ida = o.buscarVia(destino);
        NodoVia vuelta = d.buscarVia(origen);
        if (ida == null || vuelta == null) {
            return false;
        }
        ida.setEstado(estado);
        vuelta.setEstado(estado);
        return true;
    }

    public boolean existeCamino(String origen, String destino) {
        NodoSede o = buscarSede(origen);
        NodoSede d = buscarSede(destino);
        if (o == null || d == null) {
            return false;
        }
        boolean[] visitados = new boolean[sedes.getTamanio()];
        return existeCamino(o.getCodigo(), d.getCodigo(), visitados);
    }

    private boolean existeCamino(String actual, String destino, boolean[] visitados) {
        int indice = indiceDe(actual);
        if (indice < 0 || visitados[indice]) {
            return false;
        }
        if (actual.equalsIgnoreCase(destino)) {
            return true;
        }
        visitados[indice] = true;
        NodoSede sede = sedes.obtener(indice);
        NodoVia aux = sede.getVias().getPrimero();
        while (aux != null) {
            if (aux.estaHabilitada() && existeCamino(aux.getDestino(), destino, visitados)) {
                return true;
            }
            aux = aux.getSig();
        }
        return false;
    }

    public Ruta rutaMasCorta(String origen, String destino) {
        int n = sedes.getTamanio();
        int inicio = indiceDe(origen);
        int fin = indiceDe(destino);
        if (inicio < 0 || fin < 0 || n == 0) {
            return null;
        }

        String[] codigos = new String[n];
        for (int i = 0; i < n; i++) {
            codigos[i] = sedes.obtener(i).getCodigo();
        }

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
            if (actual == -1) {
                break;
            }
            if (actual == fin) {
                break;
            }
            visitados[actual] = true;

            NodoSede sede = sedes.obtener(actual);
            NodoVia via = sede.getVias().getPrimero();
            while (via != null) {
                if (via.estaHabilitada()) {
                    int vecino = indiceDe(via.getDestino());
                    if (vecino >= 0 && !visitados[vecino]) {
                        double nueva = distancias[actual] + via.getDistanciaKm();
                        if (nueva < distancias[vecino]) {
                            distancias[vecino] = nueva;
                            anteriores[vecino] = actual;
                        }
                    }
                }
                via = via.getSig();
            }
        }

        if (distancias[fin] == Double.MAX_VALUE) {
            return null;
        }

        int cantidad = 0;
        int[] camino = new int[n];
        for (int at = fin; at != -1; at = anteriores[at]) {
            camino[cantidad] = at;
            cantidad++;
            if (at == inicio) {
                break;
            }
        }

        Ruta ruta = new Ruta();
        ruta.agregarTramo(codigos[inicio], 0, 0, 0);
        for (int i = cantidad - 1; i > 0; i--) {
            String desde = codigos[camino[i]];
            String hasta = codigos[camino[i - 1]];
            NodoVia tramo = buscarSede(desde).buscarVia(hasta);
            if (tramo != null) {
                ruta.agregarTramo(hasta, tramo.getDistanciaKm(),
                        tramo.getTiempoMin(), tramo.getPeajes());
            }
        }
        return ruta;
    }

    public ListaSimpleSedes sedesAisladas() {
        ListaSimpleSedes aisladas = new ListaSimpleSedes();
        int n = sedes.getTamanio();
        if (n == 0) {
            return aisladas;
        }

        int[] componente = new int[n];
        int[] tamanios = new int[n];
        for (int i = 0; i < n; i++) {
            componente[i] = -1;
        }

        int totalComponentes = 0;
        for (int i = 0; i < n; i++) {
            if (componente[i] == -1) {
                int[] cuenta = new int[1];
                marcarComponente(i, componente, totalComponentes, cuenta);
                tamanios[totalComponentes] = cuenta[0];
                totalComponentes++;
            }
        }

        int mayor = 0;
        for (int i = 1; i < totalComponentes; i++) {
            if (tamanios[i] > tamanios[mayor]) {
                mayor = i;
            }
        }

        for (int i = 0; i < n; i++) {
            if (componente[i] != mayor) {
                aisladas.agregar(sedes.obtener(i));
            }
        }
        return aisladas;
    }

    private void marcarComponente(int indice, int[] componente, int id, int[] cuenta) {
        if (indice < 0 || componente[indice] != -1) {
            return;
        }
        componente[indice] = id;
        cuenta[0]++;
        NodoSede sede = sedes.obtener(indice);
        NodoVia via = sede.getVias().getPrimero();
        while (via != null) {
            if (via.estaHabilitada()) {
                marcarComponente(indiceDe(via.getDestino()), componente, id, cuenta);
            }
            via = via.getSig();
        }
    }

    public String mostrarRed() {
        if (sedes.estaVacia()) {
            return "No hay sedes registradas.";
        }
        StringBuilder sb = new StringBuilder("RED DE SEDES\n\n");
        NodoSede sede = sedes.getPrimero();
        while (sede != null) {
            sb.append(sede.toString()).append("\n");
            NodoVia via = sede.getVias().getPrimero();
            while (via != null) {
                if (sede.getCodigo().compareToIgnoreCase(via.getDestino()) < 0) {
                    sb.append("  -> ").append(via.toString()).append("\n");
                }
                via = via.getSig();
            }
            sb.append("\n");
            sede = sede.getSig();
        }
        return sb.toString();
    }

    private int indiceDe(String codigo) {
        if (codigo == null) {
            return -1;
        }
        NodoSede aux = sedes.getPrimero();
        int i = 0;
        while (aux != null) {
            if (aux.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return i;
            }
            aux = aux.getSig();
            i++;
        }
        return -1;
    }

    public ListaSimpleSedes getSedes() {
        return sedes;
    }

    public int getCantidadSedes() {
        return sedes.getTamanio();
    }

    public int getCantidadVias() {
        int total = 0;
        NodoSede sede = sedes.getPrimero();
        while (sede != null) {
            total += sede.getVias().getTamanio();
            sede = sede.getSig();
        }
        return total / 2;
    }
}
