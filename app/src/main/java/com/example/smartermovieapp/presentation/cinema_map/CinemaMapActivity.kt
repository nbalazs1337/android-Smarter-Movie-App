package com.example.smartermovieapp.presentation.cinema_map

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartermovieapp.R
import com.example.smartermovieapp.data.local.model.Cinema
import com.example.smartermovieapp.data.responses.Resource
import com.example.smartermovieapp.presentation.cinema_details.CinemaDetailsActivity
import com.example.smartermovieapp.presentation.home.CinemaVM
import com.example.smartermovieapp.utils.observeOnce
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.AndroidInjection
import javax.inject.Inject

class CinemaMapActivity : AppCompatActivity(), OnMapReadyCallback {

    @Inject
    lateinit var cinemaVM: CinemaVM

    private var mapType: String = ""

    companion object {
        const val KEY_CINEMA_MAP_TYPE = "cinema_map_type"
        const val TYPE_CINEMA = "type_cinema"
        const val TYPE_ALL_CINEMAS = "type_all_cinemas"

        const val KEY_CINEMA_ID_TO_SHOW = "cinema_id_to_show"

        const val DEFAULT_ZOOM = 15f

        const val DEFAULT_ZOOM_ALL_CINEMAS = 13f

        const val DEFAULT_LAT_NO_LOCATION = 46.770920
        const val DEFAULT_LNG_NO_LOCATION = 23.589920

        const val ERROR_NO_CINEMAS_DEFAULT = "Could not load cinemas, please try again later."
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cinema_map)
        //TransparentStatusBarHandler.initTransparentStatusBar(window)

        mapType = intent.getStringExtra(KEY_CINEMA_MAP_TYPE) ?: ""

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_cinema) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        if(mapType == TYPE_CINEMA) {
            setupCinemaMapObserver(map)
            return
        }

        if(mapType == TYPE_ALL_CINEMAS) {
            setupAllCinemasMapObserver(map)
        }
    }

    private fun setupCinemaMapObserver(map: GoogleMap) {
        val cinemaId = intent.getIntExtra(KEY_CINEMA_ID_TO_SHOW, -1)
        if(cinemaId < 0) return
        cinemaVM.getCinemaLiveData(cinemaId).observeOnce(this) {
            when(it) {
                is Resource.Success -> it.data?.let { cinema -> setupCinemaMap(cinema, map) }
                is Resource.Error -> showError(it.message)
                is Resource.Loading -> {}
            }
        }
    }

    private fun setupAllCinemasMapObserver(map: GoogleMap) =
        cinemaVM.cinemasLiveData.observe(this) {
            when(it) {
                is Resource.Success -> it.data?.let { cinemas -> setupAllCinemasMap(cinemas, map) }
                is Resource.Error -> showError(it.message)
                is Resource.Loading -> {}
            }
        }

    private fun showError(message: String?, fallBackError: String = CinemaDetailsActivity.ERROR_NO_CINEMAS_DEFAULT) =
        Toast.makeText(this, message ?: fallBackError, Toast.LENGTH_LONG).show()

    @SuppressLint("MissingPermission")
    private fun setupAllCinemasMap(cinemas: List<Cinema>, map: GoogleMap) {
        cinemas.forEach { cinema ->
            val cinemaLocation = LatLng(cinema.lat, cinema.lng)
            map.addCinemaMarker(cinema, cinemaLocation)
        }

        map.isMyLocationEnabled = true

        if(CheckLocationHandler.isLocationAvailable(this)) {
            CurrentLocationHandler.doOnCurrentLocation(this) { latLng ->
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM_ALL_CINEMAS))
            }
        } else cinemaVM.getLastUserLocation()?.let { lastUserLocation ->
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, DEFAULT_ZOOM_ALL_CINEMAS))
        } ?: run {
            val defaultLocation = LatLng(DEFAULT_LAT_NO_LOCATION, DEFAULT_LNG_NO_LOCATION)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM_ALL_CINEMAS))
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupCinemaMap(cinema: Cinema, map: GoogleMap) {
        val cinemaLocation = LatLng(cinema.lat, cinema.lng)

        map.addCinemaMarker(cinema, cinemaLocation)
        map.isMyLocationEnabled = true
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(cinemaLocation, DEFAULT_ZOOM))
    }

    private fun GoogleMap.addCinemaMarker(cinema: Cinema, location: LatLng) =
        addMarker(
            MarkerOptions()
                .position(location)
                .title(cinema.name)
        )

}