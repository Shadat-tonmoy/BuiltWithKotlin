package com.stcodesapp.documentscanner.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.databinding.ImageToPdfDialogLayoutBinding
import com.stcodesapp.documentscanner.helpers.FileHelper
import com.stcodesapp.documentscanner.models.ImageToPDFProgress
import java.io.File


class ImageToPDFNameDialog(private val context: Context, private val listener : Listener)
{
    private var dialog : AlertDialog? =null
    private lateinit var binding : ImageToPdfDialogLayoutBinding

    interface Listener
    {
        fun onSaveButtonClicked(name : String)

    }

    fun showDialog()
    {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.image_to_pdf_dialog_layout,null,false)
        initTextChangeListener()
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

    private fun initTextChangeListener()
    {
        binding.nameField.addTextChangedListener {
            if(it != null)
            {
                hideErrorMessage()
            }
        }

    }

    fun onSaveButtonClicked()
    {
        val text = binding.nameField.text.toString()
        if(text.isNullOrEmpty())
        {
            showErrorMessage("Please enter a valid file name!")
        }
        else
        {
            hideErrorMessage()
            val fileName = "$text.pdf"
            if(isFileAlreadyExists(fileName))
            {
                showErrorMessage("File Already Exists! Please choose a different name!")
            }
            else
            {
                listener.onSaveButtonClicked(fileName)
                showProgress()
            }
        }

    }

    private fun hideErrorMessage() {
        binding.nameInputLayout.isErrorEnabled = false
        binding.nameInputLayout.error = null
    }

    private fun showErrorMessage(errorMessage : String) {
        binding.nameInputLayout.isErrorEnabled = true
        binding.nameInputLayout.error = errorMessage
    }

    fun onCancelButtonClicked()
    {
        hideDialog()
    }

    private fun isFileAlreadyExists(fileName : String) : Boolean
    {
        val filePath = FileHelper(context).getPDFFullPathToSave(fileName)
        val file = File(filePath)
        return file.exists()
    }

    private fun showProgress()
    {
        binding.nameField.clearFocus()
        binding.saveAsPDFFieldLayout.visibility = View.GONE
        binding.saveAsPDFProgressLayout.visibility = View.VISIBLE
    }

    fun updateProgress(progress: ImageToPDFProgress)
    {
        val percent = (progress.totalDone / progress.totalToDone.toFloat()) * 100
        binding.circularProgressBar.progress = percent
        binding.progressMessage.text = "${progress.totalDone}/${progress.totalToDone}"
    }
}