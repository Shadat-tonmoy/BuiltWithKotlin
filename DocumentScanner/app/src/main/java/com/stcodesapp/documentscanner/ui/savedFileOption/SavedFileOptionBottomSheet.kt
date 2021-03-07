package com.stcodesapp.documentscanner.ui.savedFileOption

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.constants.ConstValues
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.models.SavedFile
import kotlinx.android.synthetic.main.saved_file_option_layout.*
import kotlinx.android.synthetic.main.saved_file_option_layout.view.*
import java.io.File

class SavedFileOptionBottomSheet : BottomSheetDialogFragment()
{
    companion object{
        fun newInstance(args : Bundle): SavedFileOptionBottomSheet
        {
            val fragment = SavedFileOptionBottomSheet()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var clickListener : (SavedFile?, Int) -> Unit
    var chosenFile : SavedFile? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.saved_file_option_layout,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUI()
        initClickListeners(view)
    }

    private fun initClickListeners(view: View)
    {
        view.openFile.setOnClickListener {
            clickListener(chosenFile,ConstValues.OPEN_FILE)
            dismiss()
        }
        view.shareFile.setOnClickListener {
            clickListener(chosenFile,ConstValues.SHARE_FILE)
            dismiss()
        }
        view.deleteFile.setOnClickListener {
            clickListener(chosenFile,ConstValues.DELETE_FILE)
            dismiss()
        }
    }

    private fun initUI()
    {
        if(arguments != null)
        {
            chosenFile = requireArguments().getSerializable(Tags.SAVED_FILE_NAME) as SavedFile?
            if(chosenFile != null) savedFileName.text = chosenFile?.displayName
        }
    }

    override fun show(manager: FragmentManager, tag: String?)
    {
        val fragmentTransaction = manager.beginTransaction()
        fragmentTransaction.add(this, tag)
        fragmentTransaction.commitAllowingStateLoss()
    }
}