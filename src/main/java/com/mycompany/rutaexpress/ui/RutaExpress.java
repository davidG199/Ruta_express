package com.mycompany.rutaexpress.ui;

import com.mycompany.rutaexpress.domain.client.Cliente;
import com.mycompany.rutaexpress.domain.client.ClientManager;
import com.mycompany.rutaexpress.domain.parcel.Paquete;
import com.mycompany.rutaexpress.main.SistemaRutaExpress;

/*
 * Interfaz de usuario de la aplicación RutaExpress.
 * En esta clase se implementan los diferentes menus de texto para interactuar con el sistema.
 * Se utilizan métodos de la clase Dialogos para mostrar diálogos de entrada y salida.
 */

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

    //Menu para clientes
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

                    Cliente cliente = new Cliente(documento, tipo, nombre, telefono, correo, direccion, municipio);
                    if (sistema.registrarCliente(cliente)) {
                        aviso("Cliente registrado correctamente.");
                        String resultadoCorreo = sistema.getClienteManager().getUltimoMensajeCorreo();
                        if (resultadoCorreo != null && !resultadoCorreo.isEmpty()) {
                            aviso(resultadoCorreo);
                        }
                    } else {
                        aviso(sistema.getUltimoError());
                    }

                }
                case "Buscar por prefijo" -> {
                    String prefijo = pedir("Prefijo del nombre:");
                    if (prefijo != null) {
                        mostrar("Clientes encontrados", sistema.getClienteManager().buscarPorPrefijo(prefijo));
                    }
                }
                case "Ver historial de cliente" -> {
                    String documento = pedirObligatorio("Documento del cliente:");
                    if (documento != null) {
                        mostrar("Historial del cliente",
                                sistema.getClienteManager().consultarHistorial(documento));
                    }
                }
                case "Listar clientes" ->
                    mostrar("Clientes", sistema.getClienteManager().listar());
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
                    Paquete p = sistema.admitirPaquete(datos);
                    if (p == null) {
                        aviso(sistema.getUltimoError());
                    } else {
                        mostrar("Paquete admitido", "Guía: " + p.getGuia()
                                + "\nCosto: $" + String.format("%.0f", p.getCostoEnvio())
                                + "\nFecha máxima: " + p.getFechaMaximaEntrega()
                                + (sistema.getUltimoError().isEmpty() ? ""
                                        : "\n\n" + sistema.getUltimoError())
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
                    String sede = pedirObligatorio("Código de sede de llegada:");
                    if (sede == null) {
                        continue;
                    }
                    mostrar("Llegada a sede", sistema.registrarEntrega(placa, sede));
                    boolean seguir = true;
                    while (seguir) {
                        var vehiculo = sistema.getVehiculoManager().buscarPorPlaca(placa);
                        if (vehiculo == null || !vehiculo.getSedeActual().equalsIgnoreCase(sede)
                                || !vehiculo.tienePaquetesPara(sede)) {
                            break;
                        }
                        String guia = pedirObligatorio("Guía del paquete a entregar:");
                        if (guia == null) {
                            break;
                        }
                        String resultado = menu("Resultado de entrega",
                                new String[]{"Entregado", "No entregado", "Terminar parada"});
                        if (resultado == null || resultado.equals("Terminar parada")) {
                            break;
                        }
                        mostrar("Resultado de entrega", sistema.registrarResultadoEntrega(
                                placa, sede, guia, resultado.equals("Entregado")));
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
                        aviso("PQR radicada correctamente.\n"
                                + sistema.getUltimoMensajePQR());
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

    // Métodos auxiliares para interacción con el usuario mediante diálogos, metodo reutilizable.
    private static String menu(String titulo, String[] opciones) {
        return Dialogos.menu(titulo, opciones);
    }

    private static String pedir(String mensaje) {
        return Dialogos.pedir(mensaje);
    }

    private static String pedirObligatorio(String mensaje) {
        return Dialogos.pedirObligatorio(mensaje);
    }

    private static Double pedirDouble(String mensaje) {
        return Dialogos.pedirDouble(mensaje);
    }

    //metodo para mostrar un mensaje de aviso al usuario mediante un cuadro de diálogo.
    private static void aviso(String mensaje) {
        Dialogos.aviso(mensaje);
    }

    private static void mostrar(String titulo, String texto) {
        Dialogos.mostrar(titulo, texto);
    }
}
