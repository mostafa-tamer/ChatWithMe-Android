package com.mostafatamer.chatwithme.dependency_injection

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.mostafatamer.chatwithme.data.services.StompService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("baseUrl")
    fun provideBaseUrl(): String {
        return "http://192.168.1.7:9090"
    }

    @Provides
    @Singleton
    @Named("ws_url")
    fun provideWsUrl(): String {
        return "ws://192.168.1.7:9090/gs-guide-websocket"
    }


    @Provides
    fun provideOkHttpClient(@Named("token") token: String): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", token)
                val request = requestBuilder.build()
                chain.proceed(request)
            }.build()
    }

    @Provides
    @Named("retrofit")
    fun provideRetrofit(
        @Named("baseUrl") baseUrl: String,
        gson: Gson,
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Named("retrofit_no_headers")
    fun provideRetrofitNoHeaders(
        @Named("baseUrl") baseUrl: String,
        gson: Gson,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    fun provideStompService(stompClient: StompClient, gson: Gson): StompService {
        return StompService(stompClient, gson)
    }

    @Provides
    fun provideStompClient(
        @Named("ws_url") wsUrl: String,
        @Named("token") token: String,
    ): StompClient {
        return Stomp.over(
            Stomp.ConnectionProvider.OKHTTP,
            wsUrl,
            mutableMapOf("Authorization" to token)
        )
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(
                LocalDateTime::class.java, JsonSerializer<LocalDateTime> { src, _, _ ->
                    JsonPrimitive(
                        src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"))
                    )
                }
            ).create()
    }
}
