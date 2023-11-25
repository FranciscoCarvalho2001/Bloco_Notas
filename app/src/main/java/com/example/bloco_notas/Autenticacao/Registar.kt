package com.example.bloco_notas.Autenticacao

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bloco_notas.R

class Registar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registar)

        // URL da API
        val url: String = "https://script.google.com/macros/s/AKfycbyqcurpSI4RHz4gyChcU-Kz3vjclwNizphM7ou8q_Pc-PvhplWKaWve6IrwjDAQseZs/exec"
        // ID's dos elementos
        val registoEmail = findViewById<EditText>(R.id.registoEmail)
        val registoPassword = findViewById<EditText>(R.id.registoPassword)
        val registoButton = findViewById<Button>(R.id.registoButton)

        fun addUserToDatabase(){
            val queue = Volley.newRequestQueue(this@Registar)

            // obtem os valores do email e password
            val email = registoEmail.text.toString().trim()
            val password = registoPassword.text.toString().trim()

            // Request do tipo POST para a API
            val stringRequest = object : StringRequest(
                Request.Method.POST, url,
                { response ->

                    Toast.makeText(this@Registar, ""+response, Toast.LENGTH_SHORT).show()
                },
                { error ->

                    Toast.makeText(this@Registar, ""+error, Toast.LENGTH_SHORT).show()
                }
            ){
                override fun getParams(): MutableMap<String, String>? {
                    val params = HashMap<String, String>()
                    params["email"] = email
                    params["password"] = password
                    return params
                }
            }

            queue.add(stringRequest)
        }

        registoButton.setOnClickListener{
            addUserToDatabase()
        }
    }
}