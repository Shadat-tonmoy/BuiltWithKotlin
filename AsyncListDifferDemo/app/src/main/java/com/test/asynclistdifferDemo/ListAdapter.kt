package com.test.asynclistdifferDemo


import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


class ListAdapter(private val context : Context) : RecyclerView.Adapter<ListAdapter.PersonViewHolder>()
{
    private val diffCallback: DiffUtil.ItemCallback<Person> = object : DiffUtil.ItemCallback<Person>() {
        override fun areItemsTheSame(oldItem: Person, newItem: Person): Boolean {
            return TextUtils.equals(oldItem.id, newItem.id)
        }

        override fun areContentsTheSame(oldItem: Person, newItem: Person): Boolean {
            return TextUtils.equals(oldItem.id, newItem.id)
        }
    }
    private val mDiffer : AsyncListDiffer<Person> = AsyncListDiffer<Person>(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder
    {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.person_layout, parent, false)
        return PersonViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }

    private fun getItem(position: Int): Person{
        return mDiffer.currentList[position]
    }

    fun submitList(data: List<Person>)
    {
        Log.e("TAG", "submitList: Called")
        mDiffer.submitList(data)
        Log.e("TAG", "submitList: Done")
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        Log.e("TAG", "onBindViewHolder: Called")
        holder.nameView.text = getItem(position).firstName
    }

    inner class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val nameView : TextView = itemView.findViewById(R.id.personName)
    }
}