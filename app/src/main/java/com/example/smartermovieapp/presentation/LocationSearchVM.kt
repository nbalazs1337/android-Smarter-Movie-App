package com.example.smartermovieapp.presentation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartermovieapp.data.repository.LocationRepository
import com.example.smartermovieapp.data.responses.BitCoinResponse
import com.example.smartermovieapp.data.responses.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class LocationSearchVM @Inject constructor(
    private val repository: LocationRepository
) : ViewModel() {

    companion object {
        val TAG = LocationSearchVM::class.java.simpleName
    }

    private val compositeDisposable = CompositeDisposable()

    val tradesLiveData = MutableLiveData<Resource<BitCoinResponse>>()

    init {
        getTrades()
    }

    fun getTrades() {
        tradesLiveData.value = Resource.Loading()
        compositeDisposable.add(
            repository.getTrades().observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> tradesLiveData.value = Resource.Success(result) }, // onNext
                    { error -> tradesLiveData.value = Resource.Error(error.message ?: "") },
                    { Log.d(TAG, "Complete") }) // onComplete
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}