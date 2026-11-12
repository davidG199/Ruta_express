package com.mycompany.rutaexpress.estructures.trees;

public class NodoArbol<T> {
    private T valor;
    private NodoArbol<T> izquierdo;
    private NodoArbol<T> derecho;

    public NodoArbol(T valor) { this.valor = valor; }
    public T getValor() { return valor; }
    public NodoArbol<T> getIzquierdo() { return izquierdo; }
    public void setIzquierdo(NodoArbol<T> izquierdo) { this.izquierdo = izquierdo; }
    public NodoArbol<T> getDerecho() { return derecho; }
    public void setDerecho(NodoArbol<T> derecho) { this.derecho = derecho; }
}
