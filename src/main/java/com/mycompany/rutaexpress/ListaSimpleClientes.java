package com.mycompany.rutaexpress;

public class ListaSimpleClientes {
    private NodoCliente primero;
    private int tamanio;

    public ListaSimpleClientes() {
        primero = null;
        tamanio = 0;
    }

    public void agregar(NodoCliente c) {
        if (c == null || buscarPorDocumento(c.getDocumento()) != null) {
            return;
        }
        NodoCliente nuevo = c;
        if (primero == null) {
            primero = nuevo;
        } else {
            NodoCliente aux = primero;
            while (aux.getSig() != null) {
                aux = aux.getSig();
            }
            aux.setSig(nuevo);
        }
        tamanio++;
    }

    public NodoCliente buscarPorDocumento(String doc) {
        if (doc == null) {
            return null;
        }
        NodoCliente aux = primero;
        while (aux != null) {
            if (aux.getDocumento().equalsIgnoreCase(doc.trim())) {
                return aux;
            }
            aux = aux.getSig();
        }
        return null;
    }

    public String recorrer() {
        if (primero == null) {
            return "No hay clientes registrados.";
        }
        StringBuilder sb = new StringBuilder();
        NodoCliente aux = primero;
        int i = 1;
        while (aux != null) {
            sb.append(i).append(". ").append(aux.toString()).append("\n");
            aux = aux.getSig();
            i++;
        }
        return sb.toString();
    }

    public NodoCliente getPrimero() {
        return primero;
    }

    public int getTamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return primero == null;
    }
}
