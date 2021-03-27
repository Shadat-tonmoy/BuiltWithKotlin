package com.stcodesapp.documentscanner.ui.filterOption

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseFragment
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.helpers.FilterHelper
import com.stcodesapp.documentscanner.models.Filter
import com.stcodesapp.documentscanner.ui.adapters.FilterListAdapter
import kotlinx.android.synthetic.main.filter_option_fragment.*
import javax.inject.Inject

class FilterOptionFragment : BaseFragment(), FilterListAdapter.Listener {

    companion object
    {
        private const val TAG = "FilterOptionFragment"
        fun newInstance(image : Image, imagePosition : Int) : FilterOptionFragment
        {
            val fragment = FilterOptionFragment()
            val args = Bundle()
            args.putSerializable(Tags.SERIALIZED_IMAGE,image)
            args.putInt(Tags.IMAGE_POSITION,imagePosition)
            fragment.arguments = args
            return fragment
        }
    }

    interface Listener{
        fun onFilterOptionClicked(filter: Filter)
    }

    @Inject lateinit var viewModel: FilterOptionViewModel
    private val filterHelper by lazy { FilterHelper(requireContext()) }
    private var adapter : FilterListAdapter? = null
    var listener : Listener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.filter_option_fragment, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        val imagePath = arguments?.getString(Tags.IMAGE_PATH)
        adapter = FilterListAdapter(requireContext(),this,imagePath)
        val layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        filterOptionList.layoutManager = layoutManager
        filterOptionList.adapter = adapter
        getFilters()

        //(requireActivity() as ImageCropActivity).onFilterMenuLoad()
    }

    override fun onDestroy() {
        super.onDestroy()
        //(requireActivity() as ImageCropActivity).onFilterMenuClosed()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(FilterOptionViewModel::class.java)
    }

    override fun onFilterOptionClick(filter: Filter)
    {
        listener?.onFilterOptionClicked(filter)
    }

    private fun initUI()
    {
        val serializedImage = arguments?.getSerializable(Tags.SERIALIZED_IMAGE) as Image?
        viewModel.chosenImage = serializedImage
        val imagePosition = arguments?.getInt(Tags.IMAGE_POSITION)
        viewModel.chosenImagePosition = imagePosition ?: -1
    }

    private fun getFilters()
    {
        viewModel.getFilters().observe(viewLifecycleOwner, Observer {
            adapter?.setFilters(it)
        })
    }



}