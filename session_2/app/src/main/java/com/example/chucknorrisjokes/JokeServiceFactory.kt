package com.example.chucknorrisjokes

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class JokeServiceFactory {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.chucknorris.io/jokes/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: JokeApiService = retrofit.create<JokeApiService>(JokeApiService::class.java)
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