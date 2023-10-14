package com.tegar.istoriya.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.tegar.istoriya.data.api.ResultState
import com.tegar.istoriya.data.api.request.RegistrationRequest
import com.tegar.istoriya.data.api.response.RegisterResponse
import com.tegar.istoriya.databinding.ActivityRegisterBinding
import com.tegar.istoriya.utilities.Utils
import com.tegar.istoriya.viewmodels.AuthViewModel
import com.tegar.istoriya.viewmodels.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        binding.edtPassword.isPasswordInput = true
        binding.edtEmail.isEmailInput = true
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener { register() }
    }

    private fun register() {
        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        val registrationRequest = RegistrationRequest(name, email, password)
        Log.d("Registration request", registrationRequest.email)

        val intent = Intent(this, LoginActivity::class.java)

        viewModel.register(name, email, password).observe(this) { result ->
            handleRegistrationResult(result, intent)
        }
    }

    private fun handleRegistrationResult(result: ResultState<RegisterResponse>, intent: Intent) {
        when (result) {
            is ResultState.Loading -> Utils.showLoading(binding.progressIndicator,true)
            is ResultState.Success -> {
                result.data.message?.let { Utils.showToast(this, it) }
                Utils.showLoading(binding.progressIndicator,false)
                startActivity(intent)
            }
            is ResultState.Error -> {
                Utils.showToast(this, result.error)
                Utils.showLoading(binding.progressIndicator,false)
            }
        }
    }



}