package com.example.bloco_notas.models

import com.google.gson.annotations.SerializedName

data class Utilizador(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("data") val data: String
)
