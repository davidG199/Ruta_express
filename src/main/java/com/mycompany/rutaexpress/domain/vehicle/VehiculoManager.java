package com.mycompany.rutaexpress.domain.vehicle;

import com.mycompany.rutaexpress.domain.network.RedManager;
import com.mycompany.rutaexpress.domain.network.Ruta;
import com.mycompany.rutaexpress.domain.network.Sede;
import com.mycompany.rutaexpress.estructures.lists.ListaDoble;
import com.mycompany.rutaexpress.files.GestorArchivos;
import com.mycompany.rutaexpress.files.GestorNotificaciones;

/**
 * Gestiona la flota, la carga de paquetes y las entregas.
 */
public class VehiculoManager {
    private final ListaDoble<Vehiculo> vehiculos;
    private final RedManager redManager;
    private final GestorNotificaciones notificador;
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
                + " kg de " + vehiculo.getCapacidadKg() + " kg.\n\n"
                + vehiculo.recorrerCarga();
    }

    public String despachar(String placa) {
        Vehiculo vehiculo = buscarPorPlaca(placa);
        if (vehiculo == null) {
            return "No existe el vehículo " + placa + ".";
        }
        if (!vehiculo.despachar()) {
            return "El vehículo " + placa
                    + " no está DISPONIBLE o no tiene paquetes cargados.";
        }
        if (vehiculo.getConductor() != null) {
            notificador.enviarCorreo(vehiculo.getConductor().getCorreo(),
                    "Despacho de vehículo " + placa,
                    "El vehículo " + placa + " salió de " + vehiculo.getSedeBase()
                    + " con " + vehiculo.getCarga().getTamanio() + " paquetes.");
        }
        return "Vehículo " + placa + " despachado desde "
                + vehiculo.getSedeBase() + ".\n\n" + vehiculo.recorrerCarga();
    }

    public String registrarEntrega(String placa, String codigoSede, double precioGalon) {
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
        if (!vehiculo.tienePaquetesPara(sede.getCodigo())) {
            return "El vehículo " + placa + " no lleva paquetes para la sede "
                    + sede.getCodigo() + ".";
        }
        Ruta ruta = redManager.calcularRutaMasCorta(vehiculo.getSedeBase(), sede.getCodigo());
        if (ruta == null) {
            return "No hay ruta habilitada desde " + vehiculo.getSedeBase()
                    + " hasta " + sede.getCodigo() + ".";
        }
        double km = ruta.getDistanciaTotal();
        int entregados = vehiculo.registrarEntrega(sede);
        vehiculo.setKmRecorridos(vehiculo.getKmRecorridos() + km);
        double combustible = vehiculo.calcularCostoCombustible(km, precioGalon);

        StringBuilder resultado = new StringBuilder();
        resultado.append("Entrega registrada en ").append(sede.getCodigo()).append(".\n");
        resultado.append("Paquetes entregados: ").append(entregados).append("\n");
        resultado.append("Paquetes restantes en el vehículo: ")
                .append(vehiculo.getCarga().getTamanio()).append("\n");
        resultado.append("Recorrido: ").append(String.format("%.1f", km)).append(" km\n");
        resultado.append("Costo de combustible: $")
                .append(String.format("%.0f", combustible)).append("\n");
        resultado.append("Estado del vehículo: ").append(vehiculo.getEstado()).append("\n");
        return resultado.toString();
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

    public ListaDoble<Vehiculo> getVehiculos() { return vehiculos; }
    public int cantidad() { return vehiculos.getTamanio(); }
    public String getUltimoError() { return ultimoError; }

    private double numero(String valor) {
        return Double.parseDouble(valor.trim().replace(",", "."));
    }
}
