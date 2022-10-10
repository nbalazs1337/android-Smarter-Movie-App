package com.example.smartermovieapp.data.repository

import com.example.smartermovieapp.data.responses.BitCoinResponse
import io.reactivex.rxjava3.core.Observable

interface LocationRepository {

    fun getTrades(): Observable<BitCoinResponse>
}