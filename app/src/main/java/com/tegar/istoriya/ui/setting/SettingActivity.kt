package com.tegar.istoriya.ui.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import com.tegar.istoriya.viewmodels.ViewModelFactory
import com.tegar.istoriya.databinding.ActivitySettingBinding
import com.tegar.istoriya.ui.main.MainActivity
import com.tegar.istoriya.viewmodels.AuthViewModel

class SettingActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySettingBinding
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()

    }

    private fun setupAction(){
        binding.btnChangeLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            finishAffinity()

            startActivity(Intent(this, MainActivity::class.java))
        }
    }

}