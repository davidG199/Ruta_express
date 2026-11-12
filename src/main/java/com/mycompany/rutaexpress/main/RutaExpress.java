package com.mycompany.rutaexpress.main;

import com.mycompany.rutaexpress.NodoPaquete;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class RutaExpress {

    public static void main(String[] args) {
        SistemaRutaExpress sistema = new SistemaRutaExpress();
        mostrar("Carga inicial", sistema.inicializar());

        String[] menu = {"Clientes", "Red de sedes", "Paquetes", "Flota",
            "PQR", "Reportes", "Configuración", "Salir"};
        String opcion;

        do {
            opcion = menu("RutaExpress Antioquia", menu);
            if (opcion == null) {
                opcion = "Salir";
            }
            switch (opcion) {
                case "Clientes" ->
                    menuClientes(sistema);
                case "Red de sedes" ->
                    menuRed(sistema);
                case "Paquetes" ->
                    menuPaquetes(sistema);
                case "Flota" ->
                    menuFlota(sistema);
                case "PQR" ->
                    menuPQR(sistema);
                case "Reportes" ->
                    menuReportes(sistema);
                case "Configuración" ->
                    menuConfiguracion(sistema);
                case "Salir" ->
                    aviso("Fin del programa");
            }
        } while (!opcion.equals("Salir"));
    }

    private static void menuClientes(SistemaRutaExpress sistema) {
        String[] opciones = {"Registrar cliente", "Buscar por prefijo",
            "Ver historial de cliente", "Listar clientes", "Volver"};
        String opcion;
        do {
            opcion = menu("Clientes", opciones);
            if (opcion == null || opcion.equals("Volver")) {
                return;
            }
            switch (opcion) {
                case "Registrar cliente" -> {
                    String documento = pedirObligatorio("Documento del cliente:");
                    if (documento == null) {
                        continue;
                    }
                    String tipo = menu("Tipo de cliente", new String[]{"NATURAL", "EMPRESA"});
                    if (tipo == null) {
                        continue;
                    }
                    String nombre = pedirObligatorio("Nombre completo:");
                    if (nombre == null) {
                        continue;
                    }
                    String telefono = pedir("Teléfono:");
                    if (telefono == null) {
                        continue;
                    }
                    String correo = pedir("Correo:");
                    if (correo == null) {
                        continue;
                    }
                    String direccion = pedir("Dirección:");
                    if (direccion == null) {
                        continue;
                    }
                    String municipio = pedir("Municipio:");
                    if (municipio == null) {
                        continue;
                    }
                    String[] datos = {documento, tipo, nombre, telefono,
                        correo, direccion, municipio};
                    if (sistema.registrarCliente(datos)) {
                        aviso("Cliente registrado correctamente.");
                    } else {
                        aviso(sistema.getUltimoError());
                    }
                }
                case "Buscar por prefijo" -> {
                    String prefijo = pedir("Prefijo del nombre:");
                    if (prefijo != null) {
                        mostrar("Clientes encontrados", sistema.buscarClientes(prefijo));
                    }
                }
                case "Ver historial de cliente" -> {
                    String documento = pedirObligatorio("Documento del cliente:");
                    if (documento != null) {
                        mostrar("Historial del cliente",
                                sistema.verHistorialCliente(documento));
                    }
                }
                case "Listar clientes" ->
                    mostrar("Clientes", sistema.getClientes().recorrer());
            }
        } while (true);
    }

    private static void menuRed(SistemaRutaExpress sistema) {
        String[] opciones = {"Mostrar red", "Consultar mejor ruta",
            "Cambiar estado de vía", "Sedes aisladas", "Ver bodega", "Volver"};
        String opcion;
        do {
            opcion = menu("Red de sedes", opciones);
            if (opcion == null || opcion.equals("Volver")) {
                return;
            }
            switch (opcion) {
                case "Mostrar red" ->
                    mostrar("Red de sedes", sistema.mostrarRed());
                case "Consultar mejor ruta" -> {
                    String origen = pedirObligatorio("Código de sede origen:");
                    if (origen == null) {
                        continue;
                    }
                    String destino = pedirObligatorio("Código de sede destino:");
                    if (destino == null) {
                        continue;
                    }
                    mostrar("Mejor ruta",
                            sistema.consultarMejorRuta(origen, destino));
                }
                case "Cambiar estado de vía" -> {
                    String origen = pedirObligatorio("Código de sede origen:");
                    if (origen == null) {
                        continue;
                    }
                    String destino = pedirObligatorio("Código de sede destino:");
                    if (destino == null) {
                        continue;
                    }
                    String estado = menu("Nuevo estado",
                            new String[]{"HABILITADA", "CERRADA"});
                    if (estado == null) {
                        continue;
                    }
                    mostrar("Cambio de vía",
                            sistema.cambiarEstadoVia(origen, destino, estado));
                }
                case "Sedes aisladas" ->
                    mostrar("Sedes aisladas", sistema.sedesAisladas());
                case "Ver bodega" -> {
                    String codigo = pedirObligatorio("Código de la sede:");
                    if (codigo != null) {
                        mostrar("Bodega", sistema.verBodega(codigo));
                    }
                }
            }
        } while (true);
    }

    private static void menuPaquetes(SistemaRutaExpress sistema) {
        String[] opciones = {"Admitir paquete", "Rastrear paquete",
            "Listar paquetes", "Estadísticas", "Volver"};
        String opcion;
        do {
            opcion = menu("Paquetes", opciones);
            if (opcion == null || opcion.equals("Volver")) {
                return;
            }
            switch (opcion) {
                case "Admitir paquete" -> {
                    String documento = pedirObligatorio("Documento del remitente:");
                    if (documento == null) {
                        continue;
                    }
                    String nombre = pedirObligatorio("Nombre del destinatario:");
                    if (nombre == null) {
                        continue;
                    }
                    String telefono = pedir("Teléfono del destinatario:");
                    if (telefono == null) {
                        continue;
                    }
                    String correo = pedir("Correo del destinatario:");
                    if (correo == null) {
                        continue;
                    }
                    String direccion = pedir("Dirección de entrega:");
                    if (direccion == null) {
                        continue;
                    }
                    String origen = pedirObligatorio("Código de sede origen:");
                    if (origen == null) {
                        continue;
                    }
                    String destino = pedirObligatorio("Código de sede destino:");
                    if (destino == null) {
                        continue;
                    }
                    Double peso = pedirDouble("Peso en kg:");
                    if (peso == null) {
                        continue;
                    }
                    Double largo = pedirDouble("Largo en cm:");
                    if (largo == null) {
                        continue;
                    }
                    Double ancho = pedirDouble("Ancho en cm:");
                    if (ancho == null) {
                        continue;
                    }
                    Double alto = pedirDouble("Alto en cm:");
                    if (alto == null) {
                        continue;
                    }
                    Double valor = pedirDouble("Valor declarado:");
                    if (valor == null) {
                        continue;
                    }
                    String tipo = menu("Tipo de servicio",
                            new String[]{"ESTANDAR", "EXPRESS", "CONTRA_ENTREGA"});
                    if (tipo == null) {
                        continue;
                    }
                    String fragil = menu("¿Es frágil?", new String[]{"No", "Sí"});
                    if (fragil == null) {
                        continue;
                    }
                    String[] datos = {documento, nombre, telefono, correo,
                        direccion, origen, destino, String.valueOf(peso),
                        String.valueOf(largo), String.valueOf(ancho),
                        String.valueOf(alto), String.valueOf(valor), tipo,
                        fragil.equals("Sí") ? "S" : "N"};
                    NodoPaquete p = sistema.admitirPaquete(datos);
                    if (p == null) {
                        aviso(sistema.getUltimoError());
                    } else {
                        mostrar("Paquete admitido", "Guía: " + p.getGuia()
                                + "\nCosto: $" + String.format("%.0f", p.getCostoEnvio())
                                + "\nFecha máxima: " + p.getFechaMaximaEntrega()
                                + "\n\n" + p);
                    }
                }
                case "Rastrear paquete" -> {
                    String guia = pedirObligatorio("Número de guía:");
                    if (guia != null) {
                        mostrar("Rastreo", sistema.rastrearPaquete(guia));
                    }
                }
                case "Listar paquetes" -> {
                    String estado = menu("Filtrar por estado",
                            new String[]{"TODOS", "EN_BODEGA", "EN_VEHICULO",
                                "EN_RUTA", "ENTREGADO"});
                    if (estado != null) {
                        mostrar("Paquetes", sistema.listarPaquetes(estado));
                    }
                }
                case "Estadísticas" ->
                    mostrar("Estadísticas", sistema.verEstadisticasPaquetes());
            }
        } while (true);
    }

    private static void menuFlota(SistemaRutaExpress sistema) {
        String[] opciones = {"Registrar vehículo", "Cargar vehículo desde archivo",
            "Cargar paquetes a vehículo", "Despachar vehículo",
            "Registrar entrega", "Listar flota", "Volver"};
        String opcion;
        do {
            opcion = menu("Flota", opciones);
            if (opcion == null || opcion.equals("Volver")) {
                return;
            }
            switch (opcion) {
                case "Registrar vehículo" -> {
                    String placa = pedirObligatorio("Placa:");
                    if (placa == null) {
                        continue;
                    }
                    String tipo = menu("Tipo de vehículo",
                            new String[]{"CAMION", "FURGONETA", "MOTO"});
                    if (tipo == null) {
                        continue;
                    }
                    String sede = pedirObligatorio("Código de sede base:");
                    if (sede == null) {
                        continue;
                    }
                    String estado = menu("Estado",
                            new String[]{"DISPONIBLE", "MANTENIMIENTO"});
                    if (estado == null) {
                        continue;
                    }
                    Double capacidadKg = pedirDouble("Capacidad en kg:");
                    if (capacidadKg == null) {
                        continue;
                    }
                    Double capacidadM3 = pedirDouble("Capacidad en m3:");
                    if (capacidadM3 == null) {
                        continue;
                    }
                    Double rendimiento = pedirDouble("Rendimiento km/galón:");
                    if (rendimiento == null) {
                        continue;
                    }
                    Double km = pedirDouble("Kilómetros recorridos:");
                    if (km == null) {
                        continue;
                    }
                    String cedula = pedirObligatorio("Cédula del conductor:");
                    if (cedula == null) {
                        continue;
                    }
                    String nombre = pedirObligatorio("Nombre del conductor:");
                    if (nombre == null) {
                        continue;
                    }
                    String celular = pedir("Celular del conductor:");
                    if (celular == null) {
                        continue;
                    }
                    String correo = pedir("Correo del conductor:");
                    if (correo == null) {
                        continue;
                    }
                    String categoria = pedirObligatorio("Categoría de licencia:");
                    if (categoria == null) {
                        continue;
                    }
                    String[] datos = {placa, tipo, sede, estado,
                        String.valueOf(capacidadKg), String.valueOf(capacidadM3),
                        String.valueOf(rendimiento), String.valueOf(km),
                        cedula, nombre, celular, correo, categoria};
                    if (sistema.registrarVehiculo(datos)) {
                        aviso("Vehículo registrado correctamente.");
                    } else {
                        aviso(sistema.getUltimoError());
                    }
                }
                case "Cargar vehículo desde archivo" -> {
                    String placa = pedirObligatorio("Placa:");
                    if (placa != null) {
                        if (sistema.cargarVehiculoDesdeArchivo(placa)) {
                            aviso("Vehículo cargado desde vehiculos.txt.");
                        } else {
                            aviso(sistema.getUltimoError());
                        }
                    }
                }
                case "Cargar paquetes a vehículo" -> {
                    String placa = pedirObligatorio("Placa:");
                    if (placa != null) {
                        mostrar("Carga de paquetes",
                                sistema.cargarVehiculo(placa));
                    }
                }
                case "Despachar vehículo" -> {
                    String placa = pedirObligatorio("Placa:");
                    if (placa != null) {
                        mostrar("Despacho", sistema.despacharVehiculo(placa));
                    }
                }
                case "Registrar entrega" -> {
                    String placa = pedirObligatorio("Placa:");
                    if (placa == null) {
                        continue;
                    }
                    String sede = pedirObligatorio("Código de sede de entrega:");
                    if (sede != null) {
                        mostrar("Entrega",
                                sistema.registrarEntrega(placa, sede));
                    }
                }
                case "Listar flota" -> {
                    String filtro = menu("Filtro",
                            new String[]{"TODOS", "DISPONIBLE", "EN_RUTA",
                                "MANTENIMIENTO", "POR SEDE"});
                    if (filtro == null) {
                        continue;
                    }
                    if (filtro.equals("POR SEDE")) {
                        String sede = pedirObligatorio("Código de sede:");
                        if (sede != null) {
                            mostrar("Flota", sistema.recorrerFlota(sede));
                        }
                    } else {
                        mostrar("Flota", sistema.recorrerFlota(filtro));
                    }
                }
            }
        } while (true);
    }

    private static void menuPQR(SistemaRutaExpress sistema) {
        String[] opciones = {"Radicar PQR", "Atender PQR", "Ver cola", "Volver"};
        String opcion;
        do {
            opcion = menu("PQR", opciones);
            if (opcion == null || opcion.equals("Volver")) {
                return;
            }
            switch (opcion) {
                case "Radicar PQR" -> {
                    String documento = pedirObligatorio("Documento del cliente:");
                    if (documento == null) {
                        continue;
                    }
                    String guia = pedir("Número de guía (opcional):");
                    if (guia == null) {
                        continue;
                    }
                    String tipo = menu("Tipo de PQR",
                            new String[]{"QUEJA", "RECLAMO", "SUGERENCIA", "FELICITACION"});
                    if (tipo == null) {
                        continue;
                    }
                    String descripcion = pedirObligatorio("Descripción:");
                    if (descripcion == null) {
                        continue;
                    }
                    String[] datos = {documento, guia, tipo, descripcion};
                    if (sistema.radicarPQR(datos)) {
                        aviso("PQR radicada correctamente.");
                    } else {
                        aviso(sistema.getUltimoError());
                    }
                }
                case "Atender PQR" -> {
                    String frente = sistema.verFrentePQR();
                    if (frente == null) {
                        aviso("No hay PQR pendientes.");
                    } else {
                        mostrar("PQR al frente de la cola", frente);
                        String funcionario = pedirObligatorio("Nombre del funcionario:");
                        if (funcionario == null) {
                            continue;
                        }
                        String nota = pedirObligatorio("Nota de atención:");
                        if (nota == null) {
                            continue;
                        }
                        String decision = menu("Decisión",
                                new String[]{"Cerrar PQR", "Mantener abierta"});
                        if (decision == null) {
                            continue;
                        }
                        String resultado = sistema.atenderPQR(funcionario, nota,
                                decision.equals("Cerrar PQR"));
                        mostrar("Atención de PQR", resultado);
                    }
                }
                case "Ver cola" ->
                    mostrar("Cola de PQR", sistema.verColaPQR());
            }
        } while (true);
    }

    private static void menuReportes(SistemaRutaExpress sistema) {
        String[] opciones = {"Generar informe", "Volver"};
        String opcion;
        do {
            opcion = menu("Reportes", opciones);
            if (opcion == null || opcion.equals("Volver")) {
                return;
            }
            if (opcion.equals("Generar informe")) {
                mostrar("Informe", sistema.generarInforme());
            }
        } while (true);
    }

    private static void menuConfiguracion(SistemaRutaExpress sistema) {
        String[] opciones = {"Ver precio del galón", "Cambiar precio del galón",
            "Recargar configuración", "Volver"};
        String opcion;
        do {
            opcion = menu("Configuración", opciones);
            if (opcion == null || opcion.equals("Volver")) {
                return;
            }
            switch (opcion) {
                case "Ver precio del galón" ->
                    aviso("Precio actual del galón: $"
                            + String.format("%.0f", sistema.getPrecioGalon()));
                case "Cambiar precio del galón" -> {
                    Double valor = pedirDouble("Nuevo precio del galón:");
                    if (valor != null && valor > 0) {
                        sistema.setPrecioGalon(valor);
                        aviso("Precio actualizado.");
                    } else {
                        aviso("Precio inválido.");
                    }
                }
                case "Recargar configuración" -> {
                    sistema.cargarConfiguracion();
                    aviso("Configuración recargada. Precio: $"
                            + String.format("%.0f", sistema.getPrecioGalon()));
                }
            }
        } while (true);
    }

    private static String menu(String titulo, String[] opciones) {
        return (String) JOptionPane.showInputDialog(null, "Seleccione una opción:",
                titulo, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
    }

    private static String pedir(String mensaje) {
        return JOptionPane.showInputDialog(null, mensaje);
    }

    private static String pedirObligatorio(String mensaje) {
        String valor = pedir(mensaje);
        if (valor == null) {
            return null;
        }
        if (valor.trim().isEmpty()) {
            aviso("El dato es obligatorio.");
            return pedirObligatorio(mensaje);
        }
        return valor.trim();
    }

    private static Double pedirDouble(String mensaje) {
        String valor = pedir(mensaje);
        if (valor == null) {
            return null;
        }
        try {
            return Double.parseDouble(valor.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            aviso("Valor numérico inválido.");
            return null;
        }
    }

    private static void aviso(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje);
    }

    private static void mostrar(String titulo, String texto) {
        if (texto == null) {
            texto = "(sin información)";
        }
        JTextArea area = new JTextArea(texto, 22, 90);
        area.setEditable(false);
        area.setCaretPosition(0);
        JScrollPane scroll = new JScrollPane(area);
        JOptionPane.showMessageDialog(null, scroll, titulo,
                JOptionPane.INFORMATION_MESSAGE);
    }
}
