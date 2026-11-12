package com.mycompany.rutaexpress.domain.client;

import com.mycompany.rutaexpress.GestorNotificaciones;
import com.mycompany.rutaexpress.domain.parcel.Paquete;
import com.mycompany.rutaexpress.domain.parcel.PaqueteManager;
import com.mycompany.rutaexpress.estructures.lists.List;
import com.mycompany.rutaexpress.estructures.lists.Node;
import com.mycompany.rutaexpress.estructures.trees.ArbolTernario;

public class ClienteManager {
    private List clientes = new List();
    private final ArbolTernario<Cliente> indicePorNombre;
    private final PaqueteManager paqueteManager;
    private final GestorNotificaciones notificador;
    private String ultimoError;

    public ClienteManager(PaqueteManager paqueteManager, GestorNotificaciones notificador) {
        indicePorNombre = new ArbolTernario<>(Cliente::getNombre);
        this.paqueteManager = paqueteManager;
        this.notificador = notificador;
        ultimoError = "";
    }

    public ClienteManager() {
        this(null, null);
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
            ultimoError = "Ya existe un cliente con documento "
                    + cliente.getDocumento() + ".";
            return false;
        }

        clientes.AddLast(cliente);
        indicePorNombre.insertar(cliente);
        enviarBienvenida(cliente);
        return true;
    }

    private void enviarBienvenida(Cliente cliente) {
        if (notificador != null) {
            notificador.enviarCorreo(cliente.getCorreo(), "Bienvenido a RutaExpress",
                    "Hola " + cliente.getNombre()
                    + ", tu registro en RutaExpress fue exitoso.");
        }
    }

    public Cliente buscarPorDocumento(String documento) {
        if (documento == null) return null;
        Node actual = clientes.getHead();
        while (actual != null) {
            Cliente cliente = (Cliente) actual.getData();
            if (cliente.getDocumento().equalsIgnoreCase(documento.trim())) return cliente;
            actual = actual.getLink();
        }
        return null;
    }

    public List buscarPorPrefijo(String prefijo) {
        List encontrados = new List();
        if (prefijo == null || prefijo.trim().isEmpty()) return clientes;
        indicePorNombre.buscarPorPrefijo(prefijo, encontrados::AddLast);
        return encontrados;
    }

    public String consultarHistorial(String documento) {
        Cliente cliente = buscarPorDocumento(documento);
        if (cliente == null) return "No existe el cliente " + documento + ".";

        StringBuilder resultado = new StringBuilder(cliente.toString()).append("\n");
        resultado.append("Envíos:\n");
        if (paqueteManager == null) {
            resultado.append("No hay un gestor de paquetes configurado.\n");
        } else {
            final int[] cantidad = {0};
            paqueteManager.recorrerPorRemitente(documento, paquete -> {
                cantidad[0]++;
                cliente.agregarGuia(paquete.getGuia());
                resultado.append("- Guía: ").append(paquete.getGuia())
                        .append(" | Estado: ").append(paquete.getEstado())
                        .append(" | Costo: $")
                        .append(String.format("%.0f", paquete.getCostoEnvio()))
                        .append("\n");
            });
            if (cantidad[0] == 0) resultado.append("Sin envíos registrados.\n");
            resultado.append("Total pagado: $")
                    .append(String.format("%.0f",
                            paqueteManager.totalPagadoPorRemitente(documento)))
                    .append("\n");
        }
        return resultado.toString();
    }

    public List listar() { return clientes; }
    public String getUltimoError() { return ultimoError; }
    public ArbolTernario<Cliente> getIndicePorNombre() { return indicePorNombre; }
}
