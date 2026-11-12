package com.mycompany.rutaexpress.main;

import com.mycompany.rutaexpress.*;
import com.mycompany.rutaexpress.domain.client.Cliente;
import com.mycompany.rutaexpress.domain.client.ClienteManager;
import com.mycompany.rutaexpress.estructures.lists.List;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class SistemaRutaExpress {
    private GrafoRed red;
    private ClienteManager clienteManager;
    private ArbolPaquetes paquetes;
    private ListaDobleVehiculos flota;
    private ColaPQR pqrs;
    private GestorNotificaciones notificador;
    private GestorArchivos archivos;
    private double precioGalon;
    private String ultimoError;
    private Random random;

    public SistemaRutaExpress() {
        archivos = new GestorArchivos();
        notificador = new GestorNotificaciones(archivos);
        red = new GrafoRed();
        clienteManager = new ClienteManager(null, notificador);
        paquetes = new ArbolPaquetes();
        flota = new ListaDobleVehiculos();
        pqrs = new ColaPQR();
        precioGalon = 15800;
        ultimoError = "";
        random = new Random();
    }

    public String inicializar() {
        cargarConfiguracion();
        int sedesCargadas = cargarRed("red.txt");
        int viasCargadas = red.getCantidadVias();
        int clientesCargados = cargarClientes();
        int vehiculosCargados = cargarVehiculos();
        int paquetesCargados = cargarPaquetesIniciales();
        int pqrsCargadas = cargarPQRs();

        return "RutaExpress Antioquia iniciado.\n\n"
                + "Sedes: " + sedesCargadas + "\n"
                + "Vías: " + viasCargadas + "\n"
                + "Clientes: " + clientesCargados + "\n"
                + "Vehículos: " + vehiculosCargados + "\n"
                + "Paquetes: " + paquetesCargados + "\n"
                + "PQR abiertas: " + pqrsCargadas + "\n"
                + "Precio del galón: $" + String.format("%.0f", precioGalon);
    }

    public void cargarConfiguracion() {
        notificador.cargarConfiguracion();
        String[] lineas = archivos.leerLineas("config.txt");
        for (String linea : lineas) {
            String[] partes = linea.split("=", 2);
            if (partes.length != 2) {
                continue;
            }
            if (partes[0].trim().equalsIgnoreCase("precioGalon")) {
                try {
                    double valor = Double.parseDouble(partes[1].trim().replace(",", "."));
                    if (valor > 0) {
                        precioGalon = valor;
                    }
                } catch (NumberFormatException e) {
                    registrarError("config.txt", "Precio de galón inválido");
                }
            }
        }
    }

    public int cargarRed(String ruta) {
        red = new GrafoRed();
        String[] lineas = archivos.leerLineas(ruta);
        int cargadas = 0;
        for (String linea : lineas) {
            String[] c = linea.split(";", -1);
            if (c.length == 0) {
                continue;
            }
            try {
                if (c[0].equalsIgnoreCase("SEDE") && c.length >= 9) {
                    String[] datos = new String[8];
                    System.arraycopy(c, 1, datos, 0, 8);
                    if (registrarSede(datos)) {
                        cargadas++;
                    }
                } else if (c[0].equalsIgnoreCase("VIA") && c.length >= 8) {
                    String[] datos = new String[7];
                    System.arraycopy(c, 1, datos, 0, 7);
                    if (registrarVia(datos)) {
                        cargadas++;
                    }
                }
            } catch (Exception e) {
                registrarError(ruta, "Línea inválida: " + linea);
            }
        }
        return red.getCantidadSedes();
    }

    public boolean registrarSede(String[] d) {
        if (d == null || d.length < 8 || d[0].trim().isEmpty()) {
            return false;
        }
        if (red.buscarSede(d[0]) != null) {
            return false;
        }
        try {
            NodoSede sede = new NodoSede(d[0].trim(), d[1].trim(), d[2].trim(),
                    d[3].trim(), d[4].trim(), d[5].trim(), d[6].trim(), numero(d[7]));
            red.agregarSede(sede);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean registrarVia(String[] d) {
        if (d == null || d.length < 7) {
            return false;
        }
        try {
            NodoVia via = new NodoVia(d[0].trim(), d[1].trim(), d[2].trim(),
                    d[3].trim(), numero(d[4]), numero(d[5]), numero(d[6]));
            return red.agregarVia(d[0].trim(), d[1].trim(), via);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String cambiarEstadoVia(String origen, String destino, String estado) {
        if (!red.cambiarEstadoVia(origen, destino, estado)) {
            return "No se encontró la vía " + origen + " - " + destino + ".";
        }
        StringBuilder sb = new StringBuilder("Vía " + origen + " - " + destino
                + " cambiada a " + estado + ".");
        if (estado.equalsIgnoreCase("CERRADA")) {
            ListaSimpleSedes aisladas = red.sedesAisladas();
            NodoSede sede = aisladas.getPrimero();
            int contador = 0;
            while (sede != null) {
                contador++;
                notificador.notificarSede(sede.getCorreoCoordinador(),
                        "Sede aislada por cierre de vía",
                        "La sede " + sede.getCodigo() + " (" + sede.getNombre()
                        + ") quedó aislada de la red principal.");
                sede = sede.getSig();
            }
            sb.append("\nSedes aisladas notificadas: ").append(contador).append(".");
        }
        return sb.toString();
    }

    public String mostrarRed() {
        return red.mostrarRed();
    }

    public String consultarMejorRuta(String origen, String destino) {
        Ruta ruta = red.rutaMasCorta(origen, destino);
        if (ruta == null) {
            return "No existe una ruta con vías HABILITADAS entre "
                    + origen + " y " + destino + ".";
        }
        return ruta.toString();
    }

    public String sedesAisladas() {
        ListaSimpleSedes aisladas = red.sedesAisladas();
        if (aisladas.estaVacia()) {
            return "No hay sedes aisladas.";
        }
        return "Sedes aisladas de la red principal:\n" + aisladas.recorrer();
    }

    public String verBodega(String codigoSede) {
        NodoSede sede = red.buscarSede(codigoSede);
        if (sede == null) {
            return "No existe la sede " + codigoSede + ".";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("BODEGA ").append(sede.getCodigo()).append(" - ").append(sede.getNombre()).append("\n");
        sb.append("Ocupación: ").append(String.format("%.1f", sede.calcularOcupacion()))
                .append("% (").append(String.format("%.1f", sede.pesoAlmacenado()))
                .append(" de ").append(String.format("%.1f", sede.getCapacidadBodegaKg()))
                .append(" kg)\n");
        if (sede.calcularOcupacion() > 80) {
            sb.append("ALERTA: la bodega supera el 80 % de ocupación.\n");
        }
        sb.append("Paquetes en cola: ").append(sede.getDespacho().getTamanio()).append("\n");
        sb.append("Vías:\n").append(sede.mostrarVias()).append("\n");
        sb.append("Cola de despacho:\n").append(sede.getDespacho().recorrer());
        return sb.toString();
    }

    public int cargarClientes() {
        String[] lineas = archivos.leerLineas("clientes.txt");
        int cargados = 0;
        for (String linea : lineas) {
            String[] c = linea.split(";", -1);
            if (c.length >= 7) {
                String[] datos = new String[7];
                System.arraycopy(c, 0, datos, 0, 7);
                if (registrarCliente(new Cliente(datos[0].trim(), datos[1].trim(), datos[2].trim(),
                        datos[3].trim(), datos[4].trim(), datos[5].trim(), datos[6].trim()))) {
                    cargados++;
                }
            }
        }
        return cargados;
    }

    public boolean registrarCliente(Cliente cliente) {
        if (cliente == null) {
            ultimoError = "Faltan datos del cliente.";
            return false;
        }
        boolean registrado = clienteManager.registrar(cliente);
        ultimoError = clienteManager.getUltimoError();
        return registrado;
    }

//    public Cliente buscarClientePorDocumento(String documento) {
//        return clienteManager.buscarPorDocumento(documento);
//    }

    public String buscarClientes(String prefijo) {
        List encontrados = clienteManager.buscarPorPrefijo(prefijo);
        if (encontrados.isEmpty()) {
            return "No hay clientes cuyo nombre empiece por \"" + prefijo + "\".";
        }
        return encontrados.toString();
    }

    public String verHistorialCliente(String documento) {
        Cliente c = clienteManager.buscarPorDocumento(documento);
        if (c == null) {
            return "No existe el cliente " + documento + ".";
        }
        StringBuilder sb = new StringBuilder(c.toString()).append("\n");
        sb.append("Guías del cliente:\n").append(c.getGuias()).append("\n");
        com.mycompany.rutaexpress.estructures.lists.Node guia = c.getGuias().getHead();
        while (guia != null) {
            NodoPaquete p = paquetes.buscar((String) guia.getData());
            if (p != null) {
                sb.append("Paquete ").append(p.getGuia()).append(" - ").append(p.getEstado()).append("\n");
                sb.append(p.getHistorial().recorrer()).append("\n");
            }
            guia = guia.getLink();
        }
        return sb.toString();
    }

    public int cargarVehiculos() {
        String[] lineas = archivos.leerLineas("vehiculos.txt");
        int cargados = 0;
        for (String linea : lineas) {
            String[] c = linea.split(";", -1);
            if (c.length >= 13) {
                String[] datos = new String[13];
                System.arraycopy(c, 0, datos, 0, 13);
                if (registrarVehiculo(datos)) {
                    cargados++;
                }
            }
        }
        return cargados;
    }

    public boolean registrarVehiculo(String[] d) {
        ultimoError = "";
        if (d == null || d.length < 13) {
            ultimoError = "Faltan datos del vehículo.";
            return false;
        }
        if (flota.buscarPorPlaca(d[0]) != null) {
            ultimoError = "Ya existe un vehículo con placa " + d[0] + ".";
            return false;
        }
        if (red.buscarSede(d[2]) == null) {
            ultimoError = "No existe la sede base " + d[2] + ".";
            return false;
        }
        try {
            NodoVehiculo v = new NodoVehiculo(d[0].trim(), d[1].trim(), d[2].trim(),
                    d[3].trim(), numero(d[4]), numero(d[5]), numero(d[6]), numero(d[7]));
            Conductor conductor = new Conductor(d[8].trim(), d[9].trim(),
                    d[10].trim(), d[11].trim(), d[12].trim());
            v.setConductor(conductor);
            flota.insertarOrdenado(v);
            return true;
        } catch (NumberFormatException e) {
            ultimoError = "Datos numéricos inválidos en el vehículo.";
            return false;
        }
    }

    public boolean cargarVehiculoDesdeArchivo(String placa) {
        if (flota.buscarPorPlaca(placa) != null) {
            return true;
        }
        String[] lineas = archivos.leerLineas("vehiculos.txt");
        for (String linea : lineas) {
            String[] c = linea.split(";", -1);
            if (c.length >= 13 && c[0].equalsIgnoreCase(placa)) {
                String[] datos = new String[13];
                System.arraycopy(c, 0, datos, 0, 13);
                return registrarVehiculo(datos);
            }
        }
        ultimoError = "No se encontró la placa " + placa + " en vehiculos.txt.";
        return false;
    }

    public String cargarVehiculo(String placa) {
        return cargarPaquetesEnVehiculo(placa);
    }

    public String cargarPaquetesEnVehiculo(String placa) {
        NodoVehiculo v = flota.buscarPorPlaca(placa);
        if (v == null) {
            return "No existe el vehículo " + placa + ".";
        }
        NodoSede bodega = red.buscarSede(v.getSedeBase());
        if (bodega == null) {
            return "La sede base " + v.getSedeBase() + " no existe.";
        }
        int cargados = v.cargarPaquetes(bodega, red);
        if (cargados == 0) {
            return "No se pudo cargar ningún paquete. Verifique capacidad, estado "
                    + "DISPONIBLE, ruta habilitada o regla de moto (máx. 10 kg y no frágil).";
        }
        return "Se cargaron " + cargados + " paquetes al vehículo " + placa + ".\n"
                + "Peso en pila: " + String.format("%.1f", v.getPesoCarga())
                + " kg de " + v.getCapacidadKg() + " kg.\n\n"
                + v.getCarga().recorrer();
    }

    public String despacharVehiculo(String placa) {
        NodoVehiculo v = flota.buscarPorPlaca(placa);
        if (v == null) {
            return "No existe el vehículo " + placa + ".";
        }
        if (!v.despachar()) {
            return "El vehículo " + placa + " no está DISPONIBLE o no tiene paquetes cargados.";
        }
        if (v.getConductor() != null) {
            notificador.enviarCorreo(v.getConductor().getCorreo(),
                    "Despacho de vehículo " + placa,
                    "El vehículo " + placa + " salió de " + v.getSedeBase()
                    + " con " + v.getCarga().getTamanio() + " paquetes.");
        }
        return "Vehículo " + placa + " despachado desde " + v.getSedeBase() + ".\n\n"
                + v.getCarga().recorrer();
    }

    public String registrarEntrega(String placa, String codigoSede) {
        NodoVehiculo v = flota.buscarPorPlaca(placa);
        if (v == null) {
            return "No existe el vehículo " + placa + ".";
        }
        NodoSede sede = red.buscarSede(codigoSede);
        if (sede == null) {
            return "No existe la sede " + codigoSede + ".";
        }
        if (!v.getEstado().equalsIgnoreCase("EN_RUTA")) {
            return "El vehículo " + placa + " no está EN_RUTA. Despáchelo primero.";
        }
        if (!v.tienePaquetesPara(sede.getCodigo())) {
            return "El vehículo " + placa + " no lleva paquetes para la sede "
                    + sede.getCodigo() + ".";
        }
        Ruta ruta = red.rutaMasCorta(v.getSedeBase(), sede.getCodigo());
        if (ruta == null) {
            return "No hay ruta habilitada desde " + v.getSedeBase()
                    + " hasta " + sede.getCodigo() + ".";
        }
        double km = ruta.getDistanciaTotal();
        int entregados = v.registrarEntrega(sede);
        v.setKmRecorridos(v.getKmRecorridos() + km);
        double combustible = v.calcularCostoCombustible(km, precioGalon);

        StringBuilder sb = new StringBuilder();
        sb.append("Entrega registrada en ").append(sede.getCodigo()).append(".\n");
        sb.append("Paquetes entregados: ").append(entregados).append("\n");
        sb.append("Paquetes restantes en el vehículo: ").append(v.getCarga().getTamanio()).append("\n");
        sb.append("Recorrido: ").append(String.format("%.1f", km)).append(" km\n");
        sb.append("Costo de combustible: $").append(String.format("%.0f", combustible)).append("\n");
        sb.append("Estado del vehículo: ").append(v.getEstado()).append("\n");
        return sb.toString();
    }

    public String recorrerFlota(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()
                || filtro.equalsIgnoreCase("TODOS")) {
            return flota.recorrerAdelante();
        }
        if (filtro.equalsIgnoreCase("DISPONIBLE")
                || filtro.equalsIgnoreCase("EN_RUTA")
                || filtro.equalsIgnoreCase("MANTENIMIENTO")) {
            return flota.filtrarPorEstado(filtro);
        }
        return flota.filtrarPorSede(filtro);
    }

    public NodoPaquete admitirPaquete(String[] d) {
        ultimoError = "";
        if (d == null || d.length < 14) {
            ultimoError = "Faltan datos del paquete.";
            return null;
        }
        String guia = generarGuiaUnica();
        return admitirInterno(d, guia);
    }

    public NodoPaquete admitirPaqueteConGuia(String[] d) {
        ultimoError = "";
        if (d == null || d.length < 15) {
            ultimoError = "Faltan datos del paquete.";
            return null;
        }
        String[] datos = new String[14];
        System.arraycopy(d, 1, datos, 0, 14);
        return admitirInterno(datos, d[0].trim());
    }

    private NodoPaquete admitirInterno(String[] d, String guia) {
        Cliente remitente = clienteManager.buscarPorDocumento(d[0]);
        if (remitente == null) {
            ultimoError = "No existe el cliente remitente " + d[0] + ".";
            return null;
        }
        NodoSede origen = red.buscarSede(d[5]);
        NodoSede destino = red.buscarSede(d[6]);
        if (origen == null || destino == null) {
            ultimoError = "La sede origen o destino no existe.";
            return null;
        }
        if (paquetes.buscar(guia) != null) {
            ultimoError = "Ya existe un paquete con la guía " + guia + ".";
            return null;
        }

        NodoPaquete p;
        try {
            p = new NodoPaquete(guia, d[0].trim(), d[1].trim(), d[2].trim(),
                    d[3].trim(), d[4].trim(), d[5].trim(), d[6].trim(),
                    numero(d[7]), numero(d[8]), numero(d[9]), numero(d[10]),
                    numero(d[11]), d[12].trim(), esSi(d[13]));
        } catch (NumberFormatException e) {
            ultimoError = "Datos numéricos inválidos en el paquete.";
            return null;
        }

        double pesoFacturable = p.calcularPesoFacturable();
        if (p.getPesoKg() <= 0 || p.getLargoCm() <= 0 || p.getAnchoCm() <= 0
                || p.getAltoCm() <= 0 || p.getValorDeclarado() < 0) {
            ultimoError = "Peso, dimensiones y valor declarado deben ser válidos.";
            return null;
        }
        if (origen.capacidadDisponible() < pesoFacturable) {
            ultimoError = "La bodega " + origen.getCodigo() + " no tiene capacidad. "
                    + "El paquete NO se guardó.";
            notificador.notificarSede(origen.getCorreoCoordinador(),
                    "Paquete rechazado por capacidad",
                    "No hay espacio para " + pesoFacturable + " kg en " + origen.getCodigo() + ".");
            return null;
        }

        Ruta ruta = red.rutaMasCorta(origen.getCodigo(), destino.getCodigo());
        if (ruta == null) {
            ultimoError = "No hay camino de vías HABILITADAS entre "
                    + origen.getCodigo() + " y " + destino.getCodigo() + ".";
            return null;
        }

        p.setCostoEnvio(p.calcularCostoEnvio(ruta.getDistanciaTotal()));
        p.setEstado("EN_BODEGA");
        p.agregarEvento(origen.getCodigo(), "Paquete admitido. Costo: $"
                + String.format("%.0f", p.getCostoEnvio()), "Sistema");
        p.agregarEvento(origen.getCodigo(), "Ingresado a la bodega", "Sistema");

        paquetes.insertar(p);
        remitente.agregarGuia(guia);
        origen.encolarPaquete(p);

        if (origen.calcularOcupacion() > 80) {
            notificador.notificarSede(origen.getCorreoCoordinador(),
                    "Alerta de ocupación de bodega",
                    "La bodega " + origen.getCodigo() + " está al "
                    + String.format("%.1f", origen.calcularOcupacion()) + "%.");
        }

        notificador.enviarCorreo(p.getCorreoDestinatario(),
                "Paquete " + guia + " admitido",
                "Su paquete fue admitido en " + origen.getCodigo()
                + " y será enviado a " + destino.getCodigo() + ".");
        return p;
    }

    public String rastrearPaquete(String guia) {
        NodoPaquete p = paquetes.buscar(guia);
        if (p == null) {
            return "No existe el paquete " + guia + ".";
        }
        return p.toString() + "\n\nHISTORIAL\n" + p.getHistorial().recorrer();
    }

    public String listarPaquetes(String estado) {
        if (estado == null || estado.trim().isEmpty()
                || estado.equalsIgnoreCase("TODOS")) {
            if (paquetes.estaVacio()) {
                return "No hay paquetes registrados.";
            }
            return paquetes.inOrden();
        }
        return paquetes.listarPorEstado(estado);
    }

    public String verEstadisticasPaquetes() {
        String[] estados = {"EN_BODEGA", "EN_VEHICULO", "EN_RUTA", "ENTREGADO"};
        int[] cuentas = new int[estados.length];
        int[] total = new int[1];
        int[] vencidos = new int[1];
        double[] ingresos = new double[1];
        acumular(paquetes.getRaiz(), estados, cuentas, total, vencidos, ingresos);

        StringBuilder sb = new StringBuilder("ESTADÍSTICAS DE PAQUETES\n");
        sb.append("Total: ").append(total[0]).append("\n");
        for (int i = 0; i < estados.length; i++) {
            sb.append(estados[i]).append(": ").append(cuentas[i]).append("\n");
        }
        sb.append("Vencidos sin entregar: ").append(vencidos[0]).append("\n");
        sb.append("Ingresos por entregas: $").append(String.format("%.0f", ingresos[0])).append("\n");
        sb.append("Altura del árbol: ").append(paquetes.altura()).append("\n");
        sb.append("Hojas: ").append(paquetes.contarHojas()).append("\n");
        sb.append("Guía menor: ").append(paquetes.guiaMenor()).append("\n");
        sb.append("Guía mayor: ").append(paquetes.guiaMayor()).append("\n");
        return sb.toString();
    }

    private void acumular(NodoPaquete aux, String[] estados, int[] cuentas,
            int[] total, int[] vencidos, double[] ingresos) {
        if (aux == null) {
            return;
        }
        acumular(aux.getIzq(), estados, cuentas, total, vencidos, ingresos);
        total[0]++;
        for (int i = 0; i < estados.length; i++) {
            if (aux.getEstado().equalsIgnoreCase(estados[i])) {
                cuentas[i]++;
                break;
            }
        }
        if (aux.estaVencido(LocalDate.now()) && !aux.getEstado().equalsIgnoreCase("ENTREGADO")) {
            vencidos[0]++;
        }
        if (aux.getEstado().equalsIgnoreCase("ENTREGADO")) {
            ingresos[0] += aux.getCostoEnvio();
        }
        acumular(aux.getDer(), estados, cuentas, total, vencidos, ingresos);
    }

    public int cargarPaquetesIniciales() {
        String[] lineas = archivos.leerLineas("paquetes.txt");
        int cargados = 0;
        for (String linea : lineas) {
            String[] c = linea.split(";", -1);
            if (c.length >= 15) {
                if (admitirPaqueteConGuia(c) != null) {
                    cargados++;
                }
            }
        }
        return cargados;
    }

    public int cargarPQRs() {
        String[] lineas = archivos.leerLineas("pqr.txt");
        int cargadas = 0;
        for (String linea : lineas) {
            String[] c = linea.split(";", -1);
            if (c.length < 7) {
                continue;
            }
            if (!c[4].equalsIgnoreCase("ABIERTA")) {
                continue;
            }
            if (clienteManager.buscarPorDocumento(c[1]) == null) {
                continue;
            }
            LocalDateTime fecha;
            try {
                fecha = LocalDateTime.parse(c[6].trim());
            } catch (Exception e) {
                fecha = LocalDateTime.now();
            }
            NodoPQR q = new NodoPQR(c[0].trim(), c[1].trim(), c[2].trim(),
                    c[3].trim(), c[5].trim(), fecha);
            q.agregarSeguimiento("Sistema", "PQR cargada desde archivo");
            pqrs.encolar(q);
            cargadas++;
        }
        return cargadas;
    }

    public boolean radicarPQR(String[] d) {
        ultimoError = "";
        if (d == null || d.length < 4) {
            ultimoError = "Faltan datos de la PQR.";
            return false;
        }
        if (clienteManager.buscarPorDocumento(d[0]) == null) {
            ultimoError = "No existe el cliente " + d[0] + ".";
            return false;
        }
        if (d[1] != null && !d[1].trim().isEmpty() && paquetes.buscar(d[1]) == null) {
            ultimoError = "No existe el paquete " + d[1] + ".";
            return false;
        }
        String radicado;
        do {
            radicado = "PQR-" + (100000 + random.nextInt(900000));
        } while (existeRadicado(radicado));

        NodoPQR q = new NodoPQR(radicado, d[0].trim(), d[1].trim(),
                d[2].trim(), d[3].trim(), LocalDateTime.now());
        q.agregarSeguimiento("Cliente", "Radicación de la PQR");
        pqrs.encolar(q);
        notificador.notificarSede(notificador.getCorreoCoordinador(),
                "Nueva PQR " + radicado,
                "Cliente " + q.getDocumentoCliente() + " radicó " + q.getTipo()
                + ": " + q.getDescripcion());
        return true;
    }

    private boolean existeRadicado(String radicado) {
        NodoPQR aux = pqrs.getFrente();
        while (aux != null) {
            if (aux.getRadicado().equalsIgnoreCase(radicado)) {
                return true;
            }
            aux = aux.getSig();
        }
        return false;
    }

    public String atenderPQR(String funcionario, String nota, boolean cerrar) {
        NodoPQR q = pqrs.desencolar();
        if (q == null) {
            return null;
        }
        q.agregarSeguimiento(funcionario, nota);
        if (cerrar) {
            q.cerrar("");
        } else {
            pqrs.encolar(q);
        }
        String estado = cerrar ? "CERRADA" : "ABIERTA (reenviada al final de la cola)";
        return "PQR " + q.getRadicado() + " -> " + estado + "\n\n" + q;
    }

    public String verFrentePQR() {
        NodoPQR q = pqrs.verFrente();
        if (q == null) {
            return null;
        }
        return q.toString();
    }

    public String verColaPQR() {
        return pqrs.recorrer()
                + "\nAbiertas por tipo: QUEJA=" + pqrs.contarAbiertasPorTipo("QUEJA")
                + ", RECLAMO=" + pqrs.contarAbiertasPorTipo("RECLAMO")
                + ", SUGERENCIA=" + pqrs.contarAbiertasPorTipo("SUGERENCIA")
                + ", FELICITACION=" + pqrs.contarAbiertasPorTipo("FELICITACION");
    }

    public String generarInforme() {
        LocalDate hoy = LocalDate.now();
        String nombre = "informe_" + hoy + ".txt";
        StringBuilder sb = new StringBuilder();
        sb.append("RUTAEXPRESS ANTIOQUIA\n");
        sb.append("Informe generado: ").append(LocalDateTime.now()).append("\n");
        sb.append("Precio del galón: $").append(String.format("%.0f", precioGalon)).append("\n\n");
        sb.append("Red: ").append(red.getCantidadSedes()).append(" sedes, ")
                .append(red.getCantidadVias()).append(" vías\n");
        sb.append("Clientes: ").append(clienteManager.listar().Size()).append("\n");
        sb.append("Paquetes: ").append(paquetes.cantidad()).append("\n");
        sb.append("Altura del árbol: ").append(paquetes.altura())
                .append(" | Hojas: ").append(paquetes.contarHojas()).append("\n");
        sb.append("Vehículos: ").append(flota.getTamanio()).append("\n");
        sb.append("PQR pendientes: ").append(pqrs.getTamanio()).append("\n");
        sb.append("Sedes aisladas: ").append(red.sedesAisladas().getTamanio()).append("\n\n");
        sb.append(verEstadisticasPaquetes()).append("\n");
        sb.append("DETALLE DE PAQUETES\n");
        sb.append(paquetes.inOrden()).append("\n");
        sb.append("DETALLE DE FLOTA\n");
        sb.append(flota.recorrerAdelante()).append("\n");
        sb.append("PQR EN COLA\n");
        sb.append(pqrs.recorrer());

        archivos.escribirTexto(nombre, sb.toString());
        return "Informe generado en " + nombre + ".\n\n" + sb;
    }

    private String generarGuiaUnica() {
        for (int i = 0; i < 10000; i++) {
            int numero = 100000000 + random.nextInt(900000000);
            String guia = String.valueOf(numero);
            if (paquetes.buscar(guia) == null) {
                return guia;
            }
        }
        String marca = String.valueOf(System.currentTimeMillis());
        return marca.substring(marca.length() - 9);
    }

    private double numero(String valor) {
        return Double.parseDouble(valor.trim().replace(",", "."));
    }

    private boolean esSi(String valor) {
        if (valor == null) {
            return false;
        }
        String v = valor.trim();
        return v.equalsIgnoreCase("S") || v.equalsIgnoreCase("SI")
                || v.equalsIgnoreCase("SÍ") || v.equalsIgnoreCase("TRUE")
                || v.equals("1");
    }

    private void registrarError(String archivo, String mensaje) {
        notificador.registrarFallo(archivo, mensaje);
    }

    public GrafoRed getRed() {
        return red;
    }

    public ClienteManager getClienteManager() {
        return clienteManager;
    }

    public String listarClientes() {
        List clientes = clienteManager.listar();
        return clientes.isEmpty() ? "No hay clientes registrados." : clientes.toString();
    }

    public ArbolPaquetes getPaquetes() {
        return paquetes;
    }

    public ListaDobleVehiculos getFlota() {
        return flota;
    }

    public ColaPQR getPqrs() {
        return pqrs;
    }

    public GestorNotificaciones getNotificador() {
        return notificador;
    }

    public GestorArchivos getArchivos() {
        return archivos;
    }

    public double getPrecioGalon() {
        return precioGalon;
    }

    public void setPrecioGalon(double precioGalon) {
        if (precioGalon > 0) {
            this.precioGalon = precioGalon;
        }
    }

    public String getUltimoError() {
        return ultimoError;
    }
}
