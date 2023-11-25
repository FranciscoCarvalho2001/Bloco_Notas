package com.example.bloco_notas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListaNotas : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_notas)
        // use arrayadapter and define an array
        val arrayAdapter: ArrayAdapter<*>
        var user :ArrayList<String> = arrayListOf( "Nota1","Nota2","Nota3","Nota4","Nota5","Nota6")
        // Acessa
        var mListView = findViewById<ListView>(R.id.listaNotas)
        arrayAdapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, user)
        mListView.adapter = arrayAdapter
        val fab: FloatingActionButton = findViewById(R.id.Adicionar)
        fab.setOnClickListener(){
            adicionar(user)
            mListView.adapter = arrayAdapter
        }
    }

    fun adicionar(user:ArrayList<String>){
        user.add(user.size,"Nota${user.size}")
    }
}