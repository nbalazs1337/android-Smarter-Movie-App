package com.example.smartermovieapp.data.repository

import androidx.lifecycle.MutableLiveData
import com.example.smartermovieapp.data.responses.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {

    fun loginEmail(email: String, password: String): MutableLiveData<Resource<FirebaseUser>>
    fun registerEmail(email: String, password: String, username: String): MutableLiveData<Resource<FirebaseUser>>
    fun loginGoogle(googleAuthCredential: AuthCredential): MutableLiveData<Resource<FirebaseUser>>
    fun isUserLoggedIn(): Boolean
    fun signOut()

}