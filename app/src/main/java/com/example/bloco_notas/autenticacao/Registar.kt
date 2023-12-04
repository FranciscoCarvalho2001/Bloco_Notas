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

    // Lista que guarda o utilizador
    // private val user = mutableListOf<Utilizador>()

    // URL da API
    //val url: String = "https://script.google.com/macros/s/AKfycbyqcurpSI4RHz4gyChcU-Kz3vjclwNizphM7ou8q_Pc-PvhplWKaWve6IrwjDAQseZs/exec"
    val token = "23f9c8a5ae45"

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
            api.adicionarUtilizadorAPI(email, password, this@Registar)
        }

        getButton.setOnClickListener{
            //api.buscarUtilizadoresAPI()
            api.buscarUtilizadorPorIdAPI(5, this@Registar)
        }

        updateButton.setOnClickListener {
            api.atualizarUtilizadorAPI(5, this@Registar)
        }

        deleteButton.setOnClickListener {
            api.apagarUtilizadorAPI(8, this@Registar)
        }

        mudarParaLoginButton.setOnClickListener {
            startActivity(Intent(this@Registar, Login::class.java))
        }

    }

}