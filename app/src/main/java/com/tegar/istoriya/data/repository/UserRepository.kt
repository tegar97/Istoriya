package com.tegar.istoriya.data.repository


import android.util.Log
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.tegar.istoriya.data.api.ResultState
import com.tegar.istoriya.data.api.request.LoginRequest
import com.tegar.istoriya.data.api.request.RegistrationRequest
import com.tegar.istoriya.data.api.response.LoginResponse
import com.tegar.istoriya.data.api.response.RegisterResponse
import com.tegar.istoriya.data.api.retrofit.ApiService
import com.tegar.istoriya.data.pref.UserModel
import com.tegar.istoriya.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    fun login(email: String, password: String) = liveData {
        emit(ResultState.Loading)
        val requestBody = LoginRequest(email, password)
        try {
            val loginResponse = apiService.login(
                requestBody
            )
            loginResponse?.message?.let { Log.d("Response", it) }
            val emailData: String? = loginResponse.loginResult?.userId
            val nameData: String? = loginResponse.loginResult?.name
            val tokenData: String? = loginResponse.loginResult?.token
            if (loginResponse.loginResult != null && emailData != null && nameData != null && tokenData != null) {
                val userData = UserModel(
                    emailData,
                    nameData,
                    tokenData
                )
                saveSession(userData)
                emit(ResultState.Success(loginResponse))
            }

        } catch (e: HttpException) {

            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            emit(ResultState.Error(errorResponse.message))

        }

    }

    fun register(name: String, email: String, password: String) = liveData {
        emit(ResultState.Loading)
        val requestBody = RegistrationRequest(name, email, password)

        try {
            val loginResponse = apiService.register(
                requestBody
            )
            emit(ResultState.Success(loginResponse))

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
            emit(ResultState.Error(errorResponse.message))
        }

    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService,userPreference)
            }.also { instance = it }
    }
}