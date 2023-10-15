package com.tegar.istoriya.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import com.tegar.istoriya.R
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
        setContentView(binding.root)
        setForm()
        setUpAction()
        observeSession()
        setButtonStatus()
    }

    private fun setUpAction(){
        binding.btnLogin.setOnClickListener { login() }
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
    private fun setForm() {
        binding.edtEmail.isEmailInput = true
        binding.edtPassword.isPasswordInput = true
    }

    private fun setButtonStatus() {
        setMyButtonEnable()
        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun setMyButtonEnable() {
        val email = binding.edtEmail.text?.toString().orEmpty()
        val password = binding.edtPassword.text?.toString().orEmpty()

        binding.btnLogin.isEnabled = email.isNotEmpty() && password.isNotEmpty()
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
            is ResultState.Loading -> {
                binding.btnLogin.isEnabled = false
                binding.btnLogin.text = getString(R.string.loading_text)
                Utils.showLoading(binding.progressIndicator, true)
            }

            is ResultState.Success -> {
                result.data.message?.let { Utils.showToast(this, it) }
                Utils.showLoading(binding.progressIndicator, false)
                binding.btnLogin.isEnabled = true
                binding.btnLogin.text = getString(R.string.btn_login_text)


            }

            is ResultState.Error -> {
                Utils.showToast(this, result.error)
                Utils.showLoading(binding.progressIndicator, false)
                binding.btnLogin.isEnabled = true
                binding.btnLogin.text = getString(R.string.btn_login_text)

            }
        }
    }


}