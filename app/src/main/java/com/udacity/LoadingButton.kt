package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

private const val DEFAULT_LOAD_TIME_DURATION = 10000L

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0f
    private var heightSize = 0f

    private var valueAnimator = ValueAnimator()

    private var progressStatus: Int = 0
    private var loadingColor = 0
    private var unfilledColor = 0
    private var downloadText = ""
    private var loadingText = ""

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
        isAntiAlias = true
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        isEnabled = when (new) {
            ButtonState.Completed -> true
            ButtonState.Clicked,
            ButtonState.Loading -> false
        }
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            unfilledColor = getColor(R.styleable.LoadingButton_unfilledColor, 0)
            loadingColor = getColor(R.styleable.LoadingButton_loadingColor, 0)
            loadingText = getString(R.styleable.LoadingButton_loadingText).toString()
            downloadText = getString(R.styleable.LoadingButton_text).toString()
            progressStatus = getInt(R.styleable.LoadingButton_progress, 0)
        }

        buttonState = ButtonState.Completed
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        setupButtonLayout(canvas)
        setupButtonText(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w.toFloat()
        heightSize = h.toFloat()
        setMeasuredDimension(w, h)
    }

    fun onClicked() {
        buttonState = ButtonState.Clicked
        postInvalidate()
    }

    fun onStartDownload() {
        buttonState = ButtonState.Loading
        animateProgress(100, true)
    }

    fun onReset() {
        if (valueAnimator.isRunning) {
            valueAnimator.currentPlayTime = DEFAULT_LOAD_TIME_DURATION - 500L
        }
    }

    private fun animateProgress(progress: Int, animate: Boolean) {
        if (animate) {
            valueAnimator = ValueAnimator.ofFloat(0f, 1f)
            valueAnimator.duration = DEFAULT_LOAD_TIME_DURATION

            valueAnimator.interpolator = DecelerateInterpolator()
            valueAnimator.addUpdateListener { animation ->
                val interpolation = animation.animatedValue as Float
                animateProgress((interpolation * progress).toInt(), false)
            }

            valueAnimator.doOnEnd {
                buttonState = ButtonState.Completed
                progressStatus = 0

                postInvalidate()
            }

            if (!valueAnimator.isStarted) {
                valueAnimator.start()
            }
        } else {
            progressStatus = progress
            postInvalidate()
        }
    }

    private fun setupButtonLayout(canvas: Canvas) {
        canvas.save()
        drawLoadingProgress(canvas)
        canvas.restore()
    }

    private fun setupButtonText(canvas: Canvas) {
        canvas.save()
        drawButtonStateText(canvas)
        canvas.restore()
    }

    private fun drawLoadingProgress(canvas: Canvas) {
        val progressEndX = widthSize * progressStatus / 100f

        // Draw the unfilled section
        paint.color = unfilledColor
        canvas.drawRect(progressEndX, 0f, widthSize, heightSize, paint)

        // Draw the part of the button that's filled
        paint.color = loadingColor
        canvas.drawRect(0f, 0f, progressEndX, heightSize, paint)
    }

    private fun drawButtonStateText(canvas: Canvas) {
        val displayedText = if (buttonState === ButtonState.Completed) downloadText else loadingText

        paint.color = getColor(context, R.color.white)

        // Calculate the center of the canvas
        val xTextPos = (width / 2).toFloat()
        val yTextPos = ((height / 2) - ((paint.descent() + paint.ascent()) / 2))

        canvas.drawText(displayedText, xTextPos, yTextPos, paint)
    }
}