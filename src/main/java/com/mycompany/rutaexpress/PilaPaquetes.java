package com.mycompany.rutaexpress;

public class PilaPaquetes {
    private NodoPilaPaquete tope;
    private int tamanio;

    public PilaPaquetes() {
        tope = null;
        tamanio = 0;
    }

    public void apilar(NodoPaquete p) {
        if (p == null) {
            return;
        }
        NodoPilaPaquete nuevo = new NodoPilaPaquete(p);
        nuevo.setSig(tope);
        tope = nuevo;
        tamanio++;
    }

    public NodoPaquete desapilar() {
        if (estaVacia()) {
            return null;
        }
        NodoPaquete p = tope.getPaquete();
        tope = tope.getSig();
        tamanio--;
        return p;
    }

    public NodoPaquete verTope() {
        if (estaVacia()) {
            return null;
        }
        return tope.getPaquete();
    }

    public String recorrer() {
        if (estaVacia()) {
            return "Pila vacía.";
        }
        StringBuilder sb = new StringBuilder();
        NodoPilaPaquete aux = tope;
        int i = 1;
        while (aux != null) {
            sb.append(i).append(". ").append(aux.getPaquete().getGuia())
                    .append(" -> ").append(aux.getPaquete().getSedeDestino())
                    .append("\n");
            aux = aux.getSig();
            i++;
        }
        return sb.toString();
    }

    public NodoPilaPaquete getTope() {
        return tope;
    }

    public int getTamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return tope == null;
    }
}
