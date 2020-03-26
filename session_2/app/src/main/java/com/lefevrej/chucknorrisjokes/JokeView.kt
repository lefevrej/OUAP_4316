package com.lefevrej.chucknorrisjokes

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.joke_layout.view.*

class JokeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.joke_layout, this, true)
        this.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }

    data class Model(
        val joke: Joke,
        val isSaved: Boolean,
        val onShareClickListener: (value: String) -> Unit = {},
        val onSaveClickListener: (model: Model) -> Unit = {}
    )

    fun setUpView(model: Model) {
        joke_text.text = model.joke.value
        setSavedState(model.isSaved)
        share.setOnClickListener { model.onShareClickListener(model.joke.value) }
        star.setOnClickListener { model.onSaveClickListener(model) }
    }

    private fun setSavedState(isSaved: Boolean) {
        when (isSaved) {
            true -> star.setImageResource(R.drawable.ic_star_black_24dp)
            false -> star.setImageResource(R.drawable.ic_star_border_black_24dp)
        }
    }
}