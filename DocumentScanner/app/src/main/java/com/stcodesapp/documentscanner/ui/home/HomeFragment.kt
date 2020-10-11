package com.stcodesapp.documentscanner.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stcodesapp.documentscanner.base.BaseFragment
import com.stcodesapp.documentscanner.databinding.HomeLayoutBinding
import com.stcodesapp.documentscanner.ui.helpers.ActivityNavigator
import javax.inject.Inject

class HomeFragment : BaseFragment()
{

    @Inject lateinit var viewModel : HomeViewModel
    @Inject lateinit var activityNavigator: ActivityNavigator
    @Inject lateinit var dataBinding : HomeLayoutBinding

    companion object
    {
        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
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
}