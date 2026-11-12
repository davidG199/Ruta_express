package com.mycompany.rutaexpress;

public class ListaSimpleSeguimientos {
    private NodoSeguimiento primero;
    private int tamanio;

    public ListaSimpleSeguimientos() {
        primero = null;
        tamanio = 0;
    }

    public void agregar(NodoSeguimiento s) {
        if (s == null) {
            return;
        }
        if (primero == null) {
            primero = s;
        } else {
            NodoSeguimiento aux = primero;
            while (aux.getSig() != null) {
                aux = aux.getSig();
            }
            aux.setSig(s);
        }
        tamanio++;
    }

    public String recorrer() {
        if (primero == null) {
            return "Sin seguimientos.";
        }
        StringBuilder sb = new StringBuilder();
        NodoSeguimiento aux = primero;
        while (aux != null) {
            sb.append("- ").append(aux.toString()).append("\n");
            aux = aux.getSig();
        }
        return sb.toString();
    }

    public NodoSeguimiento getPrimero() {
        return primero;
    }

    public int getTamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return primero == null;
    }
}
