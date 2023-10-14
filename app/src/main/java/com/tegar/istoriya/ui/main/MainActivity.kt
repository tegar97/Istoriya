package com.tegar.istoriya.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
