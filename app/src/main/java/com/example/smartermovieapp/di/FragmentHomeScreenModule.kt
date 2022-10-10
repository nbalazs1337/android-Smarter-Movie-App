package com.example.smartermovieapp.di

import com.example.smartermovieapp.presentation.home.FragmentHomeScreen
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentHomeScreenModule {

    @ContributesAndroidInjector
    abstract fun contributeFragmentHomeScreen(): FragmentHomeScreen
}