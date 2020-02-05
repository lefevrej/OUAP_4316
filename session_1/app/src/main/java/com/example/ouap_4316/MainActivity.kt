package com.example.ouap_4316

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener{
    val TAG: String = "MAIN ACTIVITY"
    val names = listOf<String>("Gradur", "Heuss l'enfoiré", "VALD", "Naza", "JUL")

    override fun onStart() {
        super.onStart()
        Log.wtf(TAG, "onStart")
        Toast.makeText(this, "onStart", Toast.LENGTH_LONG).show()
    }
    override fun onPause() {
        super.onPause()
        Log.wtf(TAG, "onPause")
        Toast.makeText(this, "onPause", Toast.LENGTH_LONG).show()

    }
    override fun onStop() {
        super.onStop()
        Log.wtf(TAG, "onStop")
        Toast.makeText(this, "onStop", Toast.LENGTH_LONG).show()
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.wtf(TAG, "onDestroy")
        Toast.makeText(this, "onDestroy", Toast.LENGTH_LONG).show()
    }
    override fun onResume() {
        super.onResume()
        Log.wtf(TAG, "onResume")
        Toast.makeText(this, "onResume", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.wtf(TAG, "onCreate")
        Toast.makeText(this, "onCreate", Toast.LENGTH_LONG).show()

        Log.wtf(TAG, names.toString())
        val names2 = names.sortedBy { it.length }
        Log.wtf(TAG, names2.toString())
        rapgame.text = names2.toString()

        button.setOnClickListener {
            Log.wtf(TAG, "BONKS")
            Toast.makeText(this, "BONkS", Toast.LENGTH_LONG).show()
            textView.text = names2.random()
            //textView.text = textView.text.toString().plus('E') // 4
        }

    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
