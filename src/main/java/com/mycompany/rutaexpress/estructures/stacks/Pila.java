package com.mycompany.rutaexpress.estructures.stacks;

public class Pila<T> {
    private NodoPila<T> tope;
    private int tamanio;

    public void apilar(T valor) {
        if (valor == null) return;
        NodoPila<T> nuevo = new NodoPila<>(valor);
        nuevo.setSiguiente(tope);
        tope = nuevo;
        tamanio++;
    }

    public T desapilar() {
        if (tope == null) return null;
        T valor = tope.getValor();
        tope = tope.getSiguiente();
        tamanio--;
        return valor;
    }

    public T verTope() { return tope == null ? null : tope.getValor(); }
    public NodoPila<T> getTope() { return tope; }
    public int getTamanio() { return tamanio; }
    public boolean estaVacia() { return tope == null; }
}
