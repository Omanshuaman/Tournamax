package com.omanshuaman.testingtournamentsports

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.omanshuaman.testingtournamentsports.databinding.ActivityDriverHomeBinding
import com.omanshuaman.testingtournamentsports.fragments.GroupChatsFragment
import com.omanshuaman.testingtournamentsports.fragments.MapFragment


class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    //firebase auth
    var firebaseAuth: FirebaseAuth? = null
    private lateinit var binding: ActivityDriverHomeBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth!!.uid

        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // navigationView.menu.findItem(R.id.group_chat).isVisible = user != null
        navigationView.menu.findItem(R.id.nav_sign_out).isEnabled = user != null

        Handler(Looper.getMainLooper()).postDelayed({
            // Your Code
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                MapFragment()
            ).commit()
            navigationView.setCheckedItem(R.id.navi_home)
        }, 100)
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction().replace(
//                R.id.fragment_container,
//                MapFragment()
//            ).commit()
//            navigationView.setCheckedItem(R.id.navi_home)
//        }
        navigationView.setNavigationItemSelectedListener(this)

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val user = firebaseAuth!!.uid

        when (item.itemId) {
            R.id.navi_home -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                MapFragment()
            ).commit()
            R.id.group_chat ->
                if (user != null) {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.fragment_container,
                        GroupChatsFragment()
                    ).commit()
                } else {
                    val intent1 = Intent(this, SignInActivity::class.java)
                    startActivity(intent1)
                }

            R.id.nav_sign_out ->

                builder.setTitle("Sign out")
                    .setMessage("Do you really want to sign out?")
                    .setNegativeButton(
                        "CANCEL"
                    ) { dialogInterface, _ -> dialogInterface.dismiss() }
                    .setPositiveButton("SIGN OUT") { dialogInterface, _ ->
                        FirebaseAuth.getInstance().signOut()
                        val intent =
                            Intent(this@DashboardActivity, SignInActivity::class.java)

                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }.show()

        }


        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}