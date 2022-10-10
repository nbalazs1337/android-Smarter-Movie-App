package com.example.smartermovieapp.di

import com.example.smartermovieapp.presentation.home.FragmentCategoryScreen
import com.example.smartermovieapp.presentation.home.FragmentHomeScreen
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentCategoryScreenModule {

    @ContributesAndroidInjector
    abstract fun contributeFragmentCategoryScreenModule(): FragmentCategoryScreen
}