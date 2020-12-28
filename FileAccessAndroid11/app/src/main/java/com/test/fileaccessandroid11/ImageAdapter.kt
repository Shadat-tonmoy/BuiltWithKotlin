package com.test.fileaccessandroid11

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_layout.view.*

class ImageAdapter (val context : Context) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>()
{
    companion object{
        private const val TAG = "ImageAdapter"
    }
    private var images = ArrayList<Image>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder
    {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.item_layout,parent,false)
        return ImageViewHolder(itemView)
    }

    override fun getItemCount(): Int
    {
        return images.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int)
    {
        val image = images[position]
        //holder.imageView.setImage_root_ide_package_.com.test.fileaccessandroid11.Image(image_root_ide_package_.com.test.fileaccessandroid11.Image)

        Glide.with(context)
                .load(image.uri)
                .into(holder.imageView)
        holder.imageTitleView.text = image.title
        holder.imagePathView.text = image.relativePath
    }

    fun setImages(images : List<Image>)
    {
        this.images = images as ArrayList<Image>
        notifyDataSetChanged()
    }



    inner class ImageViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    {
        val imageView = itemView.imageView
        val imageTitleView = itemView.imageTitleView
        val imagePathView = itemView.imagePathView

    }


}