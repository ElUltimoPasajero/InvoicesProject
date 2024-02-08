package com.example.invoicesproject.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.app.DatePickerDialog
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.invoicesproject.R
import com.example.invoicesproject.databinding.ActivityFilterBinding
import com.google.gson.Gson
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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
    private lateinit var intentLaunch: ActivityResultLauncher<Intent>


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
            val filter: com.example.invoicesproject.ui.view.Filter =
                Filter(maxDate, minDate, maxValueSlider, state)
            val miIntent = Intent(this, MainActivity::class.java)
            miIntent.putExtra("FILTRO_ENVIAR_RECIBIR_DATOS", gson.toJson(filter))
            intentLaunch.launch(miIntent)
            finish()
        }

        intentLaunch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

                if (result.resultCode == RESULT_OK) {

                    val maxImporte = result.data?.extras?.getDouble("MAX_IMPORTE") ?: 0.0

                    val filtroJson = result.data?.extras?.getString("FILTRO_ENVIAR_RECIBIR_DATOS")

                    if (filtroJson != null) {

                        val gson = Gson()

                        val objFiltro = gson.fromJson(filtroJson, MainActivity::class.java)

                    }

                }

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
        loadFilterIfExist()


    }

    /**
     * Intenta cargar los filtros iniciales si existen
     */
    private fun loadFilterIfExist() {
        val filtroJson = intent.getStringExtra("FILTRO_ENVIAR_RECIBIR_DATOS")
        if (filtroJson != null) {
            filter = Gson().fromJson(filtroJson, Filter::class.java)
            filter?.let { nonNullFilter ->
                loadFilters(nonNullFilter)
            }
        }
    }


    /**
     * Inicializa el calendario y maneja la selección de fechas.
     */

    private fun initCalendar() {

        // Configuración del botón de fecha "Desde".
        ButtonFromInit()
        // Configuración del botón de fecha "Hasta".
        buttonUntilInit()

    }


    /**
     * Inicializa el comportamiento del botón "Until", permitiendo al usuario seleccionar una fecha
     * a través de un DatePickerDialog. La fecha seleccionada se muestra en el botón.
     */
    private fun buttonUntilInit() {
        binding.buttonUntil.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Crear un DatePickerDialog con la fecha actual como predeterminada

            val datePickerDialog = DatePickerDialog(
                this,
                { view, year1, month1, dayOfMonth ->
                    binding.buttonUntil.text = "$dayOfMonth/${month1 + 1}/$year1"
                },
                year,
                month,
                day
            )
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateFromLocale = binding.buttonFrom.text.toString()
            val dateFrom: Date
            try {
                dateFrom = simpleDateFormat.parse(dateFromLocale)
                datePickerDialog.datePicker.minDate = dateFrom.time
            } catch (e: ParseException) {

                e.printStackTrace()
            }
            datePickerDialog.show()
        }
    }

    /**
     * Inicializa el comportamiento del botón "From", permitiendo al usuario seleccionar una fecha
     * a través de un DatePickerDialog. La fecha seleccionada se muestra en el botón y se establece
     * como la fecha máxima permitida en el DatePickerDialog del botón "Until".
     */

    private fun ButtonFromInit() {

        // Establecer un escuchador de clics para el botón "From"

        binding.buttonFrom.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            // Crear un DatePickerDialog con la fecha actual como predeterminada
            val datePickerDialog = DatePickerDialog(
                this,
                { view, year1, month1, dayOfMonth ->
                    binding.buttonFrom.text = "$dayOfMonth/${month1 + 1}/$year1"
                },
                year,
                month,
                day
            )
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateUntilLocale = binding.buttonUntil.text.toString()
            val dateUntil: Date
            try {
                dateUntil = simpleDateFormat.parse(dateUntilLocale)
                datePickerDialog.datePicker.maxDate = dateUntil.time
            } catch (e: ParseException) {

                e.printStackTrace()
            }
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
                intentLaunch.launch(filterIntent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    /**
     * Guarda los filtros proporcionados en las preferencias compartidas de la aplicación.
     *
     * @param filter Objeto Filter que contiene los valores de los filtros a ser guardados.
     */
    private fun saveFilters(filter: Filter) {

        // Obtener las preferencias compartidas en modo privado
        val prefs = getPreferences(MODE_PRIVATE)
        // Crear una instancia de Gson para convertir el objeto Filter a formato JSON
        val gson = Gson()
        val filterJson = gson.toJson(filter)

        prefs.edit().putString("FILTER_STATUS", filterJson).apply()
    }


    /**
     * Inicializa el comportamiento del botón de reinicio, que restablece los filtros a sus
     * valores predeterminados.
     */
    private fun initResetButton() {
        // Establecer un escuchador de clics para el botón de reinicio
        binding.buttonRestart.setOnClickListener {
            // Llamar a la función que restablece los filtros
            resetFilters()
        }
    }


    /**
     * Restablece los filtros a sus valores predeterminados.
     * Se utiliza para reiniciar la interfaz de usuario y los filtros de búsqueda.
     */
    private fun resetFilters() {
        // Obtener el valor máximo del importe desde los extras de la intención
        // y sumar 1 para establecerlo como el nuevo valor máximo
        maxAmount = intent.getDoubleExtra("MAX_IMPORTE", 0.0).toInt() + 1

        // Restablecer el progreso del slider de importe al valor máximo

        binding.sliderAmmount.progress = maxAmount
        binding.buttonFrom.text = getString(R.string.day_month_year)
        binding.buttonUntil.text = getString(R.string.day_month_year)
        binding.checkBoxPaid.isChecked = false
        binding.checkBoxCancel.isChecked = false
        binding.checkBoxFixedPayment.isChecked = false
        binding.checkBoxPendingPayment.isChecked = false
        binding.checkBoxPayPlan.isChecked = false

    }

    /**
     * Carga los valores de los filtros proporcionados en la interfaz de usuario.
     * Se utiliza para restaurar el estado de la interfaz de usuario con los filtros previamente guardados.
     *
     * @param filter Objeto Filter que contiene los valores de los filtros a ser cargados.
     */
    private fun loadFilters(filter: Filter) {
        // Establecer el texto de los botones de fecha con los valores del filtro

        binding.buttonFrom.text = filter.minDateValor
        binding.buttonUntil.text = filter.maxDateValor

        // Establecer el progreso del slider de importe con el valor del filtro

        binding.sliderAmmount.progress = filter.maxValueSliderValor.toInt()
        binding.checkBoxPaid.isChecked = filter.status["PAGADAS_STRING"] ?: false
        binding.checkBoxCancel.isChecked = filter.status["ANULADAS_STRING"] ?: false
        binding.checkBoxFixedPayment.isChecked = filter.status["CUOTA_FIJA_STRING"] ?: false
        binding.checkBoxPendingPayment.isChecked = filter.status["PENDIENTE_PAGO_STRING"] ?: false
        binding.checkBoxPayPlan.isChecked = filter.status["PLAN_PAGO_STRING"] ?: false
    }


    /**
     * Aplica los filtros guardados a la interfaz de usuario.
     * Se utiliza para cargar y aplicar los filtros previamente guardados al iniciar la aplicación.
     */
    private fun applyTheSavedFilters() {
        // Obtener las preferencias compartidas en modo privado

        val prefs = getPreferences(MODE_PRIVATE)

        // Obtener el JSON de filtro guardado desde las preferencias compartidas

        val filterJson = prefs.getString("FILTER_STATUS", null)

        if (filterJson != null) {
            // Crear una instancia de Gson para convertir el JSON a un objeto Filter

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