package com.omanshuaman.testingtournamentsports

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import com.omanshuaman.testingtournamentsports.R
import com.omanshuaman.testingtournamentsports.inventory.LoadingDialog

class DialogActivity : AppCompatActivity() {
    var login_btn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        login_btn = findViewById(R.id.button)
        val loadingDialog = LoadingDialog(this@DialogActivity)
        login_btn?.setOnClickListener {
            loadingDialog.startLoadingDialog()
            val handler = Handler()
            handler.postDelayed({ loadingDialog.dismissDialog() }, 5000)

            Handler(Looper.getMainLooper()).postDelayed({
                // Your Code
                loadingDialog.dismissDialog()
            }, 3000)
        }
    }
}