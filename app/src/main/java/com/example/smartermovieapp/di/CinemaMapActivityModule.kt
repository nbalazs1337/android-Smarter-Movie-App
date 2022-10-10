package com.example.smartermovieapp.di

import com.example.smartermovieapp.presentation.cinema_map.CinemaMapActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class CinemaMapActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeCinemaMapActivity(): CinemaMapActivity

}