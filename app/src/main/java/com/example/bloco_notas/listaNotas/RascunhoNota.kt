package com.example.bloco_notas.listaNotas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.example.bloco_notas.R
import com.example.bloco_notas.models.Nota
import com.example.bloco_notas.models.NotaWrapper
import com.example.bloco_notas.retrofit.RetrofitInitializer
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

class RascunhoNota : AppCompatActivity() {

    private lateinit var titulo: EditText
    private lateinit var descricao: EditText
    private lateinit var guardarBtn: ImageButton
    private lateinit var apagarBtn: ImageButton
    private lateinit var voltarBtn: ImageButton
    private val listaNota = ArrayList<Nota>()
    private var total: Int = 0
    val token = "23f9c8a5ae45"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rascunho_nota)

        voltarBtn = findViewById(R.id.voltar)
        apagarBtn = findViewById(R.id.apagarNota)
        guardarBtn = findViewById(R.id.guardarNota)
        titulo = findViewById(R.id.tituloCampo)
        descricao = findViewById(R.id.descricaoCampo)

        listaNota.addAll(getNotas())

        val index = intent.getSerializableExtra("object2") as Int

        if (index >= 0) {
            titulo.setText(listaNota[index].titulo)
            descricao.setText(listaNota[index].descricao)
        }

        voltarBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                startActivity(Intent(this@RascunhoNota, ListaNotas::class.java))
            }
        }

        guardarBtn.setOnClickListener{
            if (index < 0) {
                guardarNota()
            } else {
                atualizarNota(index)
            }
        }

        apagarBtn.setOnClickListener{
            if (index >= 0) {
                apagarNota(index)
            } else {
                startActivity(Intent(this@RascunhoNota, ListaNotas::class.java))
            }
        }

        //adicionarNota()
        //atualizarNota(10)
        //apagarNota(11)

    }

    private fun guardarNota() {
        total = getSharedPreferences("Spref", MODE_PRIVATE).getInt("totalNotes", 0)
        val id = total
        total++
        val titulo1 = titulo.text.toString()
        val descricao1 = descricao.text.toString()
        val data = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())
        getSharedPreferences("Spref", MODE_PRIVATE).edit().putInt("totalNotes", total).apply()
        val newNote = Nota("boi@ipt.pt",id, titulo1, descricao1,"$data", null)
        listaNota.add(newNote)
        salvarNotas(listaNota)
        startActivity(Intent(this@RascunhoNota, ListaNotas::class.java))
    }

    private fun atualizarNota(index: Int) {
        val data = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())
        val newNote = Nota("boi@ipt.pt",listaNota[index].idNota, titulo.text.toString(), descricao.text.toString(),"$data", null)
        listaNota[index] = newNote
        salvarNotas(listaNota)
        startActivity(Intent(this@RascunhoNota, ListaNotas::class.java))
    }

    private fun apagarNota(index: Int) {
        listaNota.remove(listaNota[index])
        salvarNotas(listaNota)
        startActivity(Intent(this@RascunhoNota, ListaNotas::class.java))
    }

    private fun salvarNotas(notes: List<Nota>) {
        val gson = Gson()
        val json = gson.toJson(notes)
        val sharedPreferences = getSharedPreferences("Spref", MODE_PRIVATE)
        sharedPreferences.edit().putString("notas", json).apply()
    }
    private fun getNotas(): List<Nota> {
        val gson = Gson()
        val sharedPreferences = getSharedPreferences("Spref", MODE_PRIVATE)
        val json = sharedPreferences.getString("notas", "")
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<Nota>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        }
    }

    // obtem os dados da nota para guardar
    private fun adicionarNotaAPI(){
        val data = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())
        var nota = Nota("algo@algo.pt", 1, "Titulo", "Descrição", "$data", null)
        addNotaAPI(nota) {
            if (it){
                Toast.makeText(this@RascunhoNota, "Adicionado", Toast.LENGTH_SHORT).show()
                Log.e("Resposta", "Response: $it")
            } else {
                Toast.makeText(this@RascunhoNota, "Não Adicionado", Toast.LENGTH_SHORT).show()
                Log.e("Resposta", "Response: $it")
            }
        }
    }
    // adiciona um utilizador na base de dados
    private fun addNotaAPI(nota: Nota, onResult: (Boolean) -> Unit) {
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
    private fun atualizarNotaAPI(id: Int){
        val data = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())
        var nota = Nota("algo@algo.pt", 1, "Titulo22222222", "Descrição22222222", "$data", null)
        val notaWrapper = NotaWrapper(nota)
        updateNotaAPI(id, notaWrapper)
    }
    private fun updateNotaAPI(id: Int, notaWrapper: NotaWrapper){
        val call = RetrofitInitializer()
            .notaService()
            .atualizarNota(autorizacao = "Bearer $token", id, notaWrapper)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                // Log detalhes da resposta
                Toast.makeText(this@RascunhoNota, "Atualizado", Toast.LENGTH_SHORT).show()
                Log.d("API_CALL_SUCCESS", "Atualizado")
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }
    // pede á API para apagar uma nota, fornecendo um ID
    private fun apagarNotaAPI(id: Int){
        val call = RetrofitInitializer()
            .notaService()
            .apagarNota(autorizacao = "Bearer $token", id)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                // Log detalhes da resposta
                Toast.makeText(this@RascunhoNota, "Apagado", Toast.LENGTH_SHORT).show()
                Log.d("API_CALL_SUCCESS", "Apagado")
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
                Log.e("API_CALL_FAILURE", "API call failed: ${t.message}")
            }
        })
    }

}