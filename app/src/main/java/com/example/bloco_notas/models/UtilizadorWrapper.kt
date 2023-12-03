package com.example.bloco_notas.models

import com.google.gson.annotations.SerializedName

data class UtilizadorWrapper(
    @SerializedName("utilizador") val utilizador: Utilizador
)
