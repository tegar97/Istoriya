package com.tegar.istoriya.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tegar.istoriya.data.api.response.LoginResult
import com.tegar.istoriya.data.pref.UserModel
import com.tegar.istoriya.data.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository : UserRepository) : ViewModel() {


    fun login(email : String , password: String) = repository.login(email,password)
    fun register(name: String , email: String,password: String) = repository.register(name,email,password)
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}