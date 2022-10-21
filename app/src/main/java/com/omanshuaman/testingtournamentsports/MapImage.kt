package com.omanshuaman.testingtournamentsports

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class MapImage : AppCompatActivity() {
    private var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_image)
        imageView = findViewById(R.id.imageView1)


//        val latEiffelTower = "48.858235"
//        val lngEiffelTower = "2.294571"
//       val url =
//           "http://maps.google.com/maps/api/staticmap?center=$latEiffelTower,$lngEiffelTower&zoom=15&size=200x200&sensor=false&key=AIzaSyBQFyDVezCU5J2fqyq0HL7tvgWkfe2_PVQ";
//
//        Picasso.get().load(url).into(imageView)

        val lat = "-12.958811"
        val lon = "-38.401606"

        var url = "https://maps.googleapis.com/maps/api/staticmap?"
        url += "&zoom=14"
        url += "&size=330x130"
        url += "&maptype=roadmap"
        url += "&markers=color:green%7Clabel:G%7C$lat, $lon"
        url += "&key=AIzaSyBQFyDVezCU5J2fqyq0HL7tvgWkfe2_PVQ"

        Picasso.get().load(url).into(imageView)

    }
}