package com.mycompany.rutaexpress;

public class ColaPaquetes {
    private NodoColaPaquete frente;
    private NodoColaPaquete fin;
    private int tamanio;

    public ColaPaquetes() {
        frente = null;
        fin = null;
        tamanio = 0;
    }

    public void encolar(NodoPaquete p) {
        if (p == null) {
            return;
        }
        NodoColaPaquete nuevo = new NodoColaPaquete(p);
        if (estaVacia()) {
            frente = nuevo;
            fin = nuevo;
        } else {
            fin.setSig(nuevo);
            fin = nuevo;
        }
        tamanio++;
    }

    public NodoPaquete desencolar() {
        if (estaVacia()) {
            return null;
        }
        NodoPaquete p = frente.getPaquete();
        frente = frente.getSig();
        if (frente == null) {
            fin = null;
        }
        tamanio--;
        return p;
    }

    public NodoPaquete verFrente() {
        if (estaVacia()) {
            return null;
        }
        return frente.getPaquete();
    }

    public String recorrer() {
        if (estaVacia()) {
            return "Cola de despacho vacía.";
        }
        StringBuilder sb = new StringBuilder();
        NodoColaPaquete aux = frente;
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

    public NodoColaPaquete getFrente() {
        return frente;
    }

    public NodoColaPaquete getFin() {
        return fin;
    }

    public int getTamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return frente == null;
    }
}
