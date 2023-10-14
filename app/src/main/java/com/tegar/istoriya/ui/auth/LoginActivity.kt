package com.tegar.istoriya.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.tegar.istoriya.data.api.ResultState
import com.tegar.istoriya.data.api.request.LoginRequest
import com.tegar.istoriya.data.api.response.LoginResponse
import com.tegar.istoriya.ui.home.HomeActivity
import com.tegar.istoriya.viewmodels.ViewModelFactory
import com.tegar.istoriya.databinding.ActivityLoginBinding
import com.tegar.istoriya.utilities.Utils
import com.tegar.istoriya.viewmodels.AuthViewModel
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.edtEmail.isEmailInput = true
        binding.edtPassword.isPasswordInput = true
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener { login() }
        observeSession()
    }

    private fun observeSession() {
        viewModel.getSession().observe(this) { user ->
            if (user.token != "") {
                finishAffinity()
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
    }

    private fun login() {
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        val loginRequest = LoginRequest(email, password)
        Log.d("Login request", loginRequest.email)

        viewModel.login(email, password).observe(this) { result ->
            handleLoginResult(result)
        }
    }

    private fun handleLoginResult(result: ResultState<LoginResponse>) {
        when (result) {
            is ResultState.Loading ->  Utils.showLoading(binding.progressIndicator,true)
            is ResultState.Success -> {
                result.data.message?.let { Utils.showToast(this,it) }
                Utils.showLoading(binding.progressIndicator,false)
            }
            is ResultState.Error -> {
                Utils.showToast(this,result.error)
                Utils.showLoading(binding.progressIndicator,false)
            }
        }
    }


}