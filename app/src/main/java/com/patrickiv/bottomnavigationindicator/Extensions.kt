package com.patrickiv.bottomnavigationindicator

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.TypedValue
import kotlin.math.roundToInt

/**
 * Convenience property for converting to float
 */
val Int.f: Float
    get() = this.toFloat()

/**
 * Use the float as density independent pixels and return the pixel value
 */
val Int.dp: Float
    get() = this * Resources.getSystem().displayMetrics.density

/**
 * Use the float as density independent pixels and return the pixel value rounded to int
 */
val Int.dpRounded: Int
    get() = dp.roundToInt()

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