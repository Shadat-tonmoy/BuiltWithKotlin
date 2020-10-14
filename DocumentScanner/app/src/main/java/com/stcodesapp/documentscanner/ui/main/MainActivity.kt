package com.stcodesapp.documentscanner.ui.main

import android.os.Bundle
import android.widget.FrameLayout
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.databinding.ActivityMainBinding
import com.stcodesapp.documentscanner.helpers.PermissionHelper
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
    lateinit var adapter : DocumentListAdapter


    companion object{
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        initUI()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val currentFragment = fragmentNavigator.getCurrentFragment()
        if(currentFragment is HomeFragment)
        {
            currentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults)

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

    private fun highlightBottomNavMenu()
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


}