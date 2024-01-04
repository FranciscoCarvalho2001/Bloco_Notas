package com.example.bloco_notas

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bloco_notas.listaNotas.ListaNotas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar


class Acerca : AppCompatActivity() {

    private lateinit var gitpagina : TextView
    private lateinit var voltarBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acerca)

        //inicialização das variaveis
        voltarBtn = findViewById(R.id.voltar)
        gitpagina = findViewById(R.id.AcercaGit)

        gitpagina.setOnClickListener {
            val url = "https://github.com/FranciscoCarvalho2001/Bloco_Notas"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        voltarBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                startActivity(Intent(this@Acerca, ListaNotas::class.java))
                finish()
            }
        }

        createCopyright()

//        val paginaGithub = Element()
//        paginaGithub.setTitle("Github do Projeto");
//        paginaGithub.setIconDrawable(R.drawable.logotipo)
//        paginaGithub.setGravity(Gravity.CENTER)
//        paginaGithub.setOnClickListener(View.OnClickListener {
//            val url = "https://github.com/FranciscoCarvalho2001/Bloco_Notas"
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.data = Uri.parse(url)
//            startActivity(intent)
//        })
//
//        val aboutPage = AboutPage(this)
//            .isRTL(false)
//            .setImage(R.drawable.notebox)
//            .setDescription("Uma aplicação de Bloco de Nota feita no âmbito da disciplina Desenvolvimento de Aplicações Movéis")
//            .addItem(Element().setTitle("Versão 1.0"))
//            .addItem(Element().setTitle("Dinis Silva nº23031"))
//            .addItem(Element().setTitle("Francisco Silva nº23035"))
//            .addItem(paginaGithub)
//
//            .addPlayStore("") //Replace all this with your package name
//            .addItem(createCopyright())
//            .create()
//        setContentView(aboutPage)
    }

    private fun createCopyright() {
        val copyright = findViewById<TextView>(R.id.AcercaCopyright)
        @SuppressLint("DefaultLocale") val copyrightString =
            String.format("Copyright %d", Calendar.getInstance().get(Calendar.YEAR))
        copyright.setText(copyrightString)
        copyright.setGravity(Gravity.CENTER)
        copyright.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@Acerca,
                copyrightString,
                Toast.LENGTH_SHORT
            ).show()
        })
    }
}