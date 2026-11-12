package com.mycompany.rutaexpress.estructures.trees;

public class NodoTernarioGenerico<T> {
    private char letra;
    private boolean fin;
    private T valor;
    private NodoTernarioGenerico<T> izquierdo;
    private NodoTernarioGenerico<T> centro;
    private NodoTernarioGenerico<T> derecho;

    public NodoTernarioGenerico(char letra) { this.letra = letra; }
    public char getLetra() { return letra; }
    public boolean isFin() { return fin; }
    public void setFin(boolean fin) { this.fin = fin; }
    public T getValor() { return valor; }
    public void setValor(T valor) { this.valor = valor; }
    public NodoTernarioGenerico<T> getIzquierdo() { return izquierdo; }
    public void setIzquierdo(NodoTernarioGenerico<T> valor) { izquierdo = valor; }
    public NodoTernarioGenerico<T> getCentro() { return centro; }
    public void setCentro(NodoTernarioGenerico<T> valor) { centro = valor; }
    public NodoTernarioGenerico<T> getDerecho() { return derecho; }
    public void setDerecho(NodoTernarioGenerico<T> valor) { derecho = valor; }
}
