package com.tegar.istoriya.ui.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.tegar.istoriya.R
import com.tegar.istoriya.ui.home.HomeActivity
import com.tegar.istoriya.ui.main.MainActivity
import com.tegar.istoriya.viewmodels.AuthViewModel
import com.tegar.istoriya.viewmodels.ViewModelFactory

class SplashActivity : AppCompatActivity() {
    private val splashDelay : Long  = 2000
    private val authViewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            observeSession()
        }, splashDelay)
    }

    private fun observeSession() {
        authViewModel.getSession().observe(this) { user ->
            if (user.token.isNotEmpty()) {
                startHomeActivity()
            }else{
                startOnBoardingActivity()
            }
        }
    }

    private fun startOnBoardingActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun startHomeActivity(){
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}