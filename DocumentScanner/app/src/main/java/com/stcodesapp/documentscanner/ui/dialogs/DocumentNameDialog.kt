package com.stcodesapp.documentscanner.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.database.entities.Document
import com.stcodesapp.documentscanner.helpers.FileHelper
import kotlinx.android.synthetic.main.document_name_dialog.view.*
import java.io.File


class DocumentNameDialog(private val context: Context, private val listener : Listener)
{
    companion object{
        private const val TAG = "DocumentNameDialog"
    }
    private var dialog : AlertDialog? =null
    private lateinit var dialogView : View

    interface Listener
    {
        fun onSaveButtonClicked(name : String)

    }

    fun showDialog(existingDoc : Document?)
    {
        dialogView = LayoutInflater.from(context).inflate(R.layout.document_name_dialog,null,false)
        initDialogUI()
        dialog = MaterialAlertDialogBuilder(context)
            .setView(dialogView)
            .create()
        if(existingDoc != null) dialogView.documentNameField.setText(existingDoc.title)
        if(dialog != null && !dialog!!.isShowing) dialog?.show()

    }

    fun hideDialog()
    {
        if(dialog!=null && dialog?.isShowing!!) dialog?.dismiss()
    }

    private fun initDialogUI()
    {

        dialogView.documentNameField.addTextChangedListener {
            if(it != null)
            {
                hideErrorMessage()
            }
        }

        dialogView.documentNameSaveButton.setOnClickListener { onSaveButtonClicked() }
        dialogView.documentNameCancelButton.setOnClickListener { onCancelButtonClicked() }

    }

    fun onSaveButtonClicked()
    {
        val text = dialogView.documentNameField.text.toString()
        if(text.isNullOrEmpty())
        {
            showErrorMessage("Please enter a valid file name!")
        }
        else
        {
            hideErrorMessage()
            listener.onSaveButtonClicked(text)
            //showProgress()
        }

    }

    private fun hideErrorMessage() {
        dialogView.nameInputLayout.isErrorEnabled = false
        dialogView.nameInputLayout.error = null
    }

    private fun showErrorMessage(errorMessage : String) {
        dialogView.nameInputLayout.isErrorEnabled = true
        dialogView.nameInputLayout.error = errorMessage
    }

    fun onCancelButtonClicked()
    {
        hideDialog()
    }

    private fun showProgress()
    {
        hideKeyboard()
        /*binding.saveAsPDFFieldLayout.visibility = View.GONE
        binding.saveAsPDFDoneLayout.visibility = View.GONE
        binding.saveAsPDFProgressLayout.visibility = View.VISIBLE*/
        dialog?.setCancelable(false)
    }

    private fun showDone()
    {
        /*binding.saveAsPDFFieldLayout.visibility = View.GONE
        binding.saveAsPDFProgressLayout.visibility = View.GONE
        binding.saveAsPDFDoneLayout.visibility = View.VISIBLE*/
    }

    private fun hideKeyboard()
    {
        val view: View? = dialogView
        if (view != null)
        {
            val manager: InputMethodManager? = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            manager?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}