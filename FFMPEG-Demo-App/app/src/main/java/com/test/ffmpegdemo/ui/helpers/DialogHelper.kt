package com.test.ffmpegdemo.ui.helpers

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.ffmpegdemo.R

class DialogHelper (val context: Context)
{


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

}