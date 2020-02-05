package com.example.chucknorrisjokes

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JokeAdapter(private val jokes: List<String>) :
    RecyclerView.Adapter<JokeAdapter.JokeViewHolder>() {

    class JokeViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JokeViewHolder {
        /*val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.joke_card, parent, false) as TextView*/

        return JokeViewHolder(TextView(parent.context))
    }
    override fun getItemCount(): Int {
        return jokes.size
    }

    override fun onBindViewHolder(holder: JokeViewHolder, position: Int) {
        holder.textView.text = jokes[position]
    }


}