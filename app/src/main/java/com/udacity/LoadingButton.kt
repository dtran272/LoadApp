package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat.getColor
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
        isAntiAlias = true
    }

    init {

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawButtonLayout(canvas)
        drawButtonText(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            View.MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun drawButtonLayout(canvas: Canvas) {
        paint.color = getColor(context, R.color.colorPrimary)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

    private fun drawButtonText(canvas: Canvas) {
        paint.color = getColor(context, R.color.white)

        // Calculate the center of the canvas
        val xTextPos = (width / 2).toFloat()
        val yTextPos = ((height / 2) - ((paint.descent() + paint.ascent()) / 2))

        canvas.drawText(resources.getString(R.string.button_download), xTextPos, yTextPos, paint)
    }
}