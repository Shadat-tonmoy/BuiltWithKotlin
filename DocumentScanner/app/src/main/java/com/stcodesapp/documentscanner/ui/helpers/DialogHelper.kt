package com.stcodesapp.documentscanner.ui.helpers

import android.content.Context
import android.content.DialogInterface
import android.text.Layout
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.stcodesapp.documentscanner.R

class DialogHelper (val context: Context)
{

    private var alertDialog : AlertDialog? =null

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

}