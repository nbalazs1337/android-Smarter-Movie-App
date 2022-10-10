package com.example.smartermovieapp.presentation.home

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartermovieapp.data.SharedPrefs
import com.example.smartermovieapp.data.local.model.Cinema
import com.example.smartermovieapp.data.local.model.Cinemas
import com.example.smartermovieapp.data.local.model.Movie
import com.example.smartermovieapp.data.repository.MoviesRepository
import com.example.smartermovieapp.data.responses.Resource
import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder
import com.google.maps.android.SphericalUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class CinemaVM @Inject constructor(
    private val repository: MoviesRepository,
    private val sharedPrefs: SharedPrefs,
    private val application: Application
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val cinemasLiveData: MutableLiveData<Resource<List<Cinema>>> = MutableLiveData(Resource.Loading())

    companion object {
        const val ERROR_MOVIES_DEFAULT = "Could not load specified movies, please try again later."
        const val ERROR_CINEMAS_DEFAULT = "Could not load cinemas, please try again later."
    }

    init {
        saveCinemas()
        loadCinemas()
    }

    private fun saveCinemas() {
        if(sharedPrefs.areCinemasSaved()) return

        val cinemasFromJson = getCinemasFromJsonFile()
        compositeDisposable.add(
            repository.insertCinemas(cinemasFromJson)
                .subscribe(
                    { sharedPrefs.markCinemasAsSaved() },
                    { error -> error.printStackTrace() }
                )
        )
    }

    private fun loadCinemas() =
        repository.getCinemas().observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> cinemasLiveData.value = Resource.Success(result) },
                { error -> cinemasLiveData.value = Resource.Error(error.message ?: ERROR_CINEMAS_DEFAULT) }
            )

    private fun getCinemasFromJsonFile(): List<Cinema> {
        val jsonFile = application.assets.open("cinemas.json").bufferedReader().use { it.readText() }
        val cinemas = GsonBuilder().create().fromJson(jsonFile, Cinemas::class.java)
        return cinemas.cinemas
    }

    fun getMoviesLiveDataOfIds(movieIds: List<Int>): MutableLiveData<Resource<List<Movie>>> {
        val moviesLiveData: MutableLiveData<Resource<List<Movie>>> = MutableLiveData(Resource.Loading())
        compositeDisposable.add(
            repository.getMoviesOfIds(movieIds).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> moviesLiveData.value = Resource.Success(result) },
                    { error -> moviesLiveData.value = Resource.Error(error.message ?: ERROR_MOVIES_DEFAULT) }
                )
        )
        return moviesLiveData
    }

    fun getCinemaLiveData(cinemaId: Int): MutableLiveData<Resource<Cinema>> {
        val cinemaLiveData: MutableLiveData<Resource<Cinema>> = MutableLiveData(Resource.Loading())
        compositeDisposable.add(
            repository.getCinema(cinemaId).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> cinemaLiveData.value = Resource.Success(result) },
                    { error -> cinemaLiveData.value = Resource.Error(error.message ?: ERROR_CINEMAS_DEFAULT)}
                )
        )
        return cinemaLiveData
    }

    fun getCinemasRunningMovieLiveData(movieId: Int): MutableLiveData<Resource<List<Cinema>>> {
        val cinemasRunningMovieLiveData: MutableLiveData<Resource<List<Cinema>>> = MutableLiveData(Resource.Loading())
        compositeDisposable.add(
            repository.getCinemasRunningMovie(movieId).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> cinemasRunningMovieLiveData.value = Resource.Success(result) },
                    { error -> cinemasRunningMovieLiveData.value = Resource.Error(error.message ?: ERROR_CINEMAS_DEFAULT)}
                )
        )
        return cinemasRunningMovieLiveData
    }

    fun getSearchCinemasLiveData(query: String): MutableLiveData<Resource<List<Cinema>>> {
        val searchCinemas: MutableLiveData<Resource<List<Cinema>>> = MutableLiveData(Resource.Loading())
        compositeDisposable.add(
            repository.getSearchCinemas(query).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> searchCinemas.value = Resource.Success(result) },
                    { error -> searchCinemas.value = Resource.Error(error.message ?: ERROR_CINEMAS_DEFAULT)}
                )
        )
        return searchCinemas
    }

    fun saveUserLocation(userLocation: LatLng) = sharedPrefs.saveUserLocation(userLocation)

    fun getLastUserLocation(): LatLng? = sharedPrefs.getLastUserLocation()

    fun getCinemasByLastLocation(cinemas: List<Cinema>): List<Cinema> {
        val lastLocation = sharedPrefs.getLastUserLocation() ?: return cinemas

        return cinemas.sortedBy { cinema ->
            val cinemaLatLng = LatLng(cinema.lat, cinema.lng)
            SphericalUtil.computeDistanceBetween(lastLocation, cinemaLatLng)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}