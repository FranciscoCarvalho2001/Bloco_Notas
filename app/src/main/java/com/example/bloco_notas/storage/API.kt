package com.example.bloco_notas.storage

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.bloco_notas.R
import com.example.bloco_notas.autenticacao.TokenManager
import com.example.bloco_notas.autenticacao.UtilizadorManager
import com.example.bloco_notas.listaNotas.ListaNotas
import com.example.bloco_notas.models.LoginResponse
import com.example.bloco_notas.models.Nota
import com.example.bloco_notas.models.NotaWrapper
import com.example.bloco_notas.models.Utilizador
import com.example.bloco_notas.models.UtilizadorWrapper
import com.example.bloco_notas.retrofit.RetrofitInitializer
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date


class API {


    var sp: MinhaSharedPreferences = MinhaSharedPreferences()
    lateinit var alertDialog : AlertDialog
    private val notaLista = ArrayList<Nota>()

    fun internetConectada(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val activeNetwork = connectivityManager.activeNetworkInfo
            if (activeNetwork != null && activeNetwork.isConnected) {
                return true
            }
        }
        return false
    }

    // ---------------------------------------------------------- Registo/Login/Logout ----------------------------------------------------------

    // obtem os dados do utilizador para registar
    fun registarUtilizadorAPI(email: String, password: String, context: Context){

        val data = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())
        var utilizador = Utilizador("$email", "$password", "$data")

        addUtilizadorAPI(utilizador) {
            if (it){
                Toast.makeText(context, "Registado!", Toast.LENGTH_SHORT).show()
                Log.e("Resposta", "Response: $it")
            } else {
                Toast.makeText(context, "Não Registado!", Toast.LENGTH_SHORT).show()
                Log.e("Resposta", "Response: $it")
            }
        }
    }
    // adiciona um utilizador na base de dados
    private fun addUtilizadorAPI(utilizador: Utilizador, onResult: (Boolean) -> Unit) {
        // obtem os valores do utilizador
        val email = utilizador.email
        val password = utilizador.password
        val data = utilizador.data

        val call = RetrofitInitializer()
            .loginLogoutService()
            .registarUtilizador("addUser", "$email", "$password", "$data")
        Log.e("SEND TO API", "$utilizador")

        call.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    onResult(true)

                    // Log detalhes da resposta
                    Log.d("API_CALL_SUCCESS", "Response: $responseBody")
                } else {
                    // Log detalhes do erro
                    Log.e("API_CALL_ERROR", "Error: ${response.code()} - ${response.errorBody()}")
                    onResult(false)
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                t.printStackTrace()
                onResult(false)
                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }

    fun loginUtilizadorAPI(email: String, password: String, context: Context){
        inflateLayout(context)
        val call = RetrofitInitializer()
            .loginLogoutService()
            .login("loginUser", "$email", "$password")

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    UtilizadorManager.getUserFromResponse(responseBody)
                    TokenManager.getTokenFromResponse(responseBody)
                    //Toast.makeText(context, "LOGADO!", Toast.LENGTH_SHORT).show()
                    Log.e("RESPONSE", "Response : $responseBody")
                    buscarNotasAPI(TokenManager.buscarToken().toString(),context)
                    context.startActivity(Intent(context, ListaNotas::class.java))
                    (context as AppCompatActivity).finish()
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                alertDialog.cancel()
                t.printStackTrace()
                Toast.makeText(context, "ERRO AO ENTRAR!", Toast.LENGTH_SHORT).show()
                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }

    fun logoutUtilizadorAPI(token: String, email: String, context: Context){
        val call = RetrofitInitializer()
            .loginLogoutService()
            .logout("logoutUser", "$email", "$token")

        call.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    UtilizadorManager.apagarUtilizador()
                    TokenManager.apagarToken()
                    Toast.makeText(context, "$responseBody", Toast.LENGTH_SHORT).show()
                    // Log detalhes da resposta
                    Log.d("API_CALL_SUCCESS", "Response: $responseBody")
                } else {
                    // Log detalhes do erro
                    Log.e("API_CALL_ERROR", "Error: ${response.code()} - ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(context, "ERRO AO SAIR!", Toast.LENGTH_SHORT).show()
                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }

    // ---------------------------------------------------------- Utilizador ----------------------------------------------------------

    // pede á API a lista de utilizadores
    fun buscarUtilizadoresAPI(token: String){
        // pede ao Retrofit para ler os dados recebidos da API
        val call = RetrofitInitializer()
            .utilizadorService()
            .listarUtilizadores(autorizacao = "Bearer $token")
        // processa os dados recebidos
        processarListaUtilizadoresAPI(call)
    }
    // processa lista de utilizadores recebida da API
    private fun processarListaUtilizadoresAPI(call: Call<Map<String, List<Utilizador>>>){
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
    fun buscarUtilizadorPorIdAPI(id: String, teste: String, context: Context){
        val call = RetrofitInitializer()
            .utilizadorService()
            .buscarUtilizador(autorizacao = "Bearer $teste", id)

        call.enqueue(object : Callback<UtilizadorWrapper> {
            override fun onResponse(
                call: Call<UtilizadorWrapper>,
                response: Response<UtilizadorWrapper>
            ) {

                val dialogBuilder = AlertDialog.Builder(context)
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
                                    "Email: $email\n"+
                                    "Password: $password\n"+
                                    "Data: $data\n"
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
    fun atualizarUtilizadorAPI(token: String, id: String, context: Context){
        // obtem os valores do email e password
//        val email = registoEmail.text.toString().trim()
//        val password = registoPassword.text.toString().trim()

        var utilizador = Utilizador("dfgdfgdfgdf", "asdasdadada", "kshdifsgdflskaihpueçiwei")

        val utilizadorWrapper = UtilizadorWrapper(utilizador)

        updateUtilizadorAPI(id, token, utilizadorWrapper, context)
    }
    private fun updateUtilizadorAPI(token: String, id: String, utilizadorWrapper: UtilizadorWrapper, context: Context){
        val call = RetrofitInitializer()
            .utilizadorService()
            .atualizarUtilizador(autorizacao = "Bearer $token", id, utilizadorWrapper)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                // Log detalhes da resposta
                Toast.makeText(context, "Atuazliado", Toast.LENGTH_SHORT).show()
                Log.d("API_CALL_SUCCESS", "Atuazliado")
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }

    // pede á API para apagar um utilizador, fornecendo um ID
    fun apagarUtilizadorAPI(token: String, id: String, context: Context){
        val call = RetrofitInitializer()
            .utilizadorService()
            .apagarUtilizador(autorizacao = "Bearer $token", id)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                // Log detalhes da resposta
                Toast.makeText(context, "Apagado", Toast.LENGTH_SHORT).show()
                Log.d("API_CALL_SUCCESS", "Apagado")
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }

    // ---------------------------------------------------------- Nota ----------------------------------------------------------

    // pede á API a lista de notas
    fun buscarNotasAPI(token: String, context: Context){
        sp.init(context)
        // pede ao Retrofit para ler os dados recebidos da API
        val call = RetrofitInitializer()
            .notaService()
            .listarNotas(autorizacao = "Bearer $token")
        // processa os dados recebidos
        processarListaNotasAPI(call,context)
    }
    // processa lista de notas recebida da API
    private fun processarListaNotasAPI(call: Call<Map<String, List<Nota>>>,context: Context){
        call.enqueue(object : Callback<Map<String, List<Nota>>> {

            override fun onResponse(
                call: Call<Map<String, List<Nota>>>,
                response: Response<Map<String, List<Nota>>>
            ) {
                notaLista.clear()
                var total = 0
                sp.salvarNotas(notaLista)
                sp.salvarNotasAPISP(notaLista)
                Toast.makeText(context, "fez2", Toast.LENGTH_SHORT).show()
                if (response.isSuccessful){
                    val responseBody = response.body()
                    val notaList = responseBody?.get("nota")
                    notaList?.forEach{ nota ->
                        // Obter dados
                        val emailUtilizador = nota.emailUtilizador
                        if( emailUtilizador == UtilizadorManager.buscarEMAIL().toString()){
                            total++
                            sp.setTotal(total)
                            val idNota = nota.idNota
                            val titulo = nota.titulo
                            val descricao = nota.descricao
                            val data = nota.data
                            val id = nota.id
                            val novaNota = Nota("$emailUtilizador", "$idNota", "$titulo", "$descricao", "$data", "$id")
                            Log.e("UT", "utilizdor: ${emailUtilizador}")
                            notaLista.add(novaNota)
                            sp.salvarNotas(notaLista)
                            sp.salvarNotasAPISP(notaLista)

                            Toast.makeText(context, "fez", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Log.e("RESPONSE_FAILURE", "Reponse not Successful: $response")
                }
            }
            override fun onFailure(call: Call<Map<String, List<Nota>>>, t: Throwable) {
                t.printStackTrace()
                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }

    // pede á API uma nota, fornecendo um ID
    private fun buscarNotaPorIdAPI(token: String, id: String, context: Context){
        val call = RetrofitInitializer()
            .notaService()
            .buscarNota(autorizacao = "Bearer $token", id)
        call.enqueue(object : Callback<NotaWrapper> {
            override fun onResponse(
                call: Call<NotaWrapper>,
                response: Response<NotaWrapper>
            ) {
                val dialogBuilder = AlertDialog.Builder(context)
                if (response.isSuccessful){
                    val notaWrapper = response.body()
                    dialogBuilder.setTitle("Nota")
                    notaWrapper?.let {
                        val nota = it.nota
                        // Obter dados
                        val emailUtilizador = nota.emailUtilizador
                        val idNota = nota.idNota
                        val titulo = nota.titulo
                        val descricao = nota.descricao
                        val data = nota.data
                        dialogBuilder.setMessage(
                            "Utilizador: $emailUtilizador\n"+
                                    "IDNota: $idNota\n"+
                                    "Titulo: $titulo\n"+
                                    "Descricao: $descricao\n"+
                                    "Data: $data\n"
                        )
                        dialogBuilder.setPositiveButton("OK") { dialog, _->
                            dialog.dismiss()
                        }
                        var alertDialog = dialogBuilder.create()
                        alertDialog.show()
                    }
                } else {
                    dialogBuilder.setMessage("Não há nota")
                    dialogBuilder.setPositiveButton("OK") { dialog, _->
                        dialog.dismiss()
                    }
                    var alertDialog = dialogBuilder.create()
                    alertDialog.show()
                    Log.e("RESPONSE_FAILURE", "Response not Successful: $response")
                }
            }
            override fun onFailure(call: Call<NotaWrapper>, t: Throwable) {
                t.printStackTrace()
                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }

    // obtem os dados da nota para guardar
    fun adicionarNotaAPI(idNota: String, utilizador:String,titulo:String, descricao:String, token: String,context: Context){
        val data = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())
        var nota = Nota(utilizador, idNota, titulo, descricao , "$data", null)
        addNotaAPI(token, nota) {
            if (it){
                Log.e("Resposta", "Response: $it")
            } else {
                Log.e("Resposta", "Response: $it")
            }
        }
    }
    // adiciona um nota na base de dados
    fun addNotaAPI(token: String, nota: Nota, onResult: (Boolean) -> Unit) {
        // embrula o utilizador
        val notaWrapper = NotaWrapper(nota)
        val call = RetrofitInitializer()
            .notaService()
            .adicionarNota(autorizacao = "Bearer $token", notaWrapper)
        Log.e("JSON to SEND", "$notaWrapper")
        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    val notaAdicionada = response.body()
                    onResult(true)
                    // Log detalhes da resposta
                    Log.d("API_CALL_SUCCESS", "Response: $notaAdicionada")
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

    // pede á API para atualizar uma nota, fornecendo um ID
    fun atualizarNotaAPI(id: String, utilizador:String,idNota: String, titulo:String, descricao:String, token: String){
        val data = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())
        var nota = Nota(utilizador, idNota, titulo, descricao, "$data", null)
        val notaWrapper = NotaWrapper(nota)
        updateNotaAPI(token, id, notaWrapper)
    }
    fun updateNotaAPI(token: String, id: String, notaWrapper: NotaWrapper){
        val call = RetrofitInitializer()
            .notaService()
            .atualizarNota(autorizacao = "Bearer $token", id, notaWrapper)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                // Log detalhes da resposta
                Log.d("API_CALL_SUCCESS", "Atualizado")
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }

    // pede á API para apagar uma nota, fornecendo um ID
    fun apagarNotaAPI(token: String, id: String){
        val call = RetrofitInitializer()
            .notaService()
            .apagarNota(autorizacao = "Bearer $token", id)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                // Log detalhes da resposta
                Log.d("API_CALL_SUCCESS", "Apagado")
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }

    private fun inflateLayout(context: Context) {
        val inflater = LayoutInflater.from(context)

        // Inflate the layout resource
        val inflatedLayout = inflater.inflate(R.layout.layout_progresso, null) as ConstraintLayout
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(inflatedLayout)
            .setCancelable(false)
        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }
}


