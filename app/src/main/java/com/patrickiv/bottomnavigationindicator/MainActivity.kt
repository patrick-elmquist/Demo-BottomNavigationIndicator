package com.patrickiv.bottomnavigationindicator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bottomNavigation.apply {
            setOnNavigationItemSelectedListener {
                pageName.text = it.title
                true
            }
            pageName.text = menu.findItem(selectedItemId).title
        }
    }
}
