package com.example.smartermovieapp.presentation.cinema_map

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

object CurrentLocationHandler {

    private const val REQUEST_LOCATION_INTERVAL = 60000L
    private const val REQUEST_LOCATION_FASTEST_INTERVAL = 60000L

    @SuppressLint("MissingPermission")
    fun doOnCurrentLocation(activity: Activity, onLocation: (latLngLocation: LatLng) -> Unit) {
        if(!CheckLocationHandler.hasLocationPermissions(activity.applicationContext)) {
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        val locationRequest = getLocationRequest()
        val locationCallback : LocationCallback = getLocationCallBack(onLocation)
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    private fun getLocationRequest() =
        LocationRequest().apply {
            interval = REQUEST_LOCATION_INTERVAL
            fastestInterval = REQUEST_LOCATION_FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }

    private fun getLocationCallBack(onLocation: (latLngLocation: LatLng) -> Unit) =
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                val locationList = locationResult.locations
                if (locationList.isEmpty()) return

                val location = locationList.last()
                val latLng = LatLng(location.latitude, location.longitude)

                onLocation(latLng)
            }
        }


}