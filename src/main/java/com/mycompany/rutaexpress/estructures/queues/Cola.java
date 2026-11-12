package com.mycompany.rutaexpress.estructures.queues;

public class Cola<T> {
    private NodoCola<T> frente;
    private NodoCola<T> fin;
    private int tamanio;

    public void encolar(T valor) {
        if (valor == null) return;
        NodoCola<T> nuevo = new NodoCola<>(valor);
        if (frente == null) frente = fin = nuevo;
        else { fin.setSiguiente(nuevo); fin = nuevo; }
        tamanio++;
    }

    public T desencolar() {
        if (frente == null) return null;
        T valor = frente.getValor();
        frente = frente.getSiguiente();
        if (frente == null) fin = null;
        tamanio--;
        return valor;
    }

    public T verFrente() { return frente == null ? null : frente.getValor(); }
    public NodoCola<T> getFrente() { return frente; }
    public int getTamanio() { return tamanio; }
    public boolean estaVacia() { return frente == null; }
}
