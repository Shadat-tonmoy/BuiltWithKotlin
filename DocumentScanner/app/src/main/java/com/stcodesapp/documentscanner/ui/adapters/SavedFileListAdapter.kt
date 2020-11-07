package com.stcodesapp.documentscanner.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.database.entities.Document
import com.stcodesapp.documentscanner.databinding.SavedFileItemLayoutBinding
import java.io.File

class SavedFileListAdapter (private val context: Context, private val listener : Listener) : RecyclerView.Adapter<SavedFileListAdapter.SavedFileViewHolder>()
{

    companion object{
        private const val TAG = "SavedFileListAdapter"
    }
    interface Listener
    {
        fun onItemClick(savedFile: File)
    }

    private var savedFiles = ArrayList<File>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedFileViewHolder {
        val inflater = LayoutInflater.from(context)
        val dataBinding : SavedFileItemLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.saved_file_item_layout,parent,false)
        return SavedFileViewHolder(dataBinding)
    }

    override fun getItemCount(): Int {
        return savedFiles.size
    }

    override fun onBindViewHolder(holder: SavedFileViewHolder, position: Int) {
        Log.e(TAG, "onBindViewHolder: Called")
        val savedFile = savedFiles[position]
        holder.bind(savedFile)
        holder.dataBinding.root.setOnClickListener { listener.onItemClick(savedFile) }
    }

    fun setSavedFiles(savedFiles : List<File>)
    {
        this.savedFiles = savedFiles as ArrayList<File>
        notifyDataSetChanged()
    }

    inner class SavedFileViewHolder(val dataBinding : SavedFileItemLayoutBinding) : RecyclerView.ViewHolder(dataBinding.root)
    {
        fun bind(savedFile: File)
        {
            dataBinding.file = savedFile
            dataBinding.executePendingBindings()
        }
    }
}