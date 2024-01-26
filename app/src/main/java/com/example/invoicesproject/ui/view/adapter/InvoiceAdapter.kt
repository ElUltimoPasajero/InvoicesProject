package com.example.invoicesproject.ui.view.adapter

import android.content.DialogInterface.OnClickListener
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.invoicesproject.R
import com.example.invoicesproject.data.database.Invoice

/**
 * Adaptador para la lista de facturas en un [RecyclerView].
 *
 * @property onClickListener Función de devolución de llamada que se invoca cuando se hace clic en una factura.
 * Esta función toma un parámetro de tipo [Invoice], que representa la factura seleccionada.
 */

class InvoiceAdapter(private val onClickListener: (Invoice) -> Unit): RecyclerView.Adapter<InvoiceViewHolder>() {

    // Lista de facturas que se mostrará en el RecyclerView

    private var listInvoices: List<Invoice>? = null

    /**
     * Establece la lista de facturas que se mostrará en el RecyclerView.
     *
     * @param listInvoices Lista de facturas.
     */

    fun setListInvoices(listInvoices: List<Invoice>?) {
        this.listInvoices = listInvoices
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        // Inflar el diseño de la vista de la factura

        val layoutInflater = LayoutInflater.from(parent.context)
        return InvoiceViewHolder(layoutInflater.inflate(R.layout.invoice_layout, parent, false))
    }

    override fun getItemCount(): Int {
        if (listInvoices == null) return 0
        // Devuelve la cantidad de elementos en la lista de facturas

        return listInvoices?.size!!
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        // Vincula los datos de la factura en la posición especificada con el ViewHolder

        holder.render(listInvoices?.get(position)!!, onClickListener)
    }
}