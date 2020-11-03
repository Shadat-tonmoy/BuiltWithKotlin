package com.stcodesapp.documentscanner.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.databinding.ImageToPdfNameDialogLayoutBinding


class ImageToPDFNameDialog(private val context: Context)
{
    private var dialog : AlertDialog? =null
    private lateinit var binding : ImageToPdfNameDialogLayoutBinding

    fun showDialog()
    {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.image_to_pdf_name_dialog_layout,null,false)
        dialog = MaterialAlertDialogBuilder(context)
            .setView(binding.root)
            .setCancelable(false)
            .create()
        dialog?.show()

    }

    fun hideDialog()
    {
        if(dialog!=null && dialog?.isShowing!!) dialog?.dismiss()

    }
}