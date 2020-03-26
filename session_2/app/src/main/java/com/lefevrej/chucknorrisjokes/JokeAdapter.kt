package com.lefevrej.chucknorrisjokes

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class JokeAdapter(
    private val onBottomReached: () -> Unit = {}
) :
    RecyclerView.Adapter<JokeAdapter.JokeViewHolder>() {
    private val models: MutableList<JokeView.Model> = mutableListOf()

    fun updateData(newModels: List<JokeView.Model>) {
        models.clear()
        models.addAll(newModels)
        notifyDataSetChanged()
    }

    class JokeViewHolder(val jokeView: JokeView) : RecyclerView.ViewHolder(jokeView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JokeViewHolder {
        val jokeView = JokeView(parent.context)
        return JokeViewHolder(jokeView)
    }

    override fun getItemCount(): Int = models.size

    override fun onBindViewHolder(holder: JokeViewHolder, position: Int) {
        holder.jokeView.setUpView(models[position])
        if (position == itemCount - 1)
            onBottomReached()
    }
}