package com.lefevrej.chucknorrisjokes

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class JokeAdapter(private val onBottomReached: () -> Unit = {}) :
    RecyclerView.Adapter<JokeAdapter.JokeViewHolder>() {

    private val jokes: MutableList<Joke> = mutableListOf()

    fun setData(data: List<Joke>) {
        jokes.clear()
        jokes.addAll(data)
        notifyDataSetChanged()
    }

    class JokeViewHolder(val jokeView: JokeView) : RecyclerView.ViewHolder(jokeView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JokeViewHolder {
        val jokeView = JokeView(parent.context)
        return JokeViewHolder(jokeView)
    }

    override fun getItemCount(): Int = jokes.size

    override fun onBindViewHolder(holder: JokeViewHolder, position: Int) {
        holder.jokeView.setUpView(JokeView.Model(jokes[position].value))
        if (position == itemCount - 1)
            onBottomReached()
    }
}