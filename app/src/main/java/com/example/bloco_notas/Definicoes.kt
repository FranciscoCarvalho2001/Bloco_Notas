package com.example.bloco_notas

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bloco_notas.autenticacao.TokenManager
import com.example.bloco_notas.autenticacao.UtilizadorManager
import com.example.bloco_notas.listaNotas.ListaNotas
import com.example.bloco_notas.storage.API
import com.example.bloco_notas.storage.MinhaSharedPreferences
import com.google.android.material.textfield.TextInputEditText


class Definicoes : AppCompatActivity() {

    private lateinit var nomePerfil : TextView
    private lateinit var utilizadorEmail : String
    private lateinit var nomeUtilizador: TextInputEditText
    private lateinit var passwordUtilizador: TextInputEditText
    private lateinit var mudarpass : TextView
    private lateinit var btnNome : ImageButton
    private lateinit var btnPassword : ImageButton
    private lateinit var acerca : TextView
    private lateinit var api : API
    private lateinit var btnApagaConta : Button
    private lateinit var utilizadorNome : String
    private var sp : MinhaSharedPreferences = MinhaSharedPreferences()

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
        utilizadorNome = UtilizadorManager.buscarUserName().toString()
        mudarpass = findViewById(R.id.chPassword)
        btnApagaConta = findViewById(R.id.apagaConta)
        api= API()
        sp.init(this)

        if(utilizadorEmail.isNotEmpty()){
            nomePerfil.text = utilizadorNome
            nomeUtilizador.setText(utilizadorNome)
        }else{
            nomePerfil.text = utilizadorNome
            nomeUtilizador.setText(utilizadorNome)
            passwordUtilizador.visibility = View.GONE
            mudarpass.visibility = View.GONE
            btnPassword.visibility = View.GONE
            btnApagaConta.visibility = View.GONE
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

        btnApagaConta.setOnClickListener {
            apagarConta()
        }
        Log.e("Definicoes", UtilizadorManager.buscarUserNameList().toString())
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
                UtilizadorManager.setUserName(nomeUtilizador.text.toString())
                nomePerfil.text = UtilizadorManager.buscarUserName().toString();
            })
            // Cria e prepara o botão para responder ao click
            .setNegativeButton("Não", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()})
            // Faz a construção do AlertDialog com todas as configurações
            .create()
            // Exibe
            .show()
    }

    fun mudarPassword() {
        // get alert_dialog.xml view
        val li = LayoutInflater.from(applicationContext)
        val promptsView: View = li.inflate(R.layout.alert_dialog_password, null)
        val alertDialogBuilder = AlertDialog.Builder(this)


        // set alert_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView)
        val userInput = promptsView.findViewById<View>(R.id.etUserInput) as EditText

        // set dialog message
        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, id -> // get user input and set it to result
                // edit text
                Toast.makeText(
                    applicationContext,
                    "Entered: " + userInput.text.toString(),
                    Toast.LENGTH_LONG
                ).show()

                api.atualizarUtilizadorAPI(
                    TokenManager.buscarToken().toString(),
                    UtilizadorManager.buscarID().toString(),
                    utilizadorEmail,
                    passwordUtilizador.text.toString(),
                    this
                )

            }
            .setNegativeButton(
                "Cancelar"
            ) { dialog, id -> dialog.cancel() }

        // create alert dialog
        val alertDialog = alertDialogBuilder.create()

        // show it
        alertDialog.show()
    }

    private fun apagarConta(){
        // Construção do AlertDialog usando padrão Builder - this referencia o contexto
        AlertDialog.Builder(this)
            // Título
            .setTitle("Apagar conta")
            // Mensagem
            .setMessage("Tem a certeza que quer apagar a sua conta?")
            // Cria e prepara o botão para responder ao click
            .setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, id ->
                api.apagarUtilizadorAPI(
                    TokenManager.buscarToken().toString(),
                    UtilizadorManager.buscarID().toString(),
                    this
                )
                UtilizadorManager.apagarUtilizador()
                TokenManager.apagarToken()
                sp.marcarFlag("buscar", true)
                sp.marcarFlag("logado", false)
                finish()
                startActivity(Intent(this@Definicoes, ListaNotas::class.java))
            })
            // Cria e prepara o botão para responder ao click
            .setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, id -> dialog.cancel()})
            // Faz a construção do AlertDialog com todas as configurações

            .create()
            // Exibe
            .show()

    }
}