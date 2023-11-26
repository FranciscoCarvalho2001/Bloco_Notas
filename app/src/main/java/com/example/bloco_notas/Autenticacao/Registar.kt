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

    // Lista que guarda o utilizador
    private val user = mutableListOf<Utilizador>()

    // URL da API
    val url: String = "https://script.google.com/macros/s/AKfycbyqcurpSI4RHz4gyChcU-Kz3vjclwNizphM7ou8q_Pc-PvhplWKaWve6IrwjDAQseZs/exec"

    // Variáveis de layout
    private lateinit var registoEmail: EditText
    private lateinit var registoPassword: EditText
    private lateinit var registoButton: Button
    private lateinit var getButton: Button
    private lateinit var updateButton: Button
    private lateinit var deleteButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registar)

        // ID's dos elementos
        registoEmail = findViewById(R.id.registoEmail)
        registoPassword = findViewById(R.id.registoPassword)
        registoButton = findViewById(R.id.registoButton)
        getButton = findViewById(R.id.getButton)
        updateButton = findViewById(R.id.updateButton)
        deleteButton = findViewById(R.id.deleteButton)

        registoButton.setOnClickListener{
            adicionarUtilizador()
        }

        getButton.setOnClickListener{
            buscarUtilizador()
        }

        updateButton.setOnClickListener {
            atualizarUtilizador()
        }

        deleteButton.setOnClickListener {
            apagarUtilizador()
        }

    }

    // adiciona o Utilizador á base de dados
    private fun adicionarUtilizador(){
        val queue = Volley.newRequestQueue(this@Registar)

        // obtem os valores do email e password
        val email = registoEmail.text.toString().trim()
        val password = registoPassword.text.toString().trim()

        // Request do tipo POST para a API
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                Toast.makeText(this@Registar, ""+response, Toast.LENGTH_SHORT).show()
                Log.e("Resposta - addUser ", "Response: $response")
            },
            { error ->
                Toast.makeText(this@Registar, ""+error, Toast.LENGTH_SHORT).show()
                Log.e("ERRO - addUser", "Error: $error")
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
    private fun buscarUtilizador(){
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
                    Log.e("JSON ERRO - getUser", "JSON Exception: ${e.message}")
                    Log.e("Resposta - getUser", "Response: $response")
                }
            },
            { error ->

                Toast.makeText(this@Registar, ""+error, Toast.LENGTH_SHORT).show()
                Log.e("ERRO - getUser", "Error: $error")
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                return super.getHeaders()
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun atualizarUtilizador(){
        val queue = Volley.newRequestQueue(this@Registar)

        // obtem os valores do email e password
        val emailToUpdate = registoEmail.text.toString().trim()
        val newPassword = registoPassword.text.toString().trim()

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->

                Toast.makeText(this@Registar, ""+response, Toast.LENGTH_SHORT).show()
                Log.e("Resposta - updateUser", "Response: $response")
            },
            { error ->

                Toast.makeText(this@Registar, ""+error, Toast.LENGTH_SHORT).show()
                Log.e("ERRO - updateUser", "Error: $error")
            }
        ){
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String, String>()
                params["action"] = "updateUser"
                params["emailToUpdate"] = emailToUpdate
                params["newPassword"] = newPassword
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun apagarUtilizador(){
        val queue = Volley.newRequestQueue(this@Registar)

        val deleteEmail = registoEmail.text.toString().trim()
        val deletePassword = registoPassword.text.toString().trim()

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->

                Toast.makeText(this@Registar, ""+response, Toast.LENGTH_SHORT).show()
                Log.e("Resposta - deleteUser", "Response: $response")
            },
            { error ->

                Toast.makeText(this@Registar, ""+error, Toast.LENGTH_SHORT).show()
                Log.e("ERRO - deleteUser", "Error: $error")
            }
        ){
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String, String>()
                params["action"] = "deleteUser"
                params["emailDelete"] = deleteEmail
                params["passwordDelete"] = deletePassword
                return params
            }
        }
        queue.add(stringRequest)
    }

}