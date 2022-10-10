package com.example.smartermovieapp.presentation

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartermovieapp.data.repository.AuthRepositoryImpl
import com.example.smartermovieapp.data.responses.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class AuthVM @Inject constructor(
    val repository: AuthRepositoryImpl, private val application: Application
) : ViewModel() {

    var userLiveData: MutableLiveData<Resource<FirebaseUser>> = MutableLiveData()
    var createdUserLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    val isUserLoggedIn = MutableLiveData<Resource<Boolean>>(Resource.Loading())
    lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    var username: String? = ""

    init {
        initGoogleSignIn()
        getIsUserLoggedIn()
    }

    fun getIsUserLoggedIn() {
        val isFirebaseLoggedIn = repository.isUserLoggedIn()
        val isGoogleLoggedIn = GoogleSignIn.getLastSignedInAccount(application.applicationContext) != null
        username = GoogleSignIn.getLastSignedInAccount(application.applicationContext)?.displayName.toString()
        val isLoggedIn = isFirebaseLoggedIn || isGoogleLoggedIn
        isUserLoggedIn.postValue(Resource.Success(isLoggedIn))
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun loginEmail(email: String, password: String) {
        userLiveData = repository.loginEmail(email, password)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun registerEmail(email: String, password: String, username: String) {
        userLiveData = repository.registerEmail(email, password, username)
    }

    fun loginGoogle(authCredential: AuthCredential){
        userLiveData = repository.loginGoogle(authCredential)
    }

    private fun initGoogleSignIn() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(application.applicationContext, gso)
    }

    fun signOut(){
        repository.auth.signOut()
        googleSignInClient.signOut()
    }

    fun getCurrentUser() = repository.getCurrentUser()




}