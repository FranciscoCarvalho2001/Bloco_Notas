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
import com.example.bloco_notas.models.Utilizador
import com.example.bloco_notas.models.UtilizadorWrapper
import com.example.bloco_notas.retrofit.RetrofitInitializer
import com.example.bloco_notas.storage.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date


class Registar : AppCompatActivity() {

    // Variáveis de layout
    private lateinit var registoEmail: EditText
    private lateinit var registoPassword: EditText
    private lateinit var registoButton: Button
    private lateinit var getButton: Button
    private lateinit var updateButton: Button
    private lateinit var deleteButton : Button
    private lateinit var mudarParaLoginButton: Button
    private lateinit var api : API

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registar)

        api = API()

        UtilizadorManager.init(applicationContext)
        TokenManager.init(applicationContext)

        // ID's dos elementos
        registoEmail = findViewById(R.id.registoEmail)
        registoPassword = findViewById(R.id.registoPassword)
        registoButton = findViewById(R.id.registoButton)
        getButton = findViewById(R.id.getButton)
        updateButton = findViewById(R.id.updateButton)
        deleteButton = findViewById(R.id.deleteButton)
        mudarParaLoginButton = findViewById(R.id.mudarParaLoginButton)

        registoButton.setOnClickListener{
            // obtem os valores do email e password
            val email = registoEmail.text.toString().trim()
            val password = registoPassword.text.toString().trim()
            api.registarUtilizadorAPI(email, password, this@Registar)
        }

        getButton.setOnClickListener{
            //api.buscarUtilizadoresAPI()
            val id = UtilizadorManager.buscarID().toString()
            val token = TokenManager.buscarToken().toString()
            api.buscarUtilizadorPorIdAPI(id, token, this@Registar)
        }

        updateButton.setOnClickListener {
            val token = TokenManager.buscarToken().toString()
            val id = UtilizadorManager.buscarID().toString()
            api.atualizarUtilizadorAPI(token, id, this@Registar)
        }

        deleteButton.setOnClickListener {
            val token = TokenManager.buscarToken().toString()
            val id = UtilizadorManager.buscarID().toString()
            api.apagarUtilizadorAPI(token, id, this@Registar)
        }

        mudarParaLoginButton.setOnClickListener {
            startActivity(Intent(this@Registar, Login::class.java))
        }

    }

}