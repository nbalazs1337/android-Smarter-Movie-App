package com.example.smartermovieapp.di

import com.example.smartermovieapp.presentation.cinema_details.CinemaDetailsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class CinemaDetailsActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeCinemaDetailsActivity(): CinemaDetailsActivity

}