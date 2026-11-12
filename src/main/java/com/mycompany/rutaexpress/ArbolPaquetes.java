package com.mycompany.rutaexpress;

public class ArbolPaquetes {
    private NodoPaquete raiz;

    public ArbolPaquetes() {
        raiz = null;
    }

    public void insertar(NodoPaquete p) {
        if (p == null) {
            return;
        }
        if (raiz == null) {
            raiz = p;
        } else {
            insertar(p, raiz);
        }
    }

    private void insertar(NodoPaquete p, NodoPaquete aux) {
        if (p.getGuia().compareToIgnoreCase(aux.getGuia()) < 0) {
            if (aux.getIzq() == null) {
                aux.setIzq(p);
            } else {
                insertar(p, aux.getIzq());
            }
        } else {
            if (aux.getDer() == null) {
                aux.setDer(p);
            } else {
                insertar(p, aux.getDer());
            }
        }
    }

    public NodoPaquete buscar(String guia) {
        return buscar(guia, raiz);
    }

    private NodoPaquete buscar(String guia, NodoPaquete aux) {
        if (aux == null || guia == null) {
            return null;
        }
        if (aux.getGuia().equalsIgnoreCase(guia.trim())) {
            return aux;
        }
        if (guia.trim().compareToIgnoreCase(aux.getGuia()) < 0) {
            return buscar(guia, aux.getIzq());
        }
        return buscar(guia, aux.getDer());
    }

    public String inOrden() {
        return inOrden(raiz);
    }

    private String inOrden(NodoPaquete aux) {
        if (aux == null) {
            return "";
        }
        return inOrden(aux.getIzq()) + aux + "\n\n" + inOrden(aux.getDer());
    }

    public String listarPorEstado(String estado) {
        StringBuilder sb = new StringBuilder();
        listarPorEstado(raiz, estado, sb);
        if (sb.length() == 0) {
            return "No hay paquetes en estado " + estado + ".";
        }
        return sb.toString();
    }

    private void listarPorEstado(NodoPaquete aux, String estado, StringBuilder sb) {
        if (aux == null) {
            return;
        }
        listarPorEstado(aux.getIzq(), estado, sb);
        if (aux.getEstado().equalsIgnoreCase(estado)) {
            sb.append(aux.getGuia()).append(" | ").append(aux.getEstado())
                    .append(" | ").append(aux.getSedeOrigen()).append(" -> ")
                    .append(aux.getSedeDestino()).append(" | $")
                    .append(String.format("%.0f", aux.getCostoEnvio())).append("\n");
        }
        listarPorEstado(aux.getDer(), estado, sb);
    }

    public int cantidad() {
        return cantidad(raiz);
    }

    private int cantidad(NodoPaquete aux) {
        if (aux == null) {
            return 0;
        }
        return 1 + cantidad(aux.getIzq()) + cantidad(aux.getDer());
    }

    public int altura() {
        return altura(raiz);
    }

    private int altura(NodoPaquete aux) {
        if (aux == null) {
            return 0;
        }
        int izq = altura(aux.getIzq());
        int der = altura(aux.getDer());
        return 1 + Math.max(izq, der);
    }

    public int contarHojas() {
        return contarHojas(raiz);
    }

    private int contarHojas(NodoPaquete aux) {
        if (aux == null) {
            return 0;
        }
        if (aux.getIzq() == null && aux.getDer() == null) {
            return 1;
        }
        return contarHojas(aux.getIzq()) + contarHojas(aux.getDer());
    }

    public String guiaMenor() {
        if (raiz == null) {
            return "";
        }
        NodoPaquete aux = raiz;
        while (aux.getIzq() != null) {
            aux = aux.getIzq();
        }
        return aux.getGuia();
    }

    public String guiaMayor() {
        if (raiz == null) {
            return "";
        }
        NodoPaquete aux = raiz;
        while (aux.getDer() != null) {
            aux = aux.getDer();
        }
        return aux.getGuia();
    }

    public NodoPaquete getRaiz() {
        return raiz;
    }

    public boolean estaVacio() {
        return raiz == null;
    }
}
