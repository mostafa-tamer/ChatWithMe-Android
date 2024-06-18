package com.mostafatamer.chatwithme

import com.mostafatamer.chatwithme.network.entity.dto.User
import retrofit2.Retrofit

class AppDependencies {
    lateinit var user: User
    lateinit var userToken: String
    lateinit var retrofit: Retrofit
}