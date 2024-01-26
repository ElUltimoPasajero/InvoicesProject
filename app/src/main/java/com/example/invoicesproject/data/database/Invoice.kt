package com.example.invoicesproject.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Clase que representa una factura.
 *
 * Esta clase se utiliza en conjunto con la biblioteca Room para la persistencia de datos en la base de datos.
 *
 * @property id Identificador único de la factura.
 * @property decEstado Estado de la factura en formato decimal.
 * @property importeordenacion Importe de la ordenación de la factura.
 * @property fecha Fecha de emisión de la factura.
 */

@Entity(tableName = "invoice_table")
class Invoice(
    @PrimaryKey(autoGenerate = true)  // Indicamos que el id sera PrimayKey y sera autogenerado
    val id: Int = 0,
    val decEstado: String?,
    val importeordenacion: Double?,
    val fecha: String?
) {}