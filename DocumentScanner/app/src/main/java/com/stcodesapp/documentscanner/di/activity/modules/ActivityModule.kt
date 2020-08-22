package com.stcodesapp.documentscanner.di.activity.modules

import android.app.Activity
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.databinding.ActivityMainBinding
import com.stcodesapp.documentscanner.ui.helpers.ActivityNavigator
import com.stcodesapp.documentscanner.ui.helpers.DialogHelper
import com.stcodesapp.documentscanner.ui.helpers.FragmentFrameWrapper
import com.stcodesapp.documentscanner.ui.helpers.FragmentNavigator
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private val activity: Activity)
{

    @Provides
    fun provideActivity() : Activity
    {
        return activity
    }

    @Provides
    fun provideFragmentActivity() : FragmentActivity
    {
        return activity as FragmentActivity
    }

    @Provides
    fun provideFragmentManager(fragmentActivity: FragmentActivity) : FragmentManager
    {
        return fragmentActivity.supportFragmentManager
    }

    @Provides
    fun provideFragmentFrameWrapper(fragmentActivity: FragmentActivity) : FragmentFrameWrapper
    {
        return fragmentActivity as FragmentFrameWrapper
    }

    @Provides
    fun provideFragmentNavigator(fragmentFrameWrapper: FragmentFrameWrapper, fragmentManager: FragmentManager) : FragmentNavigator
    {
        return FragmentNavigator(fragmentFrameWrapper,fragmentManager)
    }

    @Provides
    fun provideActivityNavigator(activity: Activity) : ActivityNavigator
    {
        return ActivityNavigator(activity)
    }

    @Provides
    fun provideLayoutInflater(activity: Activity) : LayoutInflater
    {
        return activity.layoutInflater
    }

    @Provides
    fun provideDialogHelper(activity: Activity) : DialogHelper
    {
        return DialogHelper(activity)
    }

    @Provides
    fun provideMainActivityBinding(inflater: LayoutInflater, activityNavigator: ActivityNavigator) : ActivityMainBinding
    {
        val dataBinding : ActivityMainBinding = DataBindingUtil.inflate(inflater, R.layout.activity_main,null,false)
        dataBinding.activityNavigator = activityNavigator
        return dataBinding
    }


}