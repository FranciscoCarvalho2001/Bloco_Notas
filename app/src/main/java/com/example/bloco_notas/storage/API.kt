package com.example.bloco_notas.storage

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.bloco_notas.models.Nota
import com.example.bloco_notas.models.NotaWrapper
import com.example.bloco_notas.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date

class API {
    val token = "23f9c8a5ae45"

    // obtem os dados da nota para guardar
    fun adicionarNotaAPI( idNota: Int, titulo:String, descricao:String){
        val data = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())
        var nota = Nota("algo@algo.pt", idNota, titulo, descricao , "$data", null)
        addNotaAPI(nota) {
            if (it){
                Log.e("Resposta", "Response: $it")
            } else {
                Log.e("Resposta", "Response: $it")
            }
        }
    }
    // adiciona um utilizador na base de dados
    fun addNotaAPI(nota: Nota, onResult: (Boolean) -> Unit) {
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
   fun atualizarNotaAPI(id: Int, idNota: Int, titulo:String, descricao:String){
        val data = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())
        var nota = Nota("algo@algo.pt", idNota, titulo, descricao, "$data", null)
        val notaWrapper = NotaWrapper(nota)
        updateNotaAPI(id, notaWrapper)
   }
    fun updateNotaAPI(id: Int, notaWrapper: NotaWrapper){
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
    fun apagarNotaAPI(id: Int){
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

    // pede á API a lista de notas
    private fun buscarNotas(){
        // pede ao Retrofit para ler os dados recebidos da API
        val call = RetrofitInitializer()
            .notaService()
            .listarNotas(autorizacao = "Bearer $token")
        // processa os dados recebidos
        processarListaNotas(call)
    }
    // processa lista de notas recebida da API
    private fun processarListaNotas(call: Call<Map<String, List<Nota>>>){
        call.enqueue(object : Callback<Map<String, List<Nota>>> {
            override fun onResponse(
                call: Call<Map<String, List<Nota>>>,
                response: Response<Map<String, List<Nota>>>
            ) {
                if (response.isSuccessful){
                    val responseBody = response.body()
                    val notaList = responseBody?.get("nota")
                    notaList?.forEach{ nota ->
                        // Obter dados
                        val emailUtilizador = nota.emailUtilizador
                        val idNota = nota.idNota
                        val titulo = nota.titulo
                        val descricao = nota.descricao
                        val data = nota.data
                        println("Nota details: Utilizador - $emailUtilizador, IDNota - $idNota, Titulo - $titulo, Descricao - $descricao Data - $data")
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
    private fun buscarNotaPorId(context: Context, id: Int){
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
}