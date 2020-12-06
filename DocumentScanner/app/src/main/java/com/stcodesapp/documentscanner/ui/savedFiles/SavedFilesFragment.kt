package com.stcodesapp.documentscanner.ui.savedFiles

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.stcodesapp.documentscanner.base.BaseFragment
import com.stcodesapp.documentscanner.databinding.SavedFilesLayoutBinding
import com.stcodesapp.documentscanner.ui.adapters.SavedFileListAdapter
import com.stcodesapp.documentscanner.ui.helpers.ActivityNavigator
import com.stcodesapp.documentscanner.ui.main.MainActivity
import kotlinx.android.synthetic.main.saved_files_layout.*
import java.io.File
import javax.inject.Inject

class SavedFilesFragment : BaseFragment(), SavedFileListAdapter.Listener
{

    @Inject lateinit var viewModel : SavedFilesViewModel
    @Inject lateinit var activityNavigator: ActivityNavigator
    @Inject lateinit var dataBinding : SavedFilesLayoutBinding
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
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
            if(it != null && it.isNotEmpty()) savedFileListAdapter.setSavedFiles(it)
        })
    }

    override fun onItemClick(savedFile: File) {

    }
}