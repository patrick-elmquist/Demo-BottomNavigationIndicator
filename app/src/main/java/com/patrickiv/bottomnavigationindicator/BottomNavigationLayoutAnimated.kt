package com.patrickiv.bottomnavigationindicator

import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationLayoutAnimated @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr),
    BottomNavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemReselectedListener {

    private val position = IntArray(2)

    private var externalSelectedListener: OnNavigationItemSelectedListener? = null
    private var externalReselectedListener: OnNavigationItemReselectedListener? = null

    private var animator: ValueAnimator? = null
    private val evaluator = FloatEvaluator()

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
            onItemSelected(selectedItemId, false)
        }
    }

    private fun onItemSelected(itemId: Int, animate: Boolean = true) {
        if (!isLaidOut) return

        cancelAnimator()

        findViewById<View>(itemId)?.let { itemView ->
            itemView.getLocationOnScreen(position)
            val from = indicator.x
            val currentScale = indicator.width / INDICATOR_SIZE_DP.dp

            // TODO
            // * Make the speed and size depend on the distance to travel

            animator = ValueAnimator.ofFloat(currentScale, 1f, 1f).apply {
                addUpdateListener {
                    itemView.getLocationOnScreen(position)
                    val itemViewCenterX = position[0] + itemView.width / 2f
                    val distanceTravelled = evaluator.evaluate(animatedFraction, from, itemViewCenterX)
                    indicator.translationX = distanceTravelled - indicator.width / 2f
                }
                interpolator = FastOutSlowInInterpolator()
                duration = if (animate) INDICATOR_TRANSLATION_DURATION else 0L
                start()
            }
        }
    }

    private fun cancelAnimator() {
        animator?.let {
            it.end()
            it.removeAllUpdateListeners()
            it.removeAllListeners()
            animator = null
        }
    }

    private fun onItemReselected() {
        cancelAnimator()

        animator = ValueAnimator.ofFloat(1f, INDICATOR_SCALE_MAX, 1f).apply {
            addUpdateListener {
                val fraction = it.animatedValue as Float
                indicator.scaleX = fraction
                indicator.scaleY = fraction
            }

            interpolator = FastOutSlowInInterpolator()
            duration = INDICATOR_SCALE_DURATION
            start()
        }
    }
}