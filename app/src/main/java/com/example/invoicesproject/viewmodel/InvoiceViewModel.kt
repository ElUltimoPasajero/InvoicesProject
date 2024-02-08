package com.example.invoicesproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.invoicesproject.data.database.Invoice
import com.example.invoicesproject.data.repository.InvoiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel para gestionar la lógica relacionada con las facturas en la interfaz de usuario.
 * Utiliza el repositorio de facturas para obtener datos y realizar llamadas a la API.
 */
@HiltViewModel
class InvoiceViewModel @Inject constructor(private val invoiceRepository: InvoiceRepository) : ViewModel() {

    /**
     * Obtiene una lista observable de facturas desde el repositorio.
     *
     * @return LiveData que contiene la lista de facturas.
     */
    fun getAllRepositoryList(): LiveData<List<Invoice>> {
        return invoiceRepository.getAllInvoicesFromRoom()
    }

    /**
     * Realiza una llamada a la API para obtener nuevas facturas.
     */
    fun makeApiCall() {
        invoiceRepository.makeApiCall()
    }

    fun changueService(newDatos : String){

        invoiceRepository.setDatos(newDatos)


    }
}