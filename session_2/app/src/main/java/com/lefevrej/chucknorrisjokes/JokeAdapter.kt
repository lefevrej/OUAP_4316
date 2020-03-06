package com.lefevrej.chucknorrisjokes

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class JokeAdapter(
    private val onBottomReached: () -> Unit = {},
    private val onShareClickListener: (id: String) -> Unit = {},
    private val onSaveClickListener: (id: String) -> Unit = {}
) :
    RecyclerView.Adapter<JokeAdapter.JokeViewHolder>() {
    private val jokes: MutableList<Joke> = mutableListOf()

    fun addJokes(data: List<Joke>) {
        jokes.addAll(data)
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
                jokes[position], false,
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
            }
        else
            (to until from).forEach {
                Collections.swap(jokes, it, it + 1)
            }
        this.notifyItemMoved(from, to)
    }

    fun onJokeRemoved(position: Int) {
        jokes.removeAt(position)
        this.notifyItemRemoved(position)
    }
}