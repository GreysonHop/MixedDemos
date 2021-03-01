package com.testdemo.testRecyclerViewType.itemtouchhelperdemo.contactList

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.testdemo.R
import com.testdemo.testRecyclerViewType.itemtouchhelperdemo.contactList.ContactsAdapter.ContactViewHolder
import com.testdemo.testRecyclerViewType.itemtouchhelperdemo.helper.OnDragVHListener
import com.testdemo.testRecyclerViewType.itemtouchhelperdemo.helper.OnItemMoveListener

/**
 * 仅拖拽排序
 * Created by YoKeyword on 16/1/4.
 */
class ContactsAdapter(val context: Context?, items: MutableList<String>) : RecyclerView.Adapter<ContactViewHolder>(), OnItemMoveListener {
    private val mItems: MutableList<String> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(LayoutInflater.from(context).inflate(R.layout.item_contact_list, parent, false))
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.tvContactName.text = mItems[position]
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val item = mItems[fromPosition]
        mItems.removeAt(fromPosition)
        mItems.add(toPosition, item)
        notifyItemMoved(fromPosition, toPosition)
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnDragVHListener {
        val tvContactName: TextView = itemView.findViewById(R.id.tv_contact_name)

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemFinish() {
            itemView.setBackgroundColor(0)
        }
    }

}