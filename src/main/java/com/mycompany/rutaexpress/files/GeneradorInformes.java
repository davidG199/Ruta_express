package com.mycompany.rutaexpress.files;

import com.mycompany.rutaexpress.domain.client.Cliente;
import com.mycompany.rutaexpress.domain.config.ConfiguracionManager;
import com.mycompany.rutaexpress.domain.network.RedManager;
import com.mycompany.rutaexpress.domain.parcel.Paquete;
import com.mycompany.rutaexpress.domain.parcel.PaqueteManager;
import com.mycompany.rutaexpress.domain.pqr.Pqr;
import com.mycompany.rutaexpress.domain.pqr.PqrManager;
import com.mycompany.rutaexpress.domain.vehicle.Vehiculo;
import com.mycompany.rutaexpress.domain.vehicle.VehiculoManager;
import com.mycompany.rutaexpress.estructures.lists.List;
import com.mycompany.rutaexpress.estructures.lists.Node;
import com.mycompany.rutaexpress.estructures.queues.NodoCola;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Genera el informe diario en texto plano.
 */
public class GeneradorInformes {
    private final RedManager redManager;
    private final PaqueteManager paqueteManager;
    private final VehiculoManager vehiculoManager;
    private final PqrManager pqrManager;
    private final ConfiguracionManager configuracion;
    private final GestorArchivos archivos;

    public GeneradorInformes(RedManager redManager, PaqueteManager paqueteManager,
            VehiculoManager vehiculoManager, PqrManager pqrManager,
            ConfiguracionManager configuracion, GestorArchivos archivos) {
        this.redManager = redManager;
        this.paqueteManager = paqueteManager;
        this.vehiculoManager = vehiculoManager;
        this.pqrManager = pqrManager;
        this.configuracion = configuracion;
        this.archivos = archivos == null ? new GestorArchivos() : archivos;
    }

    public String generar() {
        LocalDate hoy = LocalDate.now();
        String nombre = "informe_" + hoy + ".txt";
        StringBuilder resultado = new StringBuilder();
        resultado.append("RUTAEXPRESS ANTIOQUIA\n");
        resultado.append("Informe generado: ").append(LocalDateTime.now()).append("\n");
        resultado.append("Precio del galón: $")
                .append(String.format("%.0f", configuracion.getPrecioGalon())).append("\n\n");
        resultado.append("Red: ").append(redManager.cantidadSedes()).append(" sedes, ")
                .append(redManager.cantidadVias()).append(" vías\n");
        resultado.append("Paquetes: ").append(paqueteManager.cantidad()).append("\n");
        resultado.append("Altura del árbol: ").append(paqueteManager.altura())
                .append(" | Hojas: ").append(paqueteManager.contarHojas()).append("\n");
        resultado.append("Vehículos: ").append(vehiculoManager.cantidad()).append("\n");
        resultado.append("PQR pendientes: ").append(pqrManager.cantidad()).append("\n");
        resultado.append("Sedes aisladas: ").append(redManager.sedesAisladasLista().Size())
                .append("\n\n");

        resultado.append(resumenPaquetes()).append("\n");
        resultado.append("INGRESOS POR SEDE DE ORIGEN (solo paquetes entregados)\n")
                .append(resumenIngresosPorSede()).append("\n");
        resultado.append("INGRESOS POR TIPO DE SERVICIO (solo paquetes entregados)\n")
                .append(resumenIngresosPorTipo()).append("\n");
        resultado.append("CLIENTE CON MÁS ENVÍOS\n").append(clienteConMasEnvios()).append("\n");
        resultado.append("PAQUETE MÁS PESADO\n").append(paqueteMasPesado()).append("\n");
        resultado.append("VEHÍCULO CON MÁS KILÓMETROS\n").append(vehiculoMasKilometros()).append("\n");
        resultado.append("PAQUETES VENCIDOS SIN ENTREGAR: ")
                .append(paquetesVencidosSinEntregar()).append("\n\n");
        resultado.append("PQR ABIERTAS POR TIPO\n").append(pqrAbiertasPorTipo()).append("\n");

        resultado.append("DETALLE DE PAQUETES\n");
        resultado.append(paqueteManager.inOrden()).append("\n");
        resultado.append("DETALLE DE FLOTA\n");
        resultado.append(vehiculoManager.listar()).append("\n");
        resultado.append("PQR EN COLA\n");
        resultado.append(pqrManager.listar());

        archivos.escribirTexto(nombre, resultado.toString());
        return "Informe generado en " + nombre + ".\n\n" + resultado;
    }

    private String resumenPaquetes() {
        if (paqueteManager.cantidad() == 0) {
            return "INDICADORES DE PAQUETES\nSin datos.\n";
        }
        final double[] sumaPesos = {0};
        final int[] total = {0};
        final int[] vencidos = {0};
        paqueteManager.getPaquetes().recorrerEnOrden(paquete -> {
            total[0]++;
            sumaPesos[0] += paquete.getPesoKg();
            if (paquete.estaVencido(LocalDate.now())
                    && !"ENTREGADO".equalsIgnoreCase(paquete.getEstado())) {
                vencidos[0]++;
            }
        });
        return "INDICADORES DE PAQUETES\n"
                + "Cantidad total: " + total[0] + "\n"
                + "Peso promedio: " + String.format("%.2f", sumaPesos[0] / total[0])
                + " kg\n"
                + "Paquetes vencidos sin entregar: " + vencidos[0] + "\n";
    }

    private String resumenIngresosPorSede() {
        if (paqueteManager.cantidad() == 0) {
            return "Sin datos.\n";
        }
        List sedes = new List();
        paqueteManager.getPaquetes().recorrerEnOrden(paquete -> {
            if ("ENTREGADO".equalsIgnoreCase(paquete.getEstado())
                    && buscarTexto(sedes, paquete.getSedeOrigen()) == null) {
                sedes.AddLast(paquete.getSedeOrigen());
            }
        });
        if (sedes.isEmpty()) {
            return "Sin datos de paquetes entregados.\n";
        }
        StringBuilder salida = new StringBuilder();
        for (Node nodo = sedes.getHead(); nodo != null; nodo = nodo.getLink()) {
            String codigo = (String) nodo.getData();
            final double[] ingreso = {0};
            paqueteManager.getPaquetes().recorrerEnOrden(paquete -> {
                if ("ENTREGADO".equalsIgnoreCase(paquete.getEstado())
                        && iguales(codigo, paquete.getSedeOrigen())) {
                    ingreso[0] += paquete.getCostoEnvio();
                }
            });
            salida.append(codigo).append(": $")
                    .append(String.format("%.0f", ingreso[0])).append("\n");
        }
        return salida.toString();
    }

    private String resumenIngresosPorTipo() {
        if (paqueteManager.cantidad() == 0) {
            return "Sin datos.\n";
        }
        List tipos = new List();
        paqueteManager.getPaquetes().recorrerEnOrden(paquete -> {
            if ("ENTREGADO".equalsIgnoreCase(paquete.getEstado())
                    && buscarTexto(tipos, paquete.getTipoServicio()) == null) {
                tipos.AddLast(paquete.getTipoServicio());
            }
        });
        if (tipos.isEmpty()) {
            return "Sin datos de paquetes entregados.\n";
        }
        StringBuilder salida = new StringBuilder();
        for (Node nodo = tipos.getHead(); nodo != null; nodo = nodo.getLink()) {
            String tipo = (String) nodo.getData();
            final double[] ingreso = {0};
            paqueteManager.getPaquetes().recorrerEnOrden(paquete -> {
                if ("ENTREGADO".equalsIgnoreCase(paquete.getEstado())
                        && iguales(tipo, paquete.getTipoServicio())) {
                    ingreso[0] += paquete.getCostoEnvio();
                }
            });
            salida.append(tipo == null || tipo.trim().isEmpty() ? "(sin tipo)" : tipo)
                    .append(": $").append(String.format("%.0f", ingreso[0])).append("\n");
        }
        return salida.toString();
    }

    private String clienteConMasEnvios() {
        if (paqueteManager.cantidad() == 0) {
            return "Sin datos.\n";
        }
        final List documentos = new List();
        final String[] ganador = {null};
        final int[] maximo = {0};
        paqueteManager.getPaquetes().recorrerEnOrden(paquete -> {
            String documento = paquete.getDocumentoRemitente();
            if (buscarTexto(documentos, documento) == null) {
                documentos.AddLast(documento);
                final int[] cantidad = {0};
                paqueteManager.recorrerPorRemitente(documento, p -> cantidad[0]++);
                if (cantidad[0] > maximo[0]) {
                    maximo[0] = cantidad[0];
                    ganador[0] = documento;
                }
            }
        });
        if (ganador[0] == null) {
            return "Sin datos.\n";
        }
        Cliente cliente = paqueteManager.buscarClienteRemitente(ganador[0]);
        String nombre = cliente == null ? "" : " | " + cliente.getNombre();
        return "Documento " + ganador[0] + nombre + " | envíos: " + maximo[0]
                + (cantidadClientesEmpatados(maximo[0]) > 1 ? " (empate)" : "") + "\n";
    }

    private int cantidadClientesEmpatados(int cantidad) {
        final List documentos = new List();
        final int[] empatados = {0};
        paqueteManager.getPaquetes().recorrerEnOrden(paquete -> {
            String documento = paquete.getDocumentoRemitente();
            if (buscarTexto(documentos, documento) == null) {
                documentos.AddLast(documento);
                final int[] enviados = {0};
                paqueteManager.recorrerPorRemitente(documento, p -> enviados[0]++);
                if (enviados[0] == cantidad) {
                    empatados[0]++;
                }
            }
        });
        return empatados[0];
    }

    private String paqueteMasPesado() {
        if (paqueteManager.cantidad() == 0) {
            return "Sin datos.\n";
        }
        final Paquete[] pesado = {null};
        final int[] empatados = {0};
        paqueteManager.getPaquetes().recorrerEnOrden(paquete -> {
            if (pesado[0] == null || paquete.getPesoKg() > pesado[0].getPesoKg()) {
                pesado[0] = paquete;
                empatados[0] = 1;
            } else if (Double.compare(paquete.getPesoKg(), pesado[0].getPesoKg()) == 0) {
                empatados[0]++;
            }
        });
        return "Guía " + pesado[0].getGuia() + " | "
                + String.format("%.2f", pesado[0].getPesoKg()) + " kg"
                + (empatados[0] > 1 ? " (empate entre " + empatados[0] + ")" : "") + "\n";
    }

    private String vehiculoMasKilometros() {
        if (vehiculoManager.cantidad() == 0) {
            return "Sin datos.\n";
        }
        final Vehiculo[] mayor = {null};
        final int[] empatados = {0};
        vehiculoManager.getVehiculos().recorrerAdelante(vehiculo -> {
            if (mayor[0] == null || vehiculo.getKmRecorridos() > mayor[0].getKmRecorridos()) {
                mayor[0] = vehiculo;
                empatados[0] = 1;
            } else if (Double.compare(vehiculo.getKmRecorridos(), mayor[0].getKmRecorridos()) == 0) {
                empatados[0]++;
            }
        });
        return "Placa " + mayor[0].getPlaca() + " | "
                + String.format("%.1f", mayor[0].getKmRecorridos()) + " km"
                + (empatados[0] > 1 ? " (empate entre " + empatados[0] + ")" : "") + "\n";
    }

    private int paquetesVencidosSinEntregar() {
        final int[] vencidos = {0};
        paqueteManager.getPaquetes().recorrerEnOrden(paquete -> {
            if (paquete.estaVencido(LocalDate.now())
                    && !"ENTREGADO".equalsIgnoreCase(paquete.getEstado())) {
                vencidos[0]++;
            }
        });
        return vencidos[0];
    }

    private String pqrAbiertasPorTipo() {
        if (pqrManager.cantidad() == 0) {
            return "Sin datos.\n";
        }
        List tipos = new List();
        for (NodoCola<Pqr> nodo = pqrManager.getPqrs().getFrente();
                nodo != null; nodo = nodo.getSiguiente()) {
            Pqr pqr = nodo.getValor();
            if ("ABIERTA".equalsIgnoreCase(pqr.getEstado())
                    && buscarTexto(tipos, pqr.getTipo()) == null) {
                tipos.AddLast(pqr.getTipo());
            }
        }
        if (tipos.isEmpty()) {
            return "No hay PQR abiertas.\n";
        }
        StringBuilder salida = new StringBuilder();
        for (Node nodo = tipos.getHead(); nodo != null; nodo = nodo.getLink()) {
            String tipo = (String) nodo.getData();
            salida.append(tipo == null || tipo.trim().isEmpty() ? "(sin tipo)" : tipo)
                    .append(": ").append(pqrManager.contarAbiertasPorTipo(tipo)).append("\n");
        }
        return salida.toString();
    }

    private String buscarTexto(List lista, String texto) {
        for (Node nodo = lista.getHead(); nodo != null; nodo = nodo.getLink()) {
            String existente = (String) nodo.getData();
            if (iguales(existente, texto)) {
                return existente;
            }
        }
        return null;
    }

    private boolean iguales(String a, String b) {
        return a == null ? b == null : b != null && a.equalsIgnoreCase(b);
    }
}
