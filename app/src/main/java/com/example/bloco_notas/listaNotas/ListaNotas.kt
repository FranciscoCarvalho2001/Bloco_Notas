package com.example.bloco_notas.listaNotas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.bloco_notas.models.Nota
import com.example.bloco_notas.R
import com.example.bloco_notas.models.NotaWrapper
import com.example.bloco_notas.retrofit.RetrofitInitializer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date

class ListaNotas : AppCompatActivity() {

    private val notaLista = ArrayList<Nota>() // Lista de objetos Nota
    private lateinit var adapter: ListaNotasAdapter
    private lateinit var ListaDeNotas : RecyclerView
    private var index: Int=0
    val token = "23f9c8a5ae45"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_notas)

        ListaDeNotas = findViewById(R.id.note_list_recyclerview)

        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        ListaDeNotas.layoutManager = layoutManager
        ListaDeNotas.addItemDecoration(DecoracaoEspacoItem(this))
        adapter = ListaNotasAdapter(notaLista, this) { clickedNote ->
            Toast.makeText(this, "Item clicado: ${clickedNote.titulo}", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RascunhoNota::class.java)
            index =notaLista.indexOf(clickedNote)
            intent.putExtra("object2", index)
            startActivity(intent)
        }
        ListaDeNotas.adapter = adapter

        notaLista.clear()
        notaLista.addAll(getNotas())
        adapter.notifyDataSetChanged()

        val fab: FloatingActionButton = findViewById(R.id.Adicionar)
        fab.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val intent = Intent(this@ListaNotas, RascunhoNota::class.java)
                index=-1
                intent.putExtra("object2",index)
                startActivity(intent)
            }
            //buscarNotas()
            //buscarNotaPorId(2)
        }
    }
    private fun getNotas(): List<Nota> {
        val gson = Gson()

        val sharedPreferences = getSharedPreferences("Spref", MODE_PRIVATE)
        val json = sharedPreferences.getString("notas", "")
        if (json.isNullOrEmpty()) {
            // A string JSON é nula ou vazia, retornar uma lista vazia ou tratar conforme necessário
            return emptyList()
        }
        // Usar o TypeToken para preservar o tipo genérico List<Note>
        val type = object : TypeToken<List<Nota>>() {}.type

        // Converta o JSON string de volta para uma List<Note>
        return gson.fromJson(json, type) ?: emptyList()
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
    private fun buscarNotaPorId(id: Int){
        val call = RetrofitInitializer()
            .notaService()
            .buscarNota(autorizacao = "Bearer $token", id)
        call.enqueue(object : Callback<NotaWrapper> {
            override fun onResponse(
                call: Call<NotaWrapper>,
                response: Response<NotaWrapper>
            ) {
                val dialogBuilder = AlertDialog.Builder(this@ListaNotas)
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