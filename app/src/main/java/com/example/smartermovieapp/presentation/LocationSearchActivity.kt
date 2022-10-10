package com.example.smartermovieapp.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartermovieapp.R
import com.example.smartermovieapp.data.repository.LocationRepositoryImpl
import com.example.smartermovieapp.data.responses.Resource
import dagger.android.AndroidInjection
import javax.inject.Inject

class LocationSearchActivity : AppCompatActivity() {

    @Inject
    lateinit var vm: LocationSearchVM

    @Inject
    lateinit var authVM: AuthVM

    @Inject
    lateinit var locationRepositoryImpl: LocationRepositoryImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        // de adaugat in fiecare activitate, inainte de oncreate
        // - || - in fiecare fragment, in on attach
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_search)

        vm.tradesLiveData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(this, "success: ${it.data}", Toast.LENGTH_LONG).show()
                }
                is Resource.Error -> {
                    Toast.makeText(this, "error: ${it.message}", Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> {
                    Toast.makeText(this, "loading", Toast.LENGTH_LONG).show()

                }
            }
        }

        findViewById<Button>(R.id.btn_sign_out).setOnClickListener {
            authVM.signOut()
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

    }
}