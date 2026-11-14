package com.mycompany.rutaexpress.domain.client;

import com.mycompany.rutaexpress.domain.parcel.PaqueteManager;
import com.mycompany.rutaexpress.estructures.lists.List;
import com.mycompany.rutaexpress.estructures.lists.Node;
import com.mycompany.rutaexpress.estructures.trees.ArbolTernario;
import com.mycompany.rutaexpress.files.GestorArchivos;
import com.mycompany.rutaexpress.files.GestorNotificaciones;

/**
 * Gestiona las funcionalidades de los clientes de la aplicación.
 */
public class ClientManager implements ClienteRepository {
    private final List clientes;
    private final ArbolTernario<Cliente> indicePorNombre;
    private PaqueteManager paqueteManager;
    private final GestorNotificaciones notificador;
    private String ultimoError;
    private String ultimoMensajeCorreo;

    public ClientManager() {
        this(new GestorNotificaciones());
    }

    public ClientManager(GestorNotificaciones notificador) {
        this(null, notificador);
    }

    public ClientManager(PaqueteManager paqueteManager, GestorNotificaciones notificador) {
        this.clientes = new List();
        this.indicePorNombre = new ArbolTernario<>(Cliente::getNombre);
        this.paqueteManager = paqueteManager == null ? new PaqueteManager() : paqueteManager;
        this.notificador = notificador == null ? new GestorNotificaciones() : notificador;
        this.ultimoError = "";
        this.ultimoMensajeCorreo = "";
    }

    public int cargarDesdeArchivo(GestorArchivos archivos) {
        String[] lineas = archivos.leerLineas("clientes.txt");
        int cargados = 0;
        for (String linea : lineas) {
            String[] columnas = linea.split(";", -1);
            if (columnas.length >= 7) {
                Cliente cliente = new Cliente(columnas[0].trim(), columnas[1].trim(),
                        columnas[2].trim(), columnas[3].trim(), columnas[4].trim(),
                        columnas[5].trim(), columnas[6].trim());
                if (registrarSinNotificar(cliente)) {
                    cargados++;
                }
            }
        }
        return cargados;
    }

    public boolean registrar(Cliente cliente) {
        boolean registrado = registrarSinNotificar(cliente);
        if (registrado) {
            ultimoMensajeCorreo = enviarBienvenida(cliente);
        }
        return registrado;
    }

    public boolean registrarSinNotificar(Cliente cliente) {
        ultimoError = "";
        if (cliente == null) {
            ultimoError = "El cliente es obligatorio.";
            return false;
        }
        if (cliente.getDocumento() == null || cliente.getDocumento().trim().isEmpty()) {
            ultimoError = "El documento del cliente es obligatorio.";
            return false;
        }
        if (buscarPorDocumento(cliente.getDocumento()) != null) {
            ultimoError = "Ya existe un cliente con documento "
                    + cliente.getDocumento() + ".";
            return false;
        }

        if (clientes.isEmpty()) {
            clientes.AddFirst(cliente);
        } else {
            clientes.AddLast(cliente);
        }
        indicePorNombre.insertar(cliente);
        return true;
    }

    /**
     * Envía un correo de bienvenida al cliente.
     *
     * @param cliente El cliente al que se le enviará el correo.
     * @return Un mensaje indicando el resultado de la operación.
     */
    public String enviarBienvenida(Cliente cliente) {
        if (cliente == null) {
            return "No se pudo enviar el correo de bienvenida: cliente no válido.";
        }
        String asunto = "Bienvenido a RutaExpress";
        String cuerpo = "Hola " + cliente.getNombre()
                + ", tu registro en RutaExpress fue exitoso.";
        String destino = cliente.getCorreo();

        if (notificador != null) {
            if (notificador.enviarCorreo(destino, asunto, cuerpo)) {
                return "Correo de bienvenida enviado a " + destino + ".";
            }
            ultimoError = "No se pudo enviar el correo de bienvenida a "
                    + destino + ".";
            notificador.registrarFallo(destino, ultimoError);
            return "No se pudo enviar el correo de bienvenida a " + destino + ".";
        }
        return "No se pudo enviar el correo de bienvenida a " + destino + ".";
    }

    @Override
    public Cliente buscarPorDocumento(String documento) {
        if (documento == null) {
            return null;
        }
        Node aux = clientes.getHead();
        while (aux != null) {
            Cliente cliente = (Cliente) aux.getData();
            if (cliente.getDocumento().equalsIgnoreCase(documento.trim())) {
                return cliente;
            }
            aux = aux.getLink();
        }
        return null;
    }

    public String buscarPorPrefijo(String prefijo) {
        List encontrados = new List();
        if (prefijo == null || prefijo.trim().isEmpty()) {
            return clientes.toString();
        }
        indicePorNombre.buscarPorPrefijo(prefijo, encontrados::AddLast);
        return encontrados.toString();
    }

    public String consultarHistorial(String documento) {
        Cliente cliente = buscarPorDocumento(documento);
        if (cliente == null) {
            return "No existe el cliente " + documento + ".";
        }

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
            if (cantidad[0] == 0) {
                resultado.append("Sin envíos registrados.\n");
            }
            resultado.append("Total pagado: $")
                    .append(String.format("%.0f",
                            paqueteManager.totalPagadoPorRemitente(documento)))
                    .append("\n");
        }
        return resultado.toString();
    }

    public String listar() {
        if (clientes.isEmpty()) {
            return "No hay clientes registrados.";
        }
        return clientes.toString();
    }

    public int cantidad() {
        return clientes.Size();
    }

    public void setPaqueteManager(PaqueteManager paqueteManager) {
        this.paqueteManager = paqueteManager;
    }

    public String getUltimoError() { return ultimoError; }
    public String getUltimoMensajeCorreo() { return ultimoMensajeCorreo; }
    public ArbolTernario<Cliente> getIndicePorNombre() { return indicePorNombre; }
}
