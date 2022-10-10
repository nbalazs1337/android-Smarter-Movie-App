package com.example.smartermovieapp.presentation.home

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.smartermovieapp.R
import com.example.smartermovieapp.presentation.AuthVM
import com.example.smartermovieapp.presentation.LoginActivity
import com.example.smartermovieapp.presentation.TransparentStatusBarHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.AndroidInjection
import javax.inject.Inject

class HomeActivity : AppCompatActivity(), NavigationBarVisibilityHandler {

    @Inject
    lateinit var authVM: AuthVM

    companion object {
        const val KEY_REDIRECT_TO_AVAILABILITY = "redirect_to_movie_availability_category"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        TransparentStatusBarHandler.initTransparentStatusBar(window)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigationView)
        bottomNavigationView.setupWithNavController(navController)

        val notification_bell: ImageButton = findViewById(R.id.btn_notifications)
        notification_bell.setOnClickListener {
            val notificationsDestinationId = R.id.fragmentNotificationScreen
            if (navController.currentDestination?.id != notificationsDestinationId)
                navController.navigate(R.id.fragmentNotificationScreen)
        }

        setupProfileMenuListener()
        setupUsername()

        if(intent.getBooleanExtra(KEY_REDIRECT_TO_AVAILABILITY, false)) {
            navController.popBackStack()
            startMovieAvailabilityFragment(navController)
        }
    }

    private fun startMovieAvailabilityFragment(navController: NavController) {
        val movieIdAvailability = intent.getIntExtra(KEY_MOVIE_ID_AVAILABILITY, -1)
        val bundle = Bundle().apply {
            putString("text", CATEGORY_TITLE_AVAILABILITY)
            putInt(KEY_MOVIE_ID_AVAILABILITY, movieIdAvailability)
        }
        navController.navigate(R.id.fragmentCategoryScreen, bundle)
    }

    private fun setupProfileMenuListener() =
        findViewById<ImageView>(R.id.iv_profile).setOnClickListener {
            val menu = PopupMenu(this, findViewById(R.id.iv_profile))
            menu.inflate(R.menu.profile_menu)
            menu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_sign_out -> {
                        authVM.signOut()
                        val loginIntent = Intent(this, LoginActivity::class.java)
                        startActivity(loginIntent)
                        true
                    }
                    else -> false
                }
            }
            menu.show()
        }

    private fun setupUsername() {
        val helloText = findViewById<TextView>(R.id.txt_hello)
        val name = authVM.username
        if (name != null && name != "null")
            helloText.text = "Hello, $name"
        else helloText.text = "Hello, ${authVM.getCurrentUser()?.displayName.toString()}"
    }

    override fun hideNavigationBarForFragmentCreation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigationView)
        bottomNavigationView.menu.forEach { it.isEnabled = false }
        bottomNavigationView.alpha = 0.0f
    }

    override fun showNavigationBarForFragmentDestroy() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigationView)
        bottomNavigationView.menu.forEach { it.isEnabled = true }
        bottomNavigationView.alpha = 1.0f
    }

}