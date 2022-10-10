package com.example.smartermovieapp.presentation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartermovieapp.data.responses.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class LoginVM @Inject constructor(): ViewModel() {
//TODO: inject repo

    private val compositeDisposable = CompositeDisposable()
    val isLoginSuccessful = MutableLiveData<Resource<Boolean>>(Resource.Loading())

    init {
        getLoginStatus()
    }

    private fun getLoginStatus(){
        //isLoginSuccessful.value = Resource.Loading()
        isLoginSuccessful.postValue(Resource.Success(true))
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}