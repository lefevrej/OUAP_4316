package com.example.chucknorrisjokes

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit

class JokeApiServiceFactory {
    fun createService(): JokeApiService =  Retrofit.Builder()
            .baseUrl("https://api.chucknorris.io/jokes/")
            .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
            .build()
            .create(JokeApiService::class.java)
}

fun main() {
    /*val jokeFactory = JokeServiceFactory()
    val jokeCall = jokeFactory.service.giveMeAJoke()
    jokeCall.enqueue(object : Callback<Joke> {
        override fun onResponse(call: Call<Joke>, response: Response<Joke>) {
            val joke = response.body()
            joke?.let {
                println(joke.value)
            }
        }

        override fun onFailure(call: Call<Joke>, t: Throwable) {
            println("Error : $t")
        }
    })*/
}