package com.example.bloco_notas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bloco_notas.autenticacao.Login
import com.example.bloco_notas.autenticacao.Registar
import com.example.bloco_notas.listaNotas.ListaNotas

class PaginaInicial : AppCompatActivity() {

    private lateinit var btnEntrar : Button
    private lateinit var btnRegistar : Button
    private lateinit var convidado : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagina_inicial)

        btnRegistar = findViewById(R.id.registo)
        btnEntrar = findViewById(R.id.entrar)
        convidado = findViewById(R.id.convidado)

        btnEntrar.setOnClickListener {
            startActivity(Intent(this@PaginaInicial, Login::class.java))
        }

        btnRegistar.setOnClickListener {
            startActivity(Intent(this@PaginaInicial, Registar::class.java))
        }

        convidado.setOnClickListener{
            startActivity(Intent(this@PaginaInicial, ListaNotas::class.java))
        }

    }
}