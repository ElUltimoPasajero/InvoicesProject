package com.example.invoicesproject.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.app.DatePickerDialog
import android.provider.SyncStateContract
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import com.example.invoicesproject.R
import com.example.invoicesproject.databinding.ActivityFilterBinding
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class FilterActivity : AppCompatActivity() {


    private lateinit var binding: ActivityFilterBinding
    private var filter: Filter? = null
    private lateinit var paid: CheckBox
    private lateinit var cancelled: CheckBox
    private lateinit var fixedPayment: CheckBox
    private lateinit var pendingPayment: CheckBox
    private lateinit var paymentPlan: CheckBox
    private var maxAmount: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()

        // Establecer el título de la barra de acción.

        supportActionBar?.setTitle("Filtrar Facturas")

        // Manejar clic en el botón "Aplicar".

        binding.buttonApply.setOnClickListener {

            // Serializar el filtro y enviarlo a la actividad principal.

            val gson = Gson()
            val maxValueSlider = binding.sliderAmmount.progress.toDouble()
            val state = hashMapOf(
                "PAGADAS_STRING" to paid.isChecked,
                "ANULADAS_STRING" to cancelled.isChecked,
                "CUOTA_FIJA_STRING" to fixedPayment.isChecked,
                "PENDIENTE_PAGO_STRING" to pendingPayment.isChecked,
                "PLAN_PAGO_STRING" to paymentPlan.isChecked
            )


            val minDate = binding.buttonFrom.text.toString()
            val maxDate = binding.buttonUntil.text.toString()
            Log.d("CHECK", state.toString())
            Log.d("MAX", maxValueSlider.toString())
            Log.d("MINDATE", minDate.toString())
            Log.d("MAXDATE", maxDate.toString())
            val filter: com.example.invoicesproject.ui.view.Filter =
                Filter(maxDate, minDate, maxValueSlider, state)
            val miIntent = Intent(this, MainActivity::class.java)
            miIntent.putExtra("FILTRO_ENVIAR_RECIBIR_DATOS", gson.toJson(filter))
            startActivity(miIntent)
            Log.d("FILTROS!", filter.toString())
        }


    }

    /**
     * Inicializa los componentes de la actividad.
     */

    private fun initComponents() {
        initCalendar()
        initSeekBar()
        initCheckBoxes()
        initResetButton()
        applyTheSavedFilters()
        // Intenta cargar los filtros iniciales si existen
        val filtroJson = intent.getStringExtra("FILTRO_ENVIAR_RECIBIR_DATOS")
        Log.d("Cosa", filtroJson.toString())
        if (filtroJson != null) {
            filter = Gson().fromJson(filtroJson, Filter::class.java)
            filter?.let { nonNullFilter ->
                loadFilters(nonNullFilter)
            }
        }
        Log.d("FiltroJSON", filtroJson.toString())

        binding.buttonApply.setOnClickListener {
            updateAndSaveFilters()
            val gson = Gson()
            val maxValueSlider = binding.sliderAmmount.toString().toDouble()
            val state = hashMapOf(
                "PAGADAS_STRING" to paid.isChecked,
                "ANULADAS_STRING" to cancelled.isChecked,
                "CUOTA_FIJA_STRING" to fixedPayment.isChecked,
                "PENDIENTES_PAGO_STRING" to pendingPayment.isChecked,
                "PLAN_PAGO_STRING" to paymentPlan.isChecked
            )

            val minDate = binding.buttonFrom.text.toString()
            val maxDate = binding.buttonUntil.text.toString()
            val filter: Filter = Filter(maxDate, minDate, maxValueSlider, state)

            if (!minDate.equals("dia/mes/año") && !maxDate.equals("dia/mes/año")) {
                val miIntent = Intent(this, MainActivity::class.java)
                miIntent.putExtra("FILTRO_ENVIAR_RECIBIR_DATOS", gson.toJson(filter))

                startActivity(miIntent)
            } else {

            }
        }
    }


    /**
     * Inicializa el calendario y maneja la selección de fechas.
     */

    private fun initCalendar() {

        // Configuración del botón de fecha "Desde".

        binding.buttonFrom.setOnClickListener {
            obtainDate(binding.buttonFrom, false)

            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year1, monthOfYear, dayOfMonth ->
                    binding.buttonFrom.text = "$dayOfMonth/${monthOfYear + 1}/$year1"
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        // Configuración del botón de fecha "Hasta".

        binding.buttonUntil.setOnClickListener {
            obtainDate(binding.buttonUntil, true, minDate = obtainMinDateAux())

            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year1, monthOfYear, dayOfMonth ->
                    binding.buttonUntil.text = "$dayOfMonth/${monthOfYear + 1}/$year1"
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
    }

    private fun obtainDate(btnDate: Button, minDateRestriction: Boolean, minDate: Long? = null) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
// Crear un DatePickerDialog con la fecha actual como predeterminada
        val datePickerDialog = DatePickerDialog(
            this,
            { view, year1, month1, dayOfMonth ->
                btnDate.text = "$dayOfMonth/${month1 + 1}/$year1"
            },
            year,
            month,
            day
        )
// Aplicar restriccion de fecha minima si es necesario
        if (minDateRestriction) {
            minDate?.let {
                datePickerDialog.datePicker.minDate = it
            }
        }
        datePickerDialog.show()
    }

    private fun obtainMinDateAux(): Long {
// Formato de echa esperado en el botón
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
// Obtiene la fecha desde tu botón
        val selectedDateFrom = binding.buttonFrom.text.toString()

        try {
// Parsea la fecha y la devuelve como tipo Date
            val dateFrom = dateFormat.parse(selectedDateFrom)
            return dateFrom?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0L
    }

    /**
     * Inicializa el control deslizante (SeekBar) y maneja los cambios.
     */

    private fun initSeekBar() {
        var maxAmount = intent.getDoubleExtra("MAX_IMPORTE", 0.0).toInt() + 1
        binding.sliderAmmount.max = maxAmount
        binding.sliderCount.text = "${maxAmount}"
        binding.sliderMaxValor.text = "${maxAmount}€"
        binding.sliderMinValor.text = "0€"

        binding.sliderAmmount.progress = maxAmount


        binding.sliderAmmount.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.sliderCount.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //
            }
        })

    }

    /**
     * Inicializa las casillas de verificación.
     */
    private fun initCheckBoxes() {
        paid = binding.checkBoxPaid
        cancelled = binding.checkBoxCancel
        fixedPayment = binding.checkBoxFixedPayment
        pendingPayment = binding.checkBoxPendingPayment
        paymentPlan = binding.checkBoxPayPlan
    }

    /**
     * Infla el menú de opciones en la barra de acción.
     *
     * @param menu El menú en el que se inflan las opciones.
     * @return `true` si el menú se infla correctamente, `false` en caso contrario.
     *
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter, menu)
        return true
    }

    /**
     * Maneja los eventos de selección de elementos del menú de opciones.
     *
     * @param item El elemento de menú seleccionado.
     * @return `true` si el evento se maneja correctamente, `false` en caso contrario.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.invoice_filter_close -> {
                // Inicia la actividad de filtro al seleccionar la opción del menú
                val filterIntent = Intent(this, MainActivity::class.java)
                startActivity(filterIntent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveFilters(filter: Filter) {
        val prefs = getPreferences(MODE_PRIVATE)
        val gson = Gson()
        val filterJson = gson.toJson(filter)

        prefs.edit().putString("FILTER_STATUS", filterJson).apply()
    }

    private fun initResetButton() {
        binding.buttonRestart.setOnClickListener {
            resetFilters()
        }
    }

    private fun resetFilters() {
        maxAmount = intent.getDoubleExtra("MAX_IMPORTE", 0.0).toInt() + 1

        binding.sliderAmmount.progress = maxAmount
        binding.buttonFrom.text = getString(R.string.day_month_year)
        binding.buttonUntil.text = getString(R.string.day_month_year)
        binding.checkBoxPaid.isChecked = false
        binding.checkBoxCancel.isChecked = false
        binding.checkBoxFixedPayment.isChecked = false
        binding.checkBoxPendingPayment.isChecked = false
        binding.checkBoxPayPlan.isChecked = false

    }

    private fun loadFilters(filter: Filter) {
        binding.buttonFrom.text = filter.minDateValor
        binding.buttonUntil.text = filter.maxDateValor
        binding.sliderAmmount.progress = filter.maxValueSliderValor.toInt()
        binding.checkBoxPaid.isChecked = filter.status["PAGADAS_STRING"] ?: false
        binding.checkBoxCancel.isChecked = filter.status["ANULADAS_STRING"] ?: false
        binding.checkBoxFixedPayment.isChecked = filter.status["CUOTA_FIJA_STRING"] ?: false
        binding.checkBoxPendingPayment.isChecked = filter.status["PENDIENTE_PAGO_STRING"] ?: false
        binding.checkBoxPayPlan.isChecked = filter.status["PLAN_PAGO_STRING"] ?: false
    }

    private fun applyTheSavedFilters() {
        val prefs = getPreferences(MODE_PRIVATE)
        val filterJson = prefs.getString("FILTER_STATUS", null)

        if (filterJson != null) {
            val gson = Gson()
            filter = gson.fromJson(filterJson, Filter::class.java)
            filter?.let { nonNullFilter ->

                loadFilters(nonNullFilter)


            }
        }
    }

    private fun updateAndSaveFilters() {
        val maxValueSlider = binding.sliderAmmount.toString().toDouble()
        val state = hashMapOf(
            "PAGADAS_STRING" to paid.isChecked,
            "ANULADAS_STRING" to cancelled.isChecked,
            "CUOTA_FIJA_STRING" to fixedPayment.isChecked,
            "PENDIENTE_PAGO_STRING" to pendingPayment.isChecked,
            "PLAN_PAGO_STRING" to paymentPlan.isChecked
        )
        val minDate = binding.buttonFrom.text.toString()
        val maxDate = binding.buttonUntil.text.toString()
        filter = Filter(maxDate, minDate, maxValueSlider, state)

        saveFilters(filter!!)
    }


}