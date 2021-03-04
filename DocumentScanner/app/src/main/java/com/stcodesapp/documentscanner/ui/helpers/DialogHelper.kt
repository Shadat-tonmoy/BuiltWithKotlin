package com.stcodesapp.documentscanner.ui.helpers

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.text.Layout
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.stcodesapp.documentscanner.R

class DialogHelper (val context: Context)
{

    interface AlertDialogListener
    {
        fun onPositiveButtonClicked()
        fun onNegativeButtonClicked()
    }

    private var alertDialog : AlertDialog? =null
    private var progressDialog : ProgressDialog? =null

    init {
        progressDialog = ProgressDialog(context)
    }

    fun showWarningDialog(message : String, positiveCallback : () -> Unit)
    {
        alertDialog = MaterialAlertDialogBuilder(context)
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.yes)
            ) { p0, p1 -> positiveCallback()}
            .setNegativeButton(context.getString(R.string.no)
            ) { p0, p1 -> hideDialog()}
            .create()
        alertDialog?.show()
    }

    private fun hideDialog()
    {
        if(alertDialog!=null && alertDialog?.isShowing!!) alertDialog?.dismiss()
    }

    fun showProgressDialog(message:String)
    {
        if(progressDialog!=null)
        {
            if(progressDialog!!.isShowing) return
        }
        progressDialog?.setMessage(message)
        progressDialog?.show()
    }

    fun hideProgressDialog()
    {
        if(progressDialog!=null && progressDialog!!.isShowing)
        {
            progressDialog?.dismiss()
        }
    }

    fun showAlertDialog(message:String, listener: AlertDialogListener?,title : String = "", positiveButtonText : String = context.getString(R.string.yes), negativeButtonText : String = context.getString(R.string.no))
    {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton(positiveButtonText
            ) { dialog, which ->
                listener?.onPositiveButtonClicked()
                hideAlertDialog()
            }
            .setNegativeButton(negativeButtonText
            ) { dialog, which ->
                listener?.onNegativeButtonClicked()
                hideAlertDialog()
            }
        if(title.isNotEmpty()) builder.setTitle(title)
        alertDialog = builder.create()
        alertDialog?.show()
    }

    private fun hideAlertDialog()
    {
        if(alertDialog!=null && alertDialog!!.isShowing)
        {
            alertDialog?.dismiss()
        }
    }

}