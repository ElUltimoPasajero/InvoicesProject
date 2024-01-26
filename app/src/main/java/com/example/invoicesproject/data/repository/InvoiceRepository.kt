package com.example.invoicesproject.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.invoicesproject.data.database.InvoiceDAO
import com.example.invoicesproject.data.database.Invoice
import com.example.invoicesproject.data.network.RetroServiceInterface
import com.example.invoicesproject.data.network.model.InvoiceRepositoriesListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

/**
 * Repositorio que gestiona la obtención de datos de facturas desde una fuente remota (API)
 * y su almacenamiento local en una base de datos Room.
 *
 * @param retroService Interfaz Retrofit para realizar llamadas a la API.
 * @param invoiceDao Objeto de acceso a datos (DAO) para interactuar con la base de datos Room.
 */

class InvoiceRepository @Inject constructor(
    private val retroService: RetroServiceInterface,
    private val invoiceDao: InvoiceDAO
){

    /**
     * Obtiene todas las facturas almacenadas en la base de datos local (Room).
     *
     * @return LiveData que contiene la lista de facturas.
     */

    fun getAllInvoicesFromRoom(): LiveData<List<Invoice>> {
        return invoiceDao.getAllInvoices()
    }

    /**
     * Inserta una factura en la base de datos local.
     *
     * @param invoice Factura a insertar.
     */
    fun insertInvoicesInRoom(invoice: Invoice) {
        invoiceDao.insertInvoices(invoice)
    }

    /**
     * Realiza una llamada a la API para obtener datos de facturas y los almacena en la base de datos local.
     */

    fun makeApiCall() {
        val call: Call<InvoiceRepositoriesListResponse> = retroService.getDataFromApi()
        call?.enqueue(object : Callback<InvoiceRepositoriesListResponse>{
            override fun onResponse(
                call: Call<InvoiceRepositoriesListResponse>,
                response: Response<InvoiceRepositoriesListResponse>
            ) {
                if (response.isSuccessful) {
                    // Borra todas las facturas existentes en la base de datos local

                    invoiceDao.deleteAllInvoices()

                    // Inserta las nuevas facturas obtenidas de la API en la base de datos local

                    response.body()?.facturas?.forEach{
                        insertInvoicesInRoom(Invoice(decEstado = it.descEstado, importeordenacion = it.importeOrdenacion, fecha = it.fecha))
                    }
                }
            }


            override fun onFailure(call: Call<InvoiceRepositoriesListResponse>, t: Throwable) {
                Log.d("ERROR", "Error establishing connection")
            }
        })
    }
}