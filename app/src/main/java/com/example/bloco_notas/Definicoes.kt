package com.example.bloco_notas

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
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
import java.io.FileDescriptor
import java.io.IOException


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
    private lateinit var mudarNomeUtilizador : TextView
    private var sp : MinhaSharedPreferences = MinhaSharedPreferences()
    private var frame: ImageView? = null
    private var imageUri: Uri? = null
    private val RESULT_LOAD_IMAGE = 123
    private val IMAGE_CAPTURE_CODE = 654


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
        mudarNomeUtilizador = findViewById(R.id.chNome)
        mudarpass = findViewById(R.id.chPassword)
        btnApagaConta = findViewById(R.id.apagaConta)
        frame= findViewById(R.id.fotoPerfil)
        api= API()
        sp.init(this)

        if(utilizadorEmail.isNotEmpty()){
            nomePerfil.text = utilizadorNome
            nomeUtilizador.setText(utilizadorNome)
            if(UtilizadorManager.buscarImagemPerfil() != null){
                frame?.setImageURI(Uri.parse(UtilizadorManager.buscarImagemPerfil()))
            }
        }else{
            nomePerfil.text = "Convidado"
            nomeUtilizador.visibility = View.GONE
            mudarNomeUtilizador.visibility = View.GONE
            passwordUtilizador.visibility = View.GONE
            mudarpass.visibility = View.GONE
            btnPassword.visibility = View.GONE
            btnNome.visibility = View.GONE
            btnApagaConta.visibility = View.GONE
            frame?.setImageResource(R.drawable.png)
            frame?.isEnabled = false
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

        if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
            checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
            checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED
        ) {
            val permission = arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_MEDIA_IMAGES
            )
            requestPermissions(permission, 112)
        }

        frame?.setOnLongClickListener {
            openCamera()
            true
        }
        frame?.setOnClickListener {
            openGallery()
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
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE)
    }
    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == Activity.RESULT_OK) {
            val bitmap = uriToBitmap(imageUri!!)
            frame?.setImageBitmap(bitmap)
            // Obtém o nome do arquivo da Uri
            val imageName = getFileName(imageUri!!)
        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            val bitmap = uriToBitmap(imageUri!!)
            frame?.setImageBitmap(bitmap)
            // Obtém o nome do arquivo da Uri
            val imageName = getFileName(imageUri!!)
        }
        UtilizadorManager.setImagemPerfil(imageUri.toString())
    }


    @SuppressLint("Range")
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }





    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}
