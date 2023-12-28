package com.example.bloco_notas

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.bloco_notas.autenticacao.UtilizadorManager
import com.google.android.material.textfield.TextInputEditText

class Definicoes : AppCompatActivity() {

    private lateinit var nomePerfil : TextView
    private lateinit var utilizadorEmail : String
    private lateinit var nomeUtilizador: TextInputEditText
    private lateinit var passwordUtilizador: TextInputEditText
    private lateinit var btnNome : ImageButton
    private lateinit var btnPassword : ImageButton
    private lateinit var acerca : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_definicoes)

        nomePerfil = findViewById(R.id.nome)
        nomeUtilizador = findViewById(R.id.edNome)
        passwordUtilizador = findViewById(R.id.edPassword)
        btnNome = findViewById(R.id.btNome)
        btnPassword = findViewById(R.id.btnPassword)
        acerca = findViewById(R.id.defAcerca)
        utilizadorEmail = UtilizadorManager.buscarEMAIL().toString()

        if(utilizadorEmail.isNotEmpty()){
            nomePerfil.text = utilizadorEmail
        }else{
            nomePerfil.text = "Convidado"
        }

        btnNome.setOnClickListener {
            mudarNome()
        }

        btnPassword.setOnClickListener {
            mudarPassword()
        }

        acerca.setOnClickListener {
            startActivity(Intent(this, Acerca::class.java))
            finish()
        }
    }

    private fun mudarNome(){
        // Construção do AlertDialog usando padrão Builder - this referencia o contexto
        AlertDialog.Builder(this)
            // Título
            .setTitle("Mudar nome de utilizador")
            // Mensagem
            .setMessage("Tem a certeza que quer mudar o nome de utilizador?")
            // Cria e prepara o botão para responder ao click
            .setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, id ->
            })
            // Cria e prepara o botão para responder ao click
            .setNegativeButton("Não", DialogInterface.OnClickListener { dialog, id -> dialog.cancel()})
            // Faz a construção do AlertDialog com todas as configurações
            .create()
            // Exibe
            .show()
    }

    private fun mudarPassword(){
        // Construção do AlertDialog usando padrão Builder - this referencia o contexto
        AlertDialog.Builder(this)
            // Título
            .setTitle("Mudar Password")
            // Mensagem
            .setMessage("Tem a certeza que quer mudar de Password?")
            // Cria e prepara o botão para responder ao click
            .setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, id ->

            })
            // Cria e prepara o botão para responder ao click
            .setNegativeButton("Não", DialogInterface.OnClickListener { dialog, id -> dialog.cancel()})
            // Faz a construção do AlertDialog com todas as configurações
            .create()
            // Exibe
            .show()
    }
}