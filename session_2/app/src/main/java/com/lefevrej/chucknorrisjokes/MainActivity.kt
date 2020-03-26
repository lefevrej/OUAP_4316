package com.lefevrej.chucknorrisjokes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import java.util.*

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
    private val models: MutableList<JokeView.Model> = mutableListOf()

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

    private fun onSaveClicked(model: JokeView.Model) {
        models[models.indexOf(model)] = model.copy(isSaved = !model.isSaved)
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val json = Json(JsonConfiguration.Stable).stringify(Joke.serializer().list,
            models.filter { m -> m.isSaved }.map { it.joke })

        sharedPreferences.edit()
            .putString(SAVED_JOKES, json)
            .apply()
        viewAdapter.updateData(models)
    }

    fun onItemMoved(from: Int, to: Int) {
        if (from < to)
            (from until to).forEach {
                Collections.swap(models, it, it + 1)
            }
        else
            (to until from).forEach {
                Collections.swap(models, it, it + 1)
            }
        viewAdapter.updateData(models)
        viewAdapter.notifyItemMoved(from, to)
    }

    fun onJokeRemoved(position: Int) {
        models.removeAt(position)
        viewAdapter.updateData(models)
        viewAdapter.notifyItemRemoved(position)
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

        val jokeTouchHelper = JokeItemTouchHelper(
            { position -> onJokeRemoved(position) },
            { from, to -> onItemMoved(from, to) })
        jokeTouchHelper.attachToRecyclerView(joke_list)

        val sharedPreferences = getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)
        if (sharedPreferences.contains(SAVED_JOKES)) {
            models.addAll(
                sharedPreferences.getString(SAVED_JOKES, "")?.let {
                    Json(JsonConfiguration.Stable).parse(
                        Joke.serializer().list, it
                    )
                }!!.map {
                    JokeView.Model(it,
                        true,
                        { value -> onShareClicked(value) },
                        { model -> onSaveClicked(model) })
                }
            )
            viewAdapter.updateData(models)
        }

        if (savedInstanceState != null) {
            Log.wtf("Saved instance", "${savedInstanceState.getString(JOKES_KEY)}")
            models.addAll(
                savedInstanceState.getString(JOKES_KEY)?.let {
                    Json(JsonConfiguration.Stable).parse(
                        Joke.serializer().list, it
                    )
                }!!.map {
                    JokeView.Model(it,
                        false,
                        { value -> onShareClicked(value) },
                        { model -> onSaveClicked(model) })
                }
            )
            viewAdapter.updateData(models)
        } else
            getJoke()

        swipe.setOnRefreshListener { getJoke() }
        swipe.setColorSchemeColors(getColor(R.color.colorAccent))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(
            JOKES_KEY,
            Json(JsonConfiguration.Stable).stringify(
                Joke.serializer().list,
                models.filter { m -> !m.isSaved }.map { it.joke }
            )
        )
        super.onSaveInstanceState(outState)
    }

    private fun getJoke() {
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
                    models.add(JokeView.Model(joke,
                        false,
                        { value -> onShareClicked(value) },
                        { model -> onSaveClicked(model) }
                    ))
                },
                onComplete = {
                    viewAdapter.updateData(models)
                }
            )
        )
    }
}
