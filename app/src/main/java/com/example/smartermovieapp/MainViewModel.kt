package com.example.smartermovieapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartermovieapp.data.responses.Resource
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class MainViewModel @Inject constructor(
    // TODO inject repository for login info
): ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val isUserLoggedIn = MutableLiveData<Resource<Boolean>>(Resource.Loading())

    init {
        getIsUserLoggedIn()
    }

    private fun getIsUserLoggedIn() {
        // TODO - implement function

        // Test:
        isUserLoggedIn.postValue(Resource.Success(true))
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}