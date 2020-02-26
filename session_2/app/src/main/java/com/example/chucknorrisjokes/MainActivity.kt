package com.example.chucknorrisjokes

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class MainActivity : AppCompatActivity() {
    private lateinit var viewAdapter: JokeAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewManager = LinearLayoutManager(this)
        viewAdapter = JokeAdapter()

        joke_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        val service = JokeApiServiceFactory().createService()
        val singleJoke: Single<Joke> = service.giveMeAJoke()
        singleJoke
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribeBy(
                onError = { e -> Log.wtf("Request", e) },
                onSuccess = { joke: Joke -> Log.wtf("Joke", joke.value) }
            )

        val jokes: List<Joke> =
            ChuckJokes.jokes.map { Json(JsonConfiguration.Stable).parse(Joke.serializer(), it) }
        viewAdapter.setData(jokes)
    }
}
