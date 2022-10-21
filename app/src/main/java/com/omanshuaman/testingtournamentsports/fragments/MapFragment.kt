package com.omanshuaman.testingtournamentsports.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.omanshuaman.testingtournamentsports.SignInActivity
import com.omanshuaman.testingtournamentsports.SpinnerActivity
import com.omanshuaman.testingtournamentsports.adapters.AdapterCard
import com.omanshuaman.testingtournamentsports.models.Upload
import com.omanshuaman.testingtournamentsports.R


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var button: Button? = null
    var recyclerView: RecyclerView? = null
    var markerList: ArrayList<Upload>? = null
    var adapterCard: AdapterCard? = null
    private var mMarkerArray = ArrayList<Marker?>()
    private val REQUEST_LOCATION_PERMISSION = 1
    var firebaseAuth: FirebaseAuth? = null

    // declare a global variable of FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button = view.findViewById(R.id.button1)


        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        supportMapFragment!!.getMapAsync(this)

    }

    private fun marker() {
        val helper: SnapHelper = LinearSnapHelper()

        markerList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Tournament").child("Just Photos")
        //get all data from this ref
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                markerList!!.clear()
                for (ds in dataSnapshot.children) {
                    val modelList = ds.getValue(Upload::class.java)
                    if (modelList != null) {
                        markerList!!.add(modelList)
                    }
                    //adapter
                    adapterCard = activity?.let { AdapterCard(it, markerList) }
                    //set adapter to recyclerview
                    recyclerView!!.adapter = adapterCard

                    helper.attachToRecyclerView(recyclerView)
//                    snapHelper.attachToRecyclerView(recyclerView)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //in case of error
                Toast.makeText(activity, "" + databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        recyclerView = view?.findViewById(R.id.placesRecyclerView)

        firebaseAuth = FirebaseAuth.getInstance()

        // in onCreate() initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        //set layout to recyclerview
        recyclerView!!.layoutManager = layoutManager
        enableMyLocation()
        getLastKnownLocation()
        marker()


        mMarkerArray = ArrayList()
        // getLastKnownLocation()
        mMap.setOnMarkerClickListener { marker ->
            marker.setIcon(bitmapDescriptorFromVector(requireContext(), R.drawable.runner))

            val markerPosition = marker.position
            var selectedMarker = 0
            for (i in 0 until mMarkerArray.size) {

                if (markerPosition.latitude == mMarkerArray[i]?.position?.latitude && markerPosition.longitude == mMarkerArray[i]?.position?.longitude
                ) {
                    selectedMarker = i
                }
            }
            val zoom: Float = mMap.cameraPosition.zoom

            Log.d("TAGzx", "onMapReady: " + zoom)


            recyclerView?.smoothScrollToPosition(selectedMarker)
            // marker.showInfoWindow()

            return@setOnMarkerClickListener false
        }

        button?.setOnClickListener {

            //get current user
            val user = firebaseAuth!!.currentUser
            if (user != null) {
                //user is signed in stay here
                //set email of logged in user
//            startActivity(Intent(context, MainActivity::class.java))
//            requireActivity().finish()

                val intent = Intent(context, SpinnerActivity::class.java)
                startActivity(intent)
            } else {
                //user not signed in, go to main activity

                val intent = Intent(context, SignInActivity::class.java)
                startActivity(intent)
            }


        }

        val databaseReference1 =
            FirebaseDatabase.getInstance().getReference("Tournament").child("Just Photos")
        databaseReference1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (zoneSnapshot in dataSnapshot.children) {

                    val long =
                        zoneSnapshot.child("longitude").getValue(String::class.java)!!.toString()
                            .toDouble()
                    val lat =
                        zoneSnapshot.child("latitude").getValue(String::class.java)!!.toString()
                            .toDouble()

                    val location = LatLng(lat, long)
                    val marker: Marker? = googleMap.addMarker(
                        MarkerOptions().position(location)
                            .icon(
                                bitmapDescriptorFromVector(context!!, R.drawable.runner)
                            )
                    )
                    mMarkerArray.add(marker)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TAG", "onCancelled", databaseError.toException())
            }
        })


        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val firstCompletePosition =
                        layoutManager.findFirstCompletelyVisibleItemPosition()

                    for (i in 0 until mMarkerArray.size) {

                        if (i == firstCompletePosition
                        ) {
                            val cameraPosition = CameraPosition.Builder().target(
                                LatLng(
                                    mMarkerArray[i]!!.position.latitude,
                                    mMarkerArray[i]!!.position.longitude,
                                )
                            ).zoom(mMap.cameraPosition.zoom).build()

                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                        }
                    }
                }

            }

        })


    }


    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    // Checks that users have given permission
    private fun isPermissionGranted(): Boolean {
        return context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    // Checks if users have given their location and sets location enabled if so.
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            mMap.isMyLocationEnabled = true
        } else {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            }
        }
    }

    //     Callback for the result from requesting permissions.
//     This method is invoked for every call on requestPermissions(android.app.Activity, String[],
    // int).
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    // use your location object
                    // get latitude , longitude and other info from this
                    val latLng = LatLng(location.latitude, location.longitude)

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5F))
                }

            }

    }
//        private fun checkUserStatus() {
//        //get current user
//        val user = firebaseAuth!!.currentUser
//        if (user != null) {
//            //user is signed in stay here
//            //set email of logged in user
//        } else {
//            //user not signed in, go to main activity
//            startActivity(Intent(context, SignInActivity::class.java))
//        }
//    }
//
//        override fun onStart() {
//        //check on start of app
//        checkUserStatus()
//        super.onStart()
//    }
}

