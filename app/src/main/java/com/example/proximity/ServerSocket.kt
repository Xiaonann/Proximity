package com.example.proximity

import android.app.IntentService
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.app.JobIntentService
import android.util.Log
import java.io.*
import java.net.ServerSocket
import java.net.Socket

/**
 * normal server using blocking IO means always waiting client's
 * request then starting communication
 * to improve 1 NIO(more suitable for big project??) 低负载、低并发的应用程序可以选择同步阻塞 I/O 以降低编程复杂度
 * 2 Rxjava (easy to understand event drive)or
 * kotlin coroutines (consume less resource) response time?
 *First using RXjava(5.31)
 */


private const val port = 8080

class SocketServerService : JobIntentService(){


    private var server: ServerSocket? = null
    private var input: InputStream? = null
    private var output: OutputStream? = null
    private var socket: Socket? = null
    var sendMsg: String? = null
    var receiveMsg: String? = null
    private var broadcastReceiver : BroadcastReceiver? = null

    // initial server socket
    init {
        try {
            server = ServerSocket(port)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
    companion object{

        private const val jobId = 7777
        fun enqueueWork(context: Context, work: Intent = Intent()) {
            enqueueWork(context, SocketServerService::class.java, jobId, work)
        }
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    override fun onHandleWork(p0: Intent) {
        //val test = registerReceiver()
        Log.d("ServerSocketXXX","success")
        //beginListen()


    }
    override fun onStopCurrentWork(): Boolean {
        Log.d("stop","stop")
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ServerSocket","onDestory")

    }

    // build a server socket with specific port

    //listen data from socket
    fun beginListen() {
        try {
            //try to connect with client socket
            socket = server?.accept()
            // get data from client socket
            try {
                input = socket?.getInputStream()
                input?.let {
                    val inputStream = DataInputStream(it)
                    receiveMsg = inputStream.readUTF()
                    Log.d("Msg from client", "$receiveMsg")

                    if (receiveMsg != null) {
                        output = socket?.getOutputStream()
                        output?.let {
                            val out = DataOutputStream(it)
                            //test text
                            sendMsg = "12"
                            //val testText = "proximity"
                            out.writeUTF(sendMsg)
                            out.flush()
                        }

                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            input?.close()
            output?.close()
            socket?.close()
            // can't close server yes?
            //server?.close()
        }
    }


    //

}