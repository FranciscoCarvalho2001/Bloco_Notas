package com.example.bloco_notas.autenticacao

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

open class TokenRequest(
    method: Int,
    url: String,
    private val action: String,
    private val email: String,
    private val listener: Response.Listener<String>,
    errorListener: Response.ErrorListener
) : Request<String>(method, url, errorListener) {

    private val token: String? = TokenManager.buscarToken()

    override fun getHeaders(): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        token?.let {
            headers["Authorization"] = "Bearer $it"
        }
        return headers
    }

    override fun getParams(): MutableMap<String, String>? {
        val params = HashMap<String, String>()
        params["action"] = action
        params["email"] = email
        return params
    }

    override fun getBodyContentType(): String {
        return "application/json; charset=utf-8"
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
        return try {
            val jsonString = response?.data?.toString(Charset.defaultCharset())
            Response.success(jsonString ?: "", HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            Response.error(ParseError(e))
        }
    }

    override fun deliverResponse(response: String) {
        listener.onResponse(response)
    }
}