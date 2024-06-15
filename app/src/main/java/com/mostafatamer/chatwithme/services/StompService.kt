package com.mostafatamer.chatwithme.services

import com.google.gson.Gson
import io.reactivex.CompletableObserver
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage


class StompService {
    private val completableObserver = object : CompletableObserver {
        //TODO: check if this is needed
        override fun onSubscribe(d: Disposable) {
            println("on sub")
            println(d.isDisposed)
        }

        override fun onComplete() {
            println("on comp")
        }

        override fun onError(e: Throwable) {
            println("on error ${e.message}")
        }

    }

    private lateinit var stompClient: StompClient

    fun init(stompClient: StompClient) {
        if (this::stompClient.isInitialized) {
            this.stompClient.disconnect()
        }

        this.stompClient = stompClient
    }

    fun connect() {
        stompClient.connect()
    }

    fun isStompConnected() = stompClient.isConnected

    fun lifecycle(onLifecycleEventTypeChange: (LifecycleEvent.Type) -> Unit): Disposable? {
        val lifecycle: Flowable<LifecycleEvent> = stompClient.lifecycle()
        return lifecycle.subscribe { lifecycleEvent ->
            lifecycleEvent.type?.let { lifecycleEventType: LifecycleEvent.Type ->
                onLifecycleEventTypeChange(lifecycleEventType)
            }
        }
    }

    fun topicListener(
        topic: String,
        onSubscribe: () -> Unit = {},
        onStompMessage: (StompMessage) -> Unit,
    ): Disposable? {
        val stompMessage: Flowable<StompMessage> = stompClient.topic(topic)
            .doOnSubscribe {
                onSubscribe.invoke()
            }

        return stompMessage.subscribe { message ->
            onStompMessage(message)
        }
    }

    fun <T> send(topic: String, data: T) {
        val jsonString = Gson().toJson(data)
        println("Sending $jsonString")
        println(stompClient.isConnected)
        stompClient.send(topic, jsonString)
            .subscribe(completableObserver)
    }

    fun sendText(topic: String, data: String) {
        stompClient.send(topic, data)
            .subscribe(completableObserver)
    }

    fun disconnect() {
        stompClient.disconnect()
    }

    fun isInitialized() = ::stompClient.isInitialized
}