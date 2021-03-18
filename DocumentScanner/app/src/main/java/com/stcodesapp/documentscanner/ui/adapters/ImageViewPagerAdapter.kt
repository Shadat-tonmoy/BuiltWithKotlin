package com.stcodesapp.documentscanner.ui.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.ui.imageCrop.CropImageSingleItemFragment
import java.lang.Exception

class ImageViewPagerAdapter(fragmentActivity: FragmentActivity, val imageLoadListener: CropImageSingleItemFragment.ImageLoadListener) : FragmentStateAdapter(fragmentActivity)
{

    companion object{
        private const val TAG = "ImageViewPagerAdapter"
    }
    //private var documentPages = listOf<Image>()
    //private var lastDocumentPages = listOf<Image>()
    override fun getItemCount(): Int = mDiffer.currentList.size

    override fun createFragment(position: Int): Fragment
    {
        val currentDocPage = mDiffer.currentList[position]
        val fragment = CropImageSingleItemFragment.newInstance(currentDocPage,position)
        fragment.listener = object  : CropImageSingleItemFragment.Listener{ override fun onItemDeleted(position: Int) { notifyItemRemoved(position) } }
        fragment.imageLoadListener = imageLoadListener
        return fragment
    }

    /*fun setDocumentPages(documentPages : List<Image>)
    {
        this.documentPages = documentPages
        notifyDataSetChanged()
    }*/

    fun getDocumentPageAt(position: Int) : Image?
    {
        return try {
            mDiffer.currentList[position]
        }catch (e : Exception) {
            e.printStackTrace()
            null
        }
    }

    /*override fun getItemId(position: Int): Long {
        return mDiffer.currentList[position].hashCode().toLong()
    }*/

    private val diffCallback : DiffUtil.ItemCallback<Image> = object : DiffUtil.ItemCallback<Image>(){

        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean
        {
            return (oldItem.isCropped == newItem.isCropped && oldItem.rotationAngle == newItem.rotationAngle && oldItem.position == newItem.position && oldItem.path == newItem.path)
        }
    }

    private val mDiffer: AsyncListDiffer<Image> = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Image>)
    {
        Log.e(TAG, "submitList: Called")
        mDiffer.submitList(list)
    }

    init {
        mDiffer.addListListener { previousList, currentList ->

            //notify outside about the list is being updated
        }
    }


}