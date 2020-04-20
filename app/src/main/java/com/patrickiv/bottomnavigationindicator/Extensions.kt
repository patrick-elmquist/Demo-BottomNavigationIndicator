package com.patrickiv.bottomnavigationindicator

import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue

/**
 * Treat this as a color int and return it as a ColorStateList
 */
fun Int.toColorStateList(): ColorStateList = ColorStateList.valueOf(this)

val Context.colorAccent: Int
    get() {
        val typedValue = TypedValue()
        val a = obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorAccent))
        return a.getColor(0, 0).also { a.recycle() }
    }
