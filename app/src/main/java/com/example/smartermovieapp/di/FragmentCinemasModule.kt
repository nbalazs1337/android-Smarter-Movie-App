package com.example.smartermovieapp.di

import com.example.smartermovieapp.presentation.home.FragmentCategoryScreen
import com.example.smartermovieapp.presentation.home.FragmentCinemasScreen
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentCinemasModule {

    @ContributesAndroidInjector
    abstract fun contributeFragmentCinemasModule(): FragmentCinemasScreen
}