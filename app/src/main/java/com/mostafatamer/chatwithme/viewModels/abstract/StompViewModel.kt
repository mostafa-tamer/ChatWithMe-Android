//package com.mostafatamer.chatwithme.viewModels.abstract
//
//import com.mostafatamer.chatwithme.services.StompService
//
//import androidx.lifecycle.*
//
//abstract class StompViewModel(
//    private val stompService: StompService
//) : ViewModel(), LifecycleEventObserver {
//
//    private fun connectStompService() {
//        if (!stompService.isStompConnected()) {
//            println("StompViewModel: stomp connected")
//            stompService.connect()
//        }
//    }
//
//    private fun disconnectStompService() {
//        println("StompViewModel: stomp disconnected")
//        if (stompService.isStompConnected()) {
//            println("StompViewModel: stomp stopped")
//            stompService.disconnect()
//        }
//    }
//
//    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
//        when (event) {
//            Lifecycle.Event.ON_RESUME -> connectStompService()
//            Lifecycle.Event.ON_PAUSE -> disconnectStompService()
//            Lifecycle.Event.ON_DESTROY -> {
//                disconnectStompService()
//                source.lifecycle.removeObserver(this)
//            }
//            else -> {}
//        }
//    }
//
//    override fun onCleared() {
//        println("onCleared")
//        super.onCleared()
//        disconnectStompService()
//    }
//}
