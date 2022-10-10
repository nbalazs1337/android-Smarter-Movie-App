package com.example.smartermovieapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast

@SuppressLint("MissingPermission")
fun hasInternet(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    } else try {
        val networkInfo = connectivityManager.activeNetworkInfo
        networkInfo != null && networkInfo.isConnected
    } catch (e: NullPointerException) {
        e.printStackTrace()
        false
    }
}

const val ERROR_CHECK_INTERNET = "Please check your internet connection."

fun showInternetError(context: Context) =
    Toast.makeText(context, ERROR_CHECK_INTERNET, Toast.LENGTH_SHORT).show()