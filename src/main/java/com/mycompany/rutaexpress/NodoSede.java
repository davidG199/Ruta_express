package com.mycompany.rutaexpress;

public class NodoSede {
    private String codigo;
    private String nombre;
    private String municipio;
    private String direccion;
    private String tipo;
    private String telefono;
    private String correoCoordinador;
    private double capacidadBodegaKg;
    private double pesoAlmacenado;
    private ColaPaquetes despacho;
    private ListaSimpleVias vias;
    private NodoSede sig;

    public NodoSede(String codigo, String nombre, String municipio, String direccion,
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
        this.despacho = new ColaPaquetes();
        this.vias = new ListaSimpleVias();
        this.sig = null;
    }

    public void encolarPaquete(NodoPaquete p) {
        if (p != null) {
            despacho.encolar(p);
            pesoAlmacenado += p.calcularPesoFacturable();
        }
    }

    public NodoPaquete desencolarPaquete() {
        NodoPaquete p = despacho.desencolar();
        if (p != null) {
            pesoAlmacenado -= p.calcularPesoFacturable();
            if (pesoAlmacenado < 0) {
                pesoAlmacenado = 0;
            }
        }
        return p;
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

    public void agregarVia(NodoVia v) {
        vias.agregar(v);
    }

    public NodoVia buscarVia(String destino) {
        return vias.buscar(destino);
    }

    public String mostrarVias() {
        return vias.recorrer();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreoCoordinador() {
        return correoCoordinador;
    }

    public void setCorreoCoordinador(String correoCoordinador) {
        this.correoCoordinador = correoCoordinador;
    }

    public double getCapacidadBodegaKg() {
        return capacidadBodegaKg;
    }

    public void setCapacidadBodegaKg(double capacidadBodegaKg) {
        this.capacidadBodegaKg = capacidadBodegaKg;
    }

    public ColaPaquetes getDespacho() {
        return despacho;
    }

    public void setDespacho(ColaPaquetes despacho) {
        this.despacho = despacho;
    }

    public ListaSimpleVias getVias() {
        return vias;
    }

    public void setVias(ListaSimpleVias vias) {
        this.vias = vias;
    }

    public NodoSede getSig() {
        return sig;
    }

    public void setSig(NodoSede sig) {
        this.sig = sig;
    }

    @Override
    public String toString() {
        return codigo + " | " + nombre + " | " + municipio
                + " | Bodega: " + String.format("%.1f", pesoAlmacenado) + "/"
                + String.format("%.1f", capacidadBodegaKg) + " kg ("
                + String.format("%.1f", calcularOcupacion()) + "%)"
                + " | Cola: " + despacho.getTamanio();
    }
}
