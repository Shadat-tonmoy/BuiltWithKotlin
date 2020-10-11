package com.stcodesapp.documentscanner.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.stcodesapp.documentscanner.backup.DBBackupHelper
import com.stcodesapp.documentscanner.base.BaseFragment
import com.stcodesapp.documentscanner.databinding.HomeLayoutBinding
import com.stcodesapp.documentscanner.helpers.PermissionHelper
import com.stcodesapp.documentscanner.ui.helpers.ActivityNavigator
import kotlinx.android.synthetic.main.home_layout.*
import javax.inject.Inject

class HomeFragment : BaseFragment()
{

    @Inject lateinit var viewModel : HomeViewModel
    @Inject lateinit var activityNavigator: ActivityNavigator
    @Inject lateinit var dataBinding : HomeLayoutBinding
    @Inject lateinit var permissionHelper: PermissionHelper

    companion object
    {
        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
        private const val TAG = "HomeFragment"
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

        backupButton.setOnClickListener {
            if(permissionHelper.isWriteStoragePermissionGranted())
            {
                val backupHelper = DBBackupHelper(requireContext(),viewModel.appDatabase)
                backupHelper.backupMedia()
            }

        }

        restoreButton.setOnClickListener {
            if(permissionHelper.isWriteStoragePermissionGranted())
            {
                val backupHelper = DBBackupHelper(requireContext(),viewModel.appDatabase)
                backupHelper.restoreMedia()
            }
        }

        fetchDocumentList()
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

    private fun fetchDocumentList()
    {
        viewModel.fetchDocumentListLiveData().observe(viewLifecycleOwner, Observer {
            if(it != null && it.isNotEmpty())
            {
                Log.e(TAG, "fetchDocumentList: $it")
//                adapter.setDocuments(it)
            }
        })
    }
}