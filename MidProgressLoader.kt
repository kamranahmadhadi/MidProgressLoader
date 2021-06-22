package com.ui.widget

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.annotation.Keep
import kotlin.math.roundToInt
import kotlin.math.roundToLong


class MidProgressLoader(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val defaultDuration = 1000
    private val rectF: RectF = RectF()
    private val backgroundPaint: Paint
    private val progressbarPaint: Paint
    private var objectAnimator: ObjectAnimator? = null

    /**Don't change the progress name because it is used by ObjectAnimator*/
    private var progress = 0f
    private var strokeWidth = 3f
    private var strokeCap: Paint.Cap = Paint.Cap.ROUND
    private var duration: Int = defaultDuration
    private var progressColor = Color.DKGRAY
    private var bgColor = Color.LTGRAY
    private var twoWayLoader = true

    init {
        val typedArray: TypedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MidProgressLoader, 0, 0
        )

        try {
            twoWayLoader = typedArray.getBoolean(R.styleable.MidProgressLoader_twoWayLoader, twoWayLoader)
            strokeWidth =
                typedArray.getDimension(R.styleable.MidProgressLoader_midStrokeWidth, strokeWidth)
            progress = typedArray.getFloat(R.styleable.MidProgressLoader_midProgress, progress)
            progressColor = typedArray.getInt(R.styleable.MidProgressLoader_midProgressbarColor, progressColor)
            bgColor = typedArray.getInt(R.styleable.MidProgressLoader_midProgressBgColor, bgColor)
            duration =
                typedArray.getInt(R.styleable.MidProgressLoader_midProgressDuration, defaultDuration)
            if (duration < defaultDuration)
                duration = defaultDuration

            strokeCap = when (typedArray.getInt(R.styleable.MidProgressLoader_midStrokeCap, 1)) {
                0 -> Paint.Cap.BUTT
                1 -> Paint.Cap.ROUND
                2 -> Paint.Cap.SQUARE
                else -> Paint.Cap.ROUND
            }
        } finally {
            typedArray.recycle()
        }

        backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        backgroundPaint.color = bgColor
        backgroundPaint.style = Paint.Style.STROKE
        backgroundPaint.strokeWidth = strokeWidth

        progressbarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        progressbarPaint.color = progressColor
        progressbarPaint.style = Paint.Style.STROKE
        progressbarPaint.strokeCap = strokeCap
        progressbarPaint.strokeWidth = strokeWidth
    }

    val progressBarColor get():Int = progressColor
    val progressBgColor get():Int = bgColor
    val progressStrokeWidth get():Float = strokeWidth
    val progressDuration get():Int = duration

    fun setStrokeWidth(strokeWidth: Float) {
        this.strokeWidth = strokeWidth
        backgroundPaint.strokeWidth = strokeWidth
        progressbarPaint.strokeWidth = strokeWidth
        invalidate()
        requestLayout()//Because it should recalculate its bounds
    }

    fun setStrokeCap(strokeCap: Paint.Cap) {
        this.strokeCap = strokeCap
    }

    fun setProgressDuration(duration: Int) {
        if (duration < defaultDuration)
            this.duration = defaultDuration
        else
            this.duration = duration
    }

    fun setColor(progressColor: Int) {
        this.progressColor = setColorAlpha(progressColor)
        this.bgColor = Color.LTGRAY
        backgroundPaint.color = bgColor
        progressbarPaint.color = this.progressColor
        invalidate()
        requestLayout()
    }

    fun setColor(progressColor: Int, bgColor: Int) {
        this.progressColor = progressColor
        this.bgColor = bgColor
        backgroundPaint.color = bgColor
        progressbarPaint.color = progressColor
        invalidate()
        requestLayout()
    }

    /**NOTE: Don't change the setter name because it is used by ObjectAnimator*/
    @Keep
    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }

    /***NOTE: Don't change the getter name because it is used by ObjectAnimator*/
    @Keep
    fun getProgress(): Float {
        return progress
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val bgStart = 0f
        val stopY = 0f
        val stopX1 = width.toFloat()
        val fgStart = 0f
        val stopX2 = (stopX1 * progress / 100)
        if (twoWayLoader) {
            val mid = (bgStart + stopX1) / 2
            val midToRight = mid + (mid / 100) * progress
            val midToLeft = mid - (mid / 100) * progress
            canvas.drawLine(bgStart, bgStart, stopX1, stopY, backgroundPaint)//(0,0) to (stopX1,0)
            canvas.drawLine(
                mid,
                fgStart,
                midToRight.roundToLong().toFloat(),
                stopY,
                progressbarPaint
            )//(mid,0) to (midToRight,0)
            canvas.drawLine(
                mid,
                fgStart,
                midToLeft.roundToLong().toFloat(),
                stopY,
                progressbarPaint
            )//(mid,0) to (midToLeft,0)
        } else {
            canvas.drawLine(bgStart, bgStart, stopX1, stopY, backgroundPaint)//(0,0) to (stopX1,0)
            canvas.drawLine(
                fgStart,
                fgStart,
                stopX2.roundToLong().toFloat(),
                stopY,
                progressbarPaint
            )//(0,0) to (stopX2,0)
        }


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
        rectF.set(
            0 + strokeWidth / 2,
            0 + strokeWidth / 2,
            height - strokeWidth / 2,
            height - strokeWidth / 2
        )
    }

    /**
     * Set transparency to color
     *
     * @param color  The color to transparent
     * @return int - A transplanted color
     */
    private fun setColorAlpha(color: Int): Int {
        //1.0f to 0.0f increase transparency
        val alpha = (Color.alpha(color) * 0.4f).roundToInt()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    /**
     * Set the progress with an animation.
     * Note: [android.animation.ObjectAnimator] Class automatically set the progress
     * Don't call the [MidProgressLoader.setProgress] directly within this method.
     *
     * @param progress The progress it should animate to it.
     * @param repeatCount for reception of progress animation
     * @param listener progress animation listener
     */
    fun setProgressWithAnimation(
        progress: Float,
        repeatCount: Int = 0,
        listener: ProgressBarListener? = null
    ) {

        objectAnimator = ObjectAnimator.ofFloat(this, "progress", if(progress > 100f) 100f else progress)
            .also {
                it.repeatCount = repeatCount
                it.duration = this.duration.toLong()
                it.interpolator = DecelerateInterpolator()
                it.start()
            }

        setListener(listener)

    }

    private fun setListener(listener: ProgressBarListener?) {
        listener?.let {
            objectAnimator?.addListener(object : Animator.AnimatorListener {

                override fun onAnimationEnd(animation: Animator?) {
                    it.onProgressComplete()
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                    it.onProgressStart()
                }

                override fun onAnimationCancel(animation: Animator?) {
//                    it.onProgressCancel()
                }

            })
        }
    }

    fun resetProgress() {
        setProgress(0f)
        objectAnimator?.cancel()
    }
}



















