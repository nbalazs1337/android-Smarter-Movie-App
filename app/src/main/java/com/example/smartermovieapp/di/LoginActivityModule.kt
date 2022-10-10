package com.example.smartermovieapp.di

import com.example.smartermovieapp.presentation.LoginActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class LoginActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeLoginActivity(): LoginActivity
}