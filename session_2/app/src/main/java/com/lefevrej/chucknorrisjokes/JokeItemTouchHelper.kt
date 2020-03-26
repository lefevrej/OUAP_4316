package com.lefevrej.chucknorrisjokes

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class JokeItemTouchHelper(
    private val onJokeRemoved: (from: Int) -> Unit = {},
    private val onItemMoved: (from: Int, to: Int) -> Unit = {_, _ ->}
) : ItemTouchHelper(
    object : ItemTouchHelper.SimpleCallback(
        UP or DOWN,
        LEFT or RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val from = viewHolder.adapterPosition
            val to = target.adapterPosition
            onItemMoved(from, to)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int){
            onJokeRemoved(viewHolder.adapterPosition)
        }
    }
)
