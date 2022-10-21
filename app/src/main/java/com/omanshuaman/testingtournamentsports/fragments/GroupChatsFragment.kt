package com.omanshuaman.testingtournamentsports.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.omanshuaman.testingtournamentsports.models.ModelGroupChatList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.omanshuaman.testingtournamentsports.adapters.AdapterGroupChatList
import java.util.*
import com.omanshuaman.testingtournamentsports.R

import java.util.*
/**
 * A simple [Fragment] subclass.
 */
class GroupChatsFragment : Fragment() {
    private var groupsRv: RecyclerView? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var groupChatLists: ArrayList<ModelGroupChatList?>? = null
    private var adapterGroupChatList: AdapterGroupChatList? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_group_chats, container, false)
        groupsRv = view.findViewById(R.id.groupsRv)
        firebaseAuth = FirebaseAuth.getInstance()
        loadGroupChatsList()
        return view
    }

    private fun loadGroupChatsList() {
        groupChatLists = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference("Tournament").child("Groups")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                groupChatLists!!.clear()
                for (ds in dataSnapshot.children) {
                    //if current user's uid exists in participants lis of group then show that group
                    if (ds.child("Participants").child(firebaseAuth!!.uid!!).exists()) {
                        val model = ds.getValue(
                            ModelGroupChatList::class.java
                        )
                        groupChatLists!!.add(model)
                    }
                }
                adapterGroupChatList = activity?.let { AdapterGroupChatList(it, groupChatLists) }
                groupsRv!!.adapter = adapterGroupChatList
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    /*inflate options menu*/
    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //inflating menu
        inflater.inflate(R.menu.menu, menu)

        menu.findItem(R.id.action_groupinfo).isVisible = false;
        super.onCreateOptionsMenu(menu, inflater)
    }
}