package co.`in`.mzk.stepper

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.Dimension
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.widget.TextViewCompat

import kotlin.math.max

public class TimelineStep(context: Context?, attrs: AttributeSet?)
    : TableRow(context, attrs) {

    public constructor(context: Context) : this(context, null)

    private var isActive: Boolean = false
    private val anchor : TextView
    private val title : TextView
    private val subtitle : TextView

    init {
        View.inflate(getContext(), R.layout.timeline_step, this)

        clipToPadding = false
        clipChildren = false

        val attributes = getContext().theme.obtainStyledAttributes(
            attrs,
            R.styleable.TimelineStep,
            0,
            R.style.SequenceStep)

        anchor = findViewById(R.id.anchor)
        subtitle = findViewById(R.id.subtitle)
        title = findViewById(R.id.title)

        setupAnchor(attributes)
        setupAnchorWidth(attributes)
        setupAnchorTextAppearance(attributes)
        setupTitle(attributes)
        setupTitleTextAppearance(attributes)
        setupSubtitle(attributes)
        setupSubtitleTextAppearance(attributes)
        setupActive(attributes)

        attributes.recycle()
    }

    /**
     * Sets the anchor label
     *
     * @attr ref com.transferwise.sequencelayout.R.styleable#SequenceStep_anchor
     */

    public fun setAnchor(anchor: CharSequence?) {
        this.anchor.text = anchor
        this.anchor.visibility = View.VISIBLE
        this.anchor.minWidth = resources.getDimensionPixelSize(R.dimen.sequence_anchor_min_width)
    }




    /**
     * Sets the anchor max width
     */
    public fun setAnchorMaxWidth(@Dimension(unit = Dimension.PX) maxWidth: Int) {
        anchor.maxWidth = maxWidth
    }

    /**
     * Sets the anchor min width
     */

    public fun setAnchorMinWidth(@Dimension(unit = Dimension.PX) minWidth: Int) {
        anchor.minWidth = minWidth
    }

    /**
     * Sets the anchor text appearance
     *
     * @attr ref com.transferwise.sequencelayout.R.styleable#SequenceStep_anchorTextAppearance
     */

    public fun setAnchorTextAppearance(@StyleRes resourceId: Int) {
        TextViewCompat.setTextAppearance(anchor, resourceId)
        verticallyCenter(anchor, title)
    }

    /**
     * Sets the title label
     *
     * @attr ref com.transferwise.sequencelayout.R.styleable#SequenceStep_title
     */

    public fun setTitle(title: CharSequence?) {
        this.title.text = title
        this.title.visibility = View.VISIBLE
    }

    /**
     * Sets the title label
     *
     * @attr ref com.transferwise.sequencelayout.R.styleable#SequenceStep_title
     */
    public fun setTitle(@StringRes resId: Int) {
        setTitle(context.getString(resId))
    }

    /**
     * Sets the anchor text appearance
     *
     * @attr ref com.transferwise.sequencelayout.R.styleable#SequenceStep_titleTextAppearance
     */
    public fun setTitleTextAppearance(@StyleRes resourceId: Int) {
        TextViewCompat.setTextAppearance(title, resourceId)
        verticallyCenter(anchor, title)
    }

    /**
     * Sets the subtitle label
     *
     * @attr ref com.transferwise.sequencelayout.R.styleable#SequenceStep_subtitle
     */
    public fun setSubtitle(subtitle: CharSequence?) {
        this.subtitle.text = subtitle
        this.subtitle.visibility = View.VISIBLE
    }

    /**
     * Sets the subtitle label
     *
     * @attr ref com.transferwise.sequencelayout.R.styleable#SequenceStep_subtitle
     */
    public fun setSubtitle(@StringRes resId: Int) {
        setSubtitle(context.getString(resId))
    }

    /**
     * Sets the subtitle text appearance
     *
     * @attr ref com.transferwise.sequencelayout.R.styleable#SequenceStep_subtitleTextAppearance
     */
    public fun setSubtitleTextAppearance(@StyleRes resourceId: Int) {
        TextViewCompat.setTextAppearance(subtitle, resourceId)
    }

    /**
     * Returns whether step is active step
     *
     * @attr ref com.transferwise.sequencelayout.R.styleable#SequenceStep_active
     */
    public fun isActive(): Boolean {
        return isActive
    }

    /**
     * Sets whether step is active step
     *
     * @attr ref com.transferwise.sequencelayout.R.styleable#SequenceStep_active
     */
    public fun setActive(isActive: Boolean) {
        this.isActive = isActive
    }

    fun getDotOffset(): Int =
        (max(getViewHeight(anchor), getViewHeight(title)) - 8.toPx()) / 2 //TODO dynamic dot height

    private fun setupAnchor(attributes: TypedArray) {
        if (!attributes.hasValue(R.styleable.TimelineStep_anchor)) {
            anchor.visibility = View.INVISIBLE
        } else {
            setAnchor(attributes.getString(R.styleable.TimelineStep_anchor))
        }
    }

    private fun setupAnchorWidth(attributes: TypedArray) {
        setAnchorMinWidth(attributes.getDimensionPixelSize(R.styleable.TimelineStep_anchorMinWidth, 0))
        setAnchorMaxWidth(attributes.getDimensionPixelSize(R.styleable.TimelineStep_anchorMaxWidth, Integer.MAX_VALUE))
    }

    private fun setupSubtitle(attributes: TypedArray) {
        if (!attributes.hasValue(R.styleable.TimelineStep_subtitle)) {
            subtitle.visibility = View.GONE
        } else {
            setSubtitle(attributes.getString(R.styleable.TimelineStep_subtitle))
        }
    }

    private fun setupTitle(attributes: TypedArray) {
        if (!attributes.hasValue(R.styleable.TimelineStep_title)) {
            title.visibility = View.GONE
        } else {
            setTitle(attributes.getString(R.styleable.TimelineStep_title))
        }
    }

    private fun setupTitleTextAppearance(attributes: TypedArray) {
        if (attributes.hasValue(R.styleable.TimelineStep_titleTextAppearance)) {
            setTitleTextAppearance(attributes.getResourceId(R.styleable.TimelineStep_titleTextAppearance, 0))
        }
    }

    private fun setupSubtitleTextAppearance(attributes: TypedArray) {
        if (attributes.hasValue(R.styleable.TimelineStep_subtitleTextAppearance)) {
            setSubtitleTextAppearance(attributes.getResourceId(R.styleable.TimelineStep_subtitleTextAppearance, 0))
        }
    }

    private fun setupAnchorTextAppearance(attributes: TypedArray) {
        if (attributes.hasValue(R.styleable.TimelineStep_anchorTextAppearance)) {
            setAnchorTextAppearance(attributes.getResourceId(R.styleable.TimelineStep_anchorTextAppearance, 0))
        }
    }

    private fun setupActive(attributes: TypedArray) {
        setActive(attributes.getBoolean(R.styleable.TimelineStep_active, false))
    }

    private fun verticallyCenter(vararg views: View) {
        val maxHeight = views.map(::getViewHeight).maxOrNull() ?: 0

        views.forEach { view ->
            val height = getViewHeight(view)
            (view.layoutParams as MarginLayoutParams).topMargin = (maxHeight - height) / 2
            view.requestLayout()
        }
    }

    private fun getViewHeight(view: View) =
        if (view is TextView) {
            ((view.lineHeight - view.lineSpacingExtra) / view.lineSpacingMultiplier).toInt()
        } else {
            view.measuredHeight
        }
}