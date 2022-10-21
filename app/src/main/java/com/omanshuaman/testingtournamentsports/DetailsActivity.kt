package com.omanshuaman.testingtournamentsports

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.palette.graphics.Palette
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.omanshuaman.testingtournamentsports.R.id
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


class DetailsActivity : AppCompatActivity() {

    private var imageView: ImageView? = null
    private var relativeLayout: CoordinatorLayout? = null
    private var tournamentName: TextView? = null
    private var entryFee: TextView? = null
    private var prizeMoney: TextView? = null
    private var address: TextView? = null
    private var matchDate: TextView? = null
    private var button: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        supportActionBar?.hide();
        val collapsingToolbarLayout =
            findViewById<CollapsingToolbarLayout>(id.collapsingToolbarLayout)

        val intent = intent
        val extras = intent.extras
        val entryFee1 = extras?.getString("entryFee")
        val prizeFee1 = extras?.getString("prizeFee")
        val backgroundImage = extras?.getString("backgroundImage")
        val address1 = extras?.getString("address")
        val tournamentName1 = extras?.getString("tournamentName")
        val matchDate1 = extras?.getString("matchDate")

        imageView = findViewById(id.imageView)
        relativeLayout = findViewById(id.relativeLayout)
        tournamentName = findViewById(id.tournamentName)
        entryFee = findViewById(id.entry_fee)
        prizeMoney = findViewById(id.prizeMoney)
        address = findViewById(id.address)
        matchDate = findViewById(id.matchDate)
        button = findViewById(id.register)

        entryFee!!.text = entryFee1
        prizeMoney!!.text = prizeFee1
        address!!.text = address1
        tournamentName!!.text = tournamentName1
        matchDate!!.text = matchDate1

        getBitmapFromUrl()



        imageView!!.setOnClickListener {

            val intent1 = Intent(this, PosterZoom::class.java)
            intent1.putExtra("backgroundImage", backgroundImage)
            startActivity(intent1)

        }
    }




    private fun createPaletteSync(bitmap: Bitmap?): Palette {
        return Palette.from(bitmap!!).generate()
    }

    private fun getBitmapFromUrl() {

        CoroutineScope(Dispatchers.IO).launch {
            val intent = intent
            val extras = intent.extras
            val tournamentId = extras?.getString("tournamentId")
            val backgroundImage = extras?.getString("backgroundImage")
            val bitmap = backgroundImage?.let { getBitmap(it) }

            withContext(Dispatchers.Main) {
                Picasso.get()
                    .load(backgroundImage)
                    .fit().centerCrop().into(imageView)

                val palette = createPaletteSync(bitmap)
                val vibrantSwatch = palette.vibrantSwatch
                val darkMutedSwatch = palette.darkMutedSwatch
                if (vibrantSwatch != null) {
                    val bgColor = vibrantSwatch.rgb
                    relativeLayout!!.setBackgroundColor(bgColor)
                    window.statusBarColor = bgColor

                } else if (darkMutedSwatch != null) {
                    val bgColor = darkMutedSwatch.rgb
                    relativeLayout!!.setBackgroundColor(bgColor)
                    window.statusBarColor = bgColor
                } else {
                    relativeLayout!!.setBackgroundColor(Color.BLACK)
                    window.statusBarColor = Color.BLACK
                }
            }

            val firebaseAuth: FirebaseAuth?
            firebaseAuth = FirebaseAuth.getInstance()
            val user = firebaseAuth.uid
            if (user != null) {
                //user is signed in stay here
                //set email of logged in user
                val ref =
                    FirebaseDatabase.getInstance().getReference("Tournament").child("Groups")
                ref.child(tournamentId.toString()).child("Participants").child(user)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            button!!.isEnabled = !dataSnapshot.exists()
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
            } else {
                //noting happen
            }
            button!!.setOnClickListener {
                if (user != null) {
                    val intent1 =
                        Intent(this@DetailsActivity, ParticipantInfoActivity::class.java)
                    intent1.putExtra("tournamentId", tournamentId)
                    startActivity(intent1)
                } else {
                    val intent1 = Intent(this@DetailsActivity, SignInActivity::class.java)
                    startActivity(intent1)
                }
            }
        }
    }

    private fun getBitmap(uriImage: String): Bitmap? {
        // val imageUrl = getString(R.string.image_url)
        val url =
            URL(uriImage)
        return BitmapFactory.decodeStream(url.openConnection().getInputStream())
    }
}