package com.stcodesapp.documentscanner.di.activity.modules

import android.app.Activity
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.databinding.ActivityMainBinding
import com.stcodesapp.documentscanner.databinding.DocumentPagesLayoutBinding
import com.stcodesapp.documentscanner.databinding.HomeLayoutBinding
import com.stcodesapp.documentscanner.databinding.ImagePreviewLayoutBinding
import com.stcodesapp.documentscanner.helpers.PermissionHelper
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
    fun providePermissionHelper(activity: Activity) : PermissionHelper
    {
        return PermissionHelper(activity as AppCompatActivity)
    }

    @Provides
    fun provideMainActivityBinding(inflater: LayoutInflater, activityNavigator: ActivityNavigator) : ActivityMainBinding
    {
        val dataBinding : ActivityMainBinding = DataBindingUtil.inflate(inflater, R.layout.activity_main,null,false)
        dataBinding.activityNavigator = activityNavigator
        return dataBinding
    }

    @Provides
    fun provideDocumentPagesBinding(inflater: LayoutInflater, activityNavigator: ActivityNavigator) : DocumentPagesLayoutBinding
    {
        val dataBinding : DocumentPagesLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.document_pages_layout,null,false)
        dataBinding.activityNavigator = activityNavigator
        return dataBinding
    }

    @Provides
    fun provideImagePreviewBinding(inflater: LayoutInflater, activityNavigator: ActivityNavigator) : ImagePreviewLayoutBinding
    {
        val dataBinding : ImagePreviewLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.image_preview_layout,null,false)
        dataBinding.activityNavigator = activityNavigator
        return dataBinding
    }

    @Provides
    fun provideHomeLayoutBinding(inflater: LayoutInflater, activityNavigator: ActivityNavigator) : HomeLayoutBinding
    {
        val dataBinding : HomeLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.home_layout,null,false)
        dataBinding.activityNavigator = activityNavigator
        return dataBinding
    }


}