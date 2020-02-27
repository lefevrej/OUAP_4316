package com.example.chucknorrisjokes

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewAdapter: JokeAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

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

        val jokes: MutableList<Joke> = mutableListOf()

        val service = JokeApiServiceFactory().createService()
        val singleJoke: Single<Joke> = service.giveMeAJoke()

        button.setOnClickListener {
            compositeDisposable.add(singleJoke
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onError = { e -> Log.wtf("Request", e) },
                    onSuccess = { joke: Joke ->
                        jokes.add(joke)
                        Log.wtf("Request", joke.value)
                        viewAdapter.setData(jokes)
                    }
                )
            )
        }

        //compositeDisposable.dispose()
    }
}
