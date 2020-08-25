package com.stcodesapp.documentscanner.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.databinding.DocumentPageItemLayoutBinding
import com.stcodesapp.documentscanner.models.DocumentPage

class DocumentPageAdapter (val context : Context, val onItemClickListener : (DocumentPage) -> Unit) : RecyclerView.Adapter<DocumentPageAdapter.DocumentPageViewHolder>()
{
    private var documentPages = ArrayList<DocumentPage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentPageViewHolder {
        val inflater = LayoutInflater.from(context)
        val dataBinding : DocumentPageItemLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.document_page_item_layout,parent,false)
        return DocumentPageViewHolder(dataBinding)
    }

    override fun getItemCount(): Int
    {
        return documentPages.size
    }

    override fun onBindViewHolder(holder: DocumentPageViewHolder, position: Int)
    {
        val documentPage = documentPages[position]
        holder.bind(documentPage)
        holder.dataBinding.root.setOnClickListener { onItemClickListener(documentPage) }
    }

    fun setDocumentPages(documentPages : List<DocumentPage>)
    {
        this.documentPages = documentPages as ArrayList<DocumentPage>
        notifyDataSetChanged()
    }



    inner class DocumentPageViewHolder(val dataBinding : DocumentPageItemLayoutBinding) : RecyclerView.ViewHolder(dataBinding.root)
    {
        fun bind(documentPage: DocumentPage)
        {
            dataBinding.documentPage = documentPage
            dataBinding.executePendingBindings()
        }
    }


}