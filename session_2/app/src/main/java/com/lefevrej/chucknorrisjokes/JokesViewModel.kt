package com.lefevrej.chucknorrisjokes

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import java.util.*

class JokesViewModel(
    private val context: Context, private val sharedPrefs: SharedPreferences
) : ViewModel() {

    private val composite: CompositeDisposable = CompositeDisposable()
    private val service: JokeApiService by lazy { JokeApiServiceFactory().createService() }

    enum class LoadingStatus { LOADING, NOT_LOADING }

    /** Used as a "dynamic enum" to notify Adapter with correct action. */
    sealed class ListAction {
        data class ItemUpdatedAction(val position: Int) : ListAction()
        data class ItemInsertedAction(val position: Int) : ListAction()
        data class ItemRemovedAction(val position: Int) : ListAction()
        data class ItemMovedAction(val fromPosition: Int, val toPosition: Int) : ListAction()
        object DataSetChangedAction : ListAction()
    }

    companion object {
        const val SAVED_JOKES = "SAVED_JOKES"
    }

    private val _jokesLoadingStatus = MutableLiveData<LoadingStatus>()
    private val _jokesSetChangedAction = MutableLiveData<ListAction>()
    private val _jokes = MutableLiveData<List<Joke>>()
    private val _stared = MutableLiveData<List<Boolean>>()
    
    val jokesLoadingStatus: LiveData<LoadingStatus> = _jokesLoadingStatus
    val jokesSetChangedAction: LiveData<ListAction> = _jokesSetChangedAction
    val jokeModels: LiveData<List<JokeView.Model>> = Transformations.map(_jokes) {
        it.toJokesViewModel()
    }

    init {
        onSavedJokesRestored()
        onNewJokesRequest()
    }

    fun onNewJokesRequest(jokeCount: Int = 10, clear: Boolean = false) {
        val jokes: MutableList<Joke> = mutableListOf()
        if (!clear)
            _jokes.value?.let { jokes.addAll(it) }
        else
        //We only keep stared jokes
            _stared.value!!.forEachIndexed { i, b -> if (b) jokes.add(_jokes.value!![i]) }
        val stared: MutableList<Boolean> = mutableListOf()
        _stared.value?.let { stared.addAll(it) }
        composite.add(service.giveMeAJoke()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                _jokesLoadingStatus.value = LoadingStatus.LOADING
            }
            .repeat(jokeCount.toLong())
            .doAfterTerminate {
                _jokesLoadingStatus.value = LoadingStatus.NOT_LOADING
            }
            .subscribeBy(
                onError = { e -> Log.wtf("Request error", e) },
                onNext = { joke: Joke ->
                    stared.add(false)
                    jokes.add(joke)
                },
                onComplete = {
                    stared.addAll(List(jokes.size) { false })
                    _stared.value = stared
                    _jokes.value = jokes
                    _jokesSetChangedAction.value = ListAction.DataSetChangedAction
                }
            )
        )
    }

    fun onJokeRemovedAt(position: Int) {
        _jokes.value = _jokes.value!!.filterIndexed { index, _ ->
            index != position
        }
        _stared.value = _stared.value!!.filterIndexed { index, _ ->
            index != position
        }
        _jokesSetChangedAction.value = ListAction.ItemRemovedAction(position)
    }

    fun onJokePositionChanged(previous: Int, target: Int) {
        _stared.value = _stared.value!!.moveItem(previous, target)
        _jokes.value = _jokes.value!!.moveItem(previous, target)
        _jokesSetChangedAction.value = ListAction.ItemMovedAction(previous, target)
    }

    private fun onJokeStared(id: String) {
        jokeModels.value!!.forEachIndexed { index, model ->
            if (model.joke.id == id) {
                val temp = _stared.value!!.toMutableList()
                temp[index] = !model.isSaved
                _stared.value = temp
                //Not very elegant
                _jokes.value = _jokes.value
                //update _jokesSetChangedAction
                _jokesSetChangedAction.value = ListAction.ItemUpdatedAction(index)
            }
        }
        // add joke to shared preferences
        val json = Json(JsonConfiguration.Stable).stringify(Joke.serializer().list,
            jokeModels.value!!.filter { m -> m.isSaved }.map { it.joke })
        sharedPrefs.edit()
            .putString(SAVED_JOKES, json)
            .apply()
    }

    private fun onJokeShared(id: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, _jokes.value!!.first { it.id == id }.value)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(context, shareIntent, null)
    }

    private fun onSavedJokesRestored() {
        if (sharedPrefs.contains(SAVED_JOKES)) {
            val savedJokes: List<Joke>? = (
                    sharedPrefs.getString(SAVED_JOKES, "")?.let {
                        Json(JsonConfiguration.Stable).parse(
                            Joke.serializer().list, it
                        )
                    })
            _jokes.value = savedJokes
            _stared.value = List(savedJokes!!.size) { true }
        }
        _jokesSetChangedAction.value = ListAction.DataSetChangedAction
    }

    override fun onCleared() {
        composite.dispose()
    }

    private fun List<Joke>.toJokesViewModel(): List<JokeView.Model> = mapIndexed { index, joke ->
        JokeView.Model(joke,
            _stared.value!![index],
            { id -> onJokeShared(id) },
            { id -> onJokeStared(id) })
    }
}

/** Convenient method to change an item position in a List */
private inline fun <reified T> List<T>.moveItem(sourceIndex: Int, targetIndex: Int): List<T> =
    apply {
        if (sourceIndex <= targetIndex)
            Collections.rotate(subList(sourceIndex, targetIndex + 1), -1)
        else Collections.rotate(subList(targetIndex, sourceIndex + 1), 1)
    }