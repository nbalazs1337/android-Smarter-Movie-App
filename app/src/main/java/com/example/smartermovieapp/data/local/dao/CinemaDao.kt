package com.example.smartermovieapp.data.local.dao

import androidx.room.*
import com.example.smartermovieapp.data.local.model.Cinema
import com.example.smartermovieapp.data.local.model.CinemaEntity
import com.example.smartermovieapp.data.local.model.relations.CinemaWithRunningMovies
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

@Dao
interface CinemaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCinema(cinemaEntity: CinemaEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCinemas(cinemas: List<CinemaEntity>): Completable

    @Transaction
    @Query("SELECT * FROM table_cinema WHERE id = :cinemaId")
    fun getCinemaWithRunningMovies(cinemaId: Int): Observable<CinemaWithRunningMovies>

    @Transaction
    @Query("SELECT * FROM table_cinema")
    fun getCinemasWithRunningMovies(): Observable<List<CinemaWithRunningMovies>>

    @Transaction
    @Query("SELECT * FROM table_cinema INNER JOIN table_running_movie " +
            "ON table_cinema.id = table_running_movie.cinemaId " +
            "WHERE table_running_movie.movieId = :movieId"
    )
    fun getCinemasThatRunMovieAlongRunningMovies(movieId: Int): Observable<List<CinemaWithRunningMovies>>

    @Query("SELECT * FROM table_cinema WHERE LOWER(table_cinema.name) LIKE '%' || LOWER(:query) || '%'")
    fun getSearchCinemasWithRunningMovies(query: String): Observable<List<CinemaWithRunningMovies>>

}