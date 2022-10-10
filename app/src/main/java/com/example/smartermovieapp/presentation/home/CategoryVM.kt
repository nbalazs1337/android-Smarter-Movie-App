package com.example.smartermovieapp.presentation.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartermovieapp.data.local.model.Movie
import com.example.smartermovieapp.data.local.model.Trailer
import com.example.smartermovieapp.data.repository.MoviesRepository
import com.example.smartermovieapp.data.responses.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class CategoryVM @Inject constructor(
    val repository: MoviesRepository
): ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val categoryMoviesLiveData: MutableLiveData<Resource<MutableList<Movie>>> = MutableLiveData(Resource.Loading())
    val isLoadingMoreMoviesLiveData: MutableLiveData<Boolean> = MutableLiveData(false)

    companion object {
        const val ERROR_COULD_NOT_LOAD_MOVIES_DEFAULT = "Could not load movies. Please try again later."
    }

    fun loadCategoryMovies(category: String) {
        categoryMoviesLiveData.value = Resource.Loading()
        compositeDisposable.add(
            getCategoryMoviesObservable(category).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> categoryMoviesLiveData.value = Resource.Success(result.toMutableList()) },
                    { error -> categoryMoviesLiveData.value = Resource.Error(error.message ?: ERROR_COULD_NOT_LOAD_MOVIES_DEFAULT )}
                )
        )
    }

    private fun getCategoryMoviesObservable(category: String): Observable<List<Movie>> =
        when(category) {
            CATEGORY_TITLE_TOP -> repository.getTopRated()
            CATEGORY_TITLE_POPULAR -> repository.getPopular()
            CATEGORY_TITLE_UPCOMING -> repository.getUpcoming()
            CATEGORY_TITLE_TRAILERS -> repository.getTrailerMovies()
            else -> Observable.just(listOf())
        }

    fun loadMoreCategoryMoviesFromPage(category: String, page: Int) {
        isLoadingMoreMoviesLiveData.value = true
        compositeDisposable.add(
            getCategoryMoviesObservableOnPage(category, page).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        categoryMoviesLiveData.value?.data?.let { movies ->
                            movies.addAll(result)
                            categoryMoviesLiveData.value = Resource.Success(movies)
                            isLoadingMoreMoviesLiveData.value = false
                        }
                    },
                    { error ->
                        run {
                            error.printStackTrace()
                            isLoadingMoreMoviesLiveData.value = false
                        }
                    }
                )
        )
    }

    private fun getCategoryMoviesObservableOnPage(category: String, page: Int): Observable<List<Movie>> =
        when(category) {
            CATEGORY_TITLE_TOP -> repository.getTopRatedOnPage(page)
            CATEGORY_TITLE_POPULAR -> repository.getPopularOnPage(page)
            CATEGORY_TITLE_UPCOMING -> repository.getUpcomingOnPage(page)
            CATEGORY_TITLE_TRAILERS -> repository.getTrailerMoviesOnPage(page)
            else -> Observable.just(listOf())
        }

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

    fun loadSearchFavoriteMovies(query: String) {
        categoryMoviesLiveData.value = Resource.Loading()
        compositeDisposable.add(
            repository.getSearchFavouriteMovies(query).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> categoryMoviesLiveData.value = Resource.Success(result.toMutableList()) },
                    { error -> categoryMoviesLiveData.value = Resource.Error(error.message ?: ERROR_COULD_NOT_LOAD_MOVIES_DEFAULT )}
                )
        )
    }

    fun loadSearchMovies(query: String) {
        categoryMoviesLiveData.value = Resource.Loading()
        compositeDisposable.add(
            repository.getSearchMovies(query).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> categoryMoviesLiveData.value = Resource.Success(result.toMutableList()) },
                    { error -> categoryMoviesLiveData.value = Resource.Error(error.message ?: ERROR_COULD_NOT_LOAD_MOVIES_DEFAULT) }
                )
        )
    }

    fun loadMoreSearchMoviesOnPage(query: String, page: Int) {
        isLoadingMoreMoviesLiveData.value = true
        compositeDisposable.add(
            repository.getSearchMoviesOnPage(query, page).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        categoryMoviesLiveData.value?.data?.let { movies ->
                            movies.addAll(result)
                            categoryMoviesLiveData.value = Resource.Success(movies)
                            isLoadingMoreMoviesLiveData.value = false
                        }
                    },
                    { error ->
                        run {
                            error.printStackTrace()
                            isLoadingMoreMoviesLiveData.value = false
                        }
                    }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}