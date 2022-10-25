package com.omanshuaman.testingtournamentsports.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.omanshuaman.testingtournamentsports.DetailsActivity
import com.omanshuaman.testingtournamentsports.R
import com.omanshuaman.testingtournamentsports.models.Upload
import com.squareup.picasso.Picasso


class AdapterCard(context: Context, uploads: List<Upload?>?) :
    RecyclerView.Adapter<AdapterCard.MyHolder>() {
    private val mContext: Context = context
    private val mUploads: List<Upload?> = uploads as List<Upload?>

    //view holder classA
    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name: TextView
        var image: ImageView
        var mSport: TextView
        var mEntry: TextView

        init {
            name = itemView.findViewById(R.id.txtPlaceName)
            image = itemView.findViewById(R.id.cardImageview)
            mSport = itemView.findViewById(R.id.pSports)
            mEntry = itemView.findViewById(R.id.pEntryFee)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        //inflate layout row_post.xml
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.place_layout, parent, false)

        view.layoutParams = ViewGroup.LayoutParams(
            (parent.width * 0.90).toInt(),
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        //get data

        val model = mUploads[position]
        val id = model?.Id
        val sports = model?.sports
        val entry = model?.entryFee
        val prize = model?.prizeMoney
        val address = model?.address
        val matchDate = model?.matchDate
        val imageUrlLow = model?.imageUrlLow
        val imageUrlMid = model?.imageUrlMid
        val organizerName = model?.organizerName

        val image = model?.imageUrl
        val name: String? = mUploads[position]?.tournamentName
        Picasso.get().load(imageUrlLow).fit().centerCrop().into(holder.image)

        //set data
        holder.name.text = name
        holder.mSport.text = sports
        holder.mEntry.text = entry
        //handle group click
        holder.itemView.setOnClickListener {
            //open group chat

            val intent = Intent(mContext, DetailsActivity::class.java)
            val extras = Bundle()
            extras.putString("tournamentId", id)
            extras.putString("entryFee", entry)
            extras.putString("prizeFee", prize)
            extras.putString("address", address)
            extras.putString("tournamentName", name)
            extras.putString("backgroundImage", image)
            extras.putString("backgroundImageMid", imageUrlMid)
            extras.putString("organizerName", organizerName)

            extras.putString("matchDate", matchDate)

            intent.putExtras(extras)
            mContext.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return mUploads.size
    }
}