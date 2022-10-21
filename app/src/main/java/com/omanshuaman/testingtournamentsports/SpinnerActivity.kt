package com.omanshuaman.testingtournamentsports

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.omanshuaman.testingtournamentsports.adapters.FruitAdapter
import com.omanshuaman.testingtournamentsports.inventory.Data

class SpinnerActivity : AppCompatActivity(), CustomSpinner.OnSpinnerEventsListener {
    private var spinner_fruits: CustomSpinner? = null
    private var next_button: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spinner)
        spinner_fruits = findViewById(R.id.spinner_fruits)
        next_button = findViewById(R.id.next)
        spinner_fruits!!.setSpinnerEventsListener(this)
        val adapter = FruitAdapter(this@SpinnerActivity, Data.fruitList)
        spinner_fruits!!.adapter = adapter
        spinner_fruits!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                next_button!!.setOnClickListener {
                    if (parent.getItemAtPosition(position).toString() == "2") {
                        val intent = Intent(this@SpinnerActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@SpinnerActivity, "Only Sprint Available", Toast.LENGTH_LONG).show()

                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onPopupWindowOpened(spinner: Spinner?) {
        spinner_fruits!!.background = resources.getDrawable(R.drawable.bg_spinner_fruit_up)
    }

    override fun onPopupWindowClosed(spinner: Spinner?) {
        spinner_fruits!!.background = resources.getDrawable(R.drawable.bg_spinner_fruit)
    }
}