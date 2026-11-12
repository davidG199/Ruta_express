package com.mycompany.rutaexpress;

public class ColaPQR {
    private NodoPQR frente;
    private NodoPQR fin;
    private int tamanio;

    public ColaPQR() {
        frente = null;
        fin = null;
        tamanio = 0;
    }

    public void encolar(NodoPQR q) {
        if (q == null) {
            return;
        }
        q.setSig(null);
        if (estaVacia()) {
            frente = q;
            fin = q;
        } else {
            fin.setSig(q);
            fin = q;
        }
        tamanio++;
    }

    public NodoPQR desencolar() {
        if (estaVacia()) {
            return null;
        }
        NodoPQR q = frente;
        frente = frente.getSig();
        if (frente == null) {
            fin = null;
        }
        q.setSig(null);
        tamanio--;
        return q;
    }

    public NodoPQR verFrente() {
        if (estaVacia()) {
            return null;
        }
        return frente;
    }

    public int contarAbiertasPorTipo(String tipo) {
        int contador = 0;
        NodoPQR aux = frente;
        while (aux != null) {
            if (aux.getEstado().equalsIgnoreCase("ABIERTA")
                    && aux.getTipo().equalsIgnoreCase(tipo)) {
                contador++;
            }
            aux = aux.getSig();
        }
        return contador;
    }

    public String recorrer() {
        if (estaVacia()) {
            return "No hay PQR pendientes.";
        }
        StringBuilder sb = new StringBuilder();
        NodoPQR aux = frente;
        int i = 1;
        while (aux != null) {
            sb.append(i).append(". ").append(aux.getRadicado())
                    .append(" | ").append(aux.getTipo())
                    .append(" | ").append(aux.getEstado())
                    .append(" | Cliente: ").append(aux.getDocumentoCliente())
                    .append("\n");
            aux = aux.getSig();
            i++;
        }
        return sb.toString();
    }

    public NodoPQR getFrente() {
        return frente;
    }

    public NodoPQR getFin() {
        return fin;
    }

    public int getTamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return frente == null;
    }
}
