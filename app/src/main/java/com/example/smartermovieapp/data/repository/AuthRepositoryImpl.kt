package com.example.smartermovieapp.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.example.smartermovieapp.data.responses.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() :
    AuthRepository {


    var auth: FirebaseAuth = Firebase.auth
    var userLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()


    init {
        if (auth.currentUser != null) {
            userLiveData.postValue(auth.currentUser)
        }
    }


    override fun loginEmail(
        email: String,
        password: String
    ): MutableLiveData<Resource<FirebaseUser>> {
        val authUserLiveData: MutableLiveData<Resource<FirebaseUser>> = MutableLiveData()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        authUserLiveData.value = Resource.Success(firebaseUser)
                    }
                } else {
                    authUserLiveData.value = task.exception?.message?.let {
                        Resource.Error(it)
                    }
                }
            }
        return authUserLiveData
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun registerEmail(
        email: String,
        password: String,
        username: String
    ): MutableLiveData<Resource<FirebaseUser>> {
        val authUserLiveData: MutableLiveData<Resource<FirebaseUser>> = MutableLiveData()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = username
                    }
                    if (firebaseUser != null) {
                        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                Log.d("USER PROFILE", "User profile updated.")
                                authUserLiveData.postValue(Resource.Success(firebaseUser))
                            }
                        }

                    } else {
                        authUserLiveData.postValue(Resource.Error(""))
                    }
                } else {
                    authUserLiveData.postValue(Resource.Error(task.exception?.message ?: ""))
                }
            }
        return authUserLiveData
    }

    override fun loginGoogle(googleAuthCredential: AuthCredential): MutableLiveData<Resource<FirebaseUser>> {
        val authUserLiveData: MutableLiveData<Resource<FirebaseUser>> = MutableLiveData()
        auth.signInWithCredential(googleAuthCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser = auth.currentUser
                if (firebaseUser != null)
                    authUserLiveData.value = Resource.Success(firebaseUser)
            } else {
                authUserLiveData.value = task.exception?.message?.let {
                    Resource.Error(it)
                }
            }
        }
        return authUserLiveData
    }

    override fun isUserLoggedIn(): Boolean = auth.currentUser != null

    override fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser

}