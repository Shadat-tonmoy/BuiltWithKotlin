package com.stcodesapp.documentscanner.ui.filterOption

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseFragment

class FilterOptionFragment : BaseFragment() {

    companion object {
        fun newInstance() = FilterOptionFragment()
    }

    private lateinit var viewModel: FilterOptionViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.filter_option_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FilterOptionViewModel::class.java)
        // TODO: Use the ViewModel
    }

}