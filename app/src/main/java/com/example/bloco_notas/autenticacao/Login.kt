package com.example.bloco_notas.autenticacao

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bloco_notas.R

class Login : AppCompatActivity() {

    // URL da API
    val url: String = "https://script.google.com/macros/s/AKfycbyqcurpSI4RHz4gyChcU-Kz3vjclwNizphM7ou8q_Pc-PvhplWKaWve6IrwjDAQseZs/exec"

    // Variáveis de layout
    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var logoutButton: Button
    private lateinit var mudarParaRegistoButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        TokenManager.init(applicationContext)

        // ID's dos elementos
        loginEmail = findViewById(R.id.loginEmail)
        loginPassword = findViewById(R.id.loginPassword)
        loginButton = findViewById(R.id.loginButton)
        logoutButton = findViewById(R.id.logoutButton)
        mudarParaRegistoButton = findViewById(R.id.mudarParaRegistoButton)

        loginButton.setOnClickListener {
            loginUtilizador()
        }

        logoutButton.setOnClickListener {
            logoutUtilizador()
        }

        mudarParaRegistoButton.setOnClickListener {
            startActivity(Intent(this@Login, Registar::class.java))
        }

    }

    // função de Login
    private fun loginUtilizador(){
        val queue = Volley.newRequestQueue(this@Login)

        // obtem os valores do email e password
        val loginEmail = loginEmail.text.toString().trim()
        val loginPassword = loginPassword.text.toString().trim()

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->

                TokenManager.getTokenFromResponse(response)
                Toast.makeText(this@Login, ""+response, Toast.LENGTH_SHORT).show()
                Log.e("Resposta - loginUser ", "Response: $response")
            },
            { error ->

                Toast.makeText(this@Login, ""+error, Toast.LENGTH_SHORT).show()
                Log.e("ERRO - loginUser", "Error: $error")
            }
        ){
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String, String>()
                params["action"] = "loginUser"
                params["email"] = loginEmail
                params["password"] = loginPassword
                return params
            }
        }
        queue.add(stringRequest)
    }

    // função de Logout
    private fun logoutUtilizador(){
        val queue = Volley.newRequestQueue(this@Login)

        // obtem os valores do email e password
        val logoutEmail = loginEmail.text.toString().trim()
        val logoutPassword = loginPassword.text.toString().trim()

//        val stringRequest = object : StringRequest(
//            Request.Method.POST, url,
//            { response ->
//
//                Toast.makeText(this@Login, ""+response, Toast.LENGTH_SHORT).show()
//                Log.e("Resposta - logoutUser", "Response: $response")
//            },
//            { error ->
//
//                Toast.makeText(this@Login, ""+error, Toast.LENGTH_SHORT).show()
//                Log.e("ERRO - logoutUser", "Error: $error")
//            }
//        ){
//            override fun getParams(): MutableMap<String, String>? {
//                val params = HashMap<String, String>()
//                params["action"] = "logoutUser"
//                params["email"] = logoutEmail
//                params["password"] = logoutPassword
//                return params
//            }
//        }

        val tokenRequest = object : TokenRequest(
            Request.Method.POST, url, "logoutUser", logoutEmail,
            { response ->

                TokenManager.apagarToken()
                Toast.makeText(this@Login, ""+response, Toast.LENGTH_SHORT).show()
                Log.e("Resposta - logoutUser", "Response: $response")
            },
            { error ->
                Toast.makeText(this@Login, ""+error, Toast.LENGTH_SHORT).show()
                Log.e("ERRO - logoutUser", "Error: $error")
            }
        ){
            override fun getParams(): MutableMap<String, String>? {
                return super.getParams()
            }
        }
        queue.add(tokenRequest)
    }






}