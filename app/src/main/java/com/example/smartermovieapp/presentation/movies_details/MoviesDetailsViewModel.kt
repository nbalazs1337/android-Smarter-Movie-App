package com.example.smartermovieapp.presentation.movies_details

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

class MoviesDetailsViewModel @Inject constructor(
    val repository: MoviesRepository
): ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val movieLiveData: MutableLiveData<Resource<Movie>> = MutableLiveData(Resource.Loading())
    val trailerLiveData: MutableLiveData<Resource<Trailer>> = MutableLiveData(Resource.Loading())
    val similarMoviesLiveData: MutableLiveData<Resource<List<Movie>>> = MutableLiveData(Resource.Loading())
    val isFavoriteLiveData: MutableLiveData<Boolean> = MutableLiveData(false)

    companion object {
        const val ERROR_NO_MOVIE_DETAILS_DEFAULT = "No movie details found. Please try again later."
        const val ERROR_NO_SIMILAR_MOVIES_FOUND = "No similar movies found. Please try again later."
        const val ERROR_NO_TRAILER_DETAILS_DEFAULT = "No trailer details found. Please try again later."
    }

    fun loadData(movieId: Int) {
        if(movieId < 0) return

        loadMovie(movieId)
        loadSimilarMovies(movieId)
        loadTrailer(movieId)
        loadIsFavorite(movieId)
    }

    private fun loadTrailer(movieId: Int) {
        trailerLiveData.value = Resource.Loading()
        compositeDisposable.add(
            repository.getTrailerOfMovieId(movieId).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> trailerLiveData.value = Resource.Success(result) },
                    { error -> trailerLiveData.value = Resource.Error(error.message ?: ERROR_NO_TRAILER_DETAILS_DEFAULT)}
                )
        )
    }

    private fun loadMovie(movieId: Int) {
        movieLiveData.value = Resource.Loading()
        compositeDisposable.add(
            repository.getMovie(movieId).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> movieLiveData.value = Resource.Success(result) },
                    { error -> movieLiveData.value = Resource.Error(error.message ?: ERROR_NO_MOVIE_DETAILS_DEFAULT) }
                )
        )
    }

    private fun loadSimilarMovies(movieId: Int) {
        similarMoviesLiveData.value = Resource.Loading()
        compositeDisposable.add(
            repository.getSimilarMovies(movieId).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> similarMoviesLiveData.value = Resource.Success(result) },
                    { error -> similarMoviesLiveData.value = Resource.Error(error.message ?: ERROR_NO_SIMILAR_MOVIES_FOUND) }
                )
        )
    }

    private fun markFavorite(movieId: Int) =
        compositeDisposable.add(
            repository.insertFavourite(Favourite(movieId))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                    { loadIsFavorite(movieId) },
                    { error -> error.printStackTrace() }
                )
        )

    private fun unmarkFavorite(movieId: Int) =
        compositeDisposable.add(
            repository.removeFavouriteMovie(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                    { loadIsFavorite(movieId) },
                    { error -> error.printStackTrace() }
                )
        )

    fun toggleFavourite(movieId: Int) =
        isFavoriteLiveData.value?.let { isFavourite ->
            if(isFavourite) {
                unmarkFavorite(movieId)
            } else {
                markFavorite(movieId)
            }
        }

    private fun loadIsFavorite(movieId: Int) =
        compositeDisposable.add(
            repository.isFavourite(movieId).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> isFavoriteLiveData.value = result },
                    { error ->
                        run {
                            error.printStackTrace()
                            isFavoriteLiveData.value = false
                        }
                    }
                )
        )

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}