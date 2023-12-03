package com.example.bloco_notas.autenticacao

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.bloco_notas.R

class Login : AppCompatActivity() {

    // URL da API
    val url: String = "https://script.google.com/macros/s/AKfycbyqcurpSI4RHz4gyChcU-Kz3vjclwNizphM7ou8q_Pc-PvhplWKaWve6IrwjDAQseZs/exec"

    // Variáveis de layout
    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var logoutButton: Button
    private lateinit var mostraUtilizadorEToken: Button
    private lateinit var mudarParaRegistoButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Utilizador.init(applicationContext)
        //TokenManager.init(applicationContext)

        // ID's dos elementos
        loginEmail = findViewById(R.id.loginEmail)
        loginPassword = findViewById(R.id.loginPassword)
        loginButton = findViewById(R.id.loginButton)
        logoutButton = findViewById(R.id.logoutButton)
        mostraUtilizadorEToken = findViewById(R.id.mostraUtilizadorEToken)
        mudarParaRegistoButton = findViewById(R.id.mudarParaRegistoButton)

        loginButton.setOnClickListener {

        }

        logoutButton.setOnClickListener {

        }

        mostraUtilizadorEToken.setOnClickListener {

        }

        mudarParaRegistoButton.setOnClickListener {
            startActivity(Intent(this@Login, Registar::class.java))
        }

    }



}