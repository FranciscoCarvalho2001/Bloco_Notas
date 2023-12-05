package com.example.bloco_notas.models

import com.google.gson.annotations.SerializedName

class Nota (
    @SerializedName("emailUtilizador") val emailUtilizador: String,
    @SerializedName("idNota") val idNota: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descricao") val descricao: String,
    @SerializedName("data") val data : String,
    @SerializedName("id") val id : String?
)