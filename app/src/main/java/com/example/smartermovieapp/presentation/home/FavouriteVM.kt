package com.example.smartermovieapp.presentation.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartermovieapp.data.local.model.Favourite
import com.example.smartermovieapp.data.local.model.Movie
import com.example.smartermovieapp.data.local.model.Trailer
import com.example.smartermovieapp.data.repository.MoviesRepository
import com.example.smartermovieapp.data.responses.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class FavouriteVM @Inject constructor(
    val repository: MoviesRepository
): ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val favouriteMoviesLiveData: MutableLiveData<Resource<List<Movie>>> = MutableLiveData(Resource.Loading())

    companion object {
        const val ERROR_FAVOURITE_MOVIES_DEFAULT = "Could not load favourite movies. Please try again later"
        const val ERROR_COULD_NOT_LOAD_TRAILER_DEFAULT = "Could not load trailer. Please try again later."
    }

    init {
        loadFavouriteMovies()
    }

    private fun loadFavouriteMovies() {
        favouriteMoviesLiveData.value = Resource.Loading()
        compositeDisposable.add(
            repository.getFavouriteMovies().observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> favouriteMoviesLiveData.value = Resource.Success(result) },
                    { error -> favouriteMoviesLiveData.value = Resource.Error(error.message ?: ERROR_FAVOURITE_MOVIES_DEFAULT) }
                )
        )
    }

    fun unmarkFavourite(movieId: Int) =
        compositeDisposable.add(
            repository.removeFavouriteMovie(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { loadFavouriteMovies() },
                    { error -> error.printStackTrace() }
                )
        )

    fun markFavorite(movieId: Int) =
        compositeDisposable.add(
            repository.insertFavourite(Favourite(movieId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { loadFavouriteMovies() },
                    { error -> error.printStackTrace() }
                )
        )

    fun getTrailerOfMovie(movieId: Int): MutableLiveData<Resource<Trailer>> {
        val trailerLiveData: MutableLiveData<Resource<Trailer>> = MutableLiveData(Resource.Loading())
        compositeDisposable.add(
            repository.getTrailerOfMovieId(movieId).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> trailerLiveData.value = Resource.Success(result) },
                    { error -> trailerLiveData.value = Resource.Error(error.message ?: HomeVM.ERROR_COULD_NOT_LOAD_TRAILER_DEFAULT)}
                )
        )
        return trailerLiveData
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}