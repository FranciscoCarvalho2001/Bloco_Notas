package com.example.bloco_notas.listaNotas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import com.example.bloco_notas.R
import com.example.bloco_notas.models.Nota
import com.example.bloco_notas.storage.API
import com.example.bloco_notas.storage.MinhaSharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RascunhoNota : AppCompatActivity() {

    // Criação das variaveis
    private lateinit var titulo: EditText
    private lateinit var descricao: EditText
    private lateinit var guardarBtn: ImageButton
    private lateinit var apagarBtn: ImageButton
    private lateinit var voltarBtn: ImageButton
    private val listaNota = ArrayList<Nota>()
    private lateinit var api :API
    private lateinit var sp : MinhaSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rascunho_nota)

        //inicialização das variaveis
        voltarBtn = findViewById(R.id.voltar)
        apagarBtn = findViewById(R.id.apagarNota)
        guardarBtn = findViewById(R.id.guardarNota)
        titulo = findViewById(R.id.tituloCampo)
        descricao = findViewById(R.id.descricaoCampo)
        sp=MinhaSharedPreferences()
        sp.init(this)
        listaNota.addAll(sp.getNotas())

        // Obtém o índice do extra "objeto" da Intent
        val index = intent.getSerializableExtra("objeto") as Int

        // Se o index for maior ou igual a 0 significa que a atividade foi iniciada ao carregar numa Nota da lista logo as editView receberam os valores dessa nota
        if (index >= 0) {
            titulo.setText(listaNota[index].titulo)
            descricao.setText(listaNota[index].descricao)
        }

        // Evento ao sair do RascunhoNota para a ListaNota
        voltarBtn.setOnClickListener {
            // Caso as editView estejam preenchidas
            if(!titulo.text.isEmpty() && !descricao.text.isEmpty()){
                when {
                    // Se o index é menor significa que é uma nova Nota
                    index < 0 ->sp.guardarNota(listaNota,titulo.text.toString(),descricao.text.toString())
                    // Se não é uma Nota existente portanto será atualizada
                    else -> sp.atualizarNota(index,listaNota,titulo.text.toString(),descricao.text.toString())
                }
            }
            CoroutineScope(Dispatchers.Main).launch {
                startActivity(Intent(this@RascunhoNota, ListaNotas::class.java))
            }

        }

        // Evento ao carregar no Botão guardar
        guardarBtn.setOnClickListener{
            // Se o index é menor significa que é uma nova Nota
            if (index < 0) {
                sp.guardarNota(listaNota,titulo.text.toString(),descricao.text.toString())
                //api.adicionarNotaAPI()
            } // Se não é uma Nota existente portanto será atualizada
            else {
                sp.atualizarNota(index,listaNota,titulo.text.toString(),descricao.text.toString())
                //api.atualizarNotaAPI(index)
            }
            CoroutineScope(Dispatchers.Main).launch {
                startActivity(Intent(this@RascunhoNota, ListaNotas::class.java))
            }
        }

        // Evento ao carregar no Botão apagar
        apagarBtn.setOnClickListener{
            // Seo index é maior ou igual que 0 significa que é uma Nota existente portanto será apagada
            if (index >= 0) {
                sp.apagarNota(index,listaNota)
                //api.apagarNotaAPI(index)
            } else {
                startActivity(Intent(this@RascunhoNota, ListaNotas::class.java))
            }
            CoroutineScope(Dispatchers.Main).launch {
                startActivity(Intent(this@RascunhoNota, ListaNotas::class.java))
            }
        }

    }

}