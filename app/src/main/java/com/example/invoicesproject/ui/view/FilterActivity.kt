package com.example.invoicesproject.ui.view
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.app.DatePickerDialog
import android.util.Log
import android.widget.CheckBox
import android.widget.SeekBar
import com.example.invoicesproject.R
import com.example.invoicesproject.databinding.ActivityFilterBinding
import com.google.gson.Gson
import java.util.Calendar


class FilterActivity : AppCompatActivity() {


    private lateinit var binding: ActivityFilterBinding
    private var filter: Filter? = null
    private lateinit var paid: CheckBox
    private lateinit var cancelled: CheckBox
    private lateinit var fixedPayment: CheckBox
    private lateinit var pendingPayment: CheckBox
    private lateinit var paymentPlan: CheckBox

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
            val filter: com.example.invoicesproject.ui.view.Filter = Filter(maxDate, minDate, maxValueSlider, state)
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
        applyTheSavedFilters()
        // Intenta cargar los filtros iniciales si existen
        val filtroJson = intent.getStringExtra("FILTRO_ENVIAR_RECIBIR_DATOS")
        Log.d("Cosa",filtroJson.toString())
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
            Log.d("CHECK", state.toString())
            Log.d("MAX", maxValueSlider.toString())
            Log.d("MINDATE", minDate)
            Log.d("MAXDATE", maxDate)
            val filter: com.example.invoicesproject.ui.view.Filter = Filter(maxDate, minDate, maxValueSlider, state)
            if (!minDate.equals("Dia/Mes/Año") && !maxDate.equals("Dia/Mes/Año")) {
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
        menuInflater.inflate(R.menu.menu_main, menu)
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
            R.id.invoice_menu_filter -> {
                // Inicia la actividad de filtro al seleccionar la opción del menú
                val filterIntent = Intent(this, FilterActivity::class.java)
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

    private fun loadFilters(filter: Filter) {
        binding.buttonFrom.text = filter.minDateValor
        binding.buttonUntil.text = filter.maxDateValor
        binding.sliderAmmount.progress = filter.maxValueSliderValor.toInt()
        binding.checkBoxPaid.isChecked = filter.status["PAGADAS_STRING"] ?: false
        binding.checkBoxCancel.isChecked = filter.status["ANULADAS_STRING"] ?: false
        binding.checkBoxFixedPayment.isChecked = filter.status["CUOTA_FIJA_STRING"] ?: false
        binding.checkBoxPendingPayment.isChecked = filter.status["PENDIENTES_PAGO_STRING"] ?: false
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
            "PENDIENTES_PAGO_STRING" to pendingPayment.isChecked,
            "PLAN_PAGO_STRING" to paymentPlan.isChecked
        )
        val minDate = binding.buttonFrom.text.toString()
        val maxDate = binding.buttonUntil.text.toString()
        filter = Filter(maxDate, minDate, maxValueSlider, state)

        saveFilters(filter!!)
    }



}