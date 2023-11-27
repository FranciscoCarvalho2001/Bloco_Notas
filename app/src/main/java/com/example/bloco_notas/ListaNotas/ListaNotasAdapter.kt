package com.example.bloco_notas.ListaNotas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bloco_notas.Models.Nota
import com.example.bloco_notas.R

class ListaNotasAdapter(private val notas: ArrayList<Nota>, private val context: Context,  private val itemClickListener: (Nota) -> Unit) :
    RecyclerView.Adapter<ListaNotasAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nota = notas[position]
        holder?.let {
            it.bindView(nota)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.nota_item, parent, false)
        view.width
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notas.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(nota: Nota) {
            val title: TextView = itemView.findViewById(R.id.notaItemTitulo)
            val description: TextView = itemView.findViewById(R.id.notaItemDescricao)
            title.text = nota.titulo
            description.text = nota.descricao
        }
        init {
            // Set an OnClickListener for the entire item view
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val note = notas[position]
                    itemClickListener(note)
                }
            }
        }
    }
}