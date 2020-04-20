package com.patrickiv.bottomnavigationindicator

import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val INDICATOR_SIZE_DP = 5
private const val INDICATOR_BOTTOM_MARGIN_DP = 6
private const val INDICATOR_MAX_SCALE = 9f
private const val INDICATOR_TRANSLATION_DURATION = 500L

class BottomNavigationViewWithIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr),
    BottomNavigationView.OnNavigationItemSelectedListener {

    private var externalSelectedListener: OnNavigationItemSelectedListener? = null

    private val position = IntArray(2)
    private var animator: ValueAnimator? = null
    private val evaluator = FloatEvaluator()

    private val indicator = RectF()
    private val accentPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorAccent)
    }

    private val bottomOffset = INDICATOR_BOTTOM_MARGIN_DP.px
    private val defaultSize = INDICATOR_SIZE_DP.px
    private val radius = defaultSize / 2f

    init {
        super.setOnNavigationItemSelectedListener(this)
        setWillNotDraw(false)
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isLaidOut) canvas.drawRoundRect(indicator, radius, radius, accentPaint)
    }

    private fun onItemSelected(itemId: Int, animate: Boolean = true) {
        if (!isLaidOut) return

        cancelAnimator()

        val itemView = findViewById<View>(itemId) ?: return
        val fromCenterX = indicator.centerX()
        val currentScale = indicator.width() / defaultSize

        animator = ValueAnimator.ofFloat(currentScale, INDICATOR_MAX_SCALE, 1f).apply {
            addUpdateListener {
                val scale = it.animatedValue as Float
                val indicatorWidth = defaultSize * scale

                itemView.getLocationOnScreen(position)
                val itemViewCenterX = position[0] + itemView.width / 2f
                val distanceTravelled = lerp(animatedFraction, fromCenterX, itemViewCenterX)

                val left = distanceTravelled - indicatorWidth / 2f
                val right = distanceTravelled + indicatorWidth / 2f
                val bottom = height - bottomOffset
                val top = bottom - defaultSize

                indicator.set(left, top, right, bottom)
                invalidate()
            }

            interpolator = FastOutSlowInInterpolator()
            duration = if (animate) INDICATOR_TRANSLATION_DURATION else 0L

            start()
        }
    }

    private fun lerp(t: Float, a: Float, b: Float) = evaluator.evaluate(t, a, b)

    private fun cancelAnimator() = animator?.let {
        it.end()
        it.removeAllUpdateListeners()
        animator = null
    }

    private val Int.px get() = (this * resources.displayMetrics.density)
}
