package com.example.invoicesproject.data.network.model

/**
 * Clase de datos que representa la respuesta de una factura desde la red.
 *
 * @property descEstado Descripción del estado de la factura.
 * @property fecha Fecha de emisión de la factura.
 * @property importeOrdenacion Importe de la ordenación de la factura.
 */
data class InvoiceResponse(
    val descEstado: String,
    val fecha: String,
    val importeOrdenacion: Double
)