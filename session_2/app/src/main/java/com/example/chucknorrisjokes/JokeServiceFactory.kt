package com.example.chucknorrisjokes

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class JokeServiceFactory {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.chucknorris.io/jokes/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: JokeService = retrofit.create<JokeService>(JokeService::class.java)
}

fun main() {
    val jokeFactory = JokeServiceFactory()
    val jokeCall = jokeFactory.service.requestJoke()
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
    })
}




