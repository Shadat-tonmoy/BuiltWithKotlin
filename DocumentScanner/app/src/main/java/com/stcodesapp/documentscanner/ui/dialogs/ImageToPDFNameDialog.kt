package com.stcodesapp.documentscanner.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.databinding.ImageToPdfNameDialogLayoutBinding


class ImageToPDFNameDialog(private val context: Context, private val listener : Listener)
{
    private var dialog : AlertDialog? =null
    private lateinit var binding : ImageToPdfNameDialogLayoutBinding

    interface Listener
    {
        fun onSaveButtonClicked(name : String)

    }

    fun showDialog()
    {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.image_to_pdf_name_dialog_layout,null,false)
        dialog = MaterialAlertDialogBuilder(context)
            .setView(binding.root)
            .create()
        binding.dialog = this
        dialog?.show()

    }

    private fun hideDialog()
    {
        if(dialog!=null && dialog?.isShowing!!) dialog?.dismiss()
    }

    fun onSaveButtonClicked()
    {
        val text = binding.nameField.text.toString()
        listener.onSaveButtonClicked(text)
        hideDialog()
    }

    fun onCancelButtonClicked()
    {
        hideDialog()
    }
}