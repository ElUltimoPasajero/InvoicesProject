package com.example.invoicesproject.data.network.model

/**
 * Clase de datos que representa la respuesta de la lista de repositorios de facturas desde la red.
 *
 * @property numFacturas Número total de facturas en la respuesta.
 * @property facturas Lista de objetos [InvoiceResponse] que representan las facturas.
 */
data class InvoiceRepositoriesListResponse(

    val numFacturas: Int,
    val facturas: List<InvoiceResponse>

)