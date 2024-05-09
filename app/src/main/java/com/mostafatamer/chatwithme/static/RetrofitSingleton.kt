package com.mostafatamer.chatwithme.static

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitSingleton private constructor() {
    companion object {
        private lateinit var retrofit: Retrofit
        fun getRetrofitInstance(token: String? = null): Retrofit {
            return if (Companion::retrofit.isInitialized && token == null) {
                retrofit
            } else {
                val okHttpClient = token?.let {
                    OkHttpClient.Builder()
                        .addInterceptor { chain ->
                            val request = chain.request().newBuilder()
                                .addHeader("Authorization", it)
                                .build()
                            chain.proceed(request)
                        }.build()
                }

                retrofit = Retrofit.Builder()
                    .baseUrl(
                        "http://192.168.1.14:9090"
//                        "http://10.1.11.156:9090"
                    )
                    .addConverterFactory(
                        GsonConverterFactory.create(JsonConverter.getInstance())
                    ).client(
                        okHttpClient ?: OkHttpClient.Builder()
                            .build()
                    ).build()
                retrofit
            }
        }
    }
}