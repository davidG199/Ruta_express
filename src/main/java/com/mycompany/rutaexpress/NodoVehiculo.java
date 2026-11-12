package com.mycompany.rutaexpress;

public class NodoVehiculo {
    private String placa;
    private String tipo;
    private String sedeBase;
    private String estado;
    private double capacidadKg;
    private double capacidadM3;
    private double rendimientoKmGalon;
    private double kmRecorridos;
    private Conductor conductor;
    private PilaPaquetes carga;
    private NodoVehiculo ant;
    private NodoVehiculo sig;

    public NodoVehiculo(String placa, String tipo, String sedeBase, String estado,
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
        this.carga = new PilaPaquetes();
        this.ant = null;
        this.sig = null;
    }

    public int cargarPaquetes(NodoSede bodega, GrafoRed red) {
        if (bodega == null || red == null || !estado.equalsIgnoreCase("DISPONIBLE")) {
            return 0;
        }
        int cargados = 0;
        while (!bodega.getDespacho().estaVacia()) {
            NodoPaquete p = bodega.getDespacho().verFrente();
            if (!puedeLlevar(p)) {
                break;
            }
            if (!red.existeCamino(sedeBase, p.getSedeDestino())) {
                break;
            }
            bodega.desencolarPaquete();
            p.setEstado("EN_VEHICULO");
            p.agregarEvento(sedeBase, "Cargado al vehículo " + placa,
                    conductor == null ? "Sistema" : conductor.getNombre());
            carga.apilar(p);
            cargados++;
        }
        return cargados;
    }

    public boolean despachar() {
        if (!estado.equalsIgnoreCase("DISPONIBLE") || carga.estaVacia()) {
            return false;
        }
        estado = "EN_RUTA";
        NodoPilaPaquete aux = carga.getTope();
        while (aux != null) {
            NodoPaquete p = aux.getPaquete();
            p.setEstado("EN_RUTA");
            p.agregarEvento(sedeBase, "Despachado en el vehículo " + placa,
                    conductor == null ? "Sistema" : conductor.getNombre());
            aux = aux.getSig();
        }
        return true;
    }

    public int registrarEntrega(NodoSede sede) {
        if (sede == null) {
            return 0;
        }
        PilaPaquetes temporal = new PilaPaquetes();
        int entregados = 0;
        while (!carga.estaVacia()) {
            NodoPaquete p = carga.desapilar();
            if (p.getSedeDestino().equalsIgnoreCase(sede.getCodigo())) {
                p.setEstado("ENTREGADO");
                p.agregarEvento(sede.getCodigo(), "Entregado al destinatario",
                        conductor == null ? "Sistema" : conductor.getNombre());
                entregados++;
            } else {
                temporal.apilar(p);
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

    public boolean puedeLlevar(NodoPaquete p) {
        if (p == null || !estado.equalsIgnoreCase("DISPONIBLE")) {
            return false;
        }
        double peso = getPesoCarga() + p.calcularPesoFacturable();
        if (peso > capacidadKg) {
            return false;
        }
        if (capacidadM3 > 0) {
            double volumen = getVolumenCarga()
                    + (p.getLargoCm() * p.getAnchoCm() * p.getAltoCm()) / 1000000.0;
            if (volumen > capacidadM3) {
                return false;
            }
        }
        if (tipo.equalsIgnoreCase("MOTO")) {
            if (p.calcularPesoFacturable() > 10 || p.isFragil()) {
                return false;
            }
        }
        return true;
    }

    public double getPesoCarga() {
        double total = 0;
        NodoPilaPaquete aux = carga.getTope();
        while (aux != null) {
            total += aux.getPaquete().calcularPesoFacturable();
            aux = aux.getSig();
        }
        return total;
    }

    public boolean tienePaquetesPara(String codigoSede) {
        if (codigoSede == null) {
            return false;
        }
        NodoPilaPaquete aux = carga.getTope();
        while (aux != null) {
            if (aux.getPaquete().getSedeDestino().equalsIgnoreCase(codigoSede.trim())) {
                return true;
            }
            aux = aux.getSig();
        }
        return false;
    }

    public double getVolumenCarga() {
        double total = 0;
        NodoPilaPaquete aux = carga.getTope();
        while (aux != null) {
            NodoPaquete p = aux.getPaquete();
            total += (p.getLargoCm() * p.getAnchoCm() * p.getAltoCm()) / 1000000.0;
            aux = aux.getSig();
        }
        return total;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getSedeBase() {
        return sedeBase;
    }

    public void setSedeBase(String sedeBase) {
        this.sedeBase = sedeBase;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getCapacidadKg() {
        return capacidadKg;
    }

    public void setCapacidadKg(double capacidadKg) {
        this.capacidadKg = capacidadKg;
    }

    public double getCapacidadM3() {
        return capacidadM3;
    }

    public void setCapacidadM3(double capacidadM3) {
        this.capacidadM3 = capacidadM3;
    }

    public double getRendimientoKmGalon() {
        return rendimientoKmGalon;
    }

    public void setRendimientoKmGalon(double rendimientoKmGalon) {
        this.rendimientoKmGalon = rendimientoKmGalon;
    }

    public double getKmRecorridos() {
        return kmRecorridos;
    }

    public void setKmRecorridos(double kmRecorridos) {
        this.kmRecorridos = kmRecorridos;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }

    public PilaPaquetes getCarga() {
        return carga;
    }

    public void setCarga(PilaPaquetes carga) {
        this.carga = carga;
    }

    public NodoVehiculo getAnt() {
        return ant;
    }

    public void setAnt(NodoVehiculo ant) {
        this.ant = ant;
    }

    public NodoVehiculo getSig() {
        return sig;
    }

    public void setSig(NodoVehiculo sig) {
        this.sig = sig;
    }

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
