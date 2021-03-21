package com.stcodesapp.documentscanner.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.constants.RequestCode
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.databinding.ActivityMainBinding
import com.stcodesapp.documentscanner.helpers.PermissionHelper
import com.stcodesapp.documentscanner.tasks.imageToPDF.ImageToPDFServiceHelper
import com.stcodesapp.documentscanner.ui.adapters.DocumentListAdapter
import com.stcodesapp.documentscanner.ui.helpers.ActivityNavigator
import com.stcodesapp.documentscanner.ui.helpers.FragmentFrameWrapper
import com.stcodesapp.documentscanner.ui.helpers.FragmentNavigator
import com.stcodesapp.documentscanner.ui.home.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : BaseActivity(), FragmentFrameWrapper
{
    @Inject lateinit var activityNavigator: ActivityNavigator
    @Inject lateinit var fragmentNavigator: FragmentNavigator
    @Inject lateinit var dataBinding : ActivityMainBinding
    @Inject lateinit var viewModel: MainViewModel
    @Inject lateinit var permissionHelper: PermissionHelper
    @Inject lateinit var serviceHelper: ImageToPDFServiceHelper
    lateinit var adapter : DocumentListAdapter


    companion object{
        private const val TAG = "MainActivity"
        init {
            System.loadLibrary("native-lib")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init()
    {
        activityComponent.inject(this)
        initUI()
        serviceHelper.initService(serviceConnectionListener)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val currentFragment = fragmentNavigator.getCurrentFragment()
        if(currentFragment is HomeFragment)
        { currentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == RequestCode.OPEN_DOCUMENT_PAGES_SCREEN)
            {
                if(data != null  && data.hasExtra(Tags.SHOW_OUTPUT))
                {
                    val showOutput = data.getBooleanExtra(Tags.SHOW_OUTPUT,false)
                    if(showOutput)
                    {
                        fragmentNavigator.loadSavedFilesFragment()
                    }
                }
            }
        }

    }

    private fun initUI()
    {
        setContentView(dataBinding.root)
        fragmentNavigator.loadHomeFragment()
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_menu -> {
                    fragmentNavigator.loadHomeFragment()
                    true
                }

                R.id.saved_file_menu -> {
                    fragmentNavigator.loadSavedFilesFragment()
                    true
                }

                R.id.more_menu -> {
                    fragmentNavigator.loadMoreFragment()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun onBackPressed()
    {
        super.onBackPressed()
        highlightBottomNavMenu()

    }

    fun highlightBottomNavMenu()
    {
        when
        {
            fragmentNavigator.isHomeFragmentLoaded() -> bottomNavigationView.menu.findItem(R.id.home_menu).isChecked = true
            fragmentNavigator.isSavedFilesFragmentLoaded() -> bottomNavigationView.menu.findItem(R.id.saved_file_menu).isChecked = true
            fragmentNavigator.isMoreFragmentLoaded() -> bottomNavigationView.menu.findItem(R.id.more_menu).isChecked = true
        }

    }
    override fun getFragmentFrame(): FrameLayout? {
        return fragmentContainer
    }

    private val serviceConnectionListener = object : ImageToPDFServiceHelper.Listener{
        override fun onServiceConnected() {
            if(serviceHelper.imageToPDFService?.isConversionRunning == true)
            {
                openRunningScanPage(serviceHelper.imageToPDFService?.documentId)

            }

        }
    }

    private fun openRunningScanPage(documentId : Long?)
    {
        if(documentId != null) activityNavigator.toDocumentPagesScreen(documentId)
    }


}