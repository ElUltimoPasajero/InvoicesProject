package com.example.invoicesproject.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.invoicesproject.R

import android.content.Intent
import android.content.SharedPreferences

import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.invoicesproject.databinding.ActivityMainBinding
import com.example.invoicesproject.data.database.Invoice
import com.example.invoicesproject.ui.view.adapter.InvoiceAdapter
import com.example.invoicesproject.viewmodel.InvoiceViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Actividad principal que muestra una lista de facturas y permite aplicar filtros.
 */

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var maxAmount: Double = 0.0
    private lateinit var binding: ActivityMainBinding
    private lateinit var invoiceAdapter: InvoiceAdapter
    private var filter: Filter? = null
    private lateinit var intentLaunch: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle("Facturas")

        invoiceAdapter = InvoiceAdapter() { invoice ->
            onItemSelected(invoice)
        }

        initViewModel()
        initMainViewModel()

        intentLaunch =

            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

                if (result.resultCode == RESULT_OK) {

                    val maxImporte = result.data?.extras?.getDouble("MAX_IMPORTE") ?: 0.0

                    val filtroJson = result.data?.extras?.getString("FILTRO_ENVIAR_RECIBIR_DATOS")

                    if (filtroJson != null) {

                        val gson = Gson()

                        val objFiltro = gson.fromJson(filtroJson, FilterActivity::class.java)

                    }

                }

            }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var gson = Gson()
        return when (item.itemId) {
            R.id.invoice_menu_filter -> {

                // Abre la actividad de filtro al seleccionar el ícono de filtro en la barra de acciones.

                val miIntent = Intent(this, FilterActivity::class.java)
                miIntent.putExtra("MAX_IMPORTE", maxAmount)

                if(filter !=null){

                    miIntent.putExtra("FILTRO_ENVIAR_RECIBIR_DATOS",gson.toJson(filter))


                }
                intentLaunch.launch(miIntent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getMaxAmount(invoices: List<Invoice>): Double {
        var maxAmount = 0.0
        for (factura in invoices) {
            val actualInvoice = factura.importeordenacion
            if (maxAmount < actualInvoice!!) maxAmount = actualInvoice
        }
        return maxAmount
    }

    /**
     * Inicializa la interfaz de usuario y el adaptador de la lista de facturas.
     */

    private fun initViewModel() {
        binding.rvInvoiceList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            invoiceAdapter = InvoiceAdapter() { invoice ->
                onItemSelected(invoice)
            }
            adapter = invoiceAdapter
        }
    }

    /**
     * Inicializa el ViewModel principal y observa los cambios en la lista de facturas.
     */

    private fun initMainViewModel() {
        val viewModel = ViewModelProvider(this).get(InvoiceViewModel::class.java)
        viewModel.getAllRepositoryList().observe(this, Observer<List<Invoice>> {
            var filteredList = getSavedFilteredList()



            // Si no hay filtro o la lista filtrada está vacía, muestra la lista original.


            if (filteredList == null || filteredList.isEmpty()) {
                invoiceAdapter.setListInvoices(it)
            } else {
                invoiceAdapter.setListInvoices(filteredList)
            }

            invoiceAdapter.notifyDataSetChanged()

            // Si la lista de facturas está vacía, realiza una llamada a la API para obtener datos.


            if (it.isEmpty()) {
                viewModel.makeApiCall()
                Log.d("Datos", it.toString())
            }

            // Obtiene el filtro de la intención y aplica los filtros a la lista de facturas.

            val filtroJson = intent.getStringExtra("FILTRO_ENVIAR_RECIBIR_DATOS")
            if (filtroJson != null) {
                filter = Gson().fromJson(filtroJson, Filter::class.java)
                var invoiceList = it

                filter?.let { nonNullFilter ->

                    // Aplica los filtros.
                    if (nonNullFilter.maxDateValor.isEmpty()) {
                        val currentDate = Calendar.getInstance().time
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        nonNullFilter.maxDateValor = dateFormat.format(currentDate)
                    }
                    invoiceList = filterInvoiceByDate(nonNullFilter.minDateValor, nonNullFilter.maxDateValor, invoiceList)
                    invoiceList = filterInvoicesByCheckBox(nonNullFilter.status, invoiceList)
                    invoiceList = verifyBalanceBar(nonNullFilter.maxValueSliderValor, invoiceList)


                    //Si no hay ninguna factura por los filtros nos muestra este texto
                    if(invoiceList.isEmpty()){
                        binding.emptyInvoices.visibility= View.VISIBLE


                    }

                    invoiceAdapter.setListInvoices(invoiceList)
                    Log.d("FILTRO2", filter.toString())

                    saveInvoicesList(invoiceList)

                }

            } else {
                Log.e("MainActivity", "El valor de FILTRO_ENVIAR_RECIBIR_DATOS es null")
            }

            maxAmount = getMaxAmount(it)
           // Log.d("FILTROS!", filtroJson.toString())
        })
    }

    /**
     * Verifica si cada factura en la lista tiene un importe menor que el valor máximo del control deslizante.
     * @param maxValueSlider Valor máximo del control deslizante.
     * @param invoiceList Lista de facturas.
     * @return Lista filtrada de facturas.
     */

    private fun verifyBalanceBar(
        maxValueSlider: Double,
        invoiceList: List<Invoice>
    ): List<Invoice> {
        val filteredInvoices = ArrayList<Invoice>()
        for (factura in invoiceList) {
            if (factura.importeordenacion!! < maxValueSlider) {
                filteredInvoices.add(factura)
            }
        }
        return filteredInvoices
    }

    /**
     * Maneja la selección de una factura en la lista.
     * @param invoice Factura seleccionada.
     */

    private fun onItemSelected(invoice: Invoice) {
        // Muestra un fragmento emergente al seleccionar una factura.

        val fragmentManager = supportFragmentManager
        val customPopupFragment = FragmentPopUp()
        customPopupFragment.show(fragmentManager, "FragmentPopUp")
    }

    /**
     * Filtra la lista de facturas por rango de fechas.
     * @param dateFrom Fecha mínima en formato "dd/MM/yyyy".
     * @param dateUntil Fecha máxima en formato "dd/MM/yyyy".
     * @param filteredList Lista original de facturas.
     * @return Lista filtrada por fecha.
     */

    private fun filterInvoiceByDate(dateFrom: String, dateUntil: String,filteredList: List<Invoice>): List<Invoice> {
        val dateList= mutableListOf<Invoice>()
        if (dateFrom != getString(R.string.day_month_year) && dateUntil != getString(R.string.day_month_year)) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            val DMinDate: Date? = sdf.parse(dateFrom)
            val DMaxDate: Date? = sdf.parse(dateUntil)

            for (factura in filteredList) {
                val fecha = sdf.parse(factura.fecha)!!
                if (fecha.after(DMinDate) && fecha.before(DMaxDate)) {
                    dateList.add(factura)
                }
            }
        }else{
            return filteredList
        }

        return dateList
    }

    /**
     * Filtra la lista de facturas según el estado seleccionado en los checkboxes.
     * @param estate Estado seleccionado en los checkboxes.
     * @param invoiceList Lista original de facturas.
     * @return Lista filtrada por estado.
     */
    
    private fun filterInvoicesByCheckBox(
        estate: HashMap<String, Boolean>,
        invoiceList: List<Invoice>
    ): List<Invoice> {
        val checkBoxPaid = estate["PAGADAS_STRING"] ?: false
        val checkBoxCanceled = estate["ANULADAS_STRING"] ?: false
        val checkBoxFixedPayment = estate["CUOTA_FIJA_STRING"] ?: false
        val checkBoxPendingPayment = estate["PENDIENTE_PAGO_STRING"] ?: false
        val checkBoxPaymentPlan = estate["PLAN_PAGO_STRING"] ?: false

        val filteredInvoices = ArrayList<Invoice>()

        if (!checkBoxPaid && !checkBoxCanceled && !checkBoxFixedPayment && !checkBoxPendingPayment && !checkBoxPaymentPlan) {
            return invoiceList
        }

        for (invoice in invoiceList) {
            val status = invoice.decEstado
            val isPaid = status == "Pagada"
            val isCanceled = status == "Anuladas"
            val isFixedPayment = status == "cuotaFija"
            val isPendingPayment = status == "Pendiente de pago"
            val isPaymentPlan = status == "planPago"

            if ((isPaid && checkBoxPaid) || (isCanceled && checkBoxCanceled) || (isFixedPayment && checkBoxFixedPayment) || (isPendingPayment && checkBoxPendingPayment) || (isPaymentPlan && checkBoxPaymentPlan)) {
                filteredInvoices.add(invoice)
            }
        }
        return filteredInvoices
    }

    private fun saveInvoicesList(filteredList: List<Invoice>) {
        val gson = Gson()
        val filteredListJson = gson.toJson(filteredList)

        val prefs: SharedPreferences = getPreferences(MODE_PRIVATE)
        prefs.edit().putString("LIST", filteredListJson).apply()
    }


    private fun getSavedFilteredList(): List<Invoice>? {
        val prefs: SharedPreferences = getPreferences(MODE_PRIVATE)
        val filteredListJson: String? = prefs.getString("LIST", null)

        return if (filteredListJson != null) {
            val gson = Gson()
            val type = object : TypeToken<List<Invoice>>() {}.type
            gson.fromJson(filteredListJson, type)
        } else {
            null


        }
    }

}