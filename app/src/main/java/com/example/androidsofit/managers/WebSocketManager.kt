package com.example.androidsofit.managers

import android.util.Log
import android.widget.TextView
import com.example.androidsofit.model.BitCoin
import com.example.androidsofit.model.Currency
import com.example.androidsofit.model.DataSend
import com.google.gson.Gson
import okhttp3.*
import okio.ByteString

class WebSocketManager {
    var mWebSocket: WebSocket? = null
    private lateinit var socketListener: SocketListener
    lateinit var tv_socket: TextView
    private var gson = Gson()


    fun connectToSocket(currency: String){
        val client = OkHttpClient()
        val request: Request = Request.Builder().url("wss://ws.bitstamp.net").build()
        client.newWebSocket(request,object : WebSocketListener(){
            override fun onOpen(webSocket: WebSocket, response: Response) {
                mWebSocket = webSocket
                webSocket.send(gson.toJson(Currency("bts:subscribe",DataSend(currency))))
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d("@@@", "Receiving bytes :$bytes ")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("@@@","Receiving :$text")
                val bitCoin = gson.fromJson(text,BitCoin::class.java)
                socketListener.onSuccess(bitCoin)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("@@@", "onClosing: $code / $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.d("@@@", "onFailure: " +t.message)
                socketListener.onFailure(t.localizedMessage)
            }
        })

        client.dispatcher().executorService().shutdown()
    }

    fun socketListener(socketListener: SocketListener){
        this.socketListener = socketListener
    }

}

interface SocketListener{
    fun onSuccess(bitCoin: BitCoin)
    fun onFailure(message: String)
}