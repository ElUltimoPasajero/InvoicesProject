package com.example.invoicesproject.data.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos Room que contiene la tabla de facturas.
 *
 * @property getAppDao Método para obtener el objeto de acceso a datos (DAO) relacionado con las facturas.
 */
@Database(entities = [Invoice::class], version = 1, exportSchema = false)
abstract class InvoiceDatabase : RoomDatabase() {

    abstract fun getAppDao(): InvoiceDAO

    companion object {
        private var DB_INSTANCE: InvoiceDatabase? = null

        /**
         * Obtiene una instancia única de la base de datos o la crea si no existe.
         *
         * @param context Contexto de la aplicación.
         * @return Instancia de la base de datos de facturas.
         */
        fun getAppDBInstance(context: Context): InvoiceDatabase {
            if (DB_INSTANCE == null) {
                DB_INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    InvoiceDatabase::class.java,
                    "invoice_database"
                )
                    .allowMainThreadQueries() // Permite consultas en el hilo principal (cuidado al usar esto)
                    .build()
            }
            return DB_INSTANCE!!
        }
    }
}