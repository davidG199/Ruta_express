package com.mycompany.rutaexpress.domain.network;

import com.mycompany.rutaexpress.domain.parcel.Paquete;
import com.mycompany.rutaexpress.estructures.queues.Cola;
import com.mycompany.rutaexpress.estructures.queues.NodoCola;

public class Sede {
    private String codigo;
    private String nombre;
    private String municipio;
    private String direccion;
    private String tipo;
    private String telefono;
    private String correoCoordinador;
    private double capacidadBodegaKg;
    private double pesoAlmacenado;
    private final Cola<Paquete> despacho;

    public Sede(String codigo, String nombre, String municipio, String direccion,
            String tipo, String telefono, String correoCoordinador, double capacidadBodegaKg) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.municipio = municipio;
        this.direccion = direccion;
        this.tipo = tipo;
        this.telefono = telefono;
        this.correoCoordinador = correoCoordinador;
        this.capacidadBodegaKg = capacidadBodegaKg;
        this.pesoAlmacenado = 0;
        this.despacho = new Cola<>();
    }

    public void encolarPaquete(Paquete paquete) {
        if (paquete == null) {
            return;
        }
        despacho.encolar(paquete);
        pesoAlmacenado += paquete.calcularPesoFacturable();
    }

    public Paquete desencolarPaquete() {
        Paquete paquete = despacho.desencolar();
        if (paquete != null) {
            pesoAlmacenado -= paquete.calcularPesoFacturable();
            if (pesoAlmacenado < 0) {
                pesoAlmacenado = 0;
            }
        }
        return paquete;
    }

    public double pesoAlmacenado() {
        return pesoAlmacenado;
    }

    public double getPesoAlmacenado() {
        return pesoAlmacenado;
    }

    public double calcularOcupacion() {
        if (capacidadBodegaKg <= 0) {
            return 100;
        }
        return (pesoAlmacenado / capacidadBodegaKg) * 100;
    }

    public double capacidadDisponible() {
        double disponible = capacidadBodegaKg - pesoAlmacenado;
        return Math.max(disponible, 0);
    }

    public boolean estaSaturada() {
        return capacidadDisponible() <= 0;
    }

    public String recorrerDespacho() {
        if (despacho.estaVacia()) {
            return "Cola de despacho vacía.";
        }
        StringBuilder resultado = new StringBuilder();
        NodoCola<Paquete> actual = despacho.getFrente();
        int i = 1;
        while (actual != null) {
            Paquete paquete = actual.getValor();
            resultado.append(i).append(". ").append(paquete.getGuia())
                    .append(" -> ").append(paquete.getSedeDestino()).append("\n");
            actual = actual.getSiguiente();
            i++;
        }
        return resultado.toString();
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreoCoordinador() { return correoCoordinador; }
    public void setCorreoCoordinador(String correoCoordinador) { this.correoCoordinador = correoCoordinador; }
    public double getCapacidadBodegaKg() { return capacidadBodegaKg; }
    public void setCapacidadBodegaKg(double capacidadBodegaKg) { this.capacidadBodegaKg = capacidadBodegaKg; }
    public Cola<Paquete> getDespacho() { return despacho; }

    @Override
    public String toString() {
        return codigo + " | " + nombre + " | " + municipio
                + " | Bodega: " + String.format("%.1f", pesoAlmacenado) + "/"
                + String.format("%.1f", capacidadBodegaKg) + " kg ("
                + String.format("%.1f", calcularOcupacion()) + "%)"
                + " | Cola: " + despacho.getTamanio();
    }
}
