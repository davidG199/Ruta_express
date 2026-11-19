package com.mycompany.rutaexpress.domain.pqr;

import com.mycompany.rutaexpress.domain.parcel.PaqueteManager;
import com.mycompany.rutaexpress.domain.client.ClientManager;
import com.mycompany.rutaexpress.domain.client.Cliente;
import com.mycompany.rutaexpress.estructures.queues.Cola;
import com.mycompany.rutaexpress.estructures.queues.NodoCola;
import com.mycompany.rutaexpress.files.GestorArchivos;
import com.mycompany.rutaexpress.files.GestorNotificaciones;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Gestiona la radicación y atención de PQR.
 */
public class PqrManager {
    private final Cola<Pqr> pqrs;
    private final ClientManager clientManager;
    private final PaqueteManager paqueteManager;
    private final GestorNotificaciones notificador;
    private final Random random;
    private String ultimoError;
    private String ultimoMensajeOperacion;

    public PqrManager() {
        this(null, null, null);
    }

    public PqrManager(ClientManager clientManager, PaqueteManager paqueteManager,
            GestorNotificaciones notificador) {
        this.pqrs = new Cola<>();
        this.clientManager = clientManager;
        this.paqueteManager = paqueteManager;
        this.notificador = notificador;
        this.random = new Random();
        this.ultimoError = "";
        this.ultimoMensajeOperacion = "";
    }

    public int cargarDesdeArchivo(GestorArchivos archivos) {
        String[] lineas = archivos.leerLineas("pqr.txt");
        int cargadas = 0;
        for (String linea : lineas) {
            String[] columnas = linea.split(";", -1);
            if (columnas.length < 7) {
                continue;
            }
            if (!columnas[4].equalsIgnoreCase("ABIERTA")) {
                continue;
            }
            if (clientManager == null
                    || clientManager.buscarPorDocumento(columnas[1]) == null) {
                continue;
            }
            LocalDateTime fecha;
            try {
                fecha = LocalDateTime.parse(columnas[6].trim());
            } catch (Exception e) {
                fecha = LocalDateTime.now();
            }
            Pqr pqr = new Pqr(columnas[0].trim(), columnas[1].trim(),
                    columnas[2].trim(), columnas[3].trim(),
                    columnas[5].trim(), fecha);
            pqr.agregarSeguimiento("Sistema", "PQR cargada desde archivo");
            pqrs.encolar(pqr);
            cargadas++;
        }
        return cargadas;
    }

    public boolean radicar(Pqr pqr) {
        ultimoError = "";
        ultimoMensajeOperacion = "";
        if (pqr == null) {
            ultimoError = "La PQR es obligatoria.";
            return false;
        }
        if (pqr.getRadicado() == null || pqr.getRadicado().trim().isEmpty()) {
            ultimoError = "El radicado es obligatorio.";
            return false;
        }
        if (buscarPorRadicado(pqr.getRadicado()) != null) {
            ultimoError = "Ya existe la PQR " + pqr.getRadicado() + ".";
            return false;
        }
        if (clientManager != null
                && clientManager.buscarPorDocumento(pqr.getDocumentoCliente()) == null) {
            ultimoError = "No existe el cliente " + pqr.getDocumentoCliente() + ".";
            return false;
        }
        pqrs.encolar(pqr);
        Cliente cliente = clientManager == null ? null
                : clientManager.buscarPorDocumento(pqr.getDocumentoCliente());
        if (cliente != null && notificador != null) {
            boolean enviada = notificador.enviarCorreo(cliente.getCorreo(),
                    "Confirmación de PQR " + pqr.getRadicado(),
                    "Hola " + cliente.getNombre() + ", recibimos tu PQR.\n\n"
                    + pqr.toString());
            ultimoMensajeOperacion = enviada
                    ? "PQR radicada y confirmación enviada al cliente."
                    : "PQR radicada, pero falló el correo de confirmación; revise fallos.log.";
        } else {
            ultimoMensajeOperacion = "PQR radicada sin correo de confirmación disponible.";
        }
        return true;
    }

    public boolean radicar(String[] datos) {
        ultimoError = "";
        ultimoMensajeOperacion = "";
        if (datos == null || datos.length < 4) {
            ultimoError = "Faltan datos de la PQR.";
            return false;
        }
        if (clientManager == null
                || clientManager.buscarPorDocumento(datos[0]) == null) {
            ultimoError = "No existe el cliente " + datos[0] + ".";
            return false;
        }
        String guia = datos[1] == null ? "" : datos[1].trim();
        if (!guia.isEmpty()
                && (paqueteManager == null
                || paqueteManager.buscarPorGuia(guia) == null)) {
            ultimoError = "No existe el paquete " + guia + ".";
            return false;
        }
        String radicado;
        do {
            radicado = "PQR-" + (100000 + random.nextInt(900000));
        } while (existeRadicado(radicado));

        Pqr pqr = new Pqr(radicado, datos[0].trim(), guia,
                datos[2] == null ? "" : datos[2].trim(),
                datos[3] == null ? "" : datos[3].trim(), LocalDateTime.now());
        pqr.agregarSeguimiento("Cliente", "Radicación de la PQR");
        pqrs.encolar(pqr);
        Cliente cliente = clientManager.buscarPorDocumento(pqr.getDocumentoCliente());
        StringBuilder resultadoCorreo = new StringBuilder();
        if (notificador != null) {
            boolean confirmacion = notificador.enviarCorreo(cliente.getCorreo(),
                    "Confirmación de PQR " + radicado,
                    "Hola " + cliente.getNombre() + ", recibimos tu PQR.\n\n"
                    + pqr.toString());
            resultadoCorreo.append(confirmacion
                    ? "Confirmación enviada al cliente."
                    : "La PQR fue radicada, pero falló el correo de confirmación; "
                            + "revise fallos.log.");
            boolean avisoCoordinador = notificador.notificarSede(
                    notificador.getCorreoCoordinador(), "Nueva PQR " + radicado,
                    "Cliente " + pqr.getDocumentoCliente() + " radicó " + pqr.getTipo()
                    + ": " + pqr.getDescripcion());
            resultadoCorreo.append(" ").append(avisoCoordinador
                    ? "Coordinador notificado."
                    : "Falló también el aviso al coordinador; revise fallos.log.");
        } else {
            resultadoCorreo.append("PQR radicada; no hay servicio de correo configurado.");
        }
        ultimoMensajeOperacion = resultadoCorreo.toString();
        return true;
    }

    public Pqr atender() {
        Pqr pqr = pqrs.desencolar();
        if (pqr != null && "ABIERTA".equalsIgnoreCase(pqr.getEstado())) {
            pqrs.encolar(pqr);
        }
        return pqr;
    }

    public String atender(String funcionario, String nota, boolean cerrar) {
        ultimoMensajeOperacion = "";
        Pqr pqr = pqrs.desencolar();
        if (pqr == null) {
            return null;
        }
        pqr.agregarSeguimiento(funcionario, nota);
        String estado;
        if (cerrar) {
            pqr.cerrar("");
            estado = "CERRADA";
            Cliente cliente = clientManager == null ? null
                    : clientManager.buscarPorDocumento(pqr.getDocumentoCliente());
            if (cliente != null && notificador != null) {
                boolean enviada = notificador.enviarCorreo(cliente.getCorreo(),
                        "Respuesta a PQR " + pqr.getRadicado(),
                        "Hola " + cliente.getNombre() + ", tu PQR ha sido cerrada.\n"
                        + "Respuesta: " + (nota == null ? "" : nota.trim())
                        + "\n\nHistorial completo de atención:\n" + pqr.toString());
                ultimoMensajeOperacion = enviada
                        ? "Respuesta e historial completo enviados al cliente."
                        : "PQR cerrada, pero falló el correo al cliente; revise fallos.log.";
            } else {
                ultimoMensajeOperacion = "PQR cerrada sin correo del cliente disponible.";
            }
        } else {
            pqrs.encolar(pqr);
            estado = "ABIERTA (reenviada al final de la cola)";
        }
        return "PQR " + pqr.getRadicado() + " -> " + estado + "\n\n" + pqr
                + (ultimoMensajeOperacion.isEmpty() ? "" : "\n" + ultimoMensajeOperacion);
    }

    public Pqr verFrente() { return pqrs.verFrente(); }

    public String verFrenteTexto() {
        Pqr pqr = pqrs.verFrente();
        return pqr == null ? null : pqr.toString();
    }

    public int contarAbiertasPorTipo(String tipo) {
        int contador = 0;
        NodoCola<Pqr> nodo = pqrs.getFrente();
        while (nodo != null) {
            Pqr pqr = nodo.getValor();
            if (pqr.getEstado().equalsIgnoreCase("ABIERTA")
                    && pqr.getTipo().equalsIgnoreCase(tipo)) {
                contador++;
            }
            nodo = nodo.getSiguiente();
        }
        return contador;
    }

    public String listar() {
        if (pqrs.estaVacia()) {
            return "No hay PQR pendientes.";
        }
        StringBuilder resultado = new StringBuilder();
        NodoCola<Pqr> nodo = pqrs.getFrente();
        int i = 1;
        while (nodo != null) {
            Pqr pqr = nodo.getValor();
            resultado.append(i++).append(". ").append(pqr.getRadicado()).append(" | ")
                    .append(pqr.getTipo()).append(" | ").append(pqr.getEstado())
                    .append(" | Cliente: ").append(pqr.getDocumentoCliente()).append("\n");
            nodo = nodo.getSiguiente();
        }
        return resultado.toString();
    }

    public String verCola() {
        return listar()
                + "\nAbiertas por tipo: QUEJA=" + contarAbiertasPorTipo("QUEJA")
                + ", RECLAMO=" + contarAbiertasPorTipo("RECLAMO")
                + ", SUGERENCIA=" + contarAbiertasPorTipo("SUGERENCIA")
                + ", FELICITACION=" + contarAbiertasPorTipo("FELICITACION");
    }

    public Pqr buscarPorRadicado(String radicado) {
        if (radicado == null) {
            return null;
        }
        NodoCola<Pqr> nodo = pqrs.getFrente();
        while (nodo != null) {
            if (nodo.getValor().getRadicado().equalsIgnoreCase(radicado.trim())) {
                return nodo.getValor();
            }
            nodo = nodo.getSiguiente();
        }
        return null;
    }

    private boolean existeRadicado(String radicado) {
        return buscarPorRadicado(radicado) != null;
    }

    public Cola<Pqr> getPqrs() { return pqrs; }
    public int cantidad() { return pqrs.getTamanio(); }
    public String getUltimoError() { return ultimoError; }
    public String getUltimoMensajeOperacion() { return ultimoMensajeOperacion; }
}
