package com.mycompany.rutaexpress.domain.client;

import com.mycompany.rutaexpress.estructures.lists.ListaSimple;
import com.mycompany.rutaexpress.estructures.trees.ArbolTernario;

public class ClienteManager {
    private final ListaSimple<Cliente> clientes;
    private final ArbolTernario<Cliente> indicePorNombre;
    private String ultimoError;

    public ClienteManager() {
        clientes = new ListaSimple<>();
        indicePorNombre = new ArbolTernario<>(Cliente::getNombre);
        ultimoError = "";
    }

    public boolean registrar(Cliente cliente) {
        ultimoError = "";
        if (cliente == null) {
            ultimoError = "El cliente es obligatorio.";
            return false;
        }
        if (cliente.getDocumento() == null || cliente.getDocumento().trim().isEmpty()
                || cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            ultimoError = "Documento y nombre son obligatorios.";
            return false;
        }
        if (buscarPorDocumento(cliente.getDocumento()) != null) {
            ultimoError = "Ya existe un cliente con documento " + cliente.getDocumento() + ".";
            return false;
        }
        clientes.agregar(cliente);
        indicePorNombre.insertar(cliente);
        return true;
    }

    public Cliente buscarPorDocumento(String documento) {
        return clientes.buscar(c -> c.getDocumento().equalsIgnoreCase(documento.trim()));
    }

    public ListaSimple<Cliente> buscarPorPrefijo(String prefijo) {
        ListaSimple<Cliente> encontrados = new ListaSimple<>();
        if (prefijo == null || prefijo.trim().isEmpty()) return clientes;
        indicePorNombre.buscarPorPrefijo(prefijo, encontrados::agregar);
        return encontrados;
    }

    public ListaSimple<Cliente> listar() { return clientes; }
    public String getUltimoError() { return ultimoError; }
    public ArbolTernario<Cliente> getIndicePorNombre() { return indicePorNombre; }
}
