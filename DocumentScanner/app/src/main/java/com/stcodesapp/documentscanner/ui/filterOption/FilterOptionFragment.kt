package com.stcodesapp.documentscanner.ui.filterOption

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseFragment
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.helpers.FilterHelper
import com.stcodesapp.documentscanner.models.Filter
import com.stcodesapp.documentscanner.ui.adapters.FilterListAdapter
import com.stcodesapp.documentscanner.ui.imageCrop.ImageCropActivity
import com.stcodesapp.documentscanner.ui.imageEdit.ImagePreviewActivity
import kotlinx.android.synthetic.main.filter_option_fragment.*

class FilterOptionFragment : BaseFragment(), FilterListAdapter.Listener {

    companion object {
        fun newInstance(arg : Bundle) = FilterOptionFragment().apply { arguments = arg }
    }

    private lateinit var viewModel: FilterOptionViewModel
    private val filterHelper by lazy { FilterHelper(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.filter_option_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imagePath = arguments?.getString(Tags.IMAGE_PATH)
        val filterListAdapter = FilterListAdapter(requireContext(),this,imagePath)
        val layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        filterOptionList.layoutManager = layoutManager
        filterOptionList.adapter = filterListAdapter
        if(imagePath != null) filterListAdapter.setFilters(getFilters(imagePath))
        //(requireActivity() as ImageCropActivity).onFilterMenuLoad()
    }

    override fun onDestroy() {
        super.onDestroy()
        //(requireActivity() as ImageCropActivity).onFilterMenuClosed()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FilterOptionViewModel::class.java)
    }

    override fun onFilterOptionClick(filter: Filter)
    {
        (requireActivity() as ImagePreviewActivity).onFilterClicked(filter)
    }

    private fun getFilters(imagePath : String) : List<Filter>
    {
        return filterHelper.getFilterList(imagePath)
    }

}