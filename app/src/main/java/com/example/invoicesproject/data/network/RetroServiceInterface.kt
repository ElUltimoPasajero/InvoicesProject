package com.example.invoicesproject.data.network
import retrofit2.Call
import com.example.invoicesproject.data.network.model.InvoiceRepositoriesListResponse
import retrofit2.http.GET

/**
 * Interfaz que define las operaciones para realizar llamadas a la API de facturas mediante Retrofit con método GET.
 */
interface RetroServiceInterface {
    @GET("facturas")
    fun getDataFromApi(): Call<InvoiceRepositoriesListResponse>
}