package com.lefevrej.chucknorrisjokes

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.chucknorrisjokes.R

class WaitingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting)

        val runnable = Runnable {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        val handler = Handler()
        handler.postDelayed(runnable, 2000)

    }
}
