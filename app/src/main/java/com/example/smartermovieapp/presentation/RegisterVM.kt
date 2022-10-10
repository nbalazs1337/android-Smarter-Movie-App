package com.example.smartermovieapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartermovieapp.data.responses.Resource
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class RegisterVM @Inject constructor(): ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    val isRegisterSuccessful = MutableLiveData<Resource<Boolean>>(Resource.Loading())

    init {
        getRegisterStatus()
    }

    private fun getRegisterStatus(){
        isRegisterSuccessful.postValue(Resource.Success(true))
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}