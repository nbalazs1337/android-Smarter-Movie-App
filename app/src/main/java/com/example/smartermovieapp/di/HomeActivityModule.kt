package com.example.smartermovieapp.di

import com.example.smartermovieapp.presentation.home.HomeActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HomeActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeHomeActivity(): HomeActivity
}