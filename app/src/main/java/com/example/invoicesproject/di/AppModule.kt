package com.example.invoicesproject.di

import android.content.Context
import co.infinum.retromock.Retromock
import com.example.invoicesproject.ResourceBodyFactory
import com.example.invoicesproject.data.database.InvoiceDAO
import com.example.invoicesproject.data.database.InvoiceDatabase
import com.example.invoicesproject.data.network.APIRetrofitService
import com.example.invoicesproject.data.network.APIRetromockService
import com.example.invoicesproject.data.network.RetroServiceInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

/**
 * Módulo Dagger Hilt que proporciona las dependencias necesarias para la inyección de dependencias en la aplicación.
 */

@Module
@InstallIn(SingletonComponent::class)
class AppModule {



    /**
     * Provee una instancia de la base de datos de facturas (Room).
     *
     * @param context Contexto de la aplicación.
     * @return Instancia de [InvoiceDatabase].
     */

    @Provides
    @Singleton
    fun getAppDatabase(@ApplicationContext context: Context): InvoiceDatabase {
        return InvoiceDatabase.getAppDBInstance(context)
    }

    /**
     * Provee una instancia del DAO (Data Access Object) para interactuar con la base de datos Room.
     *
     * @param invoiceDatabase Instancia de [InvoiceDatabase].
     * @return Instancia de [InvoiceDAO].
     */

    @Provides
    @Singleton
    fun getAppDao(invoiceDatabase: InvoiceDatabase): InvoiceDAO {
        return invoiceDatabase.getAppDao()
    }


    /**
     * Provee una instancia de la interfaz Retrofit para realizar llamadas a la API.
     *
     * @param retrofit Instancia de [Retrofit].
     * @return Instancia de [RetroServiceInterface].
     */
    @Provides

    fun getRetroServiceInterface(
        retrofit: Retrofit,
        retromock: Retromock,
        datos: String
    ): RetroServiceInterface {

        return if (datos == "real")
            retrofit.create(APIRetrofitService::class.java)
        else if (datos == "ficticio")
            retromock.create(APIRetromockService::class.java)
        else
            throw Error("No implementado")


    }

    @Provides
    @Singleton
    fun buildRetromock(retrofit: Retrofit): Retromock {
        return Retromock.Builder()
            .retrofit(retrofit)
            .defaultBodyFactory(ResourceBodyFactory())
            .build()

    }

    /**
     * URL base para la comunicación con la API.
     */
    val BASE_URL = "https://viewnextandroid.wiremockapi.cloud/"

    /**
     * Provee una instancia de Retrofit.
     *
     * @return Instancia de [Retrofit].
     */
    @Provides
    @Singleton
    fun buildRetrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun getRetrofit(retrofit: Retrofit):APIRetrofitService{
        return retrofit.create(APIRetrofitService::class.java)


    }

    @Provides
    @Singleton
    fun getRetromock(retromock: Retromock):APIRetromockService{
        return retromock.create(APIRetromockService::class.java)


    }
}
