package com.example.bloco_notas.ListaNotas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.bloco_notas.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListaNotas : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_notas)

        var user :ArrayList<String> = arrayListOf( "Nota1","Nota2","Nota3","Nota4","Nota5","Nota6")

        val fab: FloatingActionButton = findViewById(R.id.Adicionar)
        fab.setOnClickListener(){
            adicionar(user)
        }
    }

    fun adicionar(user:ArrayList<String>){
        user.add(user.size,"Nota${user.size}")
    }
}