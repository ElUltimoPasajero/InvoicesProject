package com.example.invoicesproject.ui.view.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.invoicesproject.R
import com.example.invoicesproject.databinding.InvoiceLayoutBinding
import com.example.invoicesproject.data.database.Invoice

/**
 * ViewHolder personalizado para la representación de elementos de facturas en un RecyclerView.
 *
 * @param view Vista que representa el diseño de un elemento de factura.
 */

class InvoiceViewHolder(view: View): ViewHolder(view) {

    // Binding para el diseño de la factura.

    val binding = InvoiceLayoutBinding.bind(view)

    /**
     * Método para renderizar un elemento de factura en la vista del ViewHolder.
     *
     * @param item Factura a ser representada.
     * @param onClickListener Acción a realizar al hacer clic en la factura.
     */

    fun render(item: Invoice, onClickListener: (Invoice) -> Unit){

        // Establecer los valores en los elementos de la vista.

        binding.textViewStatus.text = item.decEstado
        binding.textViewOrderAmmounr.text = item.importeordenacion.toString()
        binding.textViewDate.text = item.fecha

        // Manejar el clic en la factura.

        itemView.setOnClickListener{
            onClickListener(item)
        }

        // Cambiar el color del texto según el estado de la factura.


        if (binding.textViewStatus.text.equals("Pendiente de pago")) {
            val notPaidInvoice = ContextCompat.getColor(itemView.context, R.color.negativo)
            binding.textViewStatus.setTextColor(notPaidInvoice)
        } else{
            val paidInvoice = ContextCompat.getColor(itemView.context, R.color.positivo)
            binding.textViewStatus.setTextColor(paidInvoice)
        }
    }
}