package com.example.bloco_notas.storage

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.widget.Toast
import com.example.bloco_notas.autenticacao.TokenManager
import com.example.bloco_notas.models.Nota

class Sincronizar {
    private val sp: MinhaSharedPreferences = MinhaSharedPreferences()
    private val api: API = API()

    private val handlerThread = HandlerThread("SyncHandlerThread")
    private lateinit var handler: Handler

    private val syncIntervalMillis = 1 * 60 * 1000L  // 2 minutos em milissegundos

    fun init(context: Context) {
        sp.init(context)

        // Inicializa o HandlerThread e Handler
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    // Inicia a sincronização repetida a cada dois minutos
    fun iniciarSincronizacao(context: Context) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                sync(context)
                handler.postDelayed(this, syncIntervalMillis)
            }
        }, syncIntervalMillis)
    }

    // Para a sincronização
    fun pararSincronizacao() {
        handler.removeCallbacksAndMessages(null)
    }

    fun sync(context: Context):Boolean {
        // Obtenha as listas de notas dos SharedPreferences e da API
        val notasSp = sp.getNotas()
        val notasApi = sp.getNotasAPISP()

        // Listas para armazenar notas a serem adicionadas, atualizadas e removidas
        val notasParaAdicionar = mutableListOf<Nota>()
        val notasParaAtualizar = mutableListOf<Nota>()
        val notasParaRemover = mutableListOf<Nota>()

        // Itere sobre as notas dos SharedPreferences
        notasSp.forEach { notaSp ->
            // Encontre a nota correspondente na lista da API
            val notaApi = notasApi.find { it?.idNota == notaSp.idNota }

            // Se a nota não existir na lista da API, adicione-a
            if (notaApi == null) {
                notasParaAdicionar.add(notaSp)
            } else {
                // Se a nota existe na lista da API, verifique se precisa ser atualizada
                if (notaSp.titulo != notaApi.titulo || notaSp.descricao != notaApi.descricao) {
                    notasParaAtualizar.add(notaSp)
                }
            }
        }

        // Itere sobre as notas da API
        notasApi.forEach { notaApi ->
            // Se a nota da API não existe nas SharedPreferences, marque-a para remoção
            if (notasSp.none { it.idNota == notaApi.idNota }) {
                notasParaRemover.add(notaApi)
            }
        }

        // Adicione as notas que precisam ser adicionadas
        notasParaAdicionar.forEach { notaParaAdicionar ->
            api.adicionarNotaAPI(
                notaParaAdicionar.idNota,
                notaParaAdicionar.emailUtilizador,
                notaParaAdicionar.titulo,
                notaParaAdicionar.descricao,
                TokenManager.buscarToken().toString(),
                context
            )
            Toast.makeText(context, "add", Toast.LENGTH_SHORT).show()
            Log.e("Response", "sync: add")
        }

        // Atualize as notas que precisam ser atualizadas
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
                Toast.makeText(context, "up", Toast.LENGTH_SHORT).show()
                Log.e("Response", "sync: update + ${it.emailUtilizador}")
            }
        }

        // Remova as notas que precisam ser removidas
        notasParaRemover.forEach { notaParaRemover ->
            //api.apagarNotaAPI(notaParaRemover.id.toString(), TokenManager.buscarToken().toString())
            api.atualizarNotaAPI(notaParaRemover.id.toString(), "dummy", "dummy", "dummy", "dummy", TokenManager.buscarToken().toString())
            Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show()
            Log.e("Response", "sync: delete")
        }
        return true
    }


}