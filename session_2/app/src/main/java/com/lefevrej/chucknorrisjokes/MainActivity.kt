package com.lefevrej.chucknorrisjokes

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lefevrej.chucknorrisjokes.JokesViewModel.ListAction
import com.lefevrej.chucknorrisjokes.JokesViewModel.LoadingStatus
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val SHARED_PREFS = "SHARED_PREFS"
    }

    private val viewModel: JokesViewModel by viewModels {
        JokesViewModelFactory(
            this,
            getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        )
    }

    private val jokeAdapter: JokeAdapter = JokeAdapter { viewModel.onNewJokesRequest() }
    private val viewManager: RecyclerView.LayoutManager = LinearLayoutManager(this)

    private fun observeViewModel() {
        viewModel.jokeModels.observe(
            this,
            Observer { jokes: List<JokeView.Model> ->
                jokeAdapter.updateData(jokes)
            })

        viewModel.jokesSetChangedAction.observe(
            this,
            Observer { listAction: ListAction ->
                when (listAction) {
                    is ListAction.ItemUpdatedAction
                    -> jokeAdapter.notifyItemChanged(listAction.position)
                    is ListAction.ItemInsertedAction
                    -> jokeAdapter.notifyItemInserted(listAction.position)
                    is ListAction.ItemRemovedAction
                    -> jokeAdapter.notifyItemRemoved(listAction.position)
                    is ListAction.ItemMovedAction
                    -> jokeAdapter.notifyItemMoved(listAction.fromPosition, listAction.toPosition)
                    is ListAction.DataSetChangedAction
                    -> jokeAdapter.notifyDataSetChanged()
                }
            })

        viewModel.jokesLoadingStatus.observe(
            this,
            Observer { loadingStatus: LoadingStatus ->
                swipe.isRefreshing = loadingStatus == LoadingStatus.LOADING
            })
    }

    /**
     * Convenient class used to build the instance of our JokeViewModel,
     * passing some params to its constructor.
     *
     * @see androidx.lifecycle.ViewModelProvider
     */
    private class JokesViewModelFactory(
        private val context: Context,
        private val sharedPrefs: SharedPreferences
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            JokesViewModel(context, sharedPrefs) as T
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        joke_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = jokeAdapter
        }

        val jokeTouchHelper = JokeItemTouchHelper(
            { position -> viewModel.onJokeRemovedAt(position) },
            { from, to -> viewModel.onJokePositionChanged(from, to) })
        jokeTouchHelper.attachToRecyclerView(joke_list)

        swipe.setOnRefreshListener { viewModel.onNewJokesRequest(clear = true) }
        swipe.setColorSchemeColors(getColor(R.color.colorAccent))

        observeViewModel()
    }
}