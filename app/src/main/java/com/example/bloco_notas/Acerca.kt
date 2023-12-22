package com.example.bloco_notas

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import java.lang.String
import java.util.Calendar


class Acerca : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acerca)

        val paginaGithub = Element()
        paginaGithub.setTitle("Github do Projeto");
        paginaGithub.setIconDrawable(R.drawable.logotipo)
        paginaGithub.setGravity(Gravity.CENTER)
        paginaGithub.setOnClickListener(View.OnClickListener {
            val url = "https://github.com/FranciscoCarvalho2001/Bloco_Notas"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        })

        val aboutPage = AboutPage(this)
            .isRTL(false)
            .setImage(R.drawable.notebox)
            .setDescription("Uma aplicação de Bloco de Nota feita no âmbito da disciplina Desenvolvimento de Aplicações Movéis")
            .addItem(Element().setTitle("Versão 1.0"))
            .addItem(Element().setTitle("Dinis Silva nº23031"))
            .addItem(Element().setTitle("Francisco Silva nº23035"))
            .addItem(paginaGithub)

            .addPlayStore("") //Replace all this with your package name
            .addItem(createCopyright())
            .create()
        setContentView(aboutPage)
    }

    private fun createCopyright(): Element? {
        val copyright = Element()
        @SuppressLint("DefaultLocale") val copyrightString =
            String.format("Copyright %d", Calendar.getInstance().get(Calendar.YEAR))
        copyright.setTitle(copyrightString)
        // copyright.setIcon(R.mipmap.ic_launcher);
        copyright.setGravity(Gravity.CENTER)
        copyright.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@Acerca,
                copyrightString,
                Toast.LENGTH_SHORT
            ).show()
        })
        return copyright
    }
}