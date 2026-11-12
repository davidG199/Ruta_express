package com.mycompany.rutaexpress;

public class NodoPilaPaquete {
    private NodoPaquete paquete;
    private NodoPilaPaquete sig;

    public NodoPilaPaquete(NodoPaquete paquete) {
        this.paquete = paquete;
        this.sig = null;
    }

    public NodoPaquete getPaquete() {
        return paquete;
    }

    public void setPaquete(NodoPaquete paquete) {
        this.paquete = paquete;
    }

    public NodoPilaPaquete getSig() {
        return sig;
    }

    public void setSig(NodoPilaPaquete sig) {
        this.sig = sig;
    }
}
