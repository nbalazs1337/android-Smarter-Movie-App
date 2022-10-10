package com.example.smartermovieapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smartermovieapp.data.local.model.RunningMovie
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

@Dao
interface RunningMovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRunningMovie(runningMovie: RunningMovie): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllRunningMovies(runningMovies: List<RunningMovie>): Completable

}