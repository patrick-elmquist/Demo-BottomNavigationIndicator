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
import kotlin.math.roundToInt

private const val INDICATOR_SIZE_DP = 4
private const val INDICATOR_BOTTOM_MARGIN_DP = 6
private const val INDICATOR_MAX_SCALE = 9f
private const val INDICATOR_TRANSLATION_DURATION = 500L

class BottomNavigationLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr),
    BottomNavigationView.OnNavigationItemSelectedListener {

    private var externalSelectedListener: OnNavigationItemSelectedListener? = null

    private val position = IntArray(2)
    private var animator: ValueAnimator? = null
    private val evaluator = FloatEvaluator()

    private val indicatorBaseSize = INDICATOR_SIZE_DP.pxRounded
    private val indicator = View(context).also {
        it.layoutParams = generateDefaultLayoutParams().apply {
            width = indicatorBaseSize
            height = indicatorBaseSize
            gravity = Gravity.BOTTOM
            bottomMargin = INDICATOR_BOTTOM_MARGIN_DP.pxRounded
        }
        it.background = GradientDrawable().apply {
            color = context.colorAccent.toColorStateList()
            cornerRadius = it.layoutParams.height / 2f
        }
        addView(it)
    }

    init {
        super.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (externalSelectedListener?.onNavigationItemSelected(item) != false) {
            onItemSelected(item.itemId)
            return true
        }
        return false
    }

    override fun setOnNavigationItemSelectedListener(listener: OnNavigationItemSelectedListener?) {
        externalSelectedListener = listener
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
            val currentScale = indicator.width / indicatorBaseSize.toFloat()

            animator = ValueAnimator.ofFloat(currentScale, INDICATOR_MAX_SCALE, 1f).apply {
                addUpdateListener {
                    val scale = it.animatedValue as Float
                    val newWidth = ((indicatorBaseSize * scale).roundToInt())
                    indicator.layoutParams = indicator.layoutParams.apply { width = newWidth }

                    itemView.getLocationOnScreen(position)
                    val itemViewCenterX = position[0] + itemView.width / 2f
                    val distanceTravelled = evaluator.evaluate(animatedFraction, from, itemViewCenterX)
                    indicator.translationX = distanceTravelled - newWidth / 2f
                }
                interpolator = FastOutSlowInInterpolator()
                duration = if (animate) INDICATOR_TRANSLATION_DURATION else 0L
                start()
            }
        }
    }

    private fun cancelAnimator() = animator?.let {
        it.end()
        it.removeAllUpdateListeners()
        animator = null
    }

    private val Int.pxRounded get() = (this * resources.displayMetrics.density).roundToInt()
}
