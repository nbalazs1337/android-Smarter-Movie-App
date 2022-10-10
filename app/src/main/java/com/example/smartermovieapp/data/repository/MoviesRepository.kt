package com.example.smartermovieapp.data.repository

import com.example.smartermovieapp.data.local.model.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface MoviesRepository {

    fun getPopular(): Observable<List<Movie>>

    fun getTopRated(): Observable<List<Movie>>

    fun getUpcoming(): Observable<List<Movie>>

    fun getTrailerMovies(): Observable<List<Movie>>

    fun getTrailerOfMovieId(movieId: Int): Observable<Trailer>

    fun getMovie(movieId: Int): Observable<Movie>

    fun getPopularOnPage(page: Int): Observable<List<Movie>>

    fun getTopRatedOnPage(page: Int): Observable<List<Movie>>

    fun getUpcomingOnPage(page: Int): Observable<List<Movie>>

    fun getTrailerMoviesOnPage(page: Int): Observable<List<Movie>>
    
    fun getFavourites(): Observable<List<Favourite>>

    fun insertFavourite(favourite: Favourite): Completable

    fun removeFavouriteMovie(movieId: Int): Completable

    fun isFavourite(movieId: Int): Observable<Boolean>

    fun getSimilarMovies(movieId: Int): Observable<List<Movie>>

    fun getFavouriteMovies(): Observable<List<Movie>>

    fun getSearchFavouriteMovies(query: String): Observable<List<Movie>>

    fun getMoviesOfIds(movieIds: List<Int>): Observable<List<Movie>>

    fun getSearchMovies(query: String): Observable<List<Movie>>

    fun getSearchMoviesOnPage(query: String, page: Int): Observable<List<Movie>>

    fun insertCinemas(cinemas: List<Cinema>): Completable

    fun getCinemas(): Observable<List<Cinema>>

    fun getCinema(cinemaId: Int): Observable<Cinema>

    fun getCinemasRunningMovie(movieId: Int): Observable<List<Cinema>>

    fun getSearchCinemas(query: String): Observable<List<Cinema>>

}