package com.example.smartermovieapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smartermovieapp.data.local.model.Category
import com.example.smartermovieapp.data.local.model.Movie
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: Movie): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMovies(movies: List<Movie>): Completable

    @Query("SELECT * FROM table_movie WHERE id = :movieId LIMIT 1")
    fun getMovie(movieId: Int): Observable<Movie>

    @Query("SELECT * FROM table_movie INNER JOIN table_category_and_movie " +
            "ON table_movie.id = table_category_and_movie.movieId " +
            "WHERE table_category_and_movie.category = :category " +
            "ORDER BY popularity DESC"
    )
    fun getMoviesOfCategory(category: Category): Observable<List<Movie>>

    @Query("SELECT * FROM table_movie INNER JOIN table_category_and_movie " +
            "ON table_movie.id = table_category_and_movie.movieId " +
            "WHERE table_category_and_movie.category = :category " +
            "ORDER BY score DESC"
    )
    fun getTopRatedMovies(category: Category = Category.TopRated): Observable<List<Movie>>

    @Query("SELECT * FROM table_movie INNER JOIN favourites " +
            "ON table_movie.id = favourites.movieId "
    )
    fun getFavouriteMovies(): Observable<List<Movie>>

    @Query("SELECT * FROM table_movie INNER JOIN favourites " +
            "ON table_movie.id = favourites.movieId " +
            "WHERE LOWER(table_movie.title) LIKE '%' || LOWER(:query) || '%'"
    )
    fun getSearchFavouriteMovies(query: String): Observable<List<Movie>>

    @Query("SELECT * FROM table_movie WHERE id IN (:ids)")
    fun getMoviesWithIds(ids: List<Int>): Observable<List<Movie>>

    @Query("SELECT * FROM table_movie WHERE LOWER(table_movie.title) LIKE '%' || LOWER(:query) || '%' LIMIT 20")
    fun getSearchMovies(query: String): Observable<List<Movie>>

    @Query("SELECT * FROM table_movie INNER JOIN table_similar_movies " +
            "ON table_movie.id = table_similar_movies.similarMovieId " +
            "WHERE table_similar_movies.movieId = :movieId AND id != :movieId " +
            "ORDER BY popularity DESC LIMIT 20"
    )
    fun getSimilarMovies(movieId: Int): Observable<List<Movie>>

}