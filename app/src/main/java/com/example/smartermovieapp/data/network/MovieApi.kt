package com.example.smartermovieapp.data.network
import android.net.Uri
import com.example.smartermovieapp.data.responses.movie_details.MovieDetailsResponse
import com.example.smartermovieapp.data.responses.popular.PopularResponse
import com.example.smartermovieapp.data.responses.search.SearchResponse
import com.example.smartermovieapp.data.responses.similar_movies.SimilarMoviesResponse
import com.example.smartermovieapp.data.responses.top_rated.TopRatedResponse
import com.example.smartermovieapp.data.responses.trailer.TrailerResponse
import com.example.smartermovieapp.data.responses.up_coming.UpcomingResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

    // Popular endpoint
    @GET("3/movie/popular")
    fun getPopular(): Observable<PopularResponse>

    // Top rated endpoint
    @GET("3/movie/top_rated")
    fun getTopRated(): Observable<TopRatedResponse>

    // Up coming endpoint
    @GET("3/movie/upcoming")
    fun getUpcoming(): Observable<UpcomingResponse>

    // Trailer endpoint
    @GET("3/movie/{movie_id}/videos")
    fun getTrailerOfMovieId(@Path(value = "movie_id", encoded = true) movieId: Int): Observable<TrailerResponse>

    // Movie details endpoint
    @GET("3/movie/{movie_id}")
    fun getDetailOfMovieId(@Path(value = "movie_id", encoded = true) movieId: Int): Observable<MovieDetailsResponse>

    @GET("3/movie/{movie_id}/similar")
    fun getSimilarMovies(@Path(value = "movie_id", encoded = true) movieId: Int): Observable<SimilarMoviesResponse>

    @GET("3/movie/popular")
    fun getPopularOnPage(
        @Query("page")
        page: Int
    ): Observable<PopularResponse>

    @GET("3/movie/top_rated")
    fun getTopRatedOnPage(
        @Query("page")
        page: Int
    ): Observable<TopRatedResponse>

    @GET("3/movie/upcoming")
    fun getUpcomingOnPage(
        @Query("page")
        page: Int
    ): Observable<UpcomingResponse>

    @GET("3/search/movie")
    fun getSearchMovies(
        @Query("query")
        query: Uri
    ): Observable<SearchResponse>

    @GET("3/search/movie")
    fun getSearchMoviesOnPage(
        @Query("query")
        query: Uri,
        @Query("page")
        page: Int
    ): Observable<SearchResponse>

}
