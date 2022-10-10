package com.example.smartermovieapp.presentation.cinema_map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.widget.Toast
import androidx.core.content.ContextCompat

object CheckLocationHandler {

    private const val ENABLE_PERMISSIONS_MESSAGE = "Please enable location permissions."
    private const val ENABLE_GPS_MESSAGE = "Please turn on GPS services."

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun hasLocationPermissions(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    fun isLocationAvailable(context: Context): Boolean =
        isLocationEnabled(context) && hasLocationPermissions(context)

    fun showEnablePermissionsMessage(context: Context) =
        Toast.makeText(context, ENABLE_PERMISSIONS_MESSAGE, Toast.LENGTH_LONG).show()

    fun showEnableGpsMessage(context: Context) =
        Toast.makeText(context, ENABLE_GPS_MESSAGE, Toast.LENGTH_LONG).show()

}