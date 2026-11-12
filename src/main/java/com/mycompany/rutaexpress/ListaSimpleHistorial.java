package com.mycompany.rutaexpress;

public class ListaSimpleHistorial {
    private NodoHistorial primero;
    private int tamanio;

    public ListaSimpleHistorial() {
        primero = null;
        tamanio = 0;
    }

    public void agregar(NodoHistorial e) {
        if (e == null) {
            return;
        }
        if (primero == null) {
            primero = e;
        } else {
            NodoHistorial aux = primero;
            while (aux.getSig() != null) {
                aux = aux.getSig();
            }
            aux.setSig(e);
        }
        tamanio++;
    }

    public String recorrer() {
        if (primero == null) {
            return "Sin eventos registrados.";
        }
        StringBuilder sb = new StringBuilder();
        NodoHistorial aux = primero;
        while (aux != null) {
            sb.append("- ").append(aux.toString()).append("\n");
            aux = aux.getSig();
        }
        return sb.toString();
    }

    public NodoHistorial getPrimero() {
        return primero;
    }

    public int getTamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return primero == null;
    }
}
