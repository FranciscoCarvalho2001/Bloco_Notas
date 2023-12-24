package com.example.bloco_notas.listaNotas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.SUCCESS
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.bloco_notas.R
import com.example.bloco_notas.autenticacao.TokenManager
import com.example.bloco_notas.autenticacao.UtilizadorManager
import com.example.bloco_notas.models.Nota
import com.example.bloco_notas.storage.API
import com.example.bloco_notas.storage.MinhaSharedPreferences
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale


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
    private lateinit var utilizadorEmail :String
    // Speech to text
    private lateinit var micro: FloatingActionButton
    private lateinit var textToSpeech: TextToSpeech
    private val speechRecognitionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val textoReconhecido = results?.get(0) ?: ""

            val texto = descricao.text.toString()
            val textoAtualizado : String
            if(texto.isEmpty()){
                textoAtualizado = "$texto$textoReconhecido"
            } else {
                textoAtualizado = "$texto $textoReconhecido"
            }
            
            // para Texto
            descricao.setText(textoAtualizado)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rascunho_nota)

        TokenManager.init(applicationContext)

        //inicialização das variaveis
        voltarBtn = findViewById(R.id.voltar)
        apagarBtn = findViewById(R.id.apagarNota)
        guardarBtn = findViewById(R.id.guardarNota)
        titulo = findViewById(R.id.tituloCampo)
        descricao = findViewById(R.id.descricaoCampo)
        micro = findViewById(R.id.floating_micro)
        sp=MinhaSharedPreferences()
        sp.init(this)
        listaNota.addAll(sp.getNotas())
        api=API()
        utilizadorEmail= UtilizadorManager.buscarEMAIL().toString()

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
                    index < 0 ->sp.guardarNota(listaNota,sp.getTotal().toString(),titulo.text.toString(),descricao.text.toString())
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
                var total =sp.getTotal()
//                api.adicionarNotaAPI("${total}",utilizadorEmail,"${titulo.text}","${descricao.text}","${TokenManager.buscarToken()}",this)
                Toast.makeText(this, "$total", Toast.LENGTH_SHORT).show()
                sp.guardarNota(listaNota,total.toString(),titulo.text.toString(),descricao.text.toString())
                total++
                sp.setTotal(total)

            } // Se não é uma Nota existente portanto será atualizada
            else {
//                api.atualizarNotaAPI("${listaNota[index].id}",listaNota[index].emailUtilizador,"$index","${titulo.text}","${descricao.text}","${TokenManager.buscarToken()}")
                sp.atualizarNota(index,listaNota,titulo.text.toString(),descricao.text.toString())

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
//                api.apagarNotaAPI("${TokenManager.buscarToken()}","${listaNota[index].id}")
            } else {
                startActivity(Intent(this@RascunhoNota, ListaNotas::class.java))
            }
            CoroutineScope(Dispatchers.Main).launch {
                startActivity(Intent(this@RascunhoNota, ListaNotas::class.java))
            }
        }

        // Speech to Text
        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (status == SUCCESS) {
                textToSpeech.language = Locale("pt", "PT")
            }
        })

        micro.setOnClickListener{
            reconhecimentoDeFala()
        }

    }

    // TEXT TO SPEECH
    private fun reconhecimentoDeFala() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-PT")
        speechRecognitionLauncher.launch(intent)
    }
    override fun onDestroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()

        super.onDestroy()
    }

}