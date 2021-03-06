package com.stcodesapp.documentscanner.ui.savedFiles

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseFragment
import com.stcodesapp.documentscanner.constants.ConstValues
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.databinding.SavedFilesLayoutBinding
import com.stcodesapp.documentscanner.models.SavedFile
import com.stcodesapp.documentscanner.ui.adapters.SavedFileListAdapter
import com.stcodesapp.documentscanner.ui.helpers.ActivityNavigator
import com.stcodesapp.documentscanner.ui.helpers.DialogHelper
import com.stcodesapp.documentscanner.ui.main.MainActivity
import com.stcodesapp.documentscanner.ui.savedFileOption.SavedFileOptionBottomSheet
import kotlinx.android.synthetic.main.saved_files_layout.*
import java.io.File
import javax.inject.Inject

class SavedFilesFragment : BaseFragment(), SavedFileListAdapter.Listener
{

    @Inject lateinit var viewModel : SavedFilesViewModel
    @Inject lateinit var activityNavigator: ActivityNavigator
    @Inject lateinit var dataBinding : SavedFilesLayoutBinding
    @Inject lateinit var dialogHelper: DialogHelper
    lateinit var savedFileListAdapter : SavedFileListAdapter

    companion object
    {
        private const val TAG = "SavedFilesFragment"
        fun newInstance(): SavedFilesFragment {
            val args = Bundle()
            val fragment = SavedFilesFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        activityComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        observeSavedFiles()
    }

    private fun initUI()
    {
        savedFileListAdapter = SavedFileListAdapter(requireContext(), this)
        savedFileList.layoutManager = LinearLayoutManager(requireContext())
        savedFileList.adapter = savedFileListAdapter
        if(activity is MainActivity) { (activity as MainActivity).highlightBottomNavMenu() }
    }

    private fun observeSavedFiles()
    {
        viewModel.fetchSavedFiles().observe(viewLifecycleOwner, Observer {
            if(it != null && it.isNotEmpty())
            {
                if(it.isEmpty())
                {
                    dataBinding.loadingView.visibility = View.GONE
                    dataBinding.noSavedFileFoundView.visibility = View.VISIBLE
                }
                else
                {
                    dataBinding.loadingView.visibility = View.GONE
                    dataBinding.noSavedFileFoundView.visibility = View.GONE
                    savedFileListAdapter.setSavedFiles(it)
                }

            }
        })
    }

    override fun onItemClick(savedFile: SavedFile)
    {
        val args = Bundle()
        args.putSerializable(Tags.SAVED_FILE_NAME,savedFile)
        val options = SavedFileOptionBottomSheet.newInstance(args)
        options.clickListener = { file: SavedFile?, option: Int -> onSavedFileOptionClicked(savedFile, option)}
        options.show(childFragmentManager,Tags.SAVED_FILE_OPTIONS)
    }

    private fun onSavedFileOptionClicked(file: SavedFile? , option : Int)
    {
        when (option) {
            ConstValues.OPEN_FILE -> file?.let { activityNavigator.toDocumentViewerActivity(it) }
            ConstValues.SHARE_FILE -> file?.let { activityNavigator.shareFile(it) }
            ConstValues.DELETE_FILE -> file?.let { showDeleteConfirmationDialog(it)}
        }

    }

    private fun showDeleteConfirmationDialog(file: SavedFile)
    {
        dialogHelper.showWarningDialog(getString(R.string.file_delete_warning_msg)) {onDeleteFileConfirmed(file)}

    }

    private fun onDeleteFileConfirmed(file: SavedFile)
    {
        dialogHelper.showProgressDialog(getString(R.string.deleting_file))
        viewModel.deleteFile(file).observe(viewLifecycleOwner, Observer {
            if(it)
            {
                dialogHelper.hideProgressDialog()
                savedFileListAdapter.removeFile(file)
                Toast.makeText(requireContext(),getString(R.string.file_deleted),Toast.LENGTH_SHORT).show()
            }
        })

    }


}