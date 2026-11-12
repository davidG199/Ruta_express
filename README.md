# RutaExpress Antioquia

Programa de gestión logística para el proyecto final de Estructuras de Datos.
Sigue el estilo de `Archivo601`: Java 21, Maven, menú con `JOptionPane`,
clases con getters/setters y estructuras de datos implementadas a mano.

## Requisitos

- JDK 21
- Maven 3.9+ (opcional, también se puede compilar con `javac`)

## Compilar y ejecutar

Con Maven:

```bash
mvn clean package
java -jar target/RutaExpress-1.0-SNAPSHOT.jar
```

Con `javac`:

```bash
mkdir -p target/classes
javac -encoding UTF-8 -d target/classes $(find src/main/java -name "*.java")
java -cp target/classes com.mycompany.rutaexpress.main.RutaExpress
```

También puede ejecutarse con `./run.sh` en Linux.

Los archivos de datos (`config.txt`, `red.txt`, `clientes.txt`, `paquetes.txt`,
`vehiculos.txt`, `pqr.txt`) deben quedar en la carpeta desde la que se ejecuta
el programa. El proyecto ya incluye datos de ejemplo.

## Estructuras implementadas

Todas están en clases propias; no se usa `ArrayList`, `LinkedList`, `HashMap`,
`Stack`, `Queue`, `TreeMap` ni ninguna otra colección de Java.

| Estructura | Clases |
|---|---|
| Lista simple de clientes | `ListaSimpleClientes`, `NodoCliente` |
| Lista simple de guías | `ListaSimpleGuias`, `NodoGuia` |
| Árbol ternario de búsqueda | `ArbolTernarioClientes`, `NodoTernario` |
| Lista simple de sedes | `ListaSimpleSedes`, `NodoSede` |
| Lista simple de vías | `ListaSimpleVias`, `NodoVia` |
| Cola de despacho | `ColaPaquetes`, `NodoColaPaquete` |
| Árbol binario de búsqueda | `ArbolPaquetes`, `NodoPaquete` |
| Lista simple de historial | `ListaSimpleHistorial`, `NodoHistorial` |
| Pila de paquetes | `PilaPaquetes`, `NodoPilaPaquete` |
| Lista doblemente enlazada | `ListaDobleVehiculos`, `NodoVehiculo` |
| Cola de PQR | `ColaPQR`, `NodoPQR` |
| Lista simple de seguimientos | `ListaSimpleSeguimientos`, `NodoSeguimiento` |
| Grafo ponderado | `GrafoRed` + Dijkstra con arreglos |

`NodoPaquete` es a la vez el node del árbol y la entidad del paquete. El árbol,
la cola de la sede y la pila del vehículo apuntan a la **misma instancia**; no se
copian objetos.

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

El envío de correo es **simulado** para el proyecto académico: se escribe en
`notificaciones.log`. Si la configuración falta o el destino es inválido, se
registra en `fallos.log` y la operación continúa. En `config.txt` se incluyen
credenciales de demostración, no reales.

## Archivos de datos

### `red.txt`
```text
SEDE;codigo;nombre;municipio;direccion;tipo;telefono;correoCoordinador;capacidadBodegaKg
VIA;origen;destino;tipoVia;estado;distanciaKm;tiempoMin;peajes
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
