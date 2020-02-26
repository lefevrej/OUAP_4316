package com.example.chucknorrisjokes

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class JokeApiServiceFactory {
    fun createService(): JokeApiService = Retrofit.Builder()
        .baseUrl("https://api.chucknorris.io/")
        .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(JokeApiService::class.java)
}