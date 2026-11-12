package com.mycompany.rutaexpress.estructures.trees;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ArbolBinario<T> {
    private NodoArbol<T> raiz;
    private final Comparator<T> comparador;

    public ArbolBinario(Comparator<T> comparador) {
        this.comparador = comparador;
    }

    public void insertar(T valor) {
        if (valor != null) raiz = insertar(raiz, valor);
    }

    private NodoArbol<T> insertar(NodoArbol<T> nodo, T valor) {
        if (nodo == null) return new NodoArbol<>(valor);
        if (comparador.compare(valor, nodo.getValor()) < 0) {
            nodo.setIzquierdo(insertar(nodo.getIzquierdo(), valor));
        } else {
            nodo.setDerecho(insertar(nodo.getDerecho(), valor));
        }
        return nodo;
    }

    public T buscar(Predicate<T> criterio) {
        return buscarRecursivo(raiz, criterio);
    }

    private T buscarRecursivo(NodoArbol<T> nodo, Predicate<T> criterio) {
        if (nodo == null) return null;
        if (criterio.test(nodo.getValor())) return nodo.getValor();
        T encontrado = buscarRecursivo(nodo.getIzquierdo(), criterio);
        return encontrado != null ? encontrado : buscarRecursivo(nodo.getDerecho(), criterio);
    }

    public void recorrerEnOrden(Consumer<T> consumidor) {
        recorrerEnOrden(raiz, consumidor);
    }

    private void recorrerEnOrden(NodoArbol<T> nodo, Consumer<T> consumidor) {
        if (nodo == null) return;
        recorrerEnOrden(nodo.getIzquierdo(), consumidor);
        consumidor.accept(nodo.getValor());
        recorrerEnOrden(nodo.getDerecho(), consumidor);
    }

    public int cantidad() { return cantidad(raiz); }
    private int cantidad(NodoArbol<T> nodo) {
        return nodo == null ? 0 : 1 + cantidad(nodo.getIzquierdo()) + cantidad(nodo.getDerecho());
    }

    public int altura() { return altura(raiz); }
    private int altura(NodoArbol<T> nodo) {
        return nodo == null ? 0 : 1 + Math.max(altura(nodo.getIzquierdo()), altura(nodo.getDerecho()));
    }

    public int contarHojas() { return contarHojas(raiz); }
    private int contarHojas(NodoArbol<T> nodo) {
        if (nodo == null) return 0;
        if (nodo.getIzquierdo() == null && nodo.getDerecho() == null) return 1;
        return contarHojas(nodo.getIzquierdo()) + contarHojas(nodo.getDerecho());
    }

    public NodoArbol<T> getRaiz() { return raiz; }
    public boolean estaVacio() { return raiz == null; }
}
