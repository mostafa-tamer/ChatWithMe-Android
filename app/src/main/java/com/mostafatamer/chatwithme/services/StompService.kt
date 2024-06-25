package com.mostafatamer.chatwithme.services

import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import io.reactivex.CompletableObserver
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage


class StompService(private val stompClient: StompClient, private val gson: Gson) {
    private val lazyInvokedTasks = mutableListOf<() -> Any>()

    private val mainHandler = Handler(Looper.getMainLooper())

    private val completableObserver = object : CompletableObserver {
        override fun onSubscribe(d: Disposable) {
            println("onSubscribe")
//            println("isDisposed " + d.isDisposed)
        }

        override fun onComplete() {
            println("onComplete")
        }

        override fun onError(e: Throwable) {
            println("on error ${e.message}")
        }
    }

    init {
        lifecycle { type ->
            when (type) {
                LifecycleEvent.Type.OPENED -> {
                    println("${stompClient.hashCode()} stomp connected")
                    mainHandler.post {
                        if (lazyInvokedTasks.isNotEmpty()) {
                            lazyInvokedTasks.forEach {
                                println("task ${it.hashCode()} is done")
                                it.invoke()
                            }
                            lazyInvokedTasks.clear()
                        }
                    }
                }

                LifecycleEvent.Type.ERROR -> {
                }

                LifecycleEvent.Type.CLOSED -> {
                    println("${stompClient.hashCode()} stomp disconnected")
                }

                LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                    println("${stompClient.hashCode()} FAILED_SERVER_HEARTBEAT")
                }
            }

        }
    }

    fun connect() {
        stompClient.connect()
    }

    fun isStompConnected() = stompClient.isConnected

    fun lifecycle(onLifecycleEventTypeChange: (LifecycleEvent.Type) -> Unit): Disposable {
        val lifecycle: Flowable<LifecycleEvent> = stompClient.lifecycle()
        return lifecycle
            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { lifecycleEvent ->
                lifecycleEvent.type?.let { lifecycleEventType: LifecycleEvent.Type ->
                    onLifecycleEventTypeChange(lifecycleEventType)
                }
            }
    }

    fun <T> topicListener(
        topic: String,
        clazz: Class<T>,
        onSubscribe: () -> Unit = {},
        onStompMessage: (T) -> Unit,
    ) {
        val task = {

            val stompMessageFlowable: Flowable<StompMessage> = stompClient.topic(topic)
                .doOnSubscribe { onSubscribe.invoke() }

            stompMessageFlowable.subscribe { message ->
                val data = gson.fromJson(message.payload, clazz)
                onStompMessage(data)
            }
        }

        validate(task)
    }

    fun <T> send(topic: String, data: T) {
        val task = {
            val jsonString = gson.toJson(data)
            stompClient.send(topic, jsonString)
                .subscribe(completableObserver)
        }

        validate(task)
    }

    fun sendText(topic: String, data: String) {
        val task = {
            stompClient.send(topic, data)
                .subscribe(completableObserver)
        }

        validate(task)
    }

    private fun validate(task: () -> Any) {
        if (isStompConnected()) {
            task.invoke()
        } else {
            lazyInvokedTasks.add(task)
        }
    }

    fun disconnect() {
        stompClient.disconnect()
    }

}