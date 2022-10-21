package com.omanshuaman.testingtournamentsports

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Suppress("DEPRECATION")

class ParticipantInfoActivity : AppCompatActivity() {

    private var uploadBtn: FloatingActionButton? = null
    private var myourname: EditText? = null
    private var mPhoneNumber: EditText? = null
    private var progressDialog: ProgressDialog? = null
    private var Id: String? = null
    private var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_participant_info)

        uploadBtn = findViewById(R.id.upload)
        myourname = findViewById(R.id.your_name)
        mPhoneNumber = findViewById(R.id.phone_number)
        firebaseAuth = FirebaseAuth.getInstance()

        //get id of the group
        val intent = intent
        Id = intent.getStringExtra("tournamentId")

        uploadBtn!!.setOnClickListener {
            uploadToFirebase()
            val intent1 = Intent(this, DashboardActivity::class.java)
            startActivity(intent1)

        }
    }

    private fun uploadToFirebase() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Uploading")

        val gTimestamp = "" + System.currentTimeMillis()
        progressDialog!!.show()

        //setup member inf (add current user in group's participants list)
        val hashMap1 = HashMap<String, String?>()
        hashMap1["uid"] = firebaseAuth!!.uid
        hashMap1["role"] = "participant" //roles are creator, admin, participant
        hashMap1["timestamp"] = Id
        hashMap1["yourName"] = "" + myourname!!.text.toString()
        hashMap1["phoneNumber"] = "" + mPhoneNumber?.text.toString()
        val ref1 = FirebaseDatabase.getInstance().getReference("Tournament").child("Groups")
        ref1.child(Id!!).child("Participants").child(firebaseAuth!!.uid!!)
            .setValue(hashMap1)
            .addOnSuccessListener { //participant added
                progressDialog!!.dismiss()
                Toast.makeText(
                    this@ParticipantInfoActivity,
                    "Group created...",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            .addOnFailureListener { e -> //failed adding participant
                progressDialog!!.dismiss()
                Toast.makeText(this@ParticipantInfoActivity, "" + e.message, Toast.LENGTH_SHORT)
                    .show()
            }

    }

}
