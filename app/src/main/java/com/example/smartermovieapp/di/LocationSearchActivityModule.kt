package com.example.smartermovieapp.di

import com.example.smartermovieapp.presentation.LocationSearchActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class LocationSearchActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeLocationSearchActivity(): LocationSearchActivity
}