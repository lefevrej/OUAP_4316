package com.lefevrej.chucknorrisjokes

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
    }

    private lateinit var viewAdapter: JokeAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val service: JokeApiService = JokeApiServiceFactory()
        .createService()
    private val jokes: MutableList<Joke> = mutableListOf()

    override fun onStop() {
        super.onStop()
        compositeDisposable.dispose()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewManager = LinearLayoutManager(this)
        viewAdapter = JokeAdapter { getJoke() }

        joke_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        if (savedInstanceState != null) {
            jokes.addAll(
                savedInstanceState.getString(
                    JOKES_KEY
                )?.let {
                    Json(JsonConfiguration.Stable).parse(
                        Joke.serializer().list,
                        it
                    )
                }!!
            )
            viewAdapter.setData(jokes)
        } else
            getJoke()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(
            JOKES_KEY,
            Json(JsonConfiguration.Stable).stringify(Joke.serializer().list, jokes)
        )
        super.onSaveInstanceState(outState)
    }

    private fun getJoke() {
        compositeDisposable.add(service.giveMeAJoke()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                progressBar.visibility = View.VISIBLE
            }
            .repeat(20)
            .doAfterTerminate {
                progressBar.visibility = View.GONE
            }
            .subscribeBy(
                onError = { e -> Log.wtf("Request error", e) },
                onNext = { joke: Joke -> jokes.add(joke) },
                onComplete = { viewAdapter.setData(jokes) }
            )
        )
    }
}
