package com.mycompany.rutaexpress;

import java.text.Normalizer;

public class ArbolTernarioClientes {
    private NodoTernario raiz;

    public ArbolTernarioClientes() {
        raiz = null;
    }

    public void insertar(NodoCliente c) {
        if (c == null || c.getNombre() == null) {
            return;
        }
        String nombre = normalizar(c.getNombre());
        if (nombre.isEmpty()) {
            return;
        }
        raiz = insertar(raiz, nombre, 0, c);
    }

    private NodoTernario insertar(NodoTernario aux, String nombre, int pos, NodoCliente c) {
        char letra = nombre.charAt(pos);
        if (aux == null) {
            aux = new NodoTernario(letra);
        }
        if (letra < aux.getLetra()) {
            aux.setIzq(insertar(aux.getIzq(), nombre, pos, c));
        } else if (letra > aux.getLetra()) {
            aux.setDer(insertar(aux.getDer(), nombre, pos, c));
        } else if (pos + 1 < nombre.length()) {
            aux.setCentro(insertar(aux.getCentro(), nombre, pos + 1, c));
        } else {
            aux.setEsFinNombre(true);
            if (aux.getCliente() == null) {
                aux.setCliente(c);
            }
        }
        return aux;
    }

    public ListaSimpleClientes buscarPorPrefijo(String prefijo) {
        ListaSimpleClientes encontrados = new ListaSimpleClientes();
        String buscado = normalizar(prefijo);
        if (buscado.isEmpty()) {
            return encontrados;
        }
        recolectar(raiz, buscado, encontrados);
        return encontrados;
    }

    private void recolectar(NodoTernario aux, String prefijo, ListaSimpleClientes encontrados) {
        if (aux == null) {
            return;
        }
        if (aux.isEsFinNombre() && aux.getCliente() != null) {
            if (normalizar(aux.getCliente().getNombre()).startsWith(prefijo)) {
                encontrados.agregar(copiar(aux.getCliente()));
            }
        }
        recolectar(aux.getIzq(), prefijo, encontrados);
        recolectar(aux.getCentro(), prefijo, encontrados);
        recolectar(aux.getDer(), prefijo, encontrados);
    }

    private NodoCliente copiar(NodoCliente c) {
        NodoCliente copia = new NodoCliente(c.getDocumento(), c.getTipoCliente(),
                c.getNombre(), c.getTelefono(), c.getCorreo(), c.getDireccion(),
                c.getMunicipio());
        copia.setFechaRegistro(c.getFechaRegistro());
        NodoGuia g = c.getGuias().getPrimero();
        while (g != null) {
            copia.agregarGuia(g.getGuia());
            g = g.getSig();
        }
        return copia;
    }

    public NodoCliente buscarPorDocumento(String documento) {
        return buscarPorDocumento(raiz, documento);
    }

    private NodoCliente buscarPorDocumento(NodoTernario aux, String documento) {
        if (aux == null || documento == null) {
            return null;
        }
        if (aux.isEsFinNombre() && aux.getCliente() != null
                && aux.getCliente().getDocumento().equalsIgnoreCase(documento.trim())) {
            return aux.getCliente();
        }
        NodoCliente encontrado = buscarPorDocumento(aux.getIzq(), documento);
        if (encontrado != null) {
            return encontrado;
        }
        encontrado = buscarPorDocumento(aux.getCentro(), documento);
        if (encontrado != null) {
            return encontrado;
        }
        return buscarPorDocumento(aux.getDer(), documento);
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public NodoTernario getRaiz() {
        return raiz;
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase().trim().replaceAll("\\s+", " ");
    }
}
