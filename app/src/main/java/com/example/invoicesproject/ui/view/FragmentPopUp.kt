package com.example.invoicesproject.ui.view

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.invoicesproject.databinding.PopupBinding


/**
 * Fragmento que representa un cuadro de diálogo emergente.
 */

class FragmentPopUp: DialogFragment() {

    // Binding para el cuadro de diálogo emergente.


    private var _binding: PopupBinding? = null
    private val binding get() = _binding!!

    /**
     * Crea y configura el cuadro de diálogo.
     *
     * @param savedInstanceState Estado anterior del fragmento (no utilizado en este caso).
     * @return Dialogo creado y configurado.
     */

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = PopupBinding.inflate(LayoutInflater.from(context))

        // Crear el constructor del cuadro de diálogo.

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        // Configurar el botón para cerrar el cuadro de diálogo.

        binding.buttonCerrar.setOnClickListener {
            dismiss()
        }

        // Crear el cuadro de diálogo.

        val dialog = builder.create()
        val window = dialog.window

        // Configurar el fondo transparente.

        window?.setBackgroundDrawableResource(android.R.color.transparent) // Fondo transparente
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        return dialog
    }

    // Configurar el tamaño del cuadro de diálogo.

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}