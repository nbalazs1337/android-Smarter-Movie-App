package com.example.smartermovieapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smartermovieapp.data.local.model.Favourite
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

@Dao
interface FavouriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavourite(favourite: Favourite): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllFavourites(favourites: List<Favourite>): Completable

    @Query("DELETE FROM favourites WHERE movieId = :movieId")
    fun deleteFavourite(movieId: Int): Completable

    @Query("SELECT * FROM favourites")
    fun getAllFavourites(): Observable<List<Favourite>>

    @Query("SELECT EXISTS(SELECT * FROM favourites WHERE movieId = :movieId)")
    fun isFavourite(movieId: Int): Observable<Boolean>
}