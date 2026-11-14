package com.mycompany.rutaexpress.domain.vehicle;

import com.mycompany.rutaexpress.domain.network.Sede;
import com.mycompany.rutaexpress.domain.parcel.Paquete;
import com.mycompany.rutaexpress.estructures.graphs.GrafoRed;
import com.mycompany.rutaexpress.estructures.stacks.NodoPila;
import com.mycompany.rutaexpress.estructures.stacks.Pila;

public class Vehiculo {
    private String placa;
    private String tipo;
    private String sedeBase;
    private String estado;
    private double capacidadKg;
    private double capacidadM3;
    private double rendimientoKmGalon;
    private double kmRecorridos;
    private Conductor conductor;
    private Pila<Paquete> carga;

    public Vehiculo(String placa, String tipo, String sedeBase, String estado,
            double capacidadKg, double capacidadM3, double rendimientoKmGalon,
            double kmRecorridos) {
        this.placa = placa;
        this.tipo = tipo;
        this.sedeBase = sedeBase;
        this.estado = estado;
        this.capacidadKg = capacidadKg;
        this.capacidadM3 = capacidadM3;
        this.rendimientoKmGalon = rendimientoKmGalon;
        this.kmRecorridos = kmRecorridos;
        this.conductor = null;
        this.carga = new Pila<>();
    }

    public int cargarPaquetes(Sede bodega, GrafoRed red) {
        if (bodega == null || red == null || !estado.equalsIgnoreCase("DISPONIBLE")) {
            return 0;
        }
        int cargados = 0;
        while (!bodega.getDespacho().estaVacia()) {
            Paquete paquete = bodega.getDespacho().verFrente();
            if (!puedeLlevar(paquete)) {
                break;
            }
            if (!red.existeCamino(sedeBase, paquete.getSedeDestino())) {
                break;
            }
            bodega.desencolarPaquete();
            paquete.setEstado("EN_VEHICULO");
            paquete.agregarEvento(sedeBase, "Cargado al vehículo " + placa,
                    conductor == null ? "Sistema" : conductor.getNombre());
            carga.apilar(paquete);
            cargados++;
        }
        return cargados;
    }

    public boolean despachar() {
        if (!estado.equalsIgnoreCase("DISPONIBLE") || carga.estaVacia()) {
            return false;
        }
        estado = "EN_RUTA";
        NodoPila<Paquete> aux = carga.getTope();
        while (aux != null) {
            Paquete paquete = aux.getValor();
            paquete.setEstado("EN_RUTA");
            paquete.agregarEvento(sedeBase, "Despachado en el vehículo " + placa,
                    conductor == null ? "Sistema" : conductor.getNombre());
            aux = aux.getSiguiente();
        }
        return true;
    }

    public int registrarEntrega(Sede sede) {
        if (sede == null) {
            return 0;
        }
        Pila<Paquete> temporal = new Pila<>();
        int entregados = 0;
        while (!carga.estaVacia()) {
            Paquete paquete = carga.desapilar();
            if (paquete.getSedeDestino().equalsIgnoreCase(sede.getCodigo())) {
                paquete.setEstado("ENTREGADO");
                paquete.agregarEvento(sede.getCodigo(), "Entregado al destinatario",
                        conductor == null ? "Sistema" : conductor.getNombre());
                entregados++;
            } else {
                temporal.apilar(paquete);
            }
        }
        while (!temporal.estaVacia()) {
            carga.apilar(temporal.desapilar());
        }
        sedeBase = sede.getCodigo();
        estado = carga.estaVacia() ? "DISPONIBLE" : "EN_RUTA";
        return entregados;
    }

    public double calcularCostoCombustible(double km, double precioGalon) {
        if (rendimientoKmGalon <= 0) {
            return 0;
        }
        return (km / rendimientoKmGalon) * precioGalon;
    }

    public boolean puedeLlevar(Paquete paquete) {
        if (paquete == null || !estado.equalsIgnoreCase("DISPONIBLE")) {
            return false;
        }
        double peso = getPesoCarga() + paquete.calcularPesoFacturable();
        if (peso > capacidadKg) {
            return false;
        }
        if (capacidadM3 > 0) {
            double volumen = getVolumenCarga()
                    + (paquete.getLargoCm() * paquete.getAnchoCm() * paquete.getAltoCm()) / 1000000.0;
            if (volumen > capacidadM3) {
                return false;
            }
        }
        if (tipo != null && tipo.equalsIgnoreCase("MOTO")) {
            if (paquete.calcularPesoFacturable() > 10 || paquete.isFragil()) {
                return false;
            }
        }
        return true;
    }

    public double getPesoCarga() {
        double total = 0;
        NodoPila<Paquete> aux = carga.getTope();
        while (aux != null) {
            total += aux.getValor().calcularPesoFacturable();
            aux = aux.getSiguiente();
        }
        return total;
    }

    public boolean tienePaquetesPara(String codigoSede) {
        if (codigoSede == null) {
            return false;
        }
        NodoPila<Paquete> aux = carga.getTope();
        while (aux != null) {
            if (aux.getValor().getSedeDestino().equalsIgnoreCase(codigoSede.trim())) {
                return true;
            }
            aux = aux.getSiguiente();
        }
        return false;
    }

    public double getVolumenCarga() {
        double total = 0;
        NodoPila<Paquete> aux = carga.getTope();
        while (aux != null) {
            Paquete paquete = aux.getValor();
            total += (paquete.getLargoCm() * paquete.getAnchoCm() * paquete.getAltoCm()) / 1000000.0;
            aux = aux.getSiguiente();
        }
        return total;
    }

    public String recorrerCarga() {
        if (carga.estaVacia()) {
            return "Pila vacía.";
        }
        StringBuilder resultado = new StringBuilder();
        NodoPila<Paquete> aux = carga.getTope();
        int i = 1;
        while (aux != null) {
            Paquete paquete = aux.getValor();
            resultado.append(i).append(". ").append(paquete.getGuia())
                    .append(" -> ").append(paquete.getSedeDestino()).append("\n");
            aux = aux.getSiguiente();
            i++;
        }
        return resultado.toString();
    }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getSedeBase() { return sedeBase; }
    public void setSedeBase(String sedeBase) { this.sedeBase = sedeBase; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public double getCapacidadKg() { return capacidadKg; }
    public void setCapacidadKg(double capacidadKg) { this.capacidadKg = capacidadKg; }
    public double getCapacidadM3() { return capacidadM3; }
    public void setCapacidadM3(double capacidadM3) { this.capacidadM3 = capacidadM3; }
    public double getRendimientoKmGalon() { return rendimientoKmGalon; }
    public void setRendimientoKmGalon(double rendimientoKmGalon) { this.rendimientoKmGalon = rendimientoKmGalon; }
    public double getKmRecorridos() { return kmRecorridos; }
    public void setKmRecorridos(double kmRecorridos) { this.kmRecorridos = kmRecorridos; }
    public Conductor getConductor() { return conductor; }
    public void setConductor(Conductor conductor) { this.conductor = conductor; }
    public Pila<Paquete> getCarga() { return carga; }
    public void setCarga(Pila<Paquete> carga) { this.carga = carga; }

    @Override
    public String toString() {
        return "Placa: " + placa + " | " + tipo + " | Sede: " + sedeBase
                + " | Estado: " + estado
                + " | Capacidad: " + capacidadKg + " kg / " + capacidadM3 + " m3"
                + " | Carga: " + carga.getTamanio() + " paquetes, "
                + String.format("%.1f", getPesoCarga()) + " kg"
                + " | Km: " + String.format("%.0f", kmRecorridos)
                + "\n  Conductor: " + (conductor == null ? "Sin asignar" : conductor.toString());
    }
}
