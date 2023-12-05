package com.example.bloco_notas.models

data class LoginResponse(
    val id: String,
    val data: String,
    val email: String,
    val token: String,
)
