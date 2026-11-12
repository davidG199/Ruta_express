package com.mycompany.rutaexpress;

public class ListaSimpleGuias {
    private NodoGuia primero;
    private int tamanio;

    public ListaSimpleGuias() {
        primero = null;
        tamanio = 0;
    }

    public void agregar(String guia) {
        if (guia == null || guia.trim().isEmpty() || contiene(guia)) {
            return;
        }
        NodoGuia nuevo = new NodoGuia(guia.trim());
        if (primero == null) {
            primero = nuevo;
        } else {
            NodoGuia aux = primero;
            while (aux.getSig() != null) {
                aux = aux.getSig();
            }
            aux.setSig(nuevo);
        }
        tamanio++;
    }

    public boolean contiene(String guia) {
        if (guia == null) {
            return false;
        }
        NodoGuia aux = primero;
        while (aux != null) {
            if (aux.getGuia().equalsIgnoreCase(guia.trim())) {
                return true;
            }
            aux = aux.getSig();
        }
        return false;
    }

    public String recorrer() {
        if (primero == null) {
            return "Sin guías registradas.";
        }
        StringBuilder sb = new StringBuilder();
        NodoGuia aux = primero;
        int i = 1;
        while (aux != null) {
            sb.append(i).append(". ").append(aux.getGuia()).append("\n");
            aux = aux.getSig();
            i++;
        }
        return sb.toString();
    }

    public NodoGuia getPrimero() {
        return primero;
    }

    public int getTamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return primero == null;
    }
}
