package com.lefevrej.chucknorrisjokes

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class JokeAdapter(
    private val onBottomReached: () -> Unit = {},
    private val onShareClickListener: (value: String) -> Unit = {},
    private val onSaveClickListener: (id: Joke, saved: Boolean) -> Unit = {_, _ ->}
) :
    RecyclerView.Adapter<JokeAdapter.JokeViewHolder>() {
    private val jokes: MutableList<Joke> = mutableListOf()
    private val savedState: MutableList<Boolean> = mutableListOf()

    fun addJokes(data: List<Joke>, saved:Boolean=false) {
        jokes.addAll(data)
        (0 until jokes.size).forEach { _ -> savedState.add(saved) }
        notifyDataSetChanged()
    }

    fun getJokes(): List<Joke> = jokes

    class JokeViewHolder(val jokeView: JokeView) : RecyclerView.ViewHolder(jokeView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JokeViewHolder {
        val jokeView = JokeView(parent.context)
        return JokeViewHolder(jokeView)
    }

    override fun getItemCount(): Int = jokes.size

    override fun onBindViewHolder(holder: JokeViewHolder, position: Int) {
        holder.jokeView.setUpView(
            JokeView.Model(
                jokes[position], savedState[position],
                onShareClickListener,
                onSaveClickListener
            )
        )
        if (position == itemCount - 1)
            onBottomReached()
    }

    fun onItemMoved(from: Int, to: Int) {
        if (from < to)
            (from until to).forEach {
                Collections.swap(jokes, it, it + 1)
                Collections.swap(savedState, it, it + 1)
            }
        else
            (to until from).forEach {
                Collections.swap(jokes, it, it + 1)
                Collections.swap(savedState, it, it + 1)
            }
        this.notifyItemMoved(from, to)
    }

    fun onJokeRemoved(position: Int) {
        jokes.removeAt(position)
        this.notifyItemRemoved(position)
    }
}