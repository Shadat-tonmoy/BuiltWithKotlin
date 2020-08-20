package com.stcodesapp.documentscanner.base

import androidx.fragment.app.Fragment
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.di.activity.ActivityComponent
import com.stcodesapp.documentscanner.di.activity.modules.ActivityModule
import com.tigerit.pothghat.di.application.ApplicationComponent

open class BaseFragment : Fragment()
{
    val appComponent:ApplicationComponent by lazy {
        (requireActivity().application as DocumentScannerApp).appComponent
    }

    val activityComponent: ActivityComponent by lazy {
        appComponent.getActivityComponent(ActivityModule(requireActivity()))
    }

}