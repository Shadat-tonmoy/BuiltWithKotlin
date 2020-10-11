package com.stcodesapp.documentscanner.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.database.entities.Document
import com.stcodesapp.documentscanner.databinding.DocumentItemLayoutBinding

class DocumentListAdapter (private val context: Context, private val listener : Listener) : RecyclerView.Adapter<DocumentListAdapter.DocumentViewHolder>()
{
    interface Listener
    {
        fun onItemClick(document: Document)
    }

    private var documents = ArrayList<Document>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val inflater = LayoutInflater.from(context)
        val dataBinding : DocumentItemLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.document_item_layout,parent,false)
        return DocumentViewHolder(dataBinding)
    }

    override fun getItemCount(): Int {
        return documents.size
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val document = documents[position]
        holder.bind(document)
        holder.dataBinding.root.setOnClickListener { listener.onItemClick(document) }
    }

    fun setDocuments(documents : List<Document>)
    {
        this.documents = documents as ArrayList<Document>
        notifyDataSetChanged()
    }

    inner class DocumentViewHolder(val dataBinding : DocumentItemLayoutBinding) : RecyclerView.ViewHolder(dataBinding.root)
    {
        fun bind(document: Document)
        {
            dataBinding.document = document
            dataBinding.executePendingBindings()
        }
    }
}