package com.mycompany.rutaexpress.estructures.trees;

import java.text.Normalizer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ArbolTernario<T> {
    private NodoTernarioGenerico<T> raiz;
    private final Function<T, String> extractorTexto;

    public ArbolTernario(Function<T, String> extractorTexto) {
        this.extractorTexto = extractorTexto;
    }

    public void insertar(T valor) {
        if (valor == null) return;
        String texto = normalizar(extractorTexto.apply(valor));
        if (!texto.isEmpty()) raiz = insertar(raiz, texto, 0, valor);
    }

    private NodoTernarioGenerico<T> insertar(NodoTernarioGenerico<T> nodo,
            String texto, int posicion, T valor) {
        char letra = texto.charAt(posicion);
        if (nodo == null) nodo = new NodoTernarioGenerico<>(letra);
        if (letra < nodo.getLetra()) nodo.setIzquierdo(insertar(nodo.getIzquierdo(), texto, posicion, valor));
        else if (letra > nodo.getLetra()) nodo.setDerecho(insertar(nodo.getDerecho(), texto, posicion, valor));
        else if (posicion + 1 < texto.length()) {
            nodo.setCentro(insertar(nodo.getCentro(), texto, posicion + 1, valor));
        } else {
            nodo.setFin(true);
            nodo.setValor(valor);
        }
        return nodo;
    }

    public void buscarPorPrefijo(String prefijo, Consumer<T> consumidor) {
        String buscado = normalizar(prefijo);
        if (buscado.isEmpty()) return;
        NodoTernarioGenerico<T> nodo = buscarNodo(raiz, buscado, 0);
        if (nodo == null) return;
        if (nodo.isFin() && nodo.getValor() != null) consumidor.accept(nodo.getValor());
        recolectarOrdenado(nodo.getCentro(), consumidor);
    }

    private NodoTernarioGenerico<T> buscarNodo(NodoTernarioGenerico<T> nodo,
            String texto, int posicion) {
        if (nodo == null) return null;
        char letra = texto.charAt(posicion);
        if (letra < nodo.getLetra()) return buscarNodo(nodo.getIzquierdo(), texto, posicion);
        if (letra > nodo.getLetra()) return buscarNodo(nodo.getDerecho(), texto, posicion);
        if (posicion + 1 == texto.length()) return nodo;
        return buscarNodo(nodo.getCentro(), texto, posicion + 1);
    }

    private void recolectarOrdenado(NodoTernarioGenerico<T> nodo, Consumer<T> consumidor) {
        if (nodo == null) return;
        recolectarOrdenado(nodo.getIzquierdo(), consumidor);
        if (nodo.isFin() && nodo.getValor() != null) consumidor.accept(nodo.getValor());
        recolectarOrdenado(nodo.getCentro(), consumidor);
        recolectarOrdenado(nodo.getDerecho(), consumidor);
    }

    public void recorrer(Consumer<T> consumidor) {
        recorrer(raiz, consumidor);
    }

    private void recorrer(NodoTernarioGenerico<T> nodo, Consumer<T> consumidor) {
        if (nodo == null) return;
        if (nodo.isFin() && nodo.getValor() != null) consumidor.accept(nodo.getValor());
        recorrer(nodo.getIzquierdo(), consumidor);
        recorrer(nodo.getCentro(), consumidor);
        recorrer(nodo.getDerecho(), consumidor);
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase().trim().replaceAll("\\s+", " ");
    }

    public NodoTernarioGenerico<T> getRaiz() { return raiz; }
}
