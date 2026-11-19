package com.mycompany.rutaexpress.domain.vehicle;

import com.mycompany.rutaexpress.domain.network.Ruta;
import com.mycompany.rutaexpress.domain.network.Sede;
import com.mycompany.rutaexpress.domain.network.Via;
import com.mycompany.rutaexpress.domain.parcel.Paquete;
import com.mycompany.rutaexpress.estructures.graphs.GrafoRed;
import com.mycompany.rutaexpress.estructures.queues.NodoCola;
import com.mycompany.rutaexpress.estructures.stacks.NodoPila;
import com.mycompany.rutaexpress.estructures.stacks.Pila;

public class Vehiculo {
    private String placa;
    private String tipo;
    private String sedeBase;
    private String sedeActual;
    private String estado;
    private double capacidadKg;
    private double capacidadM3;
    private double rendimientoKmGalon;
    private double kmRecorridos;
    private Conductor conductor;
    private Pila<Paquete> carga;
    private String[] itinerario;
    private int siguienteParada;
    private double kmRutaPlanificada;

    public Vehiculo(String placa, String tipo, String sedeBase, String estado,
            double capacidadKg, double capacidadM3, double rendimientoKmGalon,
            double kmRecorridos) {
        this.placa = placa;
        this.tipo = tipo;
        this.sedeBase = sedeBase;
        this.sedeActual = sedeBase;
        this.estado = estado;
        this.capacidadKg = capacidadKg;
        this.capacidadM3 = capacidadM3;
        this.rendimientoKmGalon = rendimientoKmGalon;
        this.kmRecorridos = kmRecorridos;
        this.conductor = null;
        this.carga = new Pila<>();
        this.itinerario = new String[0];
        this.siguienteParada = 0;
        this.kmRutaPlanificada = 0;
    }

    public int cargarPaquetes(Sede bodega, GrafoRed red) {
        if (bodega == null || red == null || !estado.equalsIgnoreCase("DISPONIBLE")
                || !carga.estaVacia()) {
            return 0;
        }

        int totalCola = bodega.getDespacho().getTamanio();
        if (totalCola == 0) {
            return 0;
        }
        Paquete[] colaOriginal = new Paquete[totalCola];
        NodoCola<Paquete> nodo = bodega.getDespacho().getFrente();
        for (int i = 0; i < totalCola && nodo != null; i++, nodo = nodo.getSiguiente()) {
            colaOriginal[i] = nodo.getValor();
        }
        Paquete[] pendientes = new Paquete[totalCola];
        int cantidad = 0;
        for (Paquete paquete : colaOriginal) {
            if (paquete != null && paquete.getSedeDestino() != null
                    && !paquete.getSedeDestino().equalsIgnoreCase(sedeActual)
                    && red.rutaMasCorta(sedeActual, paquete.getSedeDestino()) != null) {
                pendientes[cantidad++] = paquete;
            }
        }
        if (cantidad == 0) {
            return 0;
        }
        boolean[] seleccionados = new boolean[cantidad];
        while (!bodega.getDespacho().estaVacia()) {
            bodega.desencolarPaquete();
        }

        String[] ordenInicial = calcularOrdenDestinos(pendientes, null,
                cantidad, red, sedeActual);
        double pesoSeleccionado = 0;
        double volumenSeleccionado = 0;
        for (String destino : ordenInicial) {
            for (int i = 0; i < cantidad; i++) {
                Paquete paquete = pendientes[i];
                if (paquete == null || !paquete.getSedeDestino().equalsIgnoreCase(destino)) {
                    continue;
                }
                if (!puedeLlevar(paquete, pesoSeleccionado, volumenSeleccionado)) {
                    continue;
                }
                seleccionados[i] = true;
                pesoSeleccionado += paquete.calcularPesoFacturable();
                volumenSeleccionado += volumen(paquete);
            }
        }

        itinerario = calcularOrdenDestinos(pendientes, seleccionados, cantidad,
                red, sedeActual);
        Paquete[] ordenEntrega = new Paquete[cantidad];
        int cantidadEntrega = 0;
        for (String destino : itinerario) {
            for (int i = 0; i < cantidad; i++) {
                if (seleccionados[i] && pendientes[i].getSedeDestino().equalsIgnoreCase(destino)) {
                    ordenEntrega[cantidadEntrega++] = pendientes[i];
                }
            }
        }

        for (Paquete paquete : colaOriginal) {
            if (paquete != null && !esPaqueteSeleccionado(paquete, pendientes,
                    seleccionados, cantidad)) {
                bodega.encolarPaquete(paquete);
            }
        }
        for (int i = cantidadEntrega - 1; i >= 0; i--) {
            Paquete paquete = ordenEntrega[i];
            paquete.setEstado("EN_VEHICULO");
            paquete.agregarEvento(sedeActual, "Cargado al vehículo " + placa,
                    conductor == null ? "Sistema" : conductor.getNombre());
            carga.apilar(paquete);
        }
        siguienteParada = 0;
        actualizarKmRutaPlanificada(red);
        return cantidadEntrega;
    }

    private boolean esPaqueteSeleccionado(Paquete paquete, Paquete[] pendientes,
            boolean[] seleccionados, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            if (seleccionados[i] && pendientes[i] == paquete) {
                return true;
            }
        }
        return false;
    }

    private String[] calcularOrdenDestinos(Paquete[] paquetes, boolean[] incluidos,
            int cantidad, GrafoRed red, String origen) {
        String[] candidatos = new String[cantidad];
        int cantidadCandidatos = 0;
        for (int i = 0; i < cantidad; i++) {
            Paquete paquete = paquetes[i];
            if (paquete == null || (incluidos != null && !incluidos[i])) {
                continue;
            }
            String destino = paquete.getSedeDestino();
            if (destino == null || destino.equalsIgnoreCase(origen)
                    || red.rutaMasCorta(origen, destino) == null
                    || contiene(candidatos, cantidadCandidatos, destino)) {
                continue;
            }
            candidatos[cantidadCandidatos++] = destino;
        }

        String[] orden = new String[cantidadCandidatos];
        boolean[] visitados = new boolean[cantidadCandidatos];
        int cantidadOrden = 0;
        String actual = origen;
        while (cantidadOrden < cantidadCandidatos) {
            int masCercano = -1;
            double menorDistancia = Double.MAX_VALUE;
            for (int i = 0; i < cantidadCandidatos; i++) {
                if (visitados[i]) {
                    continue;
                }
                Ruta ruta = red.rutaMasCorta(actual, candidatos[i]);
                if (ruta != null && ruta.getDistanciaTotal() < menorDistancia) {
                    menorDistancia = ruta.getDistanciaTotal();
                    masCercano = i;
                }
            }
            if (masCercano < 0) {
                break;
            }

            String destinoElegido = candidatos[masCercano];
            Ruta rutaElegida = red.rutaMasCorta(actual, destinoElegido);
            String proximaParada = destinoElegido;
            if (rutaElegida != null) {
                String[] tramo = rutaElegida.getCodigosSede();
                for (int j = 1; j < tramo.length; j++) {
                    int indiceDestino = indiceDe(candidatos, cantidadCandidatos, tramo[j]);
                    if (indiceDestino >= 0 && !visitados[indiceDestino]) {
                        proximaParada = candidatos[indiceDestino];
                        break;
                    }
                }
            }
            int indiceParada = indiceDe(candidatos, cantidadCandidatos, proximaParada);
            if (indiceParada < 0 || visitados[indiceParada]) {
                break;
            }
            visitados[indiceParada] = true;
            orden[cantidadOrden++] = proximaParada;
            actual = proximaParada;
        }
        if (cantidadOrden < cantidadCandidatos) {
            for (int i = 0; i < cantidadCandidatos; i++) {
                if (!visitados[i]) {
                    orden[cantidadOrden++] = candidatos[i];
                }
            }
        }
        String[] resultado = new String[cantidadOrden];
        System.arraycopy(orden, 0, resultado, 0, cantidadOrden);
        return resultado;
    }

    private int indiceDe(String[] valores, int cantidad, String buscado) {
        for (int i = 0; i < cantidad; i++) {
            if (valores[i].equalsIgnoreCase(buscado)) {
                return i;
            }
        }
        return -1;
    }

    private boolean contiene(String[] valores, int cantidad, String buscado) {
        return indiceDe(valores, cantidad, buscado) >= 0;
    }

    private boolean puedeLlevar(Paquete paquete, double pesoActual, double volumenActual) {
        if (paquete == null) {
            return false;
        }
        double peso = pesoActual + paquete.calcularPesoFacturable();
        if (peso > capacidadKg) {
            return false;
        }
        if (capacidadM3 > 0 && volumenActual + volumen(paquete) > capacidadM3) {
            return false;
        }
        return tipo == null || !tipo.equalsIgnoreCase("MOTO")
                || (paquete.calcularPesoFacturable() <= 10 && !paquete.isFragil());
    }

    private double volumen(Paquete paquete) {
        return (paquete.getLargoCm() * paquete.getAnchoCm() * paquete.getAltoCm())
                / 1000000.0;
    }

    private void actualizarKmRutaPlanificada(GrafoRed red) {
        kmRutaPlanificada = 0;
        String actual = sedeActual;
        for (int i = siguienteParada; i < itinerario.length; i++) {
            Ruta tramo = red.rutaMasCorta(actual, itinerario[i]);
            if (tramo != null) {
                kmRutaPlanificada += tramo.getDistanciaTotal();
                actual = itinerario[i];
            }
        }
    }

    public String resumenRuta(GrafoRed red) {
        if (itinerario.length == 0) {
            return "Sin destinos cargados.";
        }
        StringBuilder resultado = new StringBuilder("Ruta de entrega: ").append(sedeActual);
        String actual = sedeActual;
        for (String parada : itinerario) {
            Ruta tramo = red.rutaMasCorta(actual, parada);
            if (tramo != null) {
                String[] sedes = tramo.getCodigosSede();
                for (int i = 1; i < sedes.length; i++) {
                    Via via = red.buscarVia(sedes[i - 1], sedes[i]);
                    resultado.append(" -> ").append(sedes[i]);
                    if (via != null) {
                        resultado.append(" (")
                                .append(String.format("%.1f", via.getDistanciaKm()))
                                .append(" km)");
                    }
                }
            }
            actual = parada;
        }
        resultado.append("\nDistancia estimada total: ")
                .append(String.format("%.1f", kmRutaPlanificada)).append(" km");
        return resultado.toString();
    }

    public boolean despachar() {
        if (!estado.equalsIgnoreCase("DISPONIBLE") || carga.estaVacia()
                || itinerario.length == 0) {
            return false;
        }
        estado = "EN_RUTA";
        NodoPila<Paquete> aux = carga.getTope();
        while (aux != null) {
            Paquete paquete = aux.getValor();
            paquete.setEstado("EN_RUTA");
            paquete.agregarEvento(sedeActual, "Despachado en el vehículo " + placa,
                    conductor == null ? "Sistema" : conductor.getNombre());
            aux = aux.getSiguiente();
        }
        return true;
    }

    public int posicionPaqueteEnCarga(String guia) {
        int posicion = 0;
        NodoPila<Paquete> actual = carga.getTope();
        while (actual != null) {
            if (actual.getValor().getGuia().equalsIgnoreCase(guia)) {
                return posicion;
            }
            posicion++;
            actual = actual.getSiguiente();
        }
        return -1;
    }

    public Paquete retirarPaquete(String guia) {
        Pila<Paquete> temporal = new Pila<>();
        Paquete encontrado = null;
        while (!carga.estaVacia()) {
            Paquete paquete = carga.desapilar();
            if (paquete.getGuia().equalsIgnoreCase(guia)) {
                encontrado = paquete;
                break;
            }
            temporal.apilar(paquete);
        }
        while (!temporal.estaVacia()) {
            carga.apilar(temporal.desapilar());
        }
        return encontrado;
    }

    public Paquete[] paquetesParaParada(String codigoSede) {
        int cantidad = 0;
        NodoPila<Paquete> actual = carga.getTope();
        while (actual != null) {
            if (actual.getValor().getSedeDestino().equalsIgnoreCase(codigoSede)) {
                cantidad++;
            }
            actual = actual.getSiguiente();
        }
        Paquete[] paquetes = new Paquete[cantidad];
        int indice = 0;
        actual = carga.getTope();
        while (actual != null) {
            if (actual.getValor().getSedeDestino().equalsIgnoreCase(codigoSede)) {
                paquetes[indice++] = actual.getValor();
            }
            actual = actual.getSiguiente();
        }
        return paquetes;
    }

    public void agregarPaqueteDevuelto(Paquete paquete) {
        carga.apilar(paquete);
    }

    public int registrarLlegada(Sede sede, double km) {
        if (sede == null || siguienteParada >= itinerario.length
                || !itinerario[siguienteParada].equalsIgnoreCase(sede.getCodigo())) {
            return -1;
        }
        sedeActual = sede.getCodigo();
        kmRecorridos += Math.max(km, 0);
        siguienteParada++;
        estado = carga.estaVacia() ? "DISPONIBLE" : "EN_RUTA";
        return siguienteParada;
    }

    public boolean esProximaParada(String codigoSede) {
        return codigoSede != null && siguienteParada < itinerario.length
                && itinerario[siguienteParada].equalsIgnoreCase(codigoSede.trim());
    }

    public String getProximaParada() {
        return siguienteParada < itinerario.length ? itinerario[siguienteParada] : "";
    }

    public void limpiarRuta() {
        itinerario = new String[0];
        siguienteParada = 0;
        kmRutaPlanificada = 0;
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
        return puedeLlevar(paquete, getPesoCarga(), getVolumenCarga());
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
            total += volumen(aux.getValor());
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
    public String getSedeActual() { return sedeActual; }
    public void setSedeActual(String sedeActual) { this.sedeActual = sedeActual; }
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
    public String[] getItinerario() { return itinerario.clone(); }
    public int getSiguienteParadaIndice() { return siguienteParada; }
    public double getKmRutaPlanificada() { return kmRutaPlanificada; }

    @Override
    public String toString() {
        return "Placa: " + placa + " | " + tipo + " | Sede base: " + sedeBase
                + " | Ubicación: " + sedeActual + " | Estado: " + estado
                + " | Capacidad: " + capacidadKg + " kg / " + capacidadM3 + " m3"
                + " | Carga: " + carga.getTamanio() + " paquetes, "
                + String.format("%.1f", getPesoCarga()) + " kg"
                + " | Km: " + String.format("%.0f", kmRecorridos)
                + "\n  Conductor: " + (conductor == null ? "Sin asignar" : conductor.toString());
    }
}
