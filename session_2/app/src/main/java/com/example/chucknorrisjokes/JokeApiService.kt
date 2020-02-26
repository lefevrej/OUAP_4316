package com.example.chucknorrisjokes

import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET

interface JokeApiService {
        @GET("random/")
        fun giveMeAJoke(): Single<Joke>
}