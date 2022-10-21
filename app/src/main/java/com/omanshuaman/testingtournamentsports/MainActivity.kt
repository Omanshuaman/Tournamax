package com.omanshuaman.testingtournamentsports

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adevinta.leku.LATITUDE
import com.adevinta.leku.LONGITUDE
import com.adevinta.leku.LocationPickerActivity
import com.adevinta.leku.locale.SearchZoneRect
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.omanshuaman.testingtournamentsports.adapters.ItemAdapter
import com.omanshuaman.testingtournamentsports.inventory.DatePickerFragment
import com.omanshuaman.testingtournamentsports.models.DataModel
import com.omanshuaman.testingtournamentsports.models.Upload
import java.text.DateFormat
import java.util.*


@Suppress("DEPRECATION")


class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    AdapterView.OnItemSelectedListener {

    private var mAuth: FirebaseAuth? = null
    private var uploadBtn: FloatingActionButton? = null
    private var mtournamentsportsname: EditText? = null
    private var mRegisterDate: EditText? = null
    private var mAddress: EditText? = null
    private var mEntryFee: EditText? = null
    private var mPrizeMoney: EditText? = null
    private var mbtnPicklocation: Button? = null
    private var tvMylocation: TextView? = null
    private val PLACE_PICKER_REQUEST2 = 999
    private var item: String? = null
    private var latitude: String? = null
    private var longitude: String? = null
    private var geocoder: Geocoder? = null
    private var addresses: List<Address>? = null
    private var mList: MutableList<DataModel>? = null
    private var adapter: ItemAdapter? = null
    private var button: Button? = null
    private var textView: TextView? = null
    private var text: String? = null

    private val userid = FirebaseAuth.getInstance().currentUser!!.uid
    private val databaseReference = FirebaseDatabase.getInstance().getReference("Tournament")
    private val gTimestamp = "" + System.currentTimeMillis()
    private var recyclerView: RecyclerView? = null
    private var recyclerView1: RecyclerView? = null
    private var recyclerView2: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        uploadBtn = findViewById(R.id.upload_btn)
        mtournamentsportsname = findViewById(R.id.tournament_name)
        mbtnPicklocation = findViewById(R.id.BtnPickLocation)
        mEntryFee = findViewById(R.id.entry_fee)
        mPrizeMoney = findViewById(R.id.prize_money)
        mAddress = findViewById(R.id.address)
        button = findViewById(R.id.button)
        textView = findViewById(R.id.textView)

        geocoder = Geocoder(this, Locale.getDefault())

        mAuth = FirebaseAuth.getInstance()
        val spinner = findViewById<Spinner>(R.id.spinner1)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.numbers, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this

        recyclerView = findViewById(R.id.main_recyclerview)
        recyclerView?.setHasFixedSize(false)
        recyclerView?.layoutManager = LinearLayoutManager(this)

        recyclerView1 = findViewById(R.id.entry_recyclerview)
        recyclerView1?.setHasFixedSize(false)
        recyclerView1?.layoutManager = LinearLayoutManager(this)

        recyclerView2 = findViewById(R.id.prize_recyclerview)
        recyclerView2?.setHasFixedSize(false)
        recyclerView2?.layoutManager = LinearLayoutManager(this)

        recyclerTournamentName()
        recyclerEntryFee()
        recyclerPrizeMoney()

        button!!.setOnClickListener {
            val datePicker: DialogFragment = DatePickerFragment()
            datePicker.show(supportFragmentManager, "date picker")
        }
        mbtnPicklocation!!.setOnClickListener {
            openPlacePicker()
        }

        uploadBtn!!.setOnClickListener {

            uploadToFirebase()
            val intent = Intent(this, PosterActivity::class.java)
            val extras = Bundle()
            extras.putString("tournamentName", mtournamentsportsname!!.text.toString())
            extras.putString("matchDate", textView?.text.toString())
            extras.putString("address", mAddress?.text.toString())
            extras.putString("id", gTimestamp)

            intent.putExtras(extras)
            startActivity(intent)
        }

    }


    private fun openPlacePicker() {

        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val key = value.toString()

        val locationPickerIntent = LocationPickerActivity.Builder()
            .withLocation(28.611541, 76.9972578)
            .withGeolocApiKey(key)
            .withSearchZone("es_ES")
            .withSearchZone(
                SearchZoneRect(
                    LatLng(26.525467, -18.910366),
                    LatLng(43.906271, 5.394197)
                )
            )
            .withDefaultLocaleSearchZone()
            .shouldReturnOkOnBackPressed()
            .withStreetHidden()
            .withCityHidden()
            .withZipCodeHidden()
            .withSatelliteViewHidden()
            //.withGooglePlacesEnabled()
            .withGooglePlacesApiKey(key)
            .withGoogleTimeZoneEnabled()
            .withVoiceSearchHidden()
            .withUnnamedRoadHidden()
            //   .withMapStyle()
            .build(applicationContext)

        startActivityForResult(locationPickerIntent, PLACE_PICKER_REQUEST2)

//        val intent = PlacePicker.IntentBuilder()
//            .setLatLong(
//                40.748672,
//                -73.985628
//            )  // Initial Latitude and Longitude the Map will load into
//            .showLatLong(true)  // Show Coordinates in the Activity
//            .setMapZoom(12.0f)  // Map Zoom Level. Default: 14.0
//            .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
//            .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
//            .setMarkerDrawable(R.drawable.ic_profile_black) // Change the default Marker Image
//            .setMarkerImageImageColor(R.color.colorPrimary)
//            .setFabColor(R.color.black)
//            .setPrimaryTextColor(R.color.colorPrimary) // Change text color of Shortened Address
//            .setSecondaryTextColor(R.color.colorPrimary) // Change text color of full Address
//            .setBottomViewColor(R.color.colorBlack) // Change Address View Background Color (Default: White)
//            .setMapRawResourceStyle(R.raw.map_style)  //Set Map Style (https://mapstyle.withgoogle.com/)
//            .setMapType(MapType.NORMAL)
//            .setPlaceSearchBar(
//                false,
//                MAPS_API_KEY
//            ) //Activate GooglePlace Search Bar. Default is false/not activated. SearchBar is a chargeable feature by Google
//            .onlyCoordinates(false)  //Get only Coordinates from Place Picker
//            .hideLocationButton(false)   //Hide Location Button (Default: false)
//            .disableMarkerAnimation(false) //Disable Marker Animation (Default: false)
//            .build(this)
//        startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            Log.d("RESULT****", "OK")
            if (requestCode == PLACE_PICKER_REQUEST2) {

                latitude = data.getDoubleExtra(LATITUDE, 0.0).toString()
                Log.d("LATITUDE****", latitude.toString())
                longitude = data.getDoubleExtra(LONGITUDE, 0.0).toString()
                Log.d("LONGITUDE****", longitude.toString())
                addresses =
                    geocoder?.getFromLocation(
                        latitude!!.toDouble(),
                        longitude!!.toDouble(),
                        1
                    ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                Log.d("address****", addresses.toString())
                Log.d("address****", addresses?.get(0)?.getAddressLine(0).toString())
                mAddress?.setText(addresses?.get(0)?.getAddressLine(0).toString())

                val sb = StringBuilder()
                sb.append("LATITUDE:").append(latitude).append("\n").append("LONGITUDE: ")
                    .append(longitude)
                tvMylocation?.text = sb.toString()
            }
        }
//            if (resultCode == Activity.RESULT_OK && data != null) {
//                Log.d("RESULT****", "OK")
//                if (requestCode == Constants.PLACE_PICKER_REQUEST) {
//                    val addressData =
//                        data.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
//                    Log.d("TAGm", "onActivityResult: "+addressData)
//                } else {
//                    super.onActivityResult(requestCode, resultCode, data)
//                }
//
//            }
    }

    private fun uploadToFirebase() {

        val model = Upload(
            gTimestamp,
            mtournamentsportsname!!.text.toString(),
            textView?.text.toString(),
            mRegisterDate?.text.toString(),
            text,
            "Sprint",
            mAddress?.text.toString(),
            mRegisterDate?.text.toString(),
            longitude,
            latitude,
            item,
            mEntryFee?.text.toString(),
            mPrizeMoney?.text.toString(),
            userid,
        )

        databaseReference.child("Just Photos").child(gTimestamp).setValue(model)

    }

    private fun recyclerTournamentName() {
        mList = ArrayList()

        val nestedList1: MutableList<String> = ArrayList()
        nestedList1.add("Najafgrah Sprint Tournament 2022")
        nestedList1.add("Dwarka Club Sprint Tournament")
        nestedList1.add("Delhi Athletics academy Sprint Tournament")

        mList!!.add(DataModel(nestedList1, "Example"))
        adapter = ItemAdapter(mList!!)
        recyclerView?.adapter = adapter


    }


    private fun recyclerEntryFee() {
        mList = ArrayList()

        val nestedList1: MutableList<String> = ArrayList()
        nestedList1.add("Free")
        nestedList1.add("50₹")
        nestedList1.add("100₹")
        mList!!.add(DataModel(nestedList1, "Example"))
        adapter = ItemAdapter(mList!!)
        recyclerView1?.adapter = adapter
    }

    private fun recyclerPrizeMoney() {
        mList = ArrayList()

        val nestedList1: MutableList<String> = ArrayList()
        nestedList1.add("100₹")
        nestedList1.add("1st Prize: 50% of Participant Entry fee\n2nd Prize: 25% of Participant Entry fee\n3rd Prize: 20% of Participant Entry fee")
        nestedList1.add("1000₹ +\n 1st Prize: 50% of Participant Entry fee\n2nd Prize: 25% of Participant Entry fee\n3rd Prize: 20% of Participant Entry fee")
        nestedList1.add("5000₹")
        mList!!.add(DataModel(nestedList1, "Example"))
        adapter = ItemAdapter(mList!!)
        recyclerView2?.adapter = adapter
    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val c = Calendar.getInstance()
        c[Calendar.YEAR] = year
        c[Calendar.MONTH] = month
        c[Calendar.DAY_OF_MONTH] = dayOfMonth
        val currentDateString: String = DateFormat.getDateInstance(DateFormat.FULL).format(c.time)
        textView?.text = currentDateString

    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        text = parent.getItemAtPosition(position).toString()
        //   Toast.makeText(parent.context, text, Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}
