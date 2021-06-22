package com.midprogress

import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ui.widget.MidProgressLoader

class MainActivity : AppCompatActivity() {

     lateinit var linearProgressBar: MidProgressLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        linearProgressBar = findViewById(R.id.linearProgressBar)
        load()
    }

    fun load(){
        linearProgressBar.visibility = View.VISIBLE
        linearProgressBar.setProgress(0f)
        linearProgressBar.setProgressWithAnimation(100f, ValueAnimator.INFINITE, null)
    }
}