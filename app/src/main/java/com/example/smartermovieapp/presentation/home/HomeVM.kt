package com.example.smartermovieapp.presentation.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartermovieapp.data.local.model.Movie
import com.example.smartermovieapp.data.local.model.Trailer
import com.example.smartermovieapp.data.repository.MoviesRepository
import com.example.smartermovieapp.data.responses.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class HomeVM @Inject constructor(
    val repository: MoviesRepository
): ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val trailerMoviesLiveData: MutableLiveData<Resource<List<Movie>>> = MutableLiveData(Resource.Loading())
    val popularLiveData: MutableLiveData<Resource<List<Movie>>> = MutableLiveData(Resource.Loading())
    val topRatedLiveData: MutableLiveData<Resource<List<Movie>>> = MutableLiveData(Resource.Loading())
    val upcomingLiveData: MutableLiveData<Resource<List<Movie>>> = MutableLiveData(Resource.Loading())

    companion object {
        const val ERROR_COULD_NOT_LOAD_MOVIES_DEFAULT = "Could not load movies. Please try again later."
        const val ERROR_COULD_NOT_LOAD_TRAILER_DEFAULT = "Could not load trailer. Please try again later."
    }

    init {
        loadTrailerMovies()
        loadPopularMovies()
        loadTopRatedMovies()
        loadUpcomingMovies()
    }

    private fun loadUpcomingMovies() {
        upcomingLiveData.value = Resource.Loading()
        compositeDisposable.add(
            repository.getUpcoming().observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> upcomingLiveData.value = Resource.Success(result) },
                    { error -> upcomingLiveData.value = Resource.Error(error.message ?: ERROR_COULD_NOT_LOAD_MOVIES_DEFAULT)}
                )
        )
    }

    private fun loadTopRatedMovies() {
        topRatedLiveData.value = Resource.Loading()
        compositeDisposable.add(
            repository.getTopRated().observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> topRatedLiveData.value = Resource.Success(result) },
                    { error -> topRatedLiveData.value = Resource.Error(error.message ?: ERROR_COULD_NOT_LOAD_MOVIES_DEFAULT)}
                )
        )
    }

    private fun loadPopularMovies() {
        popularLiveData.value = Resource.Loading()
        compositeDisposable.add(
            repository.getPopular().observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> popularLiveData.value = Resource.Success(result) },
                    { error -> popularLiveData.value = Resource.Error(error.message ?: ERROR_COULD_NOT_LOAD_MOVIES_DEFAULT)}
                )
        )
    }

    private fun loadTrailerMovies() {
        trailerMoviesLiveData.value = Resource.Loading()
        compositeDisposable.add(
            repository.getTrailerMovies().observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> trailerMoviesLiveData.value = Resource.Success(result) },
                    { error -> trailerMoviesLiveData.value = Resource.Error(error.message ?: ERROR_COULD_NOT_LOAD_MOVIES_DEFAULT)}
                )
        )
    }

    fun getTrailerOfMovie(movieId: Int): MutableLiveData<Resource<Trailer>> {
        val trailerLiveData: MutableLiveData<Resource<Trailer>> = MutableLiveData(Resource.Loading())
        compositeDisposable.add(
            repository.getTrailerOfMovieId(movieId).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> trailerLiveData.value = Resource.Success(result) },
                    { error -> trailerLiveData.value = Resource.Error(error.message ?: ERROR_COULD_NOT_LOAD_TRAILER_DEFAULT)}
                )
        )
        return trailerLiveData
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}