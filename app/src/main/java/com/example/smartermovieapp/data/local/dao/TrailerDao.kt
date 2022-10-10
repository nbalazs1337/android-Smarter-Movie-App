package com.example.smartermovieapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smartermovieapp.data.local.model.Trailer
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

@Dao
interface TrailerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrailer(trailer: Trailer): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTrailers(trailers: List<Trailer>): Completable

    @Query("SELECT * FROM table_trailer WHERE movieId = :movieId")
    fun getTrailerOfMovieId(movieId: Int): Observable<Trailer>

}