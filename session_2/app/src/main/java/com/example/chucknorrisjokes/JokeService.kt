package com.example.chucknorrisjokes

import retrofit2.Call
import retrofit2.http.GET

interface JokeService {
        @GET("random/")
        fun getJoke(): Call<Joke>
}