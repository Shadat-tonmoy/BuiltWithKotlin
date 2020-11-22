package com.stcodesapp.documentscanner.ui.filterOption

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseFragment
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.constants.enums.FilterType
import com.stcodesapp.documentscanner.models.Filter
import com.stcodesapp.documentscanner.ui.adapters.FilterListAdapter
import com.stcodesapp.documentscanner.ui.imageEdit.ImagePreviewActivity
import com.zomato.photofilters.FilterPack
import kotlinx.android.synthetic.main.filter_option_fragment.*

class FilterOptionFragment : BaseFragment(), FilterListAdapter.Listener {

    companion object {
        fun newInstance(arg : Bundle) = FilterOptionFragment().apply { arguments = arg }
    }

    private lateinit var viewModel: FilterOptionViewModel

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
        (requireActivity() as ImagePreviewActivity).onFilterMenuLoad()
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as ImagePreviewActivity).onFilterMenuClosed()
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
        val filters = ArrayList<Filter>()
        for(filter in FilterPack.getFilterPack(requireContext()))
        {
            filters.add(Filter(filter.name,imagePath,filter))
        }
        return filters.reversed()
    }

}