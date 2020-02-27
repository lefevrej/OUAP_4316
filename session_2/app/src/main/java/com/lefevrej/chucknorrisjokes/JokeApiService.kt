package com.lefevrej.chucknorrisjokes

import io.reactivex.Single
import retrofit2.http.GET

interface JokeApiService {
        @GET("jokes/random/")
        fun giveMeAJoke(): Single<Joke>
}