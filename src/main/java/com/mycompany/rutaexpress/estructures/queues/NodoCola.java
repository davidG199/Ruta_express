package com.mycompany.rutaexpress.estructures.queues;

public class NodoCola<T> {
    private T valor;
    private NodoCola<T> siguiente;

    public NodoCola(T valor) { this.valor = valor; }
    public T getValor() { return valor; }
    public NodoCola<T> getSiguiente() { return siguiente; }
    public void setSiguiente(NodoCola<T> siguiente) { this.siguiente = siguiente; }
}
