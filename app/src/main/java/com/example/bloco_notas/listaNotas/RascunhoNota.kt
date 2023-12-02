package com.example.bloco_notas.listaNotas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.bloco_notas.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RascunhoNota : AppCompatActivity() {

    private lateinit var guardarBtn: ImageButton
    private lateinit var apagarBtn: ImageButton
    private lateinit var voltarBtn: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rascunho_nota)

        voltarBtn = findViewById(R.id.voltar)
        apagarBtn = findViewById(R.id.apagarNota)
        guardarBtn = findViewById(R.id.guardarNota)

        voltarBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                startActivity(Intent(this@RascunhoNota, ListaNotas::class.java))
            }
        }

        guardarBtn.setOnClickListener{

        }

        apagarBtn.setOnClickListener{
            
        }
    }
}