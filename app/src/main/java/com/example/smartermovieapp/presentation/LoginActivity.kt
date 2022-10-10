package com.example.smartermovieapp.presentation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
import com.example.smartermovieapp.R
import com.example.smartermovieapp.data.responses.Resource
import com.example.smartermovieapp.presentation.home.HomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.android.AndroidInjection
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var authVM: AuthVM
    private lateinit var signInButton: AppCompatButton
    private lateinit var usernameEdit: EditText
    private lateinit var passwordEdit: EditText

    companion object {
        const val LOGIN_ERROR_MESSAGE = "Login failed. Please try again!"
        const val RC_SIGN_IN = 9001
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        TransparentStatusBarHandler.initTransparentStatusBar(window)
        setContentView(R.layout.activity_login)

        signInButton = findViewById(R.id.btn_sign_in)
        usernameEdit = findViewById(R.id.edit_username)
        passwordEdit = findViewById(R.id.edit_password)

        passwordEdit.addTextChangedListener {
            if (passwordEdit.text.isNotEmpty())
                passwordEdit.isActivated = true
        }

        usernameEdit.addTextChangedListener {
            if (usernameEdit.text.isNotEmpty())
                usernameEdit.isActivated = true
        }


        setupSignInListener()
        setupGoogleSignInListener()
        setupSignUpListener()

    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setupSignInListener() =
        signInButton.setOnClickListener {
            val username = usernameEdit.text.toString()
            val password = passwordEdit.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                signInError()
                return@setOnClickListener
            }

            authVM.loginEmail(username, password)

            authVM.userLiveData.observe(this) { authUser ->
                when (authUser) {
                    is Resource.Success -> {
                        if (authUser.data != null) {
                            startActivity(Intent(this, HomeActivity::class.java))
                        }
                    }
                    is Resource.Error -> signInError()
                    is Resource.Loading -> {}
                }
            }

        }

    private fun signInError() = Toast.makeText(this, LOGIN_ERROR_MESSAGE, Toast.LENGTH_SHORT).show()

    private fun setupGoogleSignInListener() =
        findViewById<AppCompatButton>(R.id.btn_google).setOnClickListener {
            val signInIntent = authVM.googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN)
            startHomeIfLoggedIn()
    }

    override fun onStart() {
        super.onStart()
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun startHomeIfLoggedIn() {
        authVM.getIsUserLoggedIn()
        authVM.isUserLoggedIn.observe(this) {
            when (it) {
                is Resource.Success -> it.data?.let { isUserLoggedIn ->
                    if (isUserLoggedIn) {
                        startActivity(Intent(this, HomeActivity::class.java))
                    }
                }
                is Resource.Error -> signInError()
                is Resource.Loading -> {}
            }
        }
    }

    private fun setupSignUpListener() =
        findViewById<TextView>(R.id.txt_sign_up).setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }

}