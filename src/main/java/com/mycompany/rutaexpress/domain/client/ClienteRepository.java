package com.mycompany.rutaexpress.domain.client;

/**
 * Contrato de consulta de clientes usado por otros managers del dominio.
 */
public interface ClienteRepository {
    Cliente buscarPorDocumento(String documento);
}
