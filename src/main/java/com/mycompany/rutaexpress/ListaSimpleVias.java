package com.mycompany.rutaexpress;

public class ListaSimpleVias {
    private NodoVia primero;
    private int tamanio;

    public ListaSimpleVias() {
        primero = null;
        tamanio = 0;
    }

    public void agregar(NodoVia v) {
        if (v == null) {
            return;
        }
        NodoVia nuevo = v;
        if (primero == null) {
            primero = nuevo;
        } else {
            NodoVia aux = primero;
            while (aux.getSig() != null) {
                aux = aux.getSig();
            }
            aux.setSig(nuevo);
        }
        tamanio++;
    }

    public NodoVia buscar(String destino) {
        if (destino == null) {
            return null;
        }
        NodoVia aux = primero;
        while (aux != null) {
            if (aux.getDestino().equalsIgnoreCase(destino.trim())) {
                return aux;
            }
            aux = aux.getSig();
        }
        return null;
    }

    public String recorrer() {
        if (primero == null) {
            return "Sin vías registradas.";
        }
        StringBuilder sb = new StringBuilder();
        NodoVia aux = primero;
        while (aux != null) {
            sb.append("  -> ").append(aux.toString()).append("\n");
            aux = aux.getSig();
        }
        return sb.toString();
    }

    public NodoVia getPrimero() {
        return primero;
    }

    public int getTamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return primero == null;
    }
}
