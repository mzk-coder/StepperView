package co.`in`.mzk.stepper

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Rect
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.TableLayout
import androidx.core.view.setPadding


public class TimelineLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
    : FrameLayout(context, attrs, defStyleAttr), ViewTreeObserver.OnGlobalLayoutListener{

    public constructor(context: Context) : this(context, null)
    public constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val progressBarWrapper : FrameLayout
    private val progressBarBackground : View
    private val progressBarForeground : View
    private val dotsWrapper : FrameLayout
    private val stepsWrapper : TableLayout

    init {
        inflate(getContext(), R.layout.timeline_layout, this)
        progressBarWrapper = findViewById(R.id.progressBarWrapper)
        progressBarBackground = findViewById(R.id.progressBarBackground)
        progressBarForeground = findViewById(R.id.progressBarForeground)
        dotsWrapper = findViewById(R.id.dotsWrapper)
        stepsWrapper = findViewById(R.id.stepsWrapper)

        val attributes = getContext().theme.obtainStyledAttributes(
            attrs,
            R.styleable.TimelineLayout,
            0,
            R.style.SequenceLayout)
        applyAttributes(attributes)
        attributes.recycle()

        clipToPadding = false
        clipChildren = false

        start()
    }

    @ColorInt
    private var progressBackgroundColor: Int = 0
    @ColorInt
    private var progressForegroundColor: Int = 0




    public fun start() {
        removeCallbacks(animateToActive)
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    public fun setStyle(@StyleRes defStyleAttr: Int) {
        val attributes = context.theme.obtainStyledAttributes(defStyleAttr, R.styleable.TimelineLayout)
        applyAttributes(attributes)
        attributes.recycle()
    }


    public fun setProgressForegroundColor(@ColorInt color: Int) {
        this.progressForegroundColor = color
        progressBarForeground.setBackgroundColor(color)
        //TODO apply to existing steps
    }


    public fun setProgressBackgroundColor(@ColorInt progressBackgroundColor: Int) {
        this.progressBackgroundColor = progressBackgroundColor
        progressBarBackground.setBackgroundColor(progressBackgroundColor)
        //TODO apply to existing steps
    }

    public fun removeAllSteps() {
        stepsWrapper.removeAllViews()
    }


    public fun <T> setAdapter(adapter: TimelineAdapter<T>) where T : Any {
        stop()
        removeAllSteps()
        val count = adapter.getCount()
        for (i in 0 until count) {
            val item = adapter.getItem(i)
            val view = TimelineStep(context)

            adapter.bindView(view, item)
            addView(view)
        }
        start()
    }

    private fun applyAttributes(attributes: TypedArray) {
        setupProgressForegroundColor(attributes)
        setupProgressBackgroundColor(attributes)
    }

    private fun setupProgressForegroundColor(attributes: TypedArray) {
        setProgressForegroundColor(attributes.getColor(R.styleable.TimelineLayout_progressForegroundColor, 0))
    }

    private fun setupProgressBackgroundColor(attributes: TypedArray) {
        setProgressBackgroundColor(attributes.getColor(R.styleable.TimelineLayout_progressBackgroundColor, 0))
    }

    private fun setProgressBarHorizontalOffset() {
        val firstAnchor: View = stepsWrapper.getChildAt(0).findViewById(R.id.anchor)
        progressBarWrapper.translationX = firstAnchor.measuredWidth + 4.toPx() - (progressBarWrapper.measuredWidth / 2f) //TODO dynamic dot size
    }

    private fun placeDots() {
        dotsWrapper.removeAllViews()
        var firstOffset = 0
        var lastOffset = 0

        stepsWrapper.children().forEachIndexed { i, view ->
            val sequenceStep = view as TimelineStep
            val sequenceStepDot = TimelineStepDot(context)
            sequenceStepDot.setDotBackground(progressForegroundColor, progressBackgroundColor)
            sequenceStepDot.setPulseColor(progressForegroundColor)
            sequenceStepDot.clipChildren = false
            sequenceStepDot.clipToPadding = false
            val layoutParams = LayoutParams(16.toPx(), 16.toPx()) //TODO dynamic dot size
            val totalDotOffset = getRelativeTop(sequenceStep, stepsWrapper) + sequenceStep.paddingTop + sequenceStep.getDotOffset() + 4.toPx() //TODO dynamic dot size
            layoutParams.topMargin = totalDotOffset
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL
            dotsWrapper.addView(sequenceStepDot, layoutParams)
            if (i == 0) {
                firstOffset = totalDotOffset
            }
            lastOffset = totalDotOffset
        }

        val backgroundLayoutParams = progressBarBackground.layoutParams as MarginLayoutParams
        backgroundLayoutParams.topMargin = firstOffset + 8.toPx() //TODO dynamic dot size
        backgroundLayoutParams.height = lastOffset - firstOffset
        progressBarBackground.requestLayout()

        val foregroundLayoutParams = progressBarForeground.layoutParams as MarginLayoutParams
        foregroundLayoutParams.topMargin = firstOffset + 8.toPx() //TODO dynamic dot size
        foregroundLayoutParams.height = lastOffset - firstOffset
        progressBarForeground.requestLayout()
    }

    private val animateToActive = {
        progressBarForeground.visibility = VISIBLE
        progressBarForeground.pivotY = 0f
        progressBarForeground.scaleY = 0f

        val activeStepIndex = stepsWrapper.children().indexOfFirst { it is TimelineStep && it.isActive() }

        if (activeStepIndex != -1) {
            val activeDot = dotsWrapper.getChildAt(activeStepIndex)
            val activeDotTopMargin = (activeDot.layoutParams as LayoutParams).topMargin
            val progressBarForegroundTopMargin = (progressBarForeground.layoutParams as LayoutParams).topMargin
            val scaleEnd = (activeDotTopMargin + (activeDot.measuredHeight / 2) - progressBarForegroundTopMargin) /
                    progressBarBackground.measuredHeight.toFloat()

            Log.d("scaleEnd", "scaleEnd: $scaleEnd")

            progressBarForeground
                .animate()
                .setStartDelay(resources.getInteger(R.integer.sequence_step_duration).toLong())
                .scaleY(scaleEnd)
                .setInterpolator(LinearInterpolator())
                .setDuration(activeStepIndex * resources.getInteger(R.integer.sequence_step_duration).toLong())
                .setUpdateListener {
                    val animatedOffset = progressBarForeground.scaleY * progressBarBackground.measuredHeight
                    dotsWrapper
                        .children()
                        .forEachIndexed { i, view ->
                            if (i > activeStepIndex) {
                                return@forEachIndexed
                            }
                            val dot = view as TimelineStepDot
                            val dotTopMargin = (dot.layoutParams as LayoutParams).topMargin -
                                    progressBarForegroundTopMargin -
                                    (dot.measuredHeight / 2)
                            if (animatedOffset >= dotTopMargin) {
                                if (i < activeStepIndex && !dot.isEnabled) {
                                    dot.isEnabled = true
                                } else if (i == activeStepIndex && !dot.isActivated) {
                                    dot.isActivated = true
                                }
                            }
                        }
                }
                .start()
        }
    }

    private fun getRelativeTop(child: View, parent: ViewGroup): Int {
        val offsetViewBounds = Rect()
        child.getDrawingRect(offsetViewBounds)
        parent.offsetDescendantRectToMyCoords(child, offsetViewBounds)
        return offsetViewBounds.top
    }

    private fun stop() {
        removeCallbacks(animateToActive)
        viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (child is TimelineStep) {
            if (child.isActive()) {
                child.setPadding(0)
//                child.setPadding(
//                    0, if (stepsWrapper.childCount == 0) 0 else resources.getDimensionPixelSize(R.dimen.sequence_active_step_padding_top), //no paddingTop if first step is active
//                    0,
//                    resources.getDimensionPixelSize(R.dimen.sequence_active_step_padding_bottom)
//                )
            }
            stepsWrapper.addView(child, params)
            return
        }
        super.addView(child, index, params)
    }

    override fun onGlobalLayout() {
        if (stepsWrapper.childCount > 0) {
            setProgressBarHorizontalOffset()
            placeDots()
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            post(animateToActive)
        }
    }


}