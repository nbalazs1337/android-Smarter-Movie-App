package com.example.smartermovieapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smartermovieapp.data.local.model.Category
import com.example.smartermovieapp.data.local.model.MovieCategorization
import io.reactivex.rxjava3.core.Completable

@Dao
interface MovieCategorizationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategorization(movieCategorization: MovieCategorization): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCategorizations(movieCategorizations: List<MovieCategorization>): Completable

    @Query("DELETE FROM table_category_and_movie WHERE category = :category")
    fun deleteCategorizationsOfCategory(category: Category): Completable

}