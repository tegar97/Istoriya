package com.tegar.istoriya.data.api.request

import com.google.gson.annotations.SerializedName

data class RegistrationRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
