package com.omanshuaman.testingtournamentsports.adapters

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.omanshuaman.testingtournamentsports.R
import com.omanshuaman.testingtournamentsports.models.ModelUser
import com.squareup.picasso.Picasso


class AdapterParticipantAdd(
    private val context: Context,
    userList: ArrayList<ModelUser?>?,
    groupId: String,
    myGroupRole: String
) :
    RecyclerView.Adapter<AdapterParticipantAdd.HolderParticipantAdd>() {
    private val userList: ArrayList<ModelUser?>?
    private val groupId: String
    private val myGroupRole //creator/admin/participant
            : String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderParticipantAdd {
        //inflate layout
        val view = LayoutInflater.from(context).inflate(R.layout.row_participant_add, parent, false)
        return HolderParticipantAdd(view)
    }

    override fun onBindViewHolder(holder: HolderParticipantAdd, position: Int) {
        //get data
        val modelUser: ModelUser? = userList?.get(position)
        val name: String? = modelUser?.name
        val email: String? = modelUser?.email
        val image: String? = modelUser?.image
        val uid: String? = modelUser?.uid

        //set data
        holder.nameTv.text = name
        holder.emailTv.text = email
        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_default_img).into(holder.avatarIv)
        } catch (e: Exception) {
            holder.avatarIv.setImageResource(R.drawable.ic_default_img)
        }
        checkIfAlreadyExists(modelUser!!, holder)

        holder.reportIv.setImageResource(R.drawable.ic_report_grey)

        //click to block unblock user
        holder.reportIv.setOnClickListener {

            val builder = AlertDialog.Builder(
                context
            )
            builder.setTitle("Report the user")
            val options: Array<String> = arrayOf("Yes", "No")
            builder.setItems(
                options
            ) { dialog, which ->
                //handle item clicks
                if (which == 0) {
                    //Remove Admin clicked
                    Toast.makeText(
                        context,
                        "Reported: Last 5 Messages of this user is saved in database to examine.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    //nothing happen
                }
            }.show()

        }
        //handle click
        holder.itemView.setOnClickListener {
            /*Check if user already added or not
                     * If added: show remove-participant/make-admin/remove-admin option (Admin will not able to change role of creator)
                     * If not added, show add participant option*/
            val ref = FirebaseDatabase.getInstance().getReference("Tournament").child("Groups")
            ref.child(groupId).child("Participants").child(uid!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            //user exists/participant
                            val hisPreviousRole = "" + dataSnapshot.child("role").value

                            //options to display in dialog
                            val options: Array<String>
                            val builder = AlertDialog.Builder(
                                context
                            )
                            builder.setTitle("Choose Option")
                            if (myGroupRole == "creator") {
                                if (hisPreviousRole == "admin") {
                                    //im creator, he is admin
                                    options = arrayOf("Remove Admin", "Remove User", "Info")
                                    builder.setItems(
                                        options
                                    ) { dialog, which ->
                                        //handle item clicks
                                        when (which) {
                                            0 -> {
                                                //Remove Admin clicked
                                                removeAdmin(modelUser)
                                            }
                                            1 -> {
                                                //Remove User clicked
                                                removeParticipant(modelUser)
                                            }
                                            2 -> {
                                                infoUser(modelUser)
                                            }
                                        }
                                    }.show()
                                } else if (hisPreviousRole == "participant") {
                                    //im creator, he is participant
                                    options = arrayOf("Make Admin", "Remove User", "Info")
                                    builder.setItems(
                                        options
                                    ) { dialog, which ->
                                        //handle item clicks
                                        when (which) {
                                            0 -> {
                                                //Make Admin clicked
                                                makeAdmin(modelUser)
                                            }
                                            1 -> {
                                                //Remove User clicked
                                                removeParticipant(modelUser)
                                            }
                                            2 -> {
                                                infoUser(modelUser)
                                            }
                                        }

                                    }.show()
                                }
                            } else if (myGroupRole == "admin") {
                                when (hisPreviousRole) {
                                    "creator" -> {
                                        //im admin, he is creator
                                        Toast.makeText(
                                            context,
                                            "Creator of Group...",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    "admin" -> {
                                        //im admin, he is admin too
                                        options = arrayOf("Remove Admin", "Remove User", "Info")
                                        builder.setItems(
                                            options
                                        ) { dialog, which ->
                                            //handle item clicks
                                            when (which) {
                                                0 -> {
                                                    //Remove Admin clicked
                                                    removeAdmin(modelUser)
                                                }
                                                1 -> {
                                                    //Remove User clicked
                                                    removeParticipant(modelUser)
                                                }
                                                2 -> {
                                                    infoUser(modelUser)
                                                }
                                            }
                                        }.show()
                                    }
                                    "participant" -> {
                                        //im admin, he is participant
                                        options = arrayOf("Make Admin", "Remove User", "Info")
                                        builder.setItems(
                                            options
                                        ) { dialog, which ->
                                            //handle item clicks
                                            when (which) {
                                                0 -> {
                                                    //Make Admin clicked
                                                    makeAdmin(modelUser)
                                                }
                                                1 -> {
                                                    //Remove User clicked
                                                    removeParticipant(modelUser)
                                                }
                                                2 -> {
                                                    infoUser(modelUser)
                                                }
                                            }
                                        }.show()
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Only creator or admin can remove/block user.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        }
    }

    private fun infoUser(modelUser: ModelUser) {
        val reference =
            FirebaseDatabase.getInstance().getReference("Tournament").child("Groups").child(groupId)
                .child("Participants").child(modelUser.uid!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            //already exists
                            val name = "" + dataSnapshot.child("yourName").value
                            val phone = "" + dataSnapshot.child("phoneNumber").value

                            Toast.makeText(context, "$name / $phone", Toast.LENGTH_SHORT).show()

                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
    }

    private fun makeAdmin(modelUser: ModelUser) {
        //setup data - change role
        val hashMap = HashMap<String, Any>()
        hashMap["role"] = "admin" //roles are: participant/admin/creator
        //update role in db
        val reference = FirebaseDatabase.getInstance().getReference("Tournament").child("Groups")
        reference.child(groupId).child("Participants").child(modelUser.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener { //made admin
                Toast.makeText(context, "The user is now admin...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e -> //dailed making admin
                Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun removeParticipant(modelUser: ModelUser) {
        //remove participant from group
        val reference = FirebaseDatabase.getInstance().getReference("Tournament").child("Groups")
        reference.child(groupId).child("Participants").child(modelUser.uid!!).removeValue()
            .addOnSuccessListener {
                //removed successfully
            }
            .addOnFailureListener {
                //failed removing participant
            }
    }

    private fun removeAdmin(modelUser: ModelUser) {
        //setup data - remove admin - just change role
        val hashMap = HashMap<String, Any>()
        hashMap["role"] = "participant" //roles are: participant/admin/creator
        //update role in db
        val reference = FirebaseDatabase.getInstance().getReference("Tournament").child("Groups")
        reference.child(groupId).child("Participants").child(modelUser.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener { //made admin
                Toast.makeText(context, "The user is no longer admin...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e -> //dailed making admin
                Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkIfAlreadyExists(modelUser: ModelUser, holder: HolderParticipantAdd) {
        val ref = FirebaseDatabase.getInstance().getReference("Tournament").child("Groups")
        ref.child(groupId).child("Participants").child(modelUser.uid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //already exists
                        val hisRole = "" + dataSnapshot.child("role").value
                        holder.statusTv.text = hisRole
                    } else {
                        //doesn't exists
                        holder.statusTv.text = ""
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    override fun getItemCount(): Int {
        return userList!!.size
    }

    inner class HolderParticipantAdd(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val avatarIv: ImageView
        val reportIv: ImageView
        val nameTv: TextView
        val emailTv: TextView
        val statusTv: TextView

        init {
            avatarIv = itemView.findViewById(R.id.avatarIv)
            reportIv = itemView.findViewById(R.id.reportIv)
            nameTv = itemView.findViewById(R.id.nameTv)
            emailTv = itemView.findViewById(R.id.emailTv)
            statusTv = itemView.findViewById(R.id.statusTv)
        }
    }

    init {
        this.userList = userList
        this.groupId = groupId
        this.myGroupRole = myGroupRole
    }
}