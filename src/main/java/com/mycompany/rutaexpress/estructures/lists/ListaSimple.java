package com.mycompany.rutaexpress.estructures.lists;

import java.util.function.Predicate;

public class ListaSimple<T> {
    private Nodo<T> primero;
    private int tamanio;

    public void agregar(T valor) {
        if (valor == null) return;
        Nodo<T> nuevo = new Nodo<>(valor);
        if (primero == null) primero = nuevo;
        else {
            Nodo<T> actual = primero;
            while (actual.getSiguiente() != null) actual = actual.getSiguiente();
            actual.setSiguiente(nuevo);
        }
        tamanio++;
    }

    public T buscar(Predicate<T> criterio) {
        Nodo<T> actual = primero;
        while (actual != null) {
            if (criterio.test(actual.getValor())) return actual.getValor();
            actual = actual.getSiguiente();
        }
        return null;
    }

    public boolean contiene(Predicate<T> criterio) {
        return buscar(criterio) != null;
    }

    public T obtener(int indice) {
        if (indice < 0) return null;
        Nodo<T> actual = primero;
        for (int i = 0; actual != null; i++, actual = actual.getSiguiente()) {
            if (i == indice) return actual.getValor();
        }
        return null;
    }

    public Nodo<T> getPrimero() { return primero; }
    public int getTamanio() { return tamanio; }
    public boolean estaVacia() { return primero == null; }
}
