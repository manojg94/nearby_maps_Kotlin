package com.manoj.maps_nearby_kotlin.files

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.nio.file.Files.size
import com.google.android.gms.maps.GoogleMap
import android.os.AsyncTask
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import com.manoj.maps_nearby_kotlin.MapsActivity
import java.net.URL


class GetNearbyPlacesData : AsyncTask<ArrayList<Any>, String, String>() {
    lateinit var googlePlacesData: String
    lateinit var mMap: GoogleMap
    lateinit var url: String


    override fun onPreExecute() {
        super.onPreExecute()
        MapsActivity.progressBar.show();
    }
    override fun doInBackground(vararg params: ArrayList<Any>): String {
        try {
            Log.d("GetNearbyPlacesData", "doInBackground entered")

            mMap = params.get(0)[0] as GoogleMap
            url = params.get(0)[1].toString()

            val downloadUrl = DownloadUrl()
            googlePlacesData = downloadUrl.readUrl(url)
            Log.d("GooglePlacesReadTask", "doInBackground Exit")
        } catch (e: Exception) {
            Log.d("GooglePlacesReadTask", e.toString())
        }

        return googlePlacesData
    }

    override fun onPostExecute(result: String) {
        Log.d("GooglePlacesReadTask", "onPostExecute Entered")
        var nearbyPlacesList: List<HashMap<String, String>>? = null
        val dataParser = DataParser()
        nearbyPlacesList = dataParser.parse(result)
        ShowNearbyPlaces(nearbyPlacesList!!)
        Log.d("GooglePlacesReadTask", "onPostExecute Exit")
    }

    private fun ShowNearbyPlaces(nearbyPlacesList: List<HashMap<String, String>>) {
        for (i in nearbyPlacesList.indices) {
            Log.d("onPostExecute", "Entered into showing locations")
            val markerOptions = MarkerOptions()
            val googlePlace = nearbyPlacesList[i]
            val lat = java.lang.Double.parseDouble(googlePlace["lat"]!!)
            val lng = java.lang.Double.parseDouble(googlePlace["lng"]!!)
            val placeName = googlePlace["place_name"]
            val vicinity = googlePlace["vicinity"]
            val latLng = LatLng(lat, lng)
            markerOptions.position(latLng)
            markerOptions.title("$placeName : $vicinity")
            mMap.addMarker(markerOptions)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17f))
        }
        MapsActivity.progressBar.dismiss()
    }
}