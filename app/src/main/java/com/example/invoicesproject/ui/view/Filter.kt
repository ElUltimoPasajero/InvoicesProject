package com.example.invoicesproject.ui.view


/**
 * Clase que representa un filtro para la aplicación de facturas.
 *
 * @property maxDate Fecha máxima del filtro.
 * @property minDate Fecha mínima del filtro.
 * @property maxValueSlider Valor máximo del control deslizante en el filtro.
 * @property estate Estado del filtro representado como un mapa de cadenas a booleanos.
 */
class Filter(var maxDateValor: String, var minDateValor: String, var maxValueSliderValor: Double, var status: HashMap<String, Boolean>) {
}

