package com.example.smartermovieapp.data

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class SharedPrefs @Inject constructor(context: Context) {

    companion object {
        const val PREFS_NAME = "app_preferences"
        const val KEY_ARE_CINEMAS_SAVED = "are_cinemas_initialized"
        const val KEY_LOCATION_LAT = "location_lat"
        const val KEY_LOCATION_LNG = "location_lng"
        const val FALLBACK_COORDINATE = 100.0f
        const val MAX_COORDINATE = 90.0f
    }

    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun areCinemasSaved() = sharedPrefs.getBoolean(KEY_ARE_CINEMAS_SAVED, false)

    fun markCinemasAsSaved() =
        with(sharedPrefs.edit()) {
            putBoolean(KEY_ARE_CINEMAS_SAVED, true)
            apply()
        }

    fun saveUserLocation(location: LatLng) =
        with(sharedPrefs.edit()) {
            putFloat(KEY_LOCATION_LAT, location.latitude.toFloat())
            putFloat(KEY_LOCATION_LNG, location.longitude.toFloat())
            apply()
        }

    fun getLastUserLocation(): LatLng? {
        val latitude = sharedPrefs.getFloat(KEY_LOCATION_LAT, FALLBACK_COORDINATE)
        val longitude = sharedPrefs.getFloat(KEY_LOCATION_LNG, FALLBACK_COORDINATE)

        if(latitude > MAX_COORDINATE || longitude > MAX_COORDINATE) return null

        return LatLng(latitude.toDouble(), longitude.toDouble())
    }

}