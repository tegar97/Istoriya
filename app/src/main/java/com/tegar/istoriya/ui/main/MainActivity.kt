package com.tegar.istoriya.ui.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.tegar.istoriya.ui.home.HomeActivity
import com.tegar.istoriya.ui.auth.RegisterActivity
import com.tegar.istoriya.viewmodels.ViewModelFactory
import com.tegar.istoriya.databinding.ActivityMainBinding
import com.tegar.istoriya.ui.auth.LoginActivity
import com.tegar.istoriya.viewmodels.AuthViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val authViewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupClickListeners()
        observeSession()
        playAnimation()

    
    }
    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.imgOnboardingIllustration, View.TRANSLATION_X, -10f, 10f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val appName = ObjectAnimator.ofFloat(binding.tvWelcome, View.ALPHA, 1f).setDuration(1000)
        val desc = ObjectAnimator.ofFloat(binding.tvDescription,View.ALPHA,1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin,View.ALPHA,1f).setDuration(400)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister,View.ALPHA,1f).setDuration(400)

        AnimatorSet().apply {
            playSequentially(appName,desc, btnLogin, btnRegister)
            start()
        }
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observeSession() {
        authViewModel.getSession().observe(this) { user ->
            if (user.token.isNotEmpty()) {
                startHomeActivity()
            }
        }
    }

    private fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
