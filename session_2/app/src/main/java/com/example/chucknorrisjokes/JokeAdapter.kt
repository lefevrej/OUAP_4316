package com.example.chucknorrisjokes

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.joke_layout.view.*

class JokeAdapter:
    RecyclerView.Adapter<JokeAdapter.JokeViewHolder>() {

    var jokes: List<String> = listOf()
        set(value){
            field = value
            notifyDataSetChanged()
            Log.wtf("JOKE_ADAPTER", "dataset changed")
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