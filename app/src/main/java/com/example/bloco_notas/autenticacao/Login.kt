package com.example.bloco_notas.autenticacao

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bloco_notas.R
import com.example.bloco_notas.listaNotas.ListaNotas
import com.example.bloco_notas.storage.API


class Login : AppCompatActivity() {

    // Variáveis de layout
    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var mudarPagina: TextView
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
        mudarPagina = findViewById(R.id.mudarPagina)


        loginButton.setOnClickListener {
            val email = loginEmail.text.toString().trim()
            val password = loginPassword.text.toString().trim()
            api.loginUtilizadorAPI(email, password, this@Login)

            // for input manager and initializing it.
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            // on below line hiding our keyboard.
            inputMethodManager.hideSoftInputFromWindow(loginPassword.getWindowToken(), 0)
        }

//        logoutButton.setOnClickListener {
//            val token = TokenManager.buscarToken().toString()
//            val email = UtilizadorManager.buscarEMAIL().toString()
//            api.logoutUtilizadorAPI(token, email, this@Login)
//        }
//
//        mostraUtilizadorEToken.setOnClickListener {
//            val id = UtilizadorManager.buscarID()
//            val email = UtilizadorManager.buscarEMAIL()
//            val data = UtilizadorManager.buscarDATA()
//            val token = TokenManager.buscarToken()
//            Log.e("Utilizador", "ID: $id, EMAIL: $email, DATA: $data, TOKEN: $token")
//            startActivity(Intent(this@Login, ListaNotas::class.java))
//        }
//
//        mudarParaRegistoButton.setOnClickListener {
//            startActivity(Intent(this@Login, Registar::class.java))
//
//        }
    }
}