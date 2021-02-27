package com.stcodesapp.documentscanner.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.ui.imageCrop.CropImageSingleItemFragment

class ImageViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity)
{
    private var documentPages = listOf<Image>()
    override fun getItemCount(): Int = documentPages.size

    override fun createFragment(position: Int): Fragment
    {
        val currentDocPage = documentPages[position]
        val fragment = CropImageSingleItemFragment.newInstance(currentDocPage,position)
        fragment.listener = object  : CropImageSingleItemFragment.Listener{ override fun onItemDeleted(position: Int) { notifyItemRemoved(position) } }
        return fragment
    }

    fun setDocumentPages(documentPages : List<Image>)
    {
        this.documentPages = documentPages
        notifyDataSetChanged()
    }
}