package com.example.smartermovieapp.di

import com.example.smartermovieapp.presentation.movies_details.MoviesDetailsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MoviesDetailsActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMoviesDetailsActivity(): MoviesDetailsActivity

}