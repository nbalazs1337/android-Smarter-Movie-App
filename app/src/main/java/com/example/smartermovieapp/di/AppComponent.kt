package com.example.smartermovieapp.di

import com.example.smartermovieapp.SmarterMovieApp
import com.example.smartermovieapp.presentation.cinema_map.CinemaMapActivity
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Component(
    modules = [
        LocationSearchActivityModule::class,
        AndroidInjectionModule::class,
        LocationSearchFragmentModule::class,
        LoginActivityModule::class,
        RegisterActivityModule::class,
        MoviesDetailsActivityModule::class,
        MainActivityModule::class,
        RegisterActivityModule::class,
        HomeActivityModule::class,
        FragmentHomeScreenModule::class,
        FragmentCategoryScreenModule::class,
        FragmentCinemasModule::class,
        FragmentFavoritesScreenModule::class,
        CinemaDetailsActivityModule::class,
        CinemaMapActivityModule::class,
        AppModule::class]
)
@Singleton
interface AppComponent {
    fun inject(application: SmarterMovieApp)
}