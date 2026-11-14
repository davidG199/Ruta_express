package com.mycompany.rutaexpress.main;

import com.mycompany.rutaexpress.domain.client.Cliente;
import com.mycompany.rutaexpress.domain.client.ClientManager;
import com.mycompany.rutaexpress.domain.config.ConfiguracionManager;
import com.mycompany.rutaexpress.domain.network.RedManager;
import com.mycompany.rutaexpress.domain.parcel.Paquete;
import com.mycompany.rutaexpress.domain.parcel.PaqueteManager;
import com.mycompany.rutaexpress.domain.pqr.Pqr;
import com.mycompany.rutaexpress.domain.pqr.PqrManager;
import com.mycompany.rutaexpress.domain.vehicle.Vehiculo;
import com.mycompany.rutaexpress.domain.vehicle.VehiculoManager;
import com.mycompany.rutaexpress.estructures.graphs.GrafoRed;
import com.mycompany.rutaexpress.estructures.lists.ListaDoble;
import com.mycompany.rutaexpress.estructures.queues.Cola;
import com.mycompany.rutaexpress.estructures.trees.ArbolBinario;
import com.mycompany.rutaexpress.files.GeneradorInformes;
import com.mycompany.rutaexpress.files.GestorArchivos;
import com.mycompany.rutaexpress.files.GestorNotificaciones;

/**
 * Fachada del sistema. La lógica de negocio vive en los managers del dominio.
 */
public class SistemaRutaExpress {
    private final GestorArchivos archivos;
    private final GestorNotificaciones notificador;
    private final ConfiguracionManager configuracion;
    private final RedManager redManager;
    private final ClientManager clientManager;
    private final PaqueteManager paqueteManager;
    private final VehiculoManager vehiculoManager;
    private final PqrManager pqrManager;
    private final GeneradorInformes generadorInformes;
    private String ultimoError;

    public SistemaRutaExpress() {
        archivos = new GestorArchivos();
        notificador = new GestorNotificaciones(archivos);
        configuracion = new ConfiguracionManager(archivos, notificador);
        redManager = new RedManager(archivos, notificador);
        clientManager = new ClientManager(notificador);
        paqueteManager = new PaqueteManager(clientManager, redManager, notificador);
        clientManager.setPaqueteManager(paqueteManager);
        vehiculoManager = new VehiculoManager(archivos, redManager, notificador);
        pqrManager = new PqrManager(clientManager, paqueteManager, notificador);
        generadorInformes = new GeneradorInformes(redManager, paqueteManager,
                vehiculoManager, pqrManager, configuracion, archivos);
        ultimoError = "";
    }

    public String inicializar() {
        cargarConfiguracion();
        int sedesCargadas = redManager.cargarRed("red.txt");
        int viasCargadas = redManager.cantidadVias();
        int clientesCargados = clientManager.cargarDesdeArchivo(archivos);
        int vehiculosCargados = vehiculoManager.cargarDesdeArchivo(archivos);
        int paquetesCargados = paqueteManager.cargarIniciales(archivos);
        int pqrsCargadas = pqrManager.cargarDesdeArchivo(archivos);

        return "RutaExpress Antioquia iniciado.\n\n"
                + "Sedes: " + sedesCargadas + "\n"
                + "Vías: " + viasCargadas + "\n"
                + "Clientes: " + clientesCargados + "\n"
                + "Vehículos: " + vehiculosCargados + "\n"
                + "Paquetes: " + paquetesCargados + "\n"
                + "PQR abiertas: " + pqrsCargadas + "\n"
                + "Precio del galón: $" + String.format("%.0f", getPrecioGalon());
    }

    public void cargarConfiguracion() {
        configuracion.cargar();
    }

    public int cargarRed(String ruta) {
        return redManager.cargarRed(ruta);
    }

    public boolean registrarSede(String[] datos) {
        boolean registrada = redManager.registrarSede(datos);
        ultimoError = redManager.getUltimoError();
        return registrada;
    }

    public boolean registrarVia(String[] datos) {
        boolean registrada = redManager.registrarVia(datos);
        ultimoError = redManager.getUltimoError();
        return registrada;
    }

    public String cambiarEstadoVia(String origen, String destino, String estado) {
        return redManager.cambiarEstadoViaConNotificacion(origen, destino, estado);
    }

    public String mostrarRed() {
        return redManager.mostrarRed();
    }

    public String consultarMejorRuta(String origen, String destino) {
        return redManager.consultarMejorRuta(origen, destino);
    }

    public String sedesAisladas() {
        return redManager.mostrarSedesAisladas();
    }

    public String verBodega(String codigoSede) {
        return redManager.verBodega(codigoSede);
    }

    public int cargarClientes() {
        return clientManager.cargarDesdeArchivo(archivos);
    }

    public boolean registrarCliente(Cliente cliente) {
        if (cliente == null) {
            ultimoError = "Faltan datos del cliente.";
            return false;
        }
        boolean registrado = clientManager.registrar(cliente);
        ultimoError = clientManager.getUltimoError();
        return registrado;
    }

    public int cargarVehiculos() {
        return vehiculoManager.cargarDesdeArchivo(archivos);
    }

    public boolean registrarVehiculo(String[] datos) {
        boolean registrado = vehiculoManager.registrar(datos);
        ultimoError = vehiculoManager.getUltimoError();
        return registrado;
    }

    public boolean cargarVehiculoDesdeArchivo(String placa) {
        boolean cargado = vehiculoManager.cargarDesdeArchivoPorPlaca(placa, archivos);
        ultimoError = vehiculoManager.getUltimoError();
        return cargado;
    }

    public String cargarVehiculo(String placa) {
        return cargarPaquetesEnVehiculo(placa);
    }

    public String cargarPaquetesEnVehiculo(String placa) {
        return vehiculoManager.cargarPaquetes(placa);
    }

    public String despacharVehiculo(String placa) {
        return vehiculoManager.despachar(placa);
    }

    public String registrarEntrega(String placa, String codigoSede) {
        return vehiculoManager.registrarEntrega(placa, codigoSede, getPrecioGalon());
    }

    public String recorrerFlota(String filtro) {
        return vehiculoManager.recorrerFlota(filtro);
    }

    public Paquete admitirPaquete(String[] datos) {
        Paquete paquete = paqueteManager.admitir(datos);
        ultimoError = paqueteManager.getUltimoError();
        return paquete;
    }

    public Paquete admitirPaqueteConGuia(String[] datos) {
        Paquete paquete = paqueteManager.admitirConGuia(datos);
        ultimoError = paqueteManager.getUltimoError();
        return paquete;
    }

    public String rastrearPaquete(String guia) {
        return paqueteManager.rastrear(guia);
    }

    public String listarPaquetes(String estado) {
        if (estado == null || estado.trim().isEmpty()
                || estado.equalsIgnoreCase("TODOS")) {
            return paqueteManager.listar();
        }
        return paqueteManager.listarPorEstado(estado);
    }

    public String verEstadisticasPaquetes() {
        return paqueteManager.verEstadisticas();
    }

    public int cargarPaquetesIniciales() {
        return paqueteManager.cargarIniciales(archivos);
    }

    public int cargarPQRs() {
        return pqrManager.cargarDesdeArchivo(archivos);
    }

    public boolean radicarPQR(String[] datos) {
        boolean radicada = pqrManager.radicar(datos);
        ultimoError = pqrManager.getUltimoError();
        return radicada;
    }

    public String atenderPQR(String funcionario, String nota, boolean cerrar) {
        return pqrManager.atender(funcionario, nota, cerrar);
    }

    public String verFrentePQR() {
        return pqrManager.verFrenteTexto();
    }

    public String verColaPQR() {
        return pqrManager.verCola();
    }

    public String generarInforme() {
        return generadorInformes.generar();
    }

    public GrafoRed getRed() {
        return redManager.getRed();
    }

    public RedManager getRedManager() {
        return redManager;
    }

    public ClientManager getClienteManager() {
        return clientManager;
    }

    public PaqueteManager getPaqueteManager() {
        return paqueteManager;
    }

    public ArbolBinario<Paquete> getPaquetes() {
        return paqueteManager.getPaquetes();
    }

    public VehiculoManager getVehiculoManager() {
        return vehiculoManager;
    }

    public ListaDoble<Vehiculo> getFlota() {
        return vehiculoManager.getVehiculos();
    }

    public PqrManager getPqrManager() {
        return pqrManager;
    }

    public Cola<Pqr> getPqrs() {
        return pqrManager.getPqrs();
    }

    public ConfiguracionManager getConfiguracion() {
        return configuracion;
    }

    public GestorNotificaciones getNotificador() {
        return notificador;
    }

    public GestorArchivos getArchivos() {
        return archivos;
    }

    public double getPrecioGalon() {
        return configuracion.getPrecioGalon();
    }

    public void setPrecioGalon(double precioGalon) {
        configuracion.setPrecioGalon(precioGalon);
    }

    public String getUltimoError() {
        return ultimoError;
    }
}
