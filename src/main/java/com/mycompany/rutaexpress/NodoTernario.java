package com.mycompany.rutaexpress;

public class NodoTernario {
    private char letra;
    private boolean esFinNombre;
    private NodoCliente cliente;
    private NodoTernario izq;
    private NodoTernario centro;
    private NodoTernario der;

    public NodoTernario(char letra) {
        this.letra = letra;
        this.esFinNombre = false;
        this.cliente = null;
    }

    public char getLetra() {
        return letra;
    }

    public void setLetra(char letra) {
        this.letra = letra;
    }

    public boolean isEsFinNombre() {
        return esFinNombre;
    }

    public void setEsFinNombre(boolean esFinNombre) {
        this.esFinNombre = esFinNombre;
    }

    public NodoCliente getCliente() {
        return cliente;
    }

    public void setCliente(NodoCliente cliente) {
        this.cliente = cliente;
    }

    public NodoTernario getIzq() {
        return izq;
    }

    public void setIzq(NodoTernario izq) {
        this.izq = izq;
    }

    public NodoTernario getCentro() {
        return centro;
    }

    public void setCentro(NodoTernario centro) {
        this.centro = centro;
    }

    public NodoTernario getDer() {
        return der;
    }

    public void setDer(NodoTernario der) {
        this.der = der;
    }
}
