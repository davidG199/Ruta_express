package com.mycompany.rutaexpress.domain.parcel;

import com.mycompany.rutaexpress.domain.client.Cliente;
import com.mycompany.rutaexpress.domain.client.ClientManager;
import com.mycompany.rutaexpress.domain.network.RedManager;
import com.mycompany.rutaexpress.domain.network.Ruta;
import com.mycompany.rutaexpress.domain.network.Sede;
import com.mycompany.rutaexpress.estructures.trees.ArbolBinario;
import com.mycompany.rutaexpress.files.GestorArchivos;
import com.mycompany.rutaexpress.files.GestorNotificaciones;

import java.time.LocalDate;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Gestiona la admisión, el rastreo y las estadísticas de los paquetes.
 */
public class PaqueteManager {
    private final ArbolBinario<Paquete> paquetes;
    private final ClientManager clientManager;
    private final RedManager redManager;
    private final GestorNotificaciones notificador;
    private final Random random;
    private String ultimoError;

    public PaqueteManager() {
        this(null, null, null);
    }

    public PaqueteManager(ClientManager clientManager, RedManager redManager,
            GestorNotificaciones notificador) {
        this.paquetes = new ArbolBinario<>(
                (a, b) -> a.getGuia().compareToIgnoreCase(b.getGuia()));
        this.clientManager = clientManager;
        this.redManager = redManager;
        this.notificador = notificador;
        this.random = new Random();
        this.ultimoError = "";
    }

    public boolean registrar(Paquete paquete) {
        ultimoError = "";
        if (paquete == null) {
            ultimoError = "El paquete es obligatorio.";
            return false;
        }
        if (paquete.getGuia() == null || paquete.getGuia().trim().isEmpty()) {
            ultimoError = "La guía es obligatoria.";
            return false;
        }
        if (buscarPorGuia(paquete.getGuia()) != null) {
            ultimoError = "Ya existe un paquete con guía " + paquete.getGuia() + ".";
            return false;
        }
        paquetes.insertar(paquete);
        return true;
    }

    public Paquete admitir(String[] datos) {
        ultimoError = "";
        if (datos == null || datos.length < 14) {
            ultimoError = "Faltan datos del paquete.";
            return null;
        }
        return admitirInterno(datos, generarGuiaUnica());
    }

    public Paquete admitirConGuia(String[] datos) {
        ultimoError = "";
        if (datos == null || datos.length < 15) {
            ultimoError = "Faltan datos del paquete.";
            return null;
        }
        String[] columnas = new String[14];
        System.arraycopy(datos, 1, columnas, 0, 14);
        return admitirInterno(columnas, datos[0].trim());
    }

    private Paquete admitirInterno(String[] datos, String guia) {
        if (clientManager == null || redManager == null) {
            ultimoError = "El gestor de paquetes no tiene clientes o red configurados.";
            return null;
        }

        Cliente remitente = clientManager.buscarPorDocumento(datos[0]);
        if (remitente == null) {
            ultimoError = "No existe el cliente remitente " + datos[0] + ".";
            return null;
        }
        Sede origen = redManager.buscarSede(datos[5]);
        Sede destino = redManager.buscarSede(datos[6]);
        if (origen == null || destino == null) {
            ultimoError = "La sede origen o destino no existe.";
            return null;
        }
        if (paquetes.buscar(p -> p.getGuia().equalsIgnoreCase(guia)) != null) {
            ultimoError = "Ya existe un paquete con la guía " + guia + ".";
            return null;
        }

        Paquete paquete;
        try {
            paquete = new Paquete(guia, datos[0].trim(), datos[1].trim(),
                    datos[2].trim(), datos[3].trim(), datos[4].trim(),
                    datos[5].trim(), datos[6].trim(), numero(datos[7]),
                    numero(datos[8]), numero(datos[9]), numero(datos[10]),
                    numero(datos[11]), datos[12].trim(), esSi(datos[13]));
        } catch (NumberFormatException e) {
            ultimoError = "Datos numéricos inválidos en el paquete.";
            return null;
        }

        double pesoFacturable = paquete.calcularPesoFacturable();
        if (paquete.getPesoKg() <= 0 || paquete.getLargoCm() <= 0
                || paquete.getAnchoCm() <= 0 || paquete.getAltoCm() <= 0
                || paquete.getValorDeclarado() < 0) {
            ultimoError = "Peso, dimensiones y valor declarado deben ser válidos.";
            return null;
        }
        if (origen.capacidadDisponible() < pesoFacturable) {
            ultimoError = "La bodega " + origen.getCodigo() + " no tiene capacidad. "
                    + "El paquete NO se guardó.";
            notificarSede(origen,
                    "Paquete rechazado por capacidad",
                    "No hay espacio para " + pesoFacturable + " kg en "
                    + origen.getCodigo() + ".");
            return null;
        }

        Ruta ruta = redManager.calcularRutaMasCorta(origen.getCodigo(), destino.getCodigo());
        if (ruta == null) {
            ultimoError = "No hay camino de vías HABILITADAS entre "
                    + origen.getCodigo() + " y " + destino.getCodigo() + ".";
            return null;
        }

        paquete.setCostoEnvio(paquete.calcularCostoEnvio(ruta.getDistanciaTotal()));
        paquete.setEstado("EN_BODEGA");
        paquete.agregarEvento(origen.getCodigo(), "Paquete admitido. Costo: $"
                + String.format("%.0f", paquete.getCostoEnvio()), "Sistema");
        paquete.agregarEvento(origen.getCodigo(), "Ingresado a la bodega", "Sistema");

        paquetes.insertar(paquete);
        remitente.agregarGuia(guia);
        origen.encolarPaquete(paquete);

        if (origen.calcularOcupacion() > 80) {
            notificarSede(origen,
                    "Alerta de ocupación de bodega",
                    "La bodega " + origen.getCodigo() + " está al "
                    + String.format("%.1f", origen.calcularOcupacion()) + "%.");
        }

        if (notificador != null) {
            notificador.enviarCorreo(paquete.getCorreoDestinatario(),
                    "Paquete " + guia + " admitido",
                    "Su paquete fue admitido en " + origen.getCodigo()
                    + " y será enviado a " + destino.getCodigo() + ".");
        }
        return paquete;
    }

    public int cargarIniciales(GestorArchivos archivos) {
        String[] lineas = archivos.leerLineas("paquetes.txt");
        int cargados = 0;
        for (String linea : lineas) {
            String[] columnas = linea.split(";", -1);
            if (columnas.length >= 15 && admitirConGuia(columnas) != null) {
                cargados++;
            }
        }
        return cargados;
    }

    public Paquete buscarPorGuia(String guia) {
        if (guia == null) {
            return null;
        }
        return paquetes.buscar(p -> p.getGuia().equalsIgnoreCase(guia.trim()));
    }

    public String rastrear(String guia) {
        Paquete paquete = buscarPorGuia(guia);
        if (paquete == null) {
            return "No existe el paquete " + guia + ".";
        }
        return paquete.toString() + "\n\nHISTORIAL\n" + paquete.historialTexto();
    }

    public String listar() {
        if (paquetes.estaVacio()) {
            return "No hay paquetes registrados.";
        }
        StringBuilder resultado = new StringBuilder();
        paquetes.recorrerEnOrden(p -> resultado.append(p).append("\n\n"));
        return resultado.toString();
    }

    public String inOrden() {
        StringBuilder resultado = new StringBuilder();
        paquetes.recorrerEnOrden(p -> resultado.append(p).append("\n\n"));
        return resultado.toString();
    }

    public String listarPorEstado(String estado) {
        StringBuilder resultado = new StringBuilder();
        paquetes.recorrerEnOrden(p -> {
            if (p.getEstado().equalsIgnoreCase(estado)) {
                resultado.append(p.getGuia()).append(" | ").append(p.getEstado())
                        .append(" | ").append(p.getSedeOrigen()).append(" -> ")
                        .append(p.getSedeDestino()).append(" | $")
                        .append(String.format("%.0f", p.getCostoEnvio())).append("\n");
            }
        });
        return resultado.length() == 0
                ? "No hay paquetes en estado " + estado + "." : resultado.toString();
    }

    public String verEstadisticas() {
        String[] estados = {"EN_BODEGA", "EN_VEHICULO", "EN_RUTA", "ENTREGADO"};
        int[] cuentas = new int[estados.length];
        int[] total = new int[1];
        int[] vencidos = new int[1];
        double[] ingresos = new double[1];

        paquetes.recorrerEnOrden(p -> {
            total[0]++;
            for (int i = 0; i < estados.length; i++) {
                if (p.getEstado().equalsIgnoreCase(estados[i])) {
                    cuentas[i]++;
                    break;
                }
            }
            if (p.estaVencido(LocalDate.now())
                    && !p.getEstado().equalsIgnoreCase("ENTREGADO")) {
                vencidos[0]++;
            }
            if (p.getEstado().equalsIgnoreCase("ENTREGADO")) {
                ingresos[0] += p.getCostoEnvio();
            }
        });

        StringBuilder resultado = new StringBuilder("ESTADÍSTICAS DE PAQUETES\n");
        resultado.append("Total: ").append(total[0]).append("\n");
        for (int i = 0; i < estados.length; i++) {
            resultado.append(estados[i]).append(": ").append(cuentas[i]).append("\n");
        }
        resultado.append("Vencidos sin entregar: ").append(vencidos[0]).append("\n");
        resultado.append("Ingresos por entregas: $")
                .append(String.format("%.0f", ingresos[0])).append("\n");
        resultado.append("Altura del árbol: ").append(paquetes.altura()).append("\n");
        resultado.append("Hojas: ").append(paquetes.contarHojas()).append("\n");
        resultado.append("Guía menor: ").append(guiaMenor()).append("\n");
        resultado.append("Guía mayor: ").append(guiaMayor()).append("\n");
        return resultado.toString();
    }

    public void recorrerPorRemitente(String documento, Consumer<Paquete> consumidor) {
        if (documento == null || consumidor == null) {
            return;
        }
        paquetes.recorrerEnOrden(paquete -> {
            if (documento.trim().equalsIgnoreCase(paquete.getDocumentoRemitente())) {
                consumidor.accept(paquete);
            }
        });
    }

    public double totalPagadoPorRemitente(String documento) {
        final double[] total = {0};
        recorrerPorRemitente(documento, paquete -> total[0] += paquete.getCostoEnvio());
        return total[0];
    }

    public int cantidad() { return paquetes.cantidad(); }
    public int altura() { return paquetes.altura(); }
    public int contarHojas() { return paquetes.contarHojas(); }
    public String guiaMenor() { return extremo(true); }
    public String guiaMayor() { return extremo(false); }
    public ArbolBinario<Paquete> getPaquetes() { return paquetes; }
    public String getUltimoError() { return ultimoError; }

    private String extremo(boolean menor) {
        final Paquete[] resultado = {null};
        paquetes.recorrerEnOrden(p -> {
            if (resultado[0] == null) {
                resultado[0] = p;
            } else if (!menor) {
                resultado[0] = p;
            }
        });
        return resultado[0] == null ? "" : resultado[0].getGuia();
    }

    private String generarGuiaUnica() {
        for (int i = 0; i < 10000; i++) {
            int numero = 100000000 + random.nextInt(900000000);
            String guia = String.valueOf(numero);
            if (buscarPorGuia(guia) == null) {
                return guia;
            }
        }
        String marca = String.valueOf(System.currentTimeMillis());
        return marca.substring(marca.length() - 9);
    }

    private double numero(String valor) {
        return Double.parseDouble(valor.trim().replace(",", "."));
    }

    private boolean esSi(String valor) {
        if (valor == null) {
            return false;
        }
        String v = valor.trim();
        return v.equalsIgnoreCase("S") || v.equalsIgnoreCase("SI")
                || v.equalsIgnoreCase("SÍ") || v.equalsIgnoreCase("TRUE")
                || v.equals("1");
    }

    private void notificarSede(Sede sede, String asunto, String cuerpo) {
        if (notificador != null) {
            notificador.notificarSede(sede.getCorreoCoordinador(), asunto, cuerpo);
        }
    }
}
