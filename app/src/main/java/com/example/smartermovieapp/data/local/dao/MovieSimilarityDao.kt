package com.example.smartermovieapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.smartermovieapp.data.local.model.MovieSimilarity
import io.reactivex.rxjava3.core.Completable

@Dao
interface MovieSimilarityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSimilarity(movieSimilarity: MovieSimilarity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllSimilarities(movieSimilarities: List<MovieSimilarity>): Completable

}