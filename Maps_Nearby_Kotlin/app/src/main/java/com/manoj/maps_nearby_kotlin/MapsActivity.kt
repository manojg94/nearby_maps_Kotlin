package com.manoj.maps_nearby_kotlin

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.location.*
import android.os.*
import android.util.Log
import android.view.View
import android.widget.*
import com.manoj.maps_nearby_kotlin.files.GetNearbyPlacesData
import kotlinx.android.synthetic.main.activity_maps1.*


class MapsActivity() : AppCompatActivity(),
    OnMapReadyCallback {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        public lateinit var progressBar:ProgressDialog

    }
    private lateinit var mMap: GoogleMap
    private  var latitude: Double = 0.0
    private  var Longitude: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps1)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
         progressBar =  ProgressDialog(this)
        progressBar.setTitle("Loading Please wait...")
        setUpMap()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20f))
        mMap.uiSettings.isZoomControlsEnabled = true

        val manager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val mCriteria = Criteria()
        val bestProvider = manager.getBestProvider(mCriteria, true).toString()


        val locat = manager.getLastKnownLocation(bestProvider)
        val currentLatitude = locat!!.latitude
        val currentLongitude = locat.longitude
        latitude=currentLatitude
        Longitude=currentLongitude
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(currentLatitude, currentLongitude),
                20f
            )
        )


    }

    fun searchLocation(view: View) {
        val locationSearch: EditText = findViewById<EditText>(R.id.editText)
        lateinit var location: String
        location = locationSearch.text.toString().trim()

        if (location == null || location == "") {
            Toast.makeText(applicationContext, "provide location", Toast.LENGTH_SHORT).show()
        }else {
            mMap.clear();
            var url: String = getUrl(latitude, Longitude, location);
            var DataTransfer = ArrayList<Any>()
            DataTransfer.add(mMap)
            DataTransfer.add(url)
            Log.d("onClick", url)
            val getNearbyPlacesData: GetNearbyPlacesData = GetNearbyPlacesData()
            getNearbyPlacesData.execute(DataTransfer)


        }
    }


 fun getUrl(latitude:Double,longitude:Double,nearbyPlace:String):String{
        var googlePlacesUrl:StringBuilder =  StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude)
        googlePlacesUrl.append("&radius=" + 500)
        googlePlacesUrl.append("&type=" + nearbyPlace)
        googlePlacesUrl.append("&sensor=true")
     // if below api quota is finished use this AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0
        googlePlacesUrl.append("&key=" + "AIzaSyCL3iNJS0G3LXU3eRp4IflC5w2CnjlQv3s");
        Log.d("getUrl", googlePlacesUrl.toString())
        return (googlePlacesUrl.toString())
}



}
