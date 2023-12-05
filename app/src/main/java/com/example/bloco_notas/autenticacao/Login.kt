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
import com.example.bloco_notas.storage.API

class Login : AppCompatActivity() {

    // Variáveis de layout
    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var logoutButton: Button
    private lateinit var mostraUtilizadorEToken: Button
    private lateinit var mudarParaRegistoButton: Button
    private lateinit var api : API

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        api = API()

        // inicializa sharedPreferences dos objetos
        UtilizadorManager.init(applicationContext)
        TokenManager.init(applicationContext)

        // ID's dos elementos
        loginEmail = findViewById(R.id.loginEmail)
        loginPassword = findViewById(R.id.loginPassword)
        loginButton = findViewById(R.id.loginButton)
        logoutButton = findViewById(R.id.logoutButton)
        mostraUtilizadorEToken = findViewById(R.id.mostraUtilizadorEToken)
        mudarParaRegistoButton = findViewById(R.id.mudarParaRegistoButton)

        loginButton.setOnClickListener {
            val email = loginEmail.text.toString().trim()
            val password = loginPassword.text.toString().trim()
            api.loginUtilizadorAPI(email, password, this@Login)
        }

        logoutButton.setOnClickListener {
            val token = TokenManager.buscarToken().toString()
            val email = UtilizadorManager.buscarEMAIL().toString()
            api.logoutUtilizadorAPI(token, email, this@Login)
        }

        mostraUtilizadorEToken.setOnClickListener {
            val id = UtilizadorManager.buscarID()
            val email = UtilizadorManager.buscarEMAIL()
            val data = UtilizadorManager.buscarDATA()
            val token = TokenManager.buscarToken()
            Log.e("Utilizador", "ID: $id, EMAIL: $email, DATA: $data, TOKEN: $token")
        }

        mudarParaRegistoButton.setOnClickListener {
            startActivity(Intent(this@Login, Registar::class.java))
        }
    }
}