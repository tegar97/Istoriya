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
        setForm()
        setContentView(binding.root)
        setUpAction()
        buttonListener()
    }
    private fun setForm() {
        binding.edtEmail.isEmailInput = true
        binding.edtPassword.isPasswordInput = true
    }
    private fun buttonListener() {
        setMyButtonEnable()
        binding.edtName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })
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

    private fun setUpAction(){
        binding.btnRegister.setOnClickListener { register() }
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
    }

    private fun setMyButtonEnable() {
        val email = binding.edtEmail.text?.toString().orEmpty()
        val password = binding.edtPassword.text?.toString().orEmpty()
        val name = binding.edtPassword.text?.toString().orEmpty()

        binding.btnRegister.isEnabled = email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()
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
            is ResultState.Loading -> {
                binding.btnRegister.isEnabled = false
                binding.btnRegister.text = getString(R.string.loading_text)
                Utils.showLoading(binding.progressIndicator,true)
            }
            is ResultState.Success -> {
                result.data.message.let { Utils.showToast(this, it) }
                Utils.showLoading(binding.progressIndicator,false)
                binding.btnRegister.isEnabled = false
                binding.btnRegister.text = getString(R.string.btn_register_text)
                startActivity(intent)
            }
            is ResultState.Error -> {
                Utils.showToast(this, result.error)
                binding.btnRegister.isEnabled = false
                binding.btnRegister.text = getString(R.string.btn_register_text)
                Utils.showLoading(binding.progressIndicator,false)
            }
        }
    }



}