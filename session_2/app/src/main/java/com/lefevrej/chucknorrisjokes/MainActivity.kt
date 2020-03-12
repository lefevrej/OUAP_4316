package com.lefevrej.chucknorrisjokes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list

class MainActivity : AppCompatActivity() {
    companion object {
        const val JOKES_KEY = "JOKES_KEY"
        const val SAVED_JOKES = "SAVED_JOKES"
        const val SHARED_PREFS = "SHARED_PREFS"
    }

    private lateinit var viewAdapter: JokeAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val service: JokeApiService = JokeApiServiceFactory()
        .createService()
    private val jokes: MutableList<Joke> = mutableListOf()
    private val savedState: MutableList<Boolean> = mutableListOf()

    override fun onStop() {
        super.onStop()
        compositeDisposable.dispose()
    }

    private fun onShareClicked(value: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, value)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun onSaveClicked(joke: Joke, saved: Boolean) {
        fetchJokes()
        savedState[jokes.indexOf(joke)] = saved
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val json = Json(JsonConfiguration.Stable).stringify(Joke.serializer().list,
            jokes.filterIndexed { index, _ -> savedState[index] })

        sharedPreferences.edit()
            .putString(SAVED_JOKES, json)
            .apply()

        viewAdapter.addJokes(jokes, savedState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewManager = LinearLayoutManager(this)
        viewAdapter = JokeAdapter(
            { getJoke() },
            { value -> onShareClicked(value) },
            { joke, saved -> onSaveClicked(joke, saved) }
        )

        joke_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        val jokeTouchHelper = JokeTouchHelper(
            { position -> viewAdapter.onJokeRemoved(position) },
            { from, to -> viewAdapter.onItemMoved(from, to) })
        jokeTouchHelper.attachToRecyclerView(joke_list)

        val sharedPreferences = getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)
        if (sharedPreferences.contains(SAVED_JOKES)) {
            jokes.addAll(
                sharedPreferences.getString(SAVED_JOKES, "")?.let {
                    Json(JsonConfiguration.Stable).parse(
                        Joke.serializer().list, it
                    )
                }!!
            )
            jokes.forEach { _ -> savedState.add(true) }
            viewAdapter.addJokes(jokes, savedState)
        }

        if (savedInstanceState != null) {
            jokes.addAll(
                savedInstanceState.getString(JOKES_KEY)?.let {
                    Json(JsonConfiguration.Stable).parse(
                        Joke.serializer().list, it
                    )
                }!!
            )
            jokes.clear()
        } else
            getJoke()

        swipe.setOnRefreshListener { getJoke(false) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(
            JOKES_KEY,
            Json(JsonConfiguration.Stable).stringify(Joke.serializer().list, viewAdapter.getJokes())
        )
        super.onSaveInstanceState(outState)
    }

    private fun getJoke(fetch: Boolean = true) {
        fetchJokes()
        if(!fetch)
            clearNonSavedJokes()

        compositeDisposable.add(service.giveMeAJoke()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                swipe.isRefreshing = true
            }
            .repeat(20)
            .doAfterTerminate {
                swipe.isRefreshing = false
            }
            .subscribeBy(
                onError = { e -> Log.wtf("Request error", e) },
                onNext = { joke: Joke ->
                    jokes.add(joke)
                    savedState.add(false)
                },
                onComplete = {
                    viewAdapter.addJokes(jokes, savedState)
                }
            )
        )
    }

    fun fetchJokes() {
        jokes.clear()
        savedState.clear()
        jokes.addAll(viewAdapter.getJokes())
        savedState.addAll(viewAdapter.getSavedState())
    }

    fun clearNonSavedJokes(){
        val savedJokes = mutableListOf<Joke>()
        savedState.forEachIndexed{i, b -> if(b) savedJokes.add(jokes[i])}
        jokes.clear()
        jokes.addAll(savedJokes)
        savedState.clear()
        jokes.forEach { _ -> savedState.add(true) }
    }
}
