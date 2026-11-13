package com.mycompany.rutaexpress.domain.client;

import com.mycompany.rutaexpress.files.GestorNotificaciones;
import com.mycompany.rutaexpress.domain.parcel.PaqueteManager;
import com.mycompany.rutaexpress.estructures.lists.List;
import com.mycompany.rutaexpress.estructures.lists.Node;
import com.mycompany.rutaexpress.estructures.trees.ArbolTernario;

/**
 * Clase que gestiona las funcionalidades de los clientes de la aplicación.
 */

public class ClientManager {
    private List clientes = new List();
    private ArbolTernario<Cliente> indicePorNombre = new ArbolTernario<>(Cliente::getNombre);
    private PaqueteManager paqueteManager = new PaqueteManager();
    private GestorNotificaciones notificador;
    private String ultimoError;

    //    public ClienteManager(PaqueteManager paqueteManager, GestorNotificaciones notificador) {
//        indicePorNombre = new ArbolTernario<>(Cliente::getNombre);
//        this.paqueteManager = paqueteManager;
//        this.notificador = notificador;
//        ultimoError = "";
//    }

    public ClientManager(){
        this(new GestorNotificaciones());
    }

    public ClientManager(GestorNotificaciones notificador){
        this.notificador = notificador;
    }

    public boolean registrar(Cliente cliente) {
        ultimoError = "";
        if (cliente == null) {
            ultimoError = "El cliente es obligatorio.";
            return false;
        }

        // Validar que el documento exista
        if (buscarPorDocumento(cliente.getDocumento()) != null) {
            ultimoError = "Ya existe un cliente con documento "
                    + cliente.getDocumento() + ".";
            return false;
        }

        // agregar el cliente a la lista de clientes
        if(clientes.isEmpty()){
            clientes.AddFirst(cliente);
        } else {
            clientes.AddLast(cliente);
        }
        // agregar el cliente al índice por nombre
        indicePorNombre.insertar(cliente);
        // enviar correo de bienvenida al cliente
        //enviarBienvenida(cliente);
        return true;
    }

    /**
     * Envía un correo de bienvenida al cliente.
     * Se notifica al cliente que su registro fue exitoso y se le da la bienvenida a la aplicación.
     * si no se puede enviar el correo, se registra el fallo en el gestor de notificaciones.
     *
     * @param cliente El cliente al que se le enviará el correo.
     * @return Un mensaje indicando el resultado de la operación.
     */
    public String enviarBienvenida(Cliente cliente) {

        String asunto = "Bienvenido a RutaExpress";
        String cuerpo = "Hola " + cliente.getNombre()
                + ", tu registro en RutaExpress fue exitoso.";
        String destino = cliente.getCorreo();

        if (notificador != null) {
            if (notificador.enviarCorreo(destino, asunto, cuerpo)) {
                return "Correo de bienvenida enviado a " + destino + ".";
            } else {
                ultimoError = "No se pudo enviar el correo de bienvenida a "
                        + destino + ".";
                notificador.registrarFallo(destino, ultimoError);
                return "No se pudo enviar el correo de bienvenida a " + destino + ".";
            }
        }
        return "No se pudo enviar el correo de bienvenida a " + destino + ".";
    }

    /**
     * Busca un cliente por su documento.
     *
     * @param documento El documento del cliente a buscar.
     * @return El cliente encontrado o null si no existe.
     */
    public Cliente buscarPorDocumento(String documento) {
        if (documento == null) return null;
        Node aux = clientes.getHead();
        while (aux != null) {
            Cliente cliente = (Cliente) aux.getData();
            if (cliente.getDocumento().equalsIgnoreCase(documento.trim())) return cliente;
            aux = aux.getLink();
        }
        return null;
    }

    public String buscarPorPrefijo(String prefijo) {
        List encontrados = new List();
        if (prefijo == null || prefijo.trim().isEmpty()) return clientes.toString();
        indicePorNombre.buscarPorPrefijo(prefijo, encontrados::AddLast);
        return encontrados.toString();
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

    public String listar() {
        if (clientes.isEmpty()) return "No hay clientes registrados.";
        return clientes.toString();
    }
    public String getUltimoError() { return ultimoError; }
    public ArbolTernario<Cliente> getIndicePorNombre() { return indicePorNombre; }
}
