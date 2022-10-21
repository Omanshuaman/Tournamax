package com.omanshuaman.testingtournamentsports.adapters

import com.omanshuaman.testingtournamentsports.models.DataModel
import androidx.recyclerview.widget.RecyclerView
import com.omanshuaman.testingtournamentsports.adapters.ItemAdapter.ItemViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.omanshuaman.testingtournamentsports.R
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import java.util.ArrayList

class ItemAdapter(private val mList: List<DataModel>) : RecyclerView.Adapter<ItemViewHolder>() {
    private var list: List<String> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.each_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val model = mList[position]
        holder.mTextView.text = model.itemText
        val isExpandable = model.isExpandable
        holder.expandableLayout.visibility = if (isExpandable) View.VISIBLE else View.GONE
        if (isExpandable) {
            holder.mArrowImage.setImageResource(R.drawable.arrow_up)
        } else {
            holder.mArrowImage.setImageResource(R.drawable.arrow_down)
        }
        val adapter = NestedAdapter(list)
        val layoutManager = LinearLayoutManager(
            holder.nestedRecyclerView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        holder.nestedRecyclerView.layoutManager = layoutManager
        holder.nestedRecyclerView.setHasFixedSize(false)
        holder.nestedRecyclerView.adapter = adapter
        holder.linearLayout.setOnClickListener {
            model.isExpandable = !model.isExpandable
            list = model.nestedList
            notifyItemChanged(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linearLayout: CardView
        val expandableLayout: RelativeLayout
        val mTextView: TextView
        val mArrowImage: ImageView
        val nestedRecyclerView: RecyclerView

        init {
            linearLayout = itemView.findViewById(R.id.linear_layout)
            expandableLayout = itemView.findViewById(R.id.expandable_layout1)
            mTextView = itemView.findViewById(R.id.itemTv)
            mArrowImage = itemView.findViewById(R.id.arro_imageview)
            nestedRecyclerView = itemView.findViewById(R.id.child_rv)
        }
    }
}