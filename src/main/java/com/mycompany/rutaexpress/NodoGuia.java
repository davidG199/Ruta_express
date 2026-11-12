package com.mycompany.rutaexpress;

public class NodoGuia {
    private String numeroGuia;
    private NodoGuia sig;

    public NodoGuia(String numeroGuia) {
        this.numeroGuia = numeroGuia;
    }

    public String getGuia() {
        return numeroGuia;
    }

    public void setGuia(String numeroGuia) {
        this.numeroGuia = numeroGuia;
    }

    public NodoGuia getSig() {
        return sig;
    }

    public void setSig(NodoGuia sig) {
        this.sig = sig;
    }
}
