package com.mycompany.rutaexpress.domain.vehicle;

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
    }

    public double calcularCostoCombustible(double km, double precioGalon) {
        return rendimientoKmGalon <= 0 ? 0 : (km / rendimientoKmGalon) * precioGalon;
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
}
