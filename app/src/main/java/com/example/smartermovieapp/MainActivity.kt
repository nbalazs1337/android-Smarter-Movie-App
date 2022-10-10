package com.example.smartermovieapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.smartermovieapp.data.responses.Resource
import com.example.smartermovieapp.presentation.AuthVM
import com.example.smartermovieapp.presentation.LoginActivity
import com.example.smartermovieapp.presentation.home.HomeActivity
import dagger.android.AndroidInjection
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var authVM: AuthVM

    companion object {
        const val SPLASH_SCREEN_DURATION = 1500L
        const val USER_LOGGED_IN_DATA_FETCH_ERROR = "Something went wrong, please try again later"
        const val NEW_SPLASH_SCREEN_ANDROID_VERSION = 31
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isOldVersion = Build.VERSION.SDK_INT < NEW_SPLASH_SCREEN_ANDROID_VERSION
        splashScreen.setKeepOnScreenCondition { isOldVersion }
        setupIsUserLoggedInObserver()
    }

    private fun setupIsUserLoggedInObserver() =
        authVM.isUserLoggedIn.observe(this) {
            when(it) {
                is Resource.Success -> it.data?.let { isUserLoggedIn ->
                    if(isUserLoggedIn) {
                        startNextActivityWithTimer(HomeActivity::class.java)
                    } else {
                        startNextActivityWithTimer(LoginActivity::class.java)
                    }
                }
                is Resource.Error -> userLoggedInError()
                is Resource.Loading -> {}
            }
        }

    private fun <T> startNextActivityWithTimer(nextActivity: Class<T>) =
        Timer().schedule(SPLASH_SCREEN_DURATION) { startNextActivity(nextActivity) }

    private fun <T> startNextActivity(nextActivity: Class<T>) {
        val intent = Intent(this@MainActivity, nextActivity)
        startActivity(intent)
        finish()
    }

    private fun userLoggedInError() =
        Toast.makeText(this, USER_LOGGED_IN_DATA_FETCH_ERROR, Toast.LENGTH_LONG).show()

}
