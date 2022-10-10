package com.example.smartermovieapp.data.repository

import android.util.Log
import com.example.smartermovieapp.data.network.LocationApi
import com.example.smartermovieapp.data.responses.BitCoinResponse
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    val locationApi: LocationApi
) : LocationRepository {

    companion object {
        val TAG = LocationRepositoryImpl::class.java.simpleName
    }

    override fun getTrades(): Observable<BitCoinResponse> {
        return locationApi.getTrades().subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map { result ->
                Log.d(TAG, "Save in data base")
                result
            } // Save in db first
            .onErrorResumeNext { Observable.just(BitCoinResponse()) } // in case of error, return from db
    }
}