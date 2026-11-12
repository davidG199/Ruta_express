package com.mycompany.rutaexpress;

public class ListaSimpleSedes {
    private NodoSede primero;
    private int tamanio;

    public ListaSimpleSedes() {
        primero = null;
        tamanio = 0;
    }

    public void agregar(NodoSede s) {
        if (s == null || buscar(s.getCodigo()) != null) {
            return;
        }
        NodoSede nuevo = s;
        if (primero == null) {
            primero = nuevo;
        } else {
            NodoSede aux = primero;
            while (aux.getSig() != null) {
                aux = aux.getSig();
            }
            aux.setSig(nuevo);
        }
        tamanio++;
    }

    public NodoSede buscar(String codigo) {
        if (codigo == null) {
            return null;
        }
        NodoSede aux = primero;
        while (aux != null) {
            if (aux.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return aux;
            }
            aux = aux.getSig();
        }
        return null;
    }

    public NodoSede obtener(int indice) {
        if (indice < 0) {
            return null;
        }
        NodoSede aux = primero;
        int i = 0;
        while (aux != null) {
            if (i == indice) {
                return aux;
            }
            aux = aux.getSig();
            i++;
        }
        return null;
    }

    public String recorrer() {
        if (primero == null) {
            return "No hay sedes registradas.";
        }
        StringBuilder sb = new StringBuilder();
        NodoSede aux = primero;
        while (aux != null) {
            sb.append(aux.toString()).append("\n");
            aux = aux.getSig();
        }
        return sb.toString();
    }

    public NodoSede getPrimero() {
        return primero;
    }

    public int getTamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return primero == null;
    }
}
