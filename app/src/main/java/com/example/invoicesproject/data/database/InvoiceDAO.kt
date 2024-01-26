package com.example.invoicesproject.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.invoicesproject.data.database.Invoice

/**
 * Data Access Object (DAO) para interactuar con la tabla de facturas en la base de datos.
 */
@Dao
interface InvoiceDAO {

    /**
     * Obtiene todas las facturas almacenadas en la tabla.
     *
     * @return LiveData que emite una lista de facturas en tiempo real.
     */
    @Query("SELECT * FROM invoice_table")
    fun getAllInvoices(): LiveData<List<Invoice>>

    /**
     * Inserta o reemplaza una factura en la tabla.
     *
     * @param invoice Factura a ser insertada o reemplazada.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInvoices(invoice: Invoice)

    /**
     * Elimina todas las facturas de la tabla.
     */
    @Query("DELETE FROM invoice_table")
    fun deleteAllInvoices()
}