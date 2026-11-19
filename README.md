# RutaExpress Antioquia

Programa de gestión logística para el proyecto final de Estructuras de Datos.
Sigue el estilo de `Archivo601`: Java 21, Maven, menú con `JOptionPane`,
clases con getters/setters y estructuras de datos implementadas a mano.

## Arquitectura

La aplicación está separada por responsabilidades:

- `main/SistemaRutaExpress`: fachada que compone los managers y expone las
  operaciones que consume la interfaz.
- `domain`: entidades y managers de clientes, red, paquetes, vehículos, PQR y
  configuración. Aquí vive la lógica de negocio.
- `estructures`: estructuras genéricas implementadas a mano (`List`, `Cola`,
  `Pila`, `ListaDoble`, árboles y grafo).
- `files`: lectura/escritura de archivos, notificaciones y generación de informes.
- `ui/RutaExpress`: menús de texto y diálogos.

## Requisitos

- JDK 21
- Maven 3.9+ (recomendado para empaquetar el JAR ejecutable con Jakarta Mail)
- Alternativa sin Maven: JDK 21 y Jakarta Mail 2.0.5 descargado en `~/.m2/repository`

## Compilar y ejecutar

Con Maven:

```bash
mvn clean package
java -jar target/RutaExpress-1.0-SNAPSHOT.jar
```

Sin Maven, `./run.sh` compila con Java 21 y usa Jakarta Mail, Activation API y Angus Activation descargados en `~/.m2/repository`. No requiere enviar mensajes reales durante la compilación.

Los archivos de datos (`config.txt`, `red.txt`, `clientes.txt`, `paquetes.txt`,
`vehiculos.txt`, `pqr.txt`) deben quedar en la carpeta desde la que se ejecuta
el programa. El proyecto ya incluye datos de ejemplo.

## Estructuras implementadas

Todas están en clases propias; no se usa `ArrayList`, `LinkedList`, `HashMap`,
`Stack`, `Queue`, `TreeMap` ni ninguna otra colección de Java.

| Estructura | Clases |
|---|---|
| Lista simple | `List`, `Node` |
| Árbol ternario de búsqueda | `ArbolTernario` |
| Árbol binario de búsqueda | `ArbolBinario` |
| Lista doblemente enlazada | `ListaDoble` |
| Cola | `Cola` |
| Pila | `Pila` |
| Grafo ponderado | `GrafoRed` + Dijkstra con arreglos |

La entidad activa es `domain.parcel.Paquete`. El árbol de paquetes, la cola de la
sede y la pila del vehículo apuntan a la **misma instancia**; no se copian objetos.

## Operaciones del menú

- **Clientes:** registrar, buscar por prefijo (árbol ternario), ver historial y listar.
- **Red de sedes:** mostrar grafo, consultar mejor ruta, cambiar estado de una vía,
  consultar sedes aisladas y ver bodega.
- **Paquetes:** admitir, rastrear, listar por estado y ver estadísticas.
- **Flota:** registrar vehículo, cargarlo desde archivo, cargar paquetes, despachar,
  registrar entrega y listar.
- **PQR:** radicar, atender y ver cola.
- **Reportes:** genera `informe_AAAA-MM-DD.txt`.
- **Configuración:** ver/cambiar precio del galón y recargar `config.txt`.

## Reglas de negocio implementadas

- Guía única de 9 dígitos generada al azar.
- Peso volumétrico: `(largo × ancho × alto) / 5000`.
- Peso facturable: `máx(pesoKg, pesoVolumétrico)`.
- Costo: `(tarifaBase + 1500 × pesoFacturable + 120 × distanciaKm + 0,01 × valorDeclarado) × (1 + recargoFrágil)`.
- Tarifas base: estándar $8.000, express $15.000, contra entrega $10.000 + 2 %.
- Recargo por frágil: 10 %. Plazo: 3 días estándar, 1 día express.
- Solo se admiten paquetes con camino de vías `HABILITADA`.
- Bodega: alerta al superar el 80 %; si no hay capacidad, el paquete se rechaza y
  no se guarda en ninguna estructura.
- Vehículo: solo se despacha si está `DISPONIBLE`; la moto lleva máximo 10 kg y
  no acepta paquetes frágiles.
- PQR no cerrada: vuelve al final de la cola.
- Al cerrar una vía se notifica a los coordinadores de las sedes que quedan
  aisladas de la red principal. Se considera aislada toda sede que no pertenece
  al componente conectado más grande del grafo.
- La cuenta de correo se lee de `config.txt`, nunca está escrita en el código.

## Correo y notificaciones

El envío de correo se realiza por SMTP con Jakarta Mail usando los datos de
`config.txt`. Si la configuración falta o el destino es inválido, se registra el
fallo en `fallos.log` y la operación continúa. La alternativa de compilación local
no envía correos; no se deben probar destinatarios reales sin autorización.

## Archivos de datos

### `red.txt`
La red inicial usa dos bloques separados por la línea `VIAS`. Las sedes contienen
código, nombre, municipio, dirección, tipo, capacidad en kg, teléfono y correo;
las vías contienen origen, destino, distancia en km, tiempo en minutos, peajes,
tipo de vía y estado. También se admite el formato etiquetado legado `SEDE;...`
/ `VIA;...`.

```text
SEDES
codigo;nombre;municipio;direccion;tipo;capacidadKg;telefono;correoCoordinador
VIAS
origen;destino;distanciaKm;tiempoMin;peajes;tipoVia;estado
```

### `clientes.txt`
```text
documento;tipoCliente;nombre;telefono;correo;direccion;municipio
```

### `paquetes.txt`
```text
guia;documentoRemitente;nombreDestinatario;telefonoDestinatario;correoDestinatario;direccionEntrega;sedeOrigen;sedeDestino;pesoKg;largoCm;anchoCm;altoCm;valorDeclarado;tipoServicio;fragil
```

### `vehiculos.txt`
```text
placa;tipo;sedeBase;estado;capacidadKg;capacidadM3;rendimientoKmGalon;kmRecorridos;cedula;nombre;celular;correo;categoriaLicencia
```

### `pqr.txt`
```text
radicado;documentoCliente;numeroGuia;tipo;estado;descripcion;fechaRadicacion
```

Las líneas que empiezan por `#` se ignoran.
