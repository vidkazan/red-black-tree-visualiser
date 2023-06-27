package com.example.rbt

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import java.net.ConnectException

class Client {
    private val client = HttpClient(Android){
        expectSuccess = true
    }

    private var serverIp = "192.168.68.227"
    private var serverPort = "2001"

    fun setIp(string: String){
        serverIp = string
    }
    fun setPort(string: String){
        serverPort = string
    }
    fun addNodeRequestSend(nodeKey: String){
        try {
            runBlocking {
                val httpResponse: HttpResponse = client.post("http://$serverIp:$serverPort/edit_tree/a.out") {
                    setBody("mode=add&key=$nodeKey")
                }
                if (httpResponse.status.value in 200..299) {
                    Log.v(">>>POST ADD"," 200!")
                }else {
                    Log.v(">>>POST ADD"," 500!")
                    throw ServerResponseException(httpResponse, "POST ADD 500")
                }
            }
        }
        catch (e: ConnectException){}
        catch (e: ServerResponseException){}
    }
    fun eraseNodeRequestSend(nodeKey: String){
        runBlocking {
            try {
                val httpResponse: HttpResponse = client.post("http://$serverIp:$serverPort/edit_tree/a.out") {
                    setBody("mode=erase&key=$nodeKey")
                }
                if (httpResponse.status.value in 200..299) {
                    Log.v(">>>POST ERASE"," 200!")
                } else {
                    Log.v(">>>POST ERASE"," 500!")
                    throw ServerResponseException(httpResponse, "POST ERASE 500")
                }
            }
            catch (e: ConnectException){}
            catch (e: ServerResponseException){}
        }
    }
    fun clearAllNodeRequestSend(){
        try {
            runBlocking {
                val httpResponse: HttpResponse = client.post("http://$serverIp:$serverPort/clear_tree/a.out") {
                }
                if (httpResponse.status.value !in 200..299) {
                    Log.v(">>>POST"," 500!")
                    throw ServerResponseException(httpResponse, "POST CLEAR 500")
                }
            }
        }
        catch (e: ConnectException){}
        catch (e: ServerResponseException){}
    }
    fun getTreeRequestSendStd(): String {
        val client = HttpClient()
        var response = ""
        Log.v(">>>GET tree std","start")
        try {
            runBlocking {
                val httpResponse: HttpResponse = client.get("http://$serverIp:$serverPort/get_tree_std")
                response = httpResponse.body()
            }
        }
        catch (e: ConnectException){
            response = "ConnectException"
        }
        catch (e: ServerResponseException){
            response = "ServerResponseException"
        }
        Log.v(">>>GET tree std","start 200")
        return response
    }
    fun getTreeRequestSendFt(): String {
        val client = HttpClient()
        var response = ""

        try {
            runBlocking {
                val httpResponse: HttpResponse = client.get("http://$serverIp:$serverPort/get_tree_ft")
                response = httpResponse.body()
            }
        } catch (e: ConnectException){
            response = "ConnectException"
        } catch (e: ServerResponseException){
            response = "ServerResponseException"
        }
        return response
    }
}