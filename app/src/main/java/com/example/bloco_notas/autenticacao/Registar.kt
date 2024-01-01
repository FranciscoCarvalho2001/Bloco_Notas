package com.example.bloco_notas.autenticacao

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bloco_notas.R
import com.example.bloco_notas.storage.API


class Registar : AppCompatActivity() {

    // Variáveis de layout
    private lateinit var registoEmail: EditText
    private lateinit var registoPassword: EditText
    private lateinit var registoButton: Button
    private lateinit var getButton: Button
    private lateinit var updateButton: Button
    private lateinit var deleteButton : Button
    private lateinit var mudarParaLoginButton: TextView
    private lateinit var api : API
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"

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
        mudarParaLoginButton = findViewById(R.id.mudarParaLoginButton)

        registoButton.setOnClickListener{
            // obtem os valores do email e password
            val email = registoEmail.text.toString().trim()
            val password = registoPassword.text.toString().trim()
            if(!isValidEmail(email)){
                Toast.makeText(this@Registar, "Email inválido", Toast.LENGTH_SHORT).show()
            }else{
                api.registarUtilizadorAPI(email, password, this@Registar)
            }

        }

        mudarParaLoginButton.setOnClickListener {
            startActivity(Intent(this@Registar, Login::class.java))
        }

    }
    fun isValidEmail(email: String): Boolean {
        return email.matches(emailRegex.toRegex())
    }

}