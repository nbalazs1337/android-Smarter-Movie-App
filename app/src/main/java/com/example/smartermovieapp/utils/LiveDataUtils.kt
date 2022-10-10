package com.example.smartermovieapp.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.smartermovieapp.data.responses.Resource

fun <T> MutableLiveData<Resource<T>>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<Resource<T>>) {
    observe(lifecycleOwner, object : Observer<Resource<T>> {
        override fun onChanged(t: Resource<T>?) {
            observer.onChanged(t)
            val isNotLoading = t !is Resource.Loading
            if(isNotLoading)
                removeObserver(this)
        }
    })
}