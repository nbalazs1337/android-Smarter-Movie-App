package com.example.smartermovieapp.di

import android.app.Application
import android.content.Context
import com.example.smartermovieapp.data.local.dao.*
import com.example.smartermovieapp.data.local.database.MoviesDatabase
import com.example.smartermovieapp.data.network.LocationApi
import com.example.smartermovieapp.data.network.MovieApi
import com.example.smartermovieapp.data.network.RemoteDataSource
import com.example.smartermovieapp.data.repository.LocationRepository
import com.example.smartermovieapp.data.repository.LocationRepositoryImpl
import com.example.smartermovieapp.data.repository.MoviesRepository
import com.example.smartermovieapp.data.repository.MoviesRepositoryImpl
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


// provide la instantele de care avem nevoie
@Module
class AppModule(private val application: Application) {

    @Provides
    @Singleton
    fun providesApplication(): Application = application

    @Provides
    @Singleton
    fun provideContext(): Context = application.applicationContext

    @Singleton
    @Provides
    fun providesLocationApi(remoteDataSource: RemoteDataSource, context: Context): LocationApi {
        return remoteDataSource.buildApi(LocationApi::class.java, context)
    }

    @Singleton
    @Provides
    fun providesMovieApi(remoteDataSource: RemoteDataSource, context: Context): MovieApi =
        remoteDataSource.buildApi(MovieApi::class.java, context)

    @Singleton
    @Provides
    fun providesLocationRepository(locationApi: LocationApi): LocationRepository {
        return LocationRepositoryImpl(locationApi)
    }

    @Singleton
    @Provides
    fun providesGson() = Gson()

    @Singleton
    @Provides
    fun providesMoviesDatabase(context: Context): MoviesDatabase = MoviesDatabase.getInstance(context)

    @Singleton
    @Provides
    fun providesMovieDao(moviesDatabase: MoviesDatabase): MovieDao = moviesDatabase.movieDao

    @Singleton
    @Provides
    fun providesTrailerDao(moviesDatabase: MoviesDatabase): TrailerDao = moviesDatabase.trailerDao

    @Singleton
    @Provides
    fun providesCategoryAndMovieIdDao(moviesDatabase: MoviesDatabase): MovieCategorizationDao =
        moviesDatabase.movieCategorizationDao

    @Singleton
    @Provides
    fun providesMovieSimilarityDao(moviesDatabase: MoviesDatabase): MovieSimilarityDao =
        moviesDatabase.movieSimilarityDao

    @Singleton
    @Provides
    fun providesFavouriteDao(moviesDatabase: MoviesDatabase): FavouriteDao =
        moviesDatabase.favouriteDao

    @Singleton
    @Provides
    fun providesCinemaDao(moviesDatabase: MoviesDatabase): CinemaDao =
        moviesDatabase.cinemaDao

    @Singleton
    @Provides
    fun providesRunningMovieDao(moviesDatabase: MoviesDatabase): RunningMovieDao =
        moviesDatabase.runningMovieDao

    @Singleton
    @Provides
    fun providesMoviesRepository(
        movieApi: MovieApi,
        movieDao: MovieDao,
        trailerDao: TrailerDao,
        movieCategorizationDao: MovieCategorizationDao,
        movieSimilarityDao: MovieSimilarityDao,
        favouriteDao: FavouriteDao,
        cinemaDao: CinemaDao,
        runningMovieDao: RunningMovieDao
    ): MoviesRepository =
        MoviesRepositoryImpl(movieApi, movieDao, trailerDao, movieCategorizationDao, movieSimilarityDao, favouriteDao, cinemaDao, runningMovieDao)
}