package com.mycompany.rutaexpress;

public class ListaDobleVehiculos {
    private NodoVehiculo primero;
    private NodoVehiculo ultimo;
    private int tamanio;

    public ListaDobleVehiculos() {
        primero = null;
        ultimo = null;
        tamanio = 0;
    }

    public void insertarOrdenado(NodoVehiculo v) {
        if (v == null || buscarPorPlaca(v.getPlaca()) != null) {
            return;
        }
        v.setAnt(null);
        v.setSig(null);

        if (primero == null) {
            primero = v;
            ultimo = v;
        } else if (v.getPlaca().compareToIgnoreCase(primero.getPlaca()) < 0) {
            v.setSig(primero);
            primero.setAnt(v);
            primero = v;
        } else {
            NodoVehiculo aux = primero;
            while (aux.getSig() != null
                    && aux.getSig().getPlaca().compareToIgnoreCase(v.getPlaca()) < 0) {
                aux = aux.getSig();
            }
            v.setSig(aux.getSig());
            v.setAnt(aux);
            if (aux.getSig() != null) {
                aux.getSig().setAnt(v);
            } else {
                ultimo = v;
            }
            aux.setSig(v);
        }
        tamanio++;
    }

    public NodoVehiculo buscarPorPlaca(String placa) {
        if (placa == null) {
            return null;
        }
        NodoVehiculo aux = primero;
        while (aux != null) {
            if (aux.getPlaca().equalsIgnoreCase(placa.trim())) {
                return aux;
            }
            aux = aux.getSig();
        }
        return null;
    }

    public boolean eliminarPorPlaca(String placa) {
        NodoVehiculo aux = buscarPorPlaca(placa);
        if (aux == null) {
            return false;
        }
        if (aux.getAnt() != null) {
            aux.getAnt().setSig(aux.getSig());
        } else {
            primero = aux.getSig();
        }
        if (aux.getSig() != null) {
            aux.getSig().setAnt(aux.getAnt());
        } else {
            ultimo = aux.getAnt();
        }
        tamanio--;
        return true;
    }

    public String recorrerAdelante() {
        if (primero == null) {
            return "No hay vehículos registrados.";
        }
        StringBuilder sb = new StringBuilder();
        NodoVehiculo aux = primero;
        int i = 1;
        while (aux != null) {
            sb.append(i).append(". ").append(aux.toString()).append("\n\n");
            aux = aux.getSig();
            i++;
        }
        return sb.toString();
    }

    public String recorrerAtras() {
        if (ultimo == null) {
            return "No hay vehículos registrados.";
        }
        StringBuilder sb = new StringBuilder();
        NodoVehiculo aux = ultimo;
        int i = 1;
        while (aux != null) {
            sb.append(i).append(". ").append(aux.toString()).append("\n\n");
            aux = aux.getAnt();
            i++;
        }
        return sb.toString();
    }

    public String filtrarPorSede(String codigo) {
        if (codigo == null) {
            return "Código de sede inválido.";
        }
        StringBuilder sb = new StringBuilder();
        NodoVehiculo aux = primero;
        while (aux != null) {
            if (aux.getSedeBase().equalsIgnoreCase(codigo.trim())) {
                sb.append(aux.toString()).append("\n\n");
            }
            aux = aux.getSig();
        }
        if (sb.length() == 0) {
            return "No hay vehículos en la sede " + codigo + ".";
        }
        return sb.toString();
    }

    public String filtrarPorEstado(String estado) {
        if (estado == null) {
            return "Estado inválido.";
        }
        StringBuilder sb = new StringBuilder();
        NodoVehiculo aux = primero;
        while (aux != null) {
            if (aux.getEstado().equalsIgnoreCase(estado.trim())) {
                sb.append(aux.toString()).append("\n\n");
            }
            aux = aux.getSig();
        }
        if (sb.length() == 0) {
            return "No hay vehículos en estado " + estado + ".";
        }
        return sb.toString();
    }

    public NodoVehiculo getPrimero() {
        return primero;
    }

    public NodoVehiculo getUltimo() {
        return ultimo;
    }

    public int getTamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return primero == null;
    }
}
