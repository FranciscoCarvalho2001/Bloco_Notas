package com.dam.bloco_notas.listaNotas

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.SUCCESS
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dam.bloco_notas.R
import com.dam.bloco_notas.autenticacao.TokenManager
import com.dam.bloco_notas.autenticacao.UtilizadorManager
import com.dam.bloco_notas.models.Nota
import com.dam.bloco_notas.storage.API
import com.dam.bloco_notas.storage.MinhaSharedPreferences
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
            if(titulo.hasFocus()){
                titulo.setText(textoAtualizado)
            } else {
                descricao.setText(textoAtualizado)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rascunho_nota)

        //inicialização das variaveis
        TokenManager.init(applicationContext)
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
                    index < 0 ->sp.guardarNota(listaNota,listaNota.size.toString(),titulo.text.toString(),descricao.text.toString())
                    // Se não é uma Nota existente portanto será atualizada
                    else -> sp.atualizarNota(index,listaNota,titulo.text.toString(),descricao.text.toString())
                }
            }
            CoroutineScope(Dispatchers.Main).launch {
                val intent = Intent(this@RascunhoNota, ListaNotas::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
                finish()
            }

        }

        // Evento ao carregar no Botão guardar
        guardarBtn.setOnClickListener{
            // Se o index é menor significa que é uma nova Nota
            if (index < 0) {
                // Guardar Nota
                sp.guardarNota(listaNota,listaNota.size.toString(),titulo.text.toString(),descricao.text.toString())
                // Guardar o total de Notas
                sp.setTotal(listaNota.size)

            } // Se não é uma Nota existente portanto será atualizada
            else {
                sp.atualizarNota(index,listaNota,titulo.text.toString(),descricao.text.toString())
            }
            CoroutineScope(Dispatchers.Main).launch {
                startActivity(Intent(this@RascunhoNota, ListaNotas::class.java))
                finish()
            }
        }

        // Evento ao carregar no Botão apagar
        apagarBtn.setOnClickListener{
            // Seo index é maior ou igual que 0 significa que é uma Nota existente portanto será apagada
            if (index >= 0) {
                sp.apagarNota(index,listaNota)
            } else {
                // Mudar atividade para a ListaNotas
                startActivity(Intent(this@RascunhoNota, ListaNotas::class.java))
                finish()
            }
            CoroutineScope(Dispatchers.Main).launch {
                // Mudar atividade para a ListaNotas
                startActivity(Intent(this@RascunhoNota, ListaNotas::class.java))
                finish()
            }
        }

        // Speech to Text
        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (status == SUCCESS) {
                textToSpeech.language = Locale("pt", "PT")
            }
        })

        // Evento ao carregar no botão micro para ativar o reconhecimento de fala
        micro.setOnClickListener{
            reconhecimentoDeFala()
        }

    }

    // TEXT TO SPEECH
    private fun reconhecimentoDeFala() {
        // Criação do Intent para o reconhecimento de fala
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        // Defição da lingua para português
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-PT")
        speechRecognitionLauncher.launch(intent)
    }

    // Metodo onDestroy para parar o TextToSpeech
    override fun onDestroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()

        super.onDestroy()
    }

}