package com.example.smartermovieapp.di

import com.example.smartermovieapp.presentation.home.FragmentCategoryScreen
import com.example.smartermovieapp.presentation.home.FragmentFavoritesScreen
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentFavoritesScreenModule {
    @ContributesAndroidInjector
    abstract fun contributeFragmentFavouriteScreenModule(): FragmentFavoritesScreen
}