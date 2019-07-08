package com.patrickiv.bottomnavigationindicator

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.core.view.doOnPreDraw
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationLayoutBasic @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr),
    BottomNavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemReselectedListener {

    private val position = IntArray(2)

    private var externalSelectedListener: OnNavigationItemSelectedListener? = null
    private var externalReselectedListener: OnNavigationItemReselectedListener? = null

    private val indicator = View(context).also {
        it.layoutParams = generateDefaultLayoutParams().apply {
            gravity = Gravity.BOTTOM
            width = INDICATOR_SIZE_DP.dpRounded
            height = INDICATOR_SIZE_DP.dpRounded
            bottomMargin = INDICATOR_OFFSET_DP.dpRounded
        }
        it.background = GradientDrawable().apply {
            color = context.colorAccent.toColorStateList()
            cornerRadius = it.layoutParams.height.f / 2f
        }
        addView(it)
    }

    init {
        super.setOnNavigationItemSelectedListener(this)
        super.setOnNavigationItemReselectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (externalSelectedListener?.onNavigationItemSelected(item) != false) {
            onItemSelected(item.itemId)
            return true
        }
        return false
    }

    override fun onNavigationItemReselected(item: MenuItem) {
        externalReselectedListener?.onNavigationItemReselected(item)
        onItemReselected()
    }

    override fun setOnNavigationItemSelectedListener(listener: OnNavigationItemSelectedListener?) {
        externalSelectedListener = listener
    }

    override fun setOnNavigationItemReselectedListener(listener: OnNavigationItemReselectedListener?) {
        externalReselectedListener = listener
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        doOnPreDraw {
            // Move the indicator in place when the view is laid out
            onItemSelected(selectedItemId)
        }
    }

    private fun onItemSelected(itemId: Int) {
        if (!isLaidOut) return

        findViewById<View>(itemId)?.let { itemView ->
            itemView.getLocationOnScreen(position)
            val itemViewCenterX = position[0] + itemView.width / 2f
            indicator.translationX = itemViewCenterX - indicator.width / 2f
        }
    }

    private fun onItemReselected() {
        // Do nothing for now
    }
}