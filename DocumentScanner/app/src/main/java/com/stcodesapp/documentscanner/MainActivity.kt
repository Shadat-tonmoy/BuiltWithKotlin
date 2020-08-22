package com.stcodesapp.documentscanner

import android.os.Bundle
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.databinding.ActivityMainBinding
import com.stcodesapp.documentscanner.ui.helpers.ActivityNavigator
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity()
{
    @Inject lateinit var activityNavigator: ActivityNavigator
    @Inject lateinit var dataBinding : ActivityMainBinding

    companion object{
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        initUI()
    }

    private fun initUI()
    {
        setContentView(dataBinding.root)
    }



}