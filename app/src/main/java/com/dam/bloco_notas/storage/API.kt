package com.dam.bloco_notas.storage

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.dam.bloco_notas.PaginaInicial
import com.dam.bloco_notas.R
import com.dam.bloco_notas.autenticacao.Login
import com.dam.bloco_notas.autenticacao.TokenManager
import com.dam.bloco_notas.autenticacao.UtilizadorManager
import com.dam.bloco_notas.listaNotas.ListaNotas
import com.dam.bloco_notas.models.LoginResponse
import com.dam.bloco_notas.models.Nota
import com.dam.bloco_notas.models.NotaWrapper
import com.dam.bloco_notas.models.Utilizador
import com.dam.bloco_notas.models.UtilizadorWrapper
import com.dam.bloco_notas.retrofit.RetrofitInitializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date


class API {

    // inicia as variaveis
    lateinit var alertDialog : AlertDialog
    var sp: MinhaSharedPreferences = MinhaSharedPreferences()

    // confirma se há internet
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
        if (context != null) {
            inflateLayout(context)
        }
        // obtem a data
        val data = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())
        // obtem o utilizador
        var utilizador = Utilizador("$email", "$password", "$data")
        // adiciona o utilizador á API
        addUtilizadorAPI(utilizador) {
            if (it){ // se foi registo com sucesso...
                // mostra uma mensagem de registo
                Toast.makeText(context, "Registado!", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()

                context.startActivity(Intent(context, Login::class.java))
                (context as AppCompatActivity).finish()
                Log.e("Resposta", "Response: $it")
            } else { // se não foi registado...
                if (context != null) {
                    alertDialog.cancel()
                }
                // mostra uma mensagem de não registo
                Toast.makeText(context, "Não Registado!", Toast.LENGTH_SHORT).show()
                Log.e("Resposta", "Response: $it")
            }
        }
    }
    // adiciona um utilizador na base de dados
    private fun addUtilizadorAPI(utilizador: Utilizador, onResult: (Boolean) -> Unit) {
        // obtem o email do utilizador
        val email = utilizador.email
        // obtem a password do utilizador
        val password = utilizador.password
        // obtem a data de registo do utilizador
        val data = utilizador.data

        // faz a call para a API
        val call = RetrofitInitializer()
            .loginLogoutService()
            .registarUtilizador("addUser", "$email", "$password", "$data")

        call.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.isSuccessful) { // se a reposta teve sucesso...
                    val responseBody = response.body()
                    // devolve true
                    onResult(true)
                    // Log detalhes da resposta
                    Log.d("API_CALL_SUCCESS", "Response: $responseBody")
                } else {
                    // devolve falso
                    onResult(false)
                    // Log detalhes do erro
                    Log.e("API_CALL_ERROR", "Error: ${response.code()} - ${response.errorBody()}")
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) { // se não houver resposta...
                t.printStackTrace()
                // devolve falso
                onResult(false)
                // Log detalhes do failure
                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }
    // faz login do utilizador na app
    fun loginUtilizadorAPI(email: String, password: String, context: Context){
        if (context != null) {
            inflateLayout(context)
        }
        // faz a call para a API
        val call = RetrofitInitializer()
            .loginLogoutService()
            .login("loginUser", "$email", "$password")

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) { // se a reposta teve sucesso...
                    val responseBody = response.body()
                    // guarda o utilizador na sharedPreferences
                    UtilizadorManager.getUserFromResponse(responseBody)
                    // guarda o token na sharedPreferences
                    TokenManager.getTokenFromResponse(responseBody)
                    //Toast.makeText(context, "LOGADO!", Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                    context.startActivity(Intent(context, ListaNotas::class.java))
                    (context as AppCompatActivity).finish()
                    // Log detalhes da resposta
                    Log.e("RESPONSE", "Response : $responseBody")
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) { // se não houver resposta...
                if (context != null) {
                    alertDialog.cancel()
                }
                t.printStackTrace()
                // mensagem de erro ao entrar
                Toast.makeText(context, "ERRO AO ENTRAR!", Toast.LENGTH_SHORT).show()
                // Log detalhes do failure
                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }
    // faz logout na app
    fun logoutUtilizadorAPI(token: String, email: String, context: Context){
        if (context != null) {
            inflateLayout(context)
        }
        // faz a call para a API
        val call = RetrofitInitializer()
            .loginLogoutService()
            .logout("logoutUser", "$email", "$token")

        call.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.isSuccessful) { // se a reposta teve sucesso...
                    val responseBody = response.body()
                    // apaga o utilizador na sharedPreferences
                    UtilizadorManager.apagarUtilizador()
                    // apaga o token na sharedPreferences
                    TokenManager.apagarToken()
                    //Toast.makeText(context, "$responseBody", Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                    context.startActivity(Intent(context, PaginaInicial::class.java))
                    (context as AppCompatActivity).finish()
                    // Log detalhes da resposta
                    Log.d("API_CALL_SUCCESS", "Response: $responseBody")
                } else {
                    // Log detalhes do erro
                    Log.e("API_CALL_ERROR", "Error: ${response.code()} - ${response.errorBody()}")
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) { // se não houver resposta...
                if (context != null) {
                    alertDialog.cancel()
                }
                t.printStackTrace()
                // mensagem de erro ao sair
                Toast.makeText(context, "ERRO AO SAIR!", Toast.LENGTH_SHORT).show()
                // Log detalhes do failure
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
                if (response.isSuccessful) { // se a reposta teve sucesso...
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
                    // Log detalhes do failure
                    Log.e("RESPONSE_FAILURE", "Reponse not Successful: $response")
                }
            }
            override fun onFailure(call: Call<Map<String, List<Utilizador>>>, t: Throwable) { // se não houver resposta...
                t.printStackTrace()
                // Log detalhes do failure
                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }

    // pede á API um utilizador, fornecendo um ID
    fun buscarUtilizadorPorIdAPI(id: String, teste: String, context: Context){
        // faz a call á API
        val call = RetrofitInitializer()
            .utilizadorService()
            .buscarUtilizador(autorizacao = "Bearer $teste", id)

        call.enqueue(object : Callback<UtilizadorWrapper> {
            override fun onResponse(
                call: Call<UtilizadorWrapper>,
                response: Response<UtilizadorWrapper>
            ) {

                val dialogBuilder = AlertDialog.Builder(context)
                if (response.isSuccessful){ // se teve resposta mostra o utilizador obtido numa janela
                    val utilizadorWrapper = response.body()
                    dialogBuilder.setTitle("Utilizador")
                    utilizadorWrapper?.let {
                        val utilizador = it.utilizador
                        // Obtem os dados
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
                } else { // se não houver resposta mostra uma mensagem de erro numa janela
                    dialogBuilder.setMessage("Não há utilizador")
                    dialogBuilder.setPositiveButton("OK") { dialog, _->
                        dialog.dismiss()
                    }
                    var alertDialog = dialogBuilder.create()
                    alertDialog.show()
                    // Log detalhes do failure
                    Log.e("RESPONSE_FAILURE", "Response not Successful: $response")
                }
            }
            override fun onFailure(call: Call<UtilizadorWrapper>, t: Throwable) { // se não houver resposta...
                t.printStackTrace()
                // Log detalhes do failure
                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }

    // pede á API para atualizar um utilizador, fornecendo um ID
    fun atualizarUtilizadorAPI(token: String, id: String, email:String, password:String, context: Context){
        // obtem a data
        val data = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())
        // obtem o utilizador
        var utilizador = Utilizador(email, password, data)
        // embrulha o utilizador
        val utilizadorWrapper = UtilizadorWrapper(utilizador)
        // manda o utilizador embrulhado para a API
        updateUtilizadorAPI(token, id, utilizadorWrapper, context)
    }
    // atualiza o utilizador na API
    private fun updateUtilizadorAPI(token: String, id: String, utilizadorWrapper: UtilizadorWrapper, context: Context){
        // faz a call para a API
        val call = RetrofitInitializer()
            .utilizadorService()
            .atualizarUtilizador(autorizacao = "Bearer $token", id, utilizadorWrapper)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                // Log detalhes da resposta
                Toast.makeText(context, "Atualizado", Toast.LENGTH_SHORT).show()
                Log.d("API_CALL_SUCCESS", "Atuazliado")
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
                // Log detalhes do failure
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
                // apaga o utilizador na sharedPreferences
                UtilizadorManager.apagarUtilizador()
                // apaga o token na sharedPreferences
                TokenManager.apagarToken()
                // Log detalhes da resposta
                //Toast.makeText(context, "Apagado", Toast.LENGTH_SHORT).show()
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
    // Função suspensa para buscar notas da API
    suspend fun buscarNotasAPI(token: String, context: Context): List<Nota> {
        return withContext(Dispatchers.IO) {
            sp.init(context)
            // pede ao Retrofit para ler os dados recebidos da API
            val call = RetrofitInitializer()
                .notaService()
                .listarNotas(autorizacao = "Bearer $token")

            try {
                val response = call.execute()
                if (response.isSuccessful) { // se a reposta teve sucesso...
                    val responseBody = response.body()
                    val notaList = responseBody?.get("nota") ?: emptyList()
                    processarListaNotasAPI(notaList)
                } else { // se a reposta não teve sucesso...
                    // Log do failure
                    Log.e("RESPONSE_FAILURE", "Reponse not Successful: $response")
                    emptyList()
                }
            } catch (e: Exception) { // se não houve resposta...
                e.printStackTrace()
                // Log do failure
                Log.e("API_CALL_FAILURE", "API call failed: ${e.message}")
                emptyList()
            }
        }
    }

    // Função para processar a lista de notas
    private fun processarListaNotasAPI(notaList: List<Nota>): List<Nota> {
        val notaLista = mutableListOf<Nota>()

        notaList.forEach { nota ->
            val emailUtilizador = nota.emailUtilizador
            if (emailUtilizador == UtilizadorManager.buscarEMAIL().toString()) {
                val idNota = nota.idNota
                val titulo = nota.titulo
                val descricao = nota.descricao
                val data = nota.data
                val id = nota.id
                val novaNota = Nota(
                    "$emailUtilizador",
                    "$idNota",
                    "$titulo",
                    "$descricao",
                    "$data",
                    "$id"
                )
                notaLista.add(novaNota)
            }
        }

        return notaLista
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
                if (response.isSuccessful){ // se a reposta teve sucesso mostra a nota obtida numa janela
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
                } else { // se a não teve sucesso mostra uma mensagem de erro
                    dialogBuilder.setMessage("Não há nota")
                    dialogBuilder.setPositiveButton("OK") { dialog, _->
                        dialog.dismiss()
                    }
                    var alertDialog = dialogBuilder.create()
                    alertDialog.show()
                    // Log do failure
                    Log.e("RESPONSE_FAILURE", "Response not Successful: $response")
                }
            }
            override fun onFailure(call: Call<NotaWrapper>, t: Throwable) {
                t.printStackTrace()
                // Log do failure
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
        // embrulha o utilizador
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
                if (response.isSuccessful) { // se a reposta teve sucesso
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

    // Função para inflate do layout
    private fun inflateLayout(context: Context) {
        val inflater = LayoutInflater.from(context)

        // Inflate do layout
        val inflatedLayout = inflater.inflate(R.layout.layout_progresso, null) as ConstraintLayout
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(inflatedLayout)
            .setCancelable(false)
        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }
}


