package com.example.bloco_notas.Autenticacao

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bloco_notas.R
import org.json.JSONException

class Registar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registar)

        val user = mutableListOf<Utilizador>()

        // URL da API
        val url: String = "https://script.google.com/macros/s/AKfycbyqcurpSI4RHz4gyChcU-Kz3vjclwNizphM7ou8q_Pc-PvhplWKaWve6IrwjDAQseZs/exec"
        // ID's dos elementos
        val registoEmail = findViewById<EditText>(R.id.registoEmail)
        val registoPassword = findViewById<EditText>(R.id.registoPassword)
        val registoButton = findViewById<Button>(R.id.registoButton)
        val getButton = findViewById<Button>(R.id.getButton)

        // adiciona o Utilizador á base de dados
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
                    Log.e("Resposta", "Response: $response")
                },
                { error ->

                    Toast.makeText(this@Registar, ""+error, Toast.LENGTH_SHORT).show()
                    Log.e("Erro", "Error: $error")
                }
            ){
                override fun getParams(): MutableMap<String, String>? {
                    val params = HashMap<String, String>()
                    params["action"] = "addUser"
                    params["email"] = email
                    params["password"] = password
                    return params
                }
            }

            queue.add(stringRequest)
        }

        // obtem um Utilizador da base de dados
        fun getUserFromDatabase(){
            val queue = Volley.newRequestQueue(this@Registar)

            // obtem os valor do email
            val emailGET = registoEmail.text.toString().trim()

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET, "$url?email=$emailGET", null,
                { response ->
                    try {
                        val dados = response.getJSONObject("users")
                        val userObject = Utilizador(
                            dados.getString("Id").toInt(),
                            dados.getString("Data"),
                            dados.getString("Email"),
                            dados.getString("Password")
                        )
                        user.add(userObject)

                        val dialogBuilder = AlertDialog.Builder(this@Registar)
                        dialogBuilder.setTitle("Utilizador")

                        if(user.isNotEmpty()) {
                            val id = user.map { it.id }
                            val data = user.map { it.data }
                            val email = user.map { it.email }
                            val password = user.map { it.password }
                            dialogBuilder.setMessage(
                                "ID: $id\n"+
                                        "Data: $data\n"+
                                        "Email: $email\n"+
                                        "Password: $password\n"
                            )
                        } else {
                            dialogBuilder.setMessage("Não há utilizador")
                        }

                        dialogBuilder.setPositiveButton("OK") { dialog, _->
                            dialog.dismiss()
                            user.clear()
                        }

                        var alertDialog = dialogBuilder.create()
                        alertDialog.show()

                    } catch (e: JSONException) {
                        Log.e("JSON erro", "JSON Exception: ${e.message}")
                        Log.e("Resposta", "Response: $response")
                    }
                },
                { error ->

                    Toast.makeText(this@Registar, ""+error, Toast.LENGTH_SHORT).show()
                    Log.e("Erro", "Error: $error")
                }
            ){
                override fun getHeaders(): MutableMap<String, String> {
                    return super.getHeaders()
                }
            }

            queue.add(jsonObjectRequest)


        }


        registoButton.setOnClickListener{
            addUserToDatabase()
        }

        getButton.setOnClickListener{
            getUserFromDatabase()
        }

    }
}