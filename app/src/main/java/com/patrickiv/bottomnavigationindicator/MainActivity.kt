package com.patrickiv.bottomnavigationindicator

import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

private const val SWITCH_DELAY = 3_000L

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val ids = listOf(
        R.id.bottom_navigation_home,
        R.id.bottom_navigation_search,
        R.id.bottom_navigation_add,
        R.id.bottom_navigation_history,
        R.id.bottom_navigation_profile
    )
    private val handler = Handler()
    private val selectRandomTask = object : Runnable {
        override fun run() {
            if (true) return
            bottomNavigation.selectedItemId = (ids - bottomNavigation.selectedItemId).random()
            handler.postDelayed(this, SWITCH_DELAY)
        }
    }
    override fun onStart() = super.onStart().also { handler.post(selectRandomTask) }
    override fun onStop() = super.onStop().also { handler.removeCallbacks(selectRandomTask) }
}
