package com.example.smartermovieapp.presentation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
import com.example.smartermovieapp.R
import com.example.smartermovieapp.data.responses.Resource
import com.example.smartermovieapp.presentation.home.HomeActivity
import dagger.android.AndroidInjection
import javax.inject.Inject

class RegisterActivity : AppCompatActivity() {

    @Inject
    lateinit var authVM: AuthVM

    lateinit var usernameText: EditText
    lateinit var emailText: EditText
    lateinit var passwordText: EditText
    lateinit var confirmPasswordText: EditText


    companion object {
        const val DEFAULT_REGISTER_ERROR_MESSAGE = "Register failed. Please try again!"
        const val RC_SIGN_UP = 9002
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        TransparentStatusBarHandler.initTransparentStatusBar(window)
        setContentView(R.layout.activity_register)

        usernameText = findViewById(R.id.edit_username)
        emailText = findViewById(R.id.edit_email)
        passwordText = findViewById(R.id.edit_password)
        confirmPasswordText = findViewById(R.id.edit_confirm_password)

        passwordText.addTextChangedListener {
            if (passwordText.text.isNotEmpty())
                passwordText.isActivated = true
        }

        usernameText.addTextChangedListener {
            if (usernameText.text.isNotEmpty())
                usernameText.isActivated = true
        }
        emailText.addTextChangedListener {
            if (emailText.text.isNotEmpty())
                emailText.isActivated = true
        }

        confirmPasswordText.addTextChangedListener {
            if (confirmPasswordText.text.isNotEmpty())
                confirmPasswordText.isActivated = true
        }

        setupRegisterListener()
        setupGoogleRegisterListener()
        setupSignInListener()

    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setupRegisterListener() =
        findViewById<AppCompatButton>(R.id.btn_sign_up).setOnClickListener {
            val username = usernameText.text.toString()
            val email = emailText.text.toString()
            val password = passwordText.text.toString()
            val confirmPassword = confirmPasswordText.text.toString()
            if (username.isNotEmpty() && email.isNotEmpty() && password == confirmPassword) {
                authVM.registerEmail(email, password, username)
                authVM.userLiveData.observe(this) {
                    when (it) {
                        is Resource.Success -> {
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        }
                        is Resource.Error -> {
                            if (it.message != null && it.message.isNotEmpty())
                                registerError(it.message)
                            else
                                registerError(DEFAULT_REGISTER_ERROR_MESSAGE)
                        }
                        is Resource.Loading -> {}
                    }
                }
            } else {
                registerError(DEFAULT_REGISTER_ERROR_MESSAGE)
            }
        }

    private fun registerError(errorMessage: String) =
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()

    private fun setupGoogleRegisterListener() =
        findViewById<AppCompatButton>(R.id.btn_google).setOnClickListener {
            val signInIntent = authVM.googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_UP)
        }

    private fun setupSignInListener() =
        findViewById<TextView>(R.id.txt_sign_in).setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_UP)
            startHomeIfLoggedIn()
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
                is Resource.Error -> registerError(DEFAULT_REGISTER_ERROR_MESSAGE)
                is Resource.Loading -> {}
            }
        }
    }

}