package com.patrickiv.bottomnavigationindicator

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val ids = listOf(
        R.id.bottom_navigation_home,
        R.id.bottom_navigation_search,
        R.id.bottom_navigation_add,
        R.id.bottom_navigation_history,
        R.id.bottom_navigation_profile
    )
    private var current = 0

    private val handler = Handler()
    private val selectRandomTask = object : Runnable {
        override fun run() {
            var index = current
            while (index == current) index = Random.nextInt(ids.size)

            bottomNavigation.selectedItemId = ids[index]
            current = index

            handler.postDelayed(this, 2_000L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton.setOnClickListener {
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed(selectRandomTask, 2_000L)
            startButton.isEnabled = false
            stopButton.isEnabled = true
        }

        stopButton.setOnClickListener {
            handler.removeCallbacksAndMessages(null)
            startButton.isEnabled = true
            stopButton.isEnabled = false
        }

        bottomNavigation.setOnNavigationItemSelectedListener {
            handler.removeCallbacksAndMessages(null)
            startButton.isEnabled = true
            stopButton.isEnabled = false
            true
        }

        startButton.isEnabled = false
        stopButton.isEnabled = true
        handler.postDelayed(selectRandomTask, 2_000L)
    }
}