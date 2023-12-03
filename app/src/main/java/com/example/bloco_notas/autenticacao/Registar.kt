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
        mudarParaLoginButton = findViewById(R.id.mudarParaLoginButton)

        registoButton.setOnClickListener{
            adicionarUtilizador()
        }

        getButton.setOnClickListener{
            buscarUtilizadores()
            //buscarUtilizadorPorId(1)
        }

        updateButton.setOnClickListener {
            atualizarUtilizador(4)
        }

        deleteButton.setOnClickListener {
            apagarUtilizador(4)
        }

        mudarParaLoginButton.setOnClickListener {
            startActivity(Intent(this@Registar, Login::class.java))
        }

    }

    // obtem os dados do utilizador para registar
    private fun adicionarUtilizador(){
        // obtem os valores do email e password
        val email = registoEmail.text.toString().trim()
        val password = registoPassword.text.toString().trim()
        // val simpleDate = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val data = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())

        var utilizador = Utilizador("$email", "$password", "$data")

        addUtilizador(utilizador) {
            if (it){
                Toast.makeText(this@Registar, "Registado", Toast.LENGTH_SHORT).show()
                Log.e("Resposta", "Response: $it")
            } else {
                Toast.makeText(this@Registar, "Não Registado", Toast.LENGTH_SHORT).show()
                Log.e("Resposta", "Response: $it")
            }

        }

    }

    // adiciona um utilizador na base de dados
    private fun addUtilizador(utilizador: Utilizador, onResult: (Boolean) -> Unit) {
        // embrula o utilizador
        val utilizadorWrapper = UtilizadorWrapper(utilizador)

        val call = RetrofitInitializer()
            .utilizadorService()
            .adicionarUtilizador(autorizacao = "Bearer $token", utilizadorWrapper)
        Log.e("JSON to SEND", "$utilizadorWrapper")

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    val utilizadorAdicionado = response.body()
                    onResult(true)

                    // Log detalhes da resposta
                    Log.d("API_CALL_SUCCESS", "Response: $utilizadorAdicionado")
                } else {
                    // Log detalhes do erro
                    Log.e("API_CALL_ERROR", "Error: ${response.code()} - ${response.errorBody()}")
                    onResult(false)
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
                onResult(false)

                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }

    // pede á API a lista de utilizadores
    private fun buscarUtilizadores(){
        // pede ao Retrofit para ler os dados recebidos da API
        val call = RetrofitInitializer()
            .utilizadorService()
            .listarUtilizadores(autorizacao = "Bearer $token")
        // processa os dados recebidos
        processarListaUtilizadores(call)
    }

    // processa lista de utilizadores recebida da API
    private fun processarListaUtilizadores(call: Call<Map<String, List<Utilizador>>>){
        call.enqueue(object : Callback<Map<String, List<Utilizador>>> {
            override fun onResponse(
                call: Call<Map<String, List<Utilizador>>>,
                response: Response<Map<String, List<Utilizador>>>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val utilizadorList = responseBody?.get("utilizador")

                    utilizadorList?.forEach{utilizador ->
                        // Obter dados
                        val email = utilizador.email
                        val password = utilizador.password
                        val data = utilizador.data
                        //val id = utilizador.id

                        println("User details: Email - $email, Password - $password, Data - $data")
                    }
                } else {
                    Log.e("RESPONSE_FAILURE", "Reponse not Successful: $response")
                }
            }
            override fun onFailure(call: Call<Map<String, List<Utilizador>>>, t: Throwable) {
                t.printStackTrace()
                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }

    // pede á API um utilizador, fornecendo um ID
    private fun buscarUtilizadorPorId(id: Int){
        val call = RetrofitInitializer()
            .utilizadorService()
            .buscarUtilizador(autorizacao = "Bearer $token", id)

        call.enqueue(object : Callback<UtilizadorWrapper> {
            override fun onResponse(
                call: Call<UtilizadorWrapper>,
                response: Response<UtilizadorWrapper>
            ) {

                val dialogBuilder = AlertDialog.Builder(this@Registar)
                if (response.isSuccessful){

                    val utilizadorWrapper = response.body()
                    dialogBuilder.setTitle("Utilizador")

                    utilizadorWrapper?.let {
                        val utilizador = it.utilizador
                        // Obtain user details from the wrapped object
                        val email = utilizador.email
                        val password = utilizador.password
                        val data = utilizador.data
                        // val id = utilizador.id

                        dialogBuilder.setMessage(
                            "ID: $id\n"+
                                    "Data: $data\n"+
                                    "Email: $email\n"+
                                    "Password: $password\n"
                        )

                        dialogBuilder.setPositiveButton("OK") { dialog, _->
                            dialog.dismiss()
                        }

                        var alertDialog = dialogBuilder.create()
                        alertDialog.show()
                    }
                } else {
                    dialogBuilder.setMessage("Não há utilizador")
                    dialogBuilder.setPositiveButton("OK") { dialog, _->
                        dialog.dismiss()
                    }
                    var alertDialog = dialogBuilder.create()
                    alertDialog.show()

                    Log.e("RESPONSE_FAILURE", "Response not Successful: $response")
                }
            }
            override fun onFailure(call: Call<UtilizadorWrapper>, t: Throwable) {
                t.printStackTrace()
                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }

    // pede á API para atualizar um utilizador, fornecendo um ID
    private fun atualizarUtilizador(id: Int){
        // obtem os valores do email e password
        val email = registoEmail.text.toString().trim()
        val password = registoPassword.text.toString().trim()

        var utilizador = Utilizador("$email", "$password", "sdfgfdfdgdg")

        val utilizadorWrapper = UtilizadorWrapper(utilizador)

        updateUtilizador(id, utilizadorWrapper)
    }
    private fun updateUtilizador(id: Int, utilizadorWrapper: UtilizadorWrapper){
        val call = RetrofitInitializer()
            .utilizadorService()
            .atualizarUtilizador(autorizacao = "Bearer $token", id, utilizadorWrapper)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                // Log detalhes da resposta
                Toast.makeText(this@Registar, "Atuazliado", Toast.LENGTH_SHORT).show()
                Log.d("API_CALL_SUCCESS", "Atuazliado")
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }

    // pede á API para apagar um utilizador, fornecendo um ID
    private fun apagarUtilizador(id: Int){
        val call = RetrofitInitializer()
            .utilizadorService()
            .apagarUtilizador(autorizacao = "Bearer $token", id)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                // Log detalhes da resposta
                Toast.makeText(this@Registar, "Apagado", Toast.LENGTH_SHORT).show()
                Log.d("API_CALL_SUCCESS", "Apagado")
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }

}