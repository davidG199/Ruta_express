package com.mycompany.rutaexpress.estructures.stacks;

public class NodoPila<T> {
    private T valor;
    private NodoPila<T> siguiente;

    public NodoPila(T valor) { this.valor = valor; }
    public T getValor() { return valor; }
    public NodoPila<T> getSiguiente() { return siguiente; }
    public void setSiguiente(NodoPila<T> siguiente) { this.siguiente = siguiente; }
}
