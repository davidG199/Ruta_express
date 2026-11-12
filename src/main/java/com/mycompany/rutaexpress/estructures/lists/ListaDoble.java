package com.mycompany.rutaexpress.estructures.lists;

import java.util.Comparator;
import java.util.function.Consumer;

public class ListaDoble<T> {
    private NodoDoble<T> primero;
    private NodoDoble<T> ultimo;
    private int tamanio;
    private final Comparator<T> comparador;

    public ListaDoble(Comparator<T> comparador) {
        this.comparador = comparador;
    }

    public boolean insertarOrdenado(T valor) {
        if (valor == null || buscar(valor) != null) return false;
        NodoDoble<T> nuevo = new NodoDoble<>(valor);
        if (primero == null) primero = ultimo = nuevo;
        else if (comparador.compare(valor, primero.getValor()) < 0) {
            nuevo.setSiguiente(primero);
            primero.setAnterior(nuevo);
            primero = nuevo;
        } else {
            NodoDoble<T> actual = primero;
            while (actual.getSiguiente() != null
                    && comparador.compare(actual.getSiguiente().getValor(), valor) < 0) {
                actual = actual.getSiguiente();
            }
            nuevo.setSiguiente(actual.getSiguiente());
            nuevo.setAnterior(actual);
            if (actual.getSiguiente() == null) ultimo = nuevo;
            else actual.getSiguiente().setAnterior(nuevo);
            actual.setSiguiente(nuevo);
        }
        tamanio++;
        return true;
    }

    public T buscar(T valor) {
        NodoDoble<T> actual = primero;
        while (actual != null) {
            if (comparador.compare(actual.getValor(), valor) == 0) return actual.getValor();
            actual = actual.getSiguiente();
        }
        return null;
    }

    public boolean eliminar(T valor) {
        NodoDoble<T> actual = primero;
        while (actual != null && comparador.compare(actual.getValor(), valor) != 0) {
            actual = actual.getSiguiente();
        }
        if (actual == null) return false;
        if (actual.getAnterior() == null) primero = actual.getSiguiente();
        else actual.getAnterior().setSiguiente(actual.getSiguiente());
        if (actual.getSiguiente() == null) ultimo = actual.getAnterior();
        else actual.getSiguiente().setAnterior(actual.getAnterior());
        tamanio--;
        return true;
    }

    public NodoDoble<T> getPrimero() { return primero; }
    public NodoDoble<T> getUltimo() { return ultimo; }
    public int getTamanio() { return tamanio; }

    public void recorrerAdelante(Consumer<T> consumidor) {
        NodoDoble<T> actual = primero;
        while (actual != null) {
            consumidor.accept(actual.getValor());
            actual = actual.getSiguiente();
        }
    }

    public void recorrerAtras(Consumer<T> consumidor) {
        NodoDoble<T> actual = ultimo;
        while (actual != null) {
            consumidor.accept(actual.getValor());
            actual = actual.getAnterior();
        }
    }
}
