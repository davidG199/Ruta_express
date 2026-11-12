package com.mycompany.rutaexpress.domain.network;

public class Ruta {
    private String[] codigosSede;
    private int cantidad;
    private double distanciaTotalKm;
    private double tiempoTotalMin;
    private double peajesTotal;

    public Ruta() {
        codigosSede = new String[2];
    }

    public void agregarTramo(String codigo, double km, double min, double peaje) {
        if (codigo == null) {
            return;
        }
        if (cantidad == codigosSede.length) {
            String[] nuevo = new String[codigosSede.length * 2];
            System.arraycopy(codigosSede, 0, nuevo, 0, codigosSede.length);
            codigosSede = nuevo;
        }
        codigosSede[cantidad++] = codigo;
        distanciaTotalKm += km;
        tiempoTotalMin += min;
        peajesTotal += peaje;
    }

    public String[] getCodigosSede() {
        String[] copia = new String[cantidad];
        System.arraycopy(codigosSede, 0, copia, 0, cantidad);
        return copia;
    }

    public double getDistanciaTotal() { return distanciaTotalKm; }
    public double getTiempoTotal() { return tiempoTotalMin; }
    public double getPeajesTotal() { return peajesTotal; }
}
