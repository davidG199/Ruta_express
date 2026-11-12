package com.mycompany.rutaexpress.estructures.trees;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ArbolBinario<T> {
    private BinaryNode raiz;
    private final Comparator<T> comparador;

    public ArbolBinario(Comparator<T> comparador) {
        this.comparador = comparador;
    }

    public void insertar(T valor) {
        if (valor != null) raiz = insertar(raiz, valor);
    }

    private BinaryNode insertar(BinaryNode nodo, T valor) {
        if (nodo == null) return new BinaryNode(valor);
        if (comparador.compare(valor, (T) nodo.getData()) < 0) {
            nodo.setLeft(insertar(nodo.getLeft(), valor));
        } else {
            nodo.setRight(insertar(nodo.getRight(), valor));
        }
        return nodo;
    }

    public T buscar(Predicate<T> criterio) {
        return buscarRecursivo(raiz, criterio);
    }

    private T buscarRecursivo(BinaryNode nodo, Predicate<T> criterio) {
        if (nodo == null) return null;
        T valor = (T) nodo.getData();
        if (criterio.test(valor)) return valor;
        T encontrado = buscarRecursivo(nodo.getLeft(), criterio);
        return encontrado != null ? encontrado : buscarRecursivo(nodo.getRight(), criterio);
    }

    public void recorrerEnOrden(Consumer<T> consumidor) {
        recorrerEnOrden(raiz, consumidor);
    }

    private void recorrerEnOrden(BinaryNode nodo, Consumer<T> consumidor) {
        if (nodo == null) return;
        recorrerEnOrden(nodo.getLeft(), consumidor);
        consumidor.accept((T) nodo.getData());
        recorrerEnOrden(nodo.getRight(), consumidor);
    }

    public int cantidad() { return cantidad(raiz); }
    private int cantidad(BinaryNode nodo) {
        return nodo == null ? 0 : 1 + cantidad(nodo.getLeft()) + cantidad(nodo.getRight());
    }

    public int altura() { return altura(raiz); }
    private int altura(BinaryNode nodo) {
        return nodo == null ? 0 : 1 + Math.max(altura(nodo.getLeft()), altura(nodo.getRight()));
    }

    public int contarHojas() { return contarHojas(raiz); }
    private int contarHojas(BinaryNode nodo) {
        if (nodo == null) return 0;
        if (nodo.getLeft() == null && nodo.getRight() == null) return 1;
        return contarHojas(nodo.getLeft()) + contarHojas(nodo.getRight());
    }

    public BinaryNode getRaiz() { return raiz; }
    public boolean estaVacio() { return raiz == null; }
}
