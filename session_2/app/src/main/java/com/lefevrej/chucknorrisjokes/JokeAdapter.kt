package com.lefevrej.chucknorrisjokes

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.joke_layout.view.*

class JokeAdapter(private val onBottomReached: () -> Unit = {}) :
    RecyclerView.Adapter<JokeAdapter.JokeViewHolder>() {
    private var onShareClickListener: (id: String) -> Unit = {}
    private var onSaveClickListener: (id: String, item: View) -> Unit = { _: String, _: View -> }
    private val jokes: MutableList<Joke> = mutableListOf()

    fun setOnShareCLickListener(f: (id: String) -> Unit) {
        onShareClickListener = f
    }

    fun setOnSaveCLickListener(f: (id: String, item: View) -> Unit) {
        onSaveClickListener = f
    }

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
        holder.jokeView.share.setOnClickListener { onShareClickListener(jokes[position].id) }
        holder.jokeView.star.setOnClickListener {
            onSaveClickListener(jokes[position].id, holder.jokeView)
        }
        if (position == itemCount - 1)
            onBottomReached()
    }
}