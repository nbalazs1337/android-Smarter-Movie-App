package com.example.smartermovieapp.di

import com.example.smartermovieapp.presentation.LocationSearchFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class LocationSearchFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeLocationSearchFragment(): LocationSearchFragment
}