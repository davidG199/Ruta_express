package com.mycompany.rutaexpress.domain.vehicle;

import com.mycompany.rutaexpress.estructures.lists.ListaDoble;

public class VehiculoManager {
    private final ListaDoble<Vehiculo> vehiculos;
    private String ultimoError;

    public VehiculoManager() {
        vehiculos = new ListaDoble<>(java.util.Comparator.comparing(Vehiculo::getPlaca, String.CASE_INSENSITIVE_ORDER));
        ultimoError = "";
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

    public Vehiculo buscarPorPlaca(String placa) {
        if (placa == null) return null;
        return vehiculos.buscar(new Vehiculo(placa, "", "", "", 0, 0, 0, 0));
    }

    public boolean eliminar(String placa) {
        return vehiculos.eliminar(new Vehiculo(placa, "", "", "", 0, 0, 0, 0));
    }

    public String listar() {
        StringBuilder resultado = new StringBuilder();
        vehiculos.recorrerAdelante(v -> resultado.append(v).append("\n\n"));
        return resultado.length() == 0 ? "No hay vehículos registrados." : resultado.toString();
    }

    public String listarDesdeElFinal() {
        StringBuilder resultado = new StringBuilder();
        vehiculos.recorrerAtras(v -> resultado.append(v).append("\n\n"));
        return resultado.length() == 0 ? "No hay vehículos registrados." : resultado.toString();
    }

    public String listarPorSede(String sede) {
        return filtrar(v -> v.getSedeBase().equalsIgnoreCase(sede), "sede " + sede);
    }

    public String listarPorEstado(String estado) {
        return filtrar(v -> v.getEstado().equalsIgnoreCase(estado), "estado " + estado);
    }

    private String filtrar(java.util.function.Predicate<Vehiculo> criterio, String descripcion) {
        StringBuilder resultado = new StringBuilder();
        vehiculos.recorrerAdelante(v -> {
            if (criterio.test(v)) resultado.append(v).append("\n\n");
        });
        return resultado.length() == 0 ? "No hay vehículos en " + descripcion + "." : resultado.toString();
    }
    public ListaDoble<Vehiculo> getVehiculos() { return vehiculos; }
    public String getUltimoError() { return ultimoError; }
}
