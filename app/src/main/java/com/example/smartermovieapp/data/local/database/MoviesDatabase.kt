package com.example.smartermovieapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.smartermovieapp.data.local.dao.*
import com.example.smartermovieapp.data.local.model.*

@Database(
    entities = [
        Movie::class,
        Trailer::class,
        MovieCategorization::class,
        MovieSimilarity::class,
        Favourite::class,
        CinemaEntity::class,
        RunningMovie::class
    ],
    version = 5
)
abstract class MoviesDatabase : RoomDatabase() {

    abstract val movieDao: MovieDao
    abstract val trailerDao: TrailerDao
    abstract val movieCategorizationDao: MovieCategorizationDao
    abstract val movieSimilarityDao: MovieSimilarityDao
    abstract val favouriteDao: FavouriteDao
    abstract val cinemaDao: CinemaDao
    abstract val runningMovieDao: RunningMovieDao

    companion object {
        @Volatile
        private var INSTANCE: MoviesDatabase? = null

        fun getInstance(context: Context): MoviesDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MoviesDatabase::class.java,
                    "movies_database"
                )
                    .fallbackToDestructiveMigration()
                    .build().also {
                        INSTANCE = it
                    }
            }
        }

    }

}