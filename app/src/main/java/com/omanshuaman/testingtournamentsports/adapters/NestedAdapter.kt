package com.omanshuaman.testingtournamentsports.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.omanshuaman.testingtournamentsports.R
import com.omanshuaman.testingtournamentsports.adapters.NestedAdapter.NestedViewHolder


class NestedAdapter(private val mList: List<String>) : RecyclerView.Adapter<NestedViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.nested_item, parent, false)
        return NestedViewHolder(view)
    }

    override fun onBindViewHolder(holder: NestedViewHolder, position: Int) {
        holder.mTv.text = mList[position]

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class NestedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTv: TextView

        init {
            mTv = itemView.findViewById(R.id.nestedItemTv)
        }
    }
}