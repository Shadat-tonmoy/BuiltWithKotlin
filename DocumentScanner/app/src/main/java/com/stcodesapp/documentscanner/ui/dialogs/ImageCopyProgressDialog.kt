package com.stcodesapp.documentscanner.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.databinding.ProgressDialogLayoutBinding


class ImageCopyProgressDialog(private val context: Context)
{
    private var dialog : AlertDialog? =null
    private lateinit var binding : ProgressDialogLayoutBinding
    private var message : String = context.getString(R.string.processing)

    fun showDialog()
    {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.progress_dialog_layout,null,false)
        binding.progressMessage = message
        dialog = MaterialAlertDialogBuilder(context)
            .setView(binding.root)
            .create()
        dialog?.show()

    }

    fun updateMessage(message : String)
    {
        binding.progressMessage = message
    }

    fun hideDialog()
    {
        if(dialog!=null && dialog?.isShowing!!) dialog?.dismiss()

    }
}