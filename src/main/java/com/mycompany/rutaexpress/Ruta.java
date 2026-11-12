package com.mycompany.rutaexpress;

public class Ruta {
    private String[] codigosSede;
    private int cantidad;
    private double distanciaTotalKm;
    private double tiempoTotalMin;
    private double peajesTotal;

    public Ruta() {
        codigosSede = new String[2];
        cantidad = 0;
        distanciaTotalKm = 0;
        tiempoTotalMin = 0;
        peajesTotal = 0;
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
        codigosSede[cantidad] = codigo;
        cantidad++;
        distanciaTotalKm += km;
        tiempoTotalMin += min;
        peajesTotal += peaje;
    }

    public String[] getCodigosSede() {
        String[] copia = new String[cantidad];
        System.arraycopy(codigosSede, 0, copia, 0, cantidad);
        return copia;
    }

    public double getDistanciaTotal() {
        return distanciaTotalKm;
    }

    public double getTiempoTotal() {
        return tiempoTotalMin;
    }

    public double getPeajesTotal() {
        return peajesTotal;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Ruta: ");
        for (int i = 0; i < cantidad; i++) {
            sb.append(codigosSede[i]);
            if (i < cantidad - 1) {
                sb.append(" -> ");
            }
        }
        sb.append("\nDistancia total: ").append(String.format("%.1f", distanciaTotalKm)).append(" km");
        sb.append("\nTiempo estimado: ").append(String.format("%.1f", tiempoTotalMin)).append(" min");
        sb.append("\nPeajes: $").append(String.format("%.0f", peajesTotal));
        return sb.toString();
    }
}
