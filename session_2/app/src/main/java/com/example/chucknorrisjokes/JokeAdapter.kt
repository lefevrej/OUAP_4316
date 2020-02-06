package com.example.chucknorrisjokes

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.joke_layout.view.*

class JokeAdapter:
    RecyclerView.Adapter<JokeAdapter.JokeViewHolder>() {

    private val jokes: MutableList<String> = mutableListOf()

    fun setData(data: List<String>){
        jokes.clear()
        jokes.addAll(data)
        notifyDataSetChanged()
    }

    class JokeViewHolder(val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JokeViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.joke_layout, parent, false) as LinearLayout
        return JokeViewHolder(linearLayout)
    }

    override fun getItemCount(): Int = jokes.size

    override fun onBindViewHolder(holder: JokeViewHolder, position: Int) {
        holder.linearLayout.joke_text.text = jokes[position]
    }
}