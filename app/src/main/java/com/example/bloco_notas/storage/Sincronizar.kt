package com.example.bloco_notas.storage

import android.content.Context
import android.util.Log
import com.example.bloco_notas.autenticacao.TokenManager
import com.example.bloco_notas.models.Nota

class Sincronizar {
    // Criação das variaveis
    private val sp: MinhaSharedPreferences = MinhaSharedPreferences()
    private val api: API by lazy { API() }

    // Inicializa a SharedPreferences
    fun init(context: Context) {
        sp.init(context)
    }

    fun sync(context: Context):Boolean {
        // Obtem as listas de notas da SharedPreferences e da API
        val notasSp = sp.getNotas()
        val notasApi = sp.getNotasAPISP()

        // Listas para armazenar notas a serem adicionadas, atualizadas e removidas
        val notasParaAdicionar = mutableListOf<Nota>()
        val notasParaAtualizar = mutableListOf<Nota>()
        val notasParaRemover = mutableListOf<Nota>()

        // Itere sobre as notas das SharedPreferences
        notasSp.forEach { notaSp ->
            // Encontra a nota correspondente na lista da API
            val notaApi = notasApi.find { it?.idNota == notaSp.idNota }

            // Se a nota não existir na lista da API, adicione-a à lista de notas para adicionar
            if (notaApi == null) {
                notasParaAdicionar.add(notaSp)
            } else {
                // Se a nota existe na lista da API, verifique se precisa ser atualizada e se sim, adicione-a à lista de notas para atualizar
                if (notaSp.titulo != notaApi.titulo || notaSp.descricao != notaApi.descricao) {
                    notasParaAtualizar.add(notaSp)
                }
            }
        }

        // Itere sobre as notas da API
        notasApi.forEach { notaApi ->
            // Se a nota da API não existe nas SharedPreferences, adiciona-a à lista de notas para remover
            if (notasSp.none { it.idNota == notaApi.idNota }) {
                notasParaRemover.add(notaApi)
            }
        }

        // Adiciona as notas que precisam ser adicionadas
        notasParaAdicionar.forEach { notaParaAdicionar ->
            api.adicionarNotaAPI(
                notaParaAdicionar.idNota,
                notaParaAdicionar.emailUtilizador,
                notaParaAdicionar.titulo,
                notaParaAdicionar.descricao,
                TokenManager.buscarToken().toString(),
                context
            )
            Log.d("Response", "sync: add")
        }

        // Atualiza as notas que precisam ser atualizadas
        notasParaAtualizar.forEach { notaParaAtualizar ->
            val notaApi = notasApi.find { it?.idNota == notaParaAtualizar.idNota }
            notaApi?.let {
                api.atualizarNotaAPI(
                    it.id.toString(),
                    it.emailUtilizador,
                    it.idNota,
                    notaParaAtualizar.titulo,
                    notaParaAtualizar.descricao,
                    TokenManager.buscarToken().toString()
                )
                Log.d("Response", "sync: update + ${it.emailUtilizador}")
            }
        }

        // Remove as notas que precisam ser removidas
        notasParaRemover.forEach { notaParaRemover ->
            // é utilizado um método diferente para remover a nota da API devido a limitações da API
            api.atualizarNotaAPI(notaParaRemover.id.toString(), "dummy", "dummy", "dummy", "dummy", TokenManager.buscarToken().toString())
            Log.d("Response", "sync: delete")
        }
        return true
    }


}