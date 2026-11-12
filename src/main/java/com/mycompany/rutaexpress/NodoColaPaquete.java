package com.mycompany.rutaexpress;

public class NodoColaPaquete {
    private NodoPaquete paquete;
    private NodoColaPaquete sig;

    public NodoColaPaquete(NodoPaquete paquete) {
        this.paquete = paquete;
        this.sig = null;
    }

    public NodoPaquete getPaquete() {
        return paquete;
    }

    public void setPaquete(NodoPaquete paquete) {
        this.paquete = paquete;
    }

    public NodoColaPaquete getSig() {
        return sig;
    }

    public void setSig(NodoColaPaquete sig) {
        this.sig = sig;
    }
}
