package com.mostafatamer.chatwithme.Singleton

import com.mostafatamer.chatwithme.network.entity.dto.User

//class UserSingleton {
//    companion object {
//        @Volatile
//        private var instance: User? = null
//        fun getInstance(user: User? = null): User {
//            if (instance == null && user == null)
//                throw IllegalStateException("instance is null")
//
//            return instance ?: synchronized(this) {
//                user.also { instance = user }!!
//            }
//        }
//    }
//}