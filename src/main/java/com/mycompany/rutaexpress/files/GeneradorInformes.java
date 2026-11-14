package com.mycompany.rutaexpress.files;

import com.mycompany.rutaexpress.domain.config.ConfiguracionManager;
import com.mycompany.rutaexpress.domain.network.RedManager;
import com.mycompany.rutaexpress.domain.parcel.PaqueteManager;
import com.mycompany.rutaexpress.domain.pqr.PqrManager;
import com.mycompany.rutaexpress.domain.vehicle.VehiculoManager;

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
        resultado.append(paqueteManager.verEstadisticas()).append("\n");
        resultado.append("DETALLE DE PAQUETES\n");
        resultado.append(paqueteManager.inOrden()).append("\n");
        resultado.append("DETALLE DE FLOTA\n");
        resultado.append(vehiculoManager.listar()).append("\n");
        resultado.append("PQR EN COLA\n");
        resultado.append(pqrManager.listar());

        archivos.escribirTexto(nombre, resultado.toString());
        return "Informe generado en " + nombre + ".\n\n" + resultado;
    }
}
