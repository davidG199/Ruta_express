package com.mycompany.rutaexpress.domain.vehicle;

import com.mycompany.rutaexpress.domain.network.RedManager;
import com.mycompany.rutaexpress.domain.network.Ruta;
import com.mycompany.rutaexpress.domain.network.Sede;
import com.mycompany.rutaexpress.domain.client.ClientManager;
import com.mycompany.rutaexpress.domain.client.Cliente;
import com.mycompany.rutaexpress.domain.parcel.EventoPaquete;
import com.mycompany.rutaexpress.domain.parcel.Paquete;
import com.mycompany.rutaexpress.estructures.lists.ListaDoble;
import com.mycompany.rutaexpress.estructures.lists.Node;
import com.mycompany.rutaexpress.estructures.queues.NodoCola;
import com.mycompany.rutaexpress.estructures.stacks.NodoPila;
import com.mycompany.rutaexpress.files.GestorArchivos;
import com.mycompany.rutaexpress.files.GestorNotificaciones;

/**
 * Gestiona la flota, la carga de paquetes y las entregas.
 */
public class VehiculoManager {
    private final ListaDoble<Vehiculo> vehiculos;
    private final RedManager redManager;
    private final GestorNotificaciones notificador;
    private ClientManager clientManager;
    private String ultimoError;

    public VehiculoManager() {
        this(new GestorArchivos(), new RedManager(), new GestorNotificaciones());
    }

    public VehiculoManager(GestorArchivos archivos, RedManager redManager,
            GestorNotificaciones notificador) {
        this.vehiculos = new ListaDoble<>(
                java.util.Comparator.comparing(Vehiculo::getPlaca,
                        String.CASE_INSENSITIVE_ORDER));
        this.redManager = redManager == null ? new RedManager() : redManager;
        this.notificador = notificador == null ? new GestorNotificaciones() : notificador;
        this.ultimoError = "";
    }

    public boolean registrar(Vehiculo vehiculo) {
        ultimoError = "";
        if (vehiculo == null || vehiculo.getPlaca() == null
                || vehiculo.getPlaca().trim().isEmpty()) {
            ultimoError = "El vehículo y su placa son obligatorios.";
            return false;
        }
        if (buscarPorPlaca(vehiculo.getPlaca()) != null) {
            ultimoError = "Ya existe el vehículo " + vehiculo.getPlaca() + ".";
            return false;
        }
        return vehiculos.insertarOrdenado(vehiculo);
    }

    public int cargarDesdeArchivo(GestorArchivos archivos) {
        String[] lineas = archivos.leerLineas("vehiculos.txt");
        int cargados = 0;
        for (String linea : lineas) {
            String[] columnas = linea.split(";", -1);
            if (columnas.length >= 13) {
                String[] datos = new String[13];
                System.arraycopy(columnas, 0, datos, 0, 13);
                if (registrar(datos)) {
                    cargados++;
                }
            }
        }
        return cargados;
    }

    public boolean registrar(String[] datos) {
        ultimoError = "";
        if (datos == null || datos.length < 13) {
            ultimoError = "Faltan datos del vehículo.";
            return false;
        }
        if (buscarPorPlaca(datos[0]) != null) {
            ultimoError = "Ya existe un vehículo con placa " + datos[0] + ".";
            return false;
        }
        if (redManager.buscarSede(datos[2]) == null) {
            ultimoError = "No existe la sede base " + datos[2] + ".";
            return false;
        }
        try {
            Vehiculo vehiculo = new Vehiculo(datos[0].trim(), datos[1].trim(),
                    datos[2].trim(), datos[3].trim(), numero(datos[4]),
                    numero(datos[5]), numero(datos[6]), numero(datos[7]));
            Conductor conductor = new Conductor(datos[8].trim(), datos[9].trim(),
                    datos[10].trim(), datos[11].trim(), datos[12].trim());
            vehiculo.setConductor(conductor);
            return registrar(vehiculo);
        } catch (NumberFormatException e) {
            ultimoError = "Datos numéricos inválidos en el vehículo.";
            return false;
        }
    }

    public boolean cargarDesdeArchivoPorPlaca(String placa, GestorArchivos archivos) {
        if (buscarPorPlaca(placa) != null) {
            return true;
        }
        String[] lineas = archivos.leerLineas("vehiculos.txt");
        for (String linea : lineas) {
            String[] columnas = linea.split(";", -1);
            if (columnas.length >= 13 && columnas[0].equalsIgnoreCase(placa)) {
                String[] datos = new String[13];
                System.arraycopy(columnas, 0, datos, 0, 13);
                return registrar(datos);
            }
        }
        ultimoError = "No se encontró la placa " + placa + " en vehiculos.txt.";
        return false;
    }

    public String cargarPaquetes(String placa) {
        Vehiculo vehiculo = buscarPorPlaca(placa);
        if (vehiculo == null) {
            return "No existe el vehículo " + placa + ".";
        }
        if (!vehiculo.getEstado().equalsIgnoreCase("DISPONIBLE")) {
            return "El vehículo " + placa + " debe estar DISPONIBLE para cargar paquetes.";
        }
        if (!vehiculo.getSedeBase().equalsIgnoreCase(vehiculo.getSedeActual())) {
            return "El vehículo está en " + vehiculo.getSedeActual()
                    + " y debe estar en su sede base " + vehiculo.getSedeBase()
                    + " para cargar paquetes.";
        }
        Sede bodega = redManager.buscarSede(vehiculo.getSedeBase());
        if (bodega == null) {
            return "La sede base " + vehiculo.getSedeBase() + " no existe.";
        }
        int cargados = vehiculo.cargarPaquetes(bodega, redManager.getRed());
        if (cargados == 0) {
            return "No se pudo cargar ningún paquete. Verifique capacidad, estado "
                    + "DISPONIBLE, ruta habilitada o regla de moto (máx. 10 kg y no frágil).";
        }
        return "Se cargaron " + cargados + " paquetes al vehículo " + placa + ".\n"
                + "Peso en pila: " + String.format("%.1f", vehiculo.getPesoCarga())
                + " kg de " + vehiculo.getCapacidadKg() + " kg.\n"
                + vehiculo.resumenRuta(redManager.getRed()) + "\n\n"
                + vehiculo.recorrerCarga();
    }

    public String despachar(String placa) {
        return despachar(placa, 15800);
    }

    public String despachar(String placa, double precioGalon) {
        Vehiculo vehiculo = buscarPorPlaca(placa);
        if (vehiculo == null) {
            return "No existe el vehículo " + placa + ".";
        }
        if (!vehiculo.despachar()) {
            return "El vehículo " + placa
                    + " no está DISPONIBLE o no tiene paquetes/ruta cargados.";
        }
        String ruta = vehiculo.resumenRuta(redManager.getRed());
        StringBuilder cuerpo = new StringBuilder("Vehículo: ").append(placa)
                .append("\nConductor: ")
                .append(vehiculo.getConductor() == null ? "Sin asignar"
                        : vehiculo.getConductor().getNombre())
                .append("\nPaquetes cargados:\n").append(vehiculo.recorrerCarga())
                .append("\n").append(ruta)
                .append("\nCosto estimado de combustible: $")
                .append(String.format("%.0f", vehiculo.calcularCostoCombustible(
                        vehiculo.getKmRutaPlanificada(), precioGalon)));
        double costoEstimado = vehiculo.calcularCostoCombustible(
                vehiculo.getKmRutaPlanificada(), precioGalon);
        boolean correoConductor = vehiculo.getConductor() == null
                || notificador.enviarCorreo(vehiculo.getConductor().getCorreo(),
                        "Hoja de ruta - vehículo " + placa, cuerpo.toString());
        int notificados = 0;
        int fallidos = 0;
        NodoPila<Paquete> nodo = vehiculo.getCarga().getTope();
        while (nodo != null) {
            Paquete paquete = nodo.getValor();
            if (notificador.enviarCorreo(paquete.getCorreoDestinatario(),
                    "Paquete en camino " + paquete.getGuia(),
                    "Tu paquete " + paquete.getGuia() + " salió de "
                    + vehiculo.getSedeBase() + ".\n" + ruta)) {
                notificados++;
            } else {
                fallidos++;
            }
            nodo = nodo.getSiguiente();
        }
        return "Vehículo " + placa + " despachado desde "
                + vehiculo.getSedeBase() + ".\n" + ruta
                + "\nCosto estimado de combustible: $"
                + String.format("%.0f", costoEstimado)
                + "\nAvisos a destinatarios enviados: " + notificados
                + (fallidos > 0 ? " (fallidos: " + fallidos + ")" : "")
                + (!correoConductor ? "\nFalló el envío de la hoja de ruta al conductor." : "")
                + "\n\n" + vehiculo.recorrerCarga();
    }

    public String registrarEntrega(String placa, String codigoSede, double precioGalon) {
        return registrarLlegada(placa, codigoSede, precioGalon);
    }

    public String registrarLlegada(String placa, String codigoSede, double precioGalon) {
        Vehiculo vehiculo = buscarPorPlaca(placa);
        if (vehiculo == null) {
            return "No existe el vehículo " + placa + ".";
        }
        Sede sede = redManager.buscarSede(codigoSede);
        if (sede == null) {
            return "No existe la sede " + codigoSede + ".";
        }
        if (!vehiculo.getEstado().equalsIgnoreCase("EN_RUTA")) {
            return "El vehículo " + placa + " no está EN_RUTA. Despáchelo primero.";
        }
        if (vehiculo.tienePaquetesPara(vehiculo.getSedeActual())) {
            return "Aún hay paquetes pendientes de resultado en la parada "
                    + vehiculo.getSedeActual() + ". Registre cada entrega o devolución antes de avanzar.";
        }
        if (!vehiculo.esProximaParada(sede.getCodigo())) {
            return "La próxima parada de la ruta es " + vehiculo.getProximaParada()
                    + ". No se puede registrar llegada a " + sede.getCodigo() + ".";
        }
        Ruta ruta = redManager.calcularRutaMasCorta(vehiculo.getSedeActual(), sede.getCodigo());
        if (ruta == null) {
            return "No hay ruta habilitada desde " + vehiculo.getSedeActual()
                    + " hasta " + sede.getCodigo() + ".";
        }
        double km = ruta.getDistanciaTotal();
        int avance = vehiculo.registrarLlegada(sede, km);
        double combustible = vehiculo.calcularCostoCombustible(km, precioGalon);
        return "Llegada registrada en " + sede.getCodigo() + ".\n"
                + "Paquetes en esta parada:\n" + listarPaquetesParada(vehiculo, sede.getCodigo())
                + "\nRecorrido: " + String.format("%.1f", km) + " km\n"
                + "Costo de combustible del tramo: $" + String.format("%.0f", combustible)
                + "\n" + (avance < vehiculo.getItinerario().length
                        ? "Próxima parada: " + vehiculo.getProximaParada()
                        : "Última parada de la ruta.");
    }

    public String registrarResultadoEntrega(String placa, String codigoSede,
            String guia, boolean entregado) {
        Vehiculo vehiculo = buscarPorPlaca(placa);
        if (vehiculo == null) {
            return "No existe el vehículo " + placa + ".";
        }
        Sede sede = redManager.buscarSede(codigoSede);
        if (sede == null || !sede.getCodigo().equalsIgnoreCase(vehiculo.getSedeActual())) {
            return "La sede indicada no coincide con la ubicación actual del vehículo.";
        }
        int posicion = vehiculo.posicionPaqueteEnCarga(guia);
        if (posicion < 0) {
            return "El paquete " + guia + " no está cargado en el vehículo.";
        }
        Paquete[] destino = vehiculo.paquetesParaParada(sede.getCodigo());
        boolean corresponde = false;
        for (Paquete paquete : destino) {
            if (paquete.getGuia().equalsIgnoreCase(guia)) {
                corresponde = true;
                break;
            }
        }
        if (!corresponde) {
            return "El paquete " + guia + " no corresponde a esta parada.";
        }
        Paquete paquete = vehiculo.retirarPaquete(guia);
        if (paquete == null) {
            return "No fue posible retirar el paquete " + guia + " de la pila.";
        }
        String responsable = vehiculo.getConductor() == null ? "Sistema"
                : vehiculo.getConductor().getNombre();
        String resultado;
        if (entregado) {
            paquete.setEstado("ENTREGADO");
            paquete.agregarEvento(sede.getCodigo(), "Entregado al destinatario", responsable);
            int erroresCorreo = notificarEntrega(paquete, "Paquete entregado " + guia,
                    "El paquete " + guia + " fue entregado en " + sede.getCodigo() + ".");
            resultado = "Paquete " + guia + " marcado como ENTREGADO."
                    + prefijoReordenamiento(posicion)
                    + (erroresCorreo > 0 ? " Falló el envío de " + erroresCorreo
                            + " notificación(es); revise fallos.log." : "");
        } else {
            paquete.setEstado("DEVUELTO");
            paquete.agregarEvento(sede.getCodigo(), "Entrega fallida; paquete devuelto a origen",
                    responsable);
            Sede origen = redManager.buscarSede(paquete.getSedeOrigen());
            if (origen != null && origen.capacidadDisponible() >= paquete.calcularPesoFacturable()) {
                origen.encolarPaquete(paquete);
                resultado = "Paquete " + guia + " marcado como DEVUELTO y reencolado en "
                        + origen.getCodigo() + "." + prefijoReordenamiento(posicion);
            } else {
                boolean alertaEnviada = origen != null && notificador.notificarSede(
                        origen.getCorreoCoordinador(),
                        "Devolución sin capacidad de bodega - " + guia,
                        "El paquete " + guia + " no se pudo reencolar en "
                        + origen.getCodigo() + " por falta de capacidad.");
                resultado = "Paquete " + guia + " marcado como DEVUELTO, pero no se pudo "
                        + "reencolar por falta de capacidad. "
                        + (alertaEnviada ? "Se alertó al coordinador."
                                : "Falló el aviso al coordinador; revise fallos.log.")
                        + prefijoReordenamiento(posicion);
            }
        }
        if (vehiculo.getCarga().estaVacia()) {
            vehiculo.limpiarRuta();
            vehiculo.setEstado("DISPONIBLE");
        }
        return resultado;
    }

    private String listarPaquetesParada(Vehiculo vehiculo, String codigoSede) {
        Paquete[] paquetes = vehiculo.paquetesParaParada(codigoSede);
        if (paquetes.length == 0) {
            return "No hay paquetes para esta sede.";
        }
        StringBuilder resultado = new StringBuilder();
        for (Paquete paquete : paquetes) {
            resultado.append("- Guía ").append(paquete.getGuia())
                    .append(" | Destinatario: ").append(paquete.getNombreDestinatario())
                    .append(" | Dirección: ").append(paquete.getDireccionEntrega())
                    .append(" | Posición pila: ")
                    .append(vehiculo.posicionPaqueteEnCarga(paquete.getGuia()) + 1)
                    .append("\n");
        }
        return resultado.toString();
    }

    private String prefijoReordenamiento(int posicion) {
        return posicion == 0 ? "" : " Se bajaron temporalmente " + posicion
                + " paquete(s) que estaban encima y se volvieron a subir en el mismo orden.";
    }

    private int notificarEntrega(Paquete paquete, String asunto, String cuerpo) {
        int fallidos = 0;
        if (!notificador.enviarCorreo(paquete.getCorreoDestinatario(), asunto, cuerpo)) {
            fallidos++;
        }
        Cliente remitente = clientManager == null ? null
                : clientManager.buscarPorDocumento(paquete.getDocumentoRemitente());
        if (remitente != null && !notificador.enviarCorreo(remitente.getCorreo(), asunto, cuerpo)) {
            fallidos++;
        }
        return fallidos;
    }

    public String recorrerFlota(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()
                || filtro.equalsIgnoreCase("TODOS")) {
            return listar();
        }
        if (filtro.equalsIgnoreCase("DISPONIBLE")
                || filtro.equalsIgnoreCase("EN_RUTA")
                || filtro.equalsIgnoreCase("MANTENIMIENTO")) {
            return filtrarPorEstado(filtro);
        }
        return filtrarPorSede(filtro);
    }

    public Vehiculo buscarPorPlaca(String placa) {
        if (placa == null) {
            return null;
        }
        return vehiculos.buscar(
                new Vehiculo(placa, "", "", "", 0, 0, 0, 0));
    }

    public boolean eliminar(String placa) {
        return vehiculos.eliminar(new Vehiculo(placa, "", "", "", 0, 0, 0, 0));
    }

    public String listar() {
        if (vehiculos.getTamanio() == 0) {
            return "No hay vehículos registrados.";
        }
        StringBuilder resultado = new StringBuilder();
        final int[] i = {1};
        vehiculos.recorrerAdelante(v -> {
            resultado.append(i[0]++).append(". ").append(v).append("\n\n");
        });
        return resultado.toString();
    }

    public String listarDesdeElFinal() {
        if (vehiculos.getTamanio() == 0) {
            return "No hay vehículos registrados.";
        }
        StringBuilder resultado = new StringBuilder();
        final int[] i = {1};
        vehiculos.recorrerAtras(v -> {
            resultado.append(i[0]++).append(". ").append(v).append("\n\n");
        });
        return resultado.toString();
    }

    public String filtrarPorSede(String sede) {
        if (sede == null) {
            return "Código de sede inválido.";
        }
        StringBuilder resultado = new StringBuilder();
        vehiculos.recorrerAdelante(v -> {
            if (v.getSedeBase().equalsIgnoreCase(sede.trim())) {
                resultado.append(v).append("\n\n");
            }
        });
        return resultado.length() == 0
                ? "No hay vehículos en la sede " + sede + "." : resultado.toString();
    }

    public String filtrarPorEstado(String estado) {
        if (estado == null) {
            return "Estado inválido.";
        }
        StringBuilder resultado = new StringBuilder();
        vehiculos.recorrerAdelante(v -> {
            if (v.getEstado().equalsIgnoreCase(estado.trim())) {
                resultado.append(v).append("\n\n");
            }
        });
        return resultado.length() == 0
                ? "No hay vehículos en estado " + estado + "." : resultado.toString();
    }

    public void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    public ListaDoble<Vehiculo> getVehiculos() { return vehiculos; }
    public int cantidad() { return vehiculos.getTamanio(); }
    public String getUltimoError() { return ultimoError; }

    private double numero(String valor) {
        return Double.parseDouble(valor.trim().replace(",", "."));
    }
}
