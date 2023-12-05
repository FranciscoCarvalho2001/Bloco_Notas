package com.example.bloco_notas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bloco_notas.autenticacao.Registar
import com.example.bloco_notas.listaNotas.ListaNotas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000L)
            startActivity(Intent(this@SplashScreen, Registar::class.java))
            finish()
        }
    }
}