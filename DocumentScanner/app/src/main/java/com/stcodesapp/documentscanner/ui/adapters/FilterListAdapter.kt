package com.stcodesapp.documentscanner.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.databinding.FilterItemLayoutBinding
import com.stcodesapp.documentscanner.models.Filter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class FilterListAdapter (private val context: Context, private val listener : Listener, private val sourceImagePath : String?) : RecyclerView.Adapter<FilterListAdapter.FilterViewHolder>()
{

    companion object{
        private const val TAG = "DocumentListAdapter"
    }
    interface Listener
    {
        fun onFilterOptionClick(filter: Filter)
    }

    private var filters = ArrayList<Filter>()
    private val ioCoroutine = CoroutineScope(Dispatchers.IO)
    private val uiCoroutine = CoroutineScope(Dispatchers.Main)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val inflater = LayoutInflater.from(context)
        val dataBinding : FilterItemLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.filter_item_layout,parent,false)
        return FilterViewHolder(dataBinding)
    }

    override fun getItemCount(): Int {
        return filters.size
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val document = filters[position]
        holder.bind(document)
        holder.dataBinding.root.setOnClickListener { listener.onFilterOptionClick(document) }
    }

    fun setFilters(filters : List<Filter>)
    {
        this.filters = filters as ArrayList<Filter>
        notifyDataSetChanged()
    }

    inner class FilterViewHolder(val dataBinding : FilterItemLayoutBinding) : RecyclerView.ViewHolder(dataBinding.root)
    {
        fun bind(filter: Filter)
        {
            Log.e(TAG, "bind: filter : $filter")
            dataBinding.filter = filter
            dataBinding.executePendingBindings()
        }
    }
}