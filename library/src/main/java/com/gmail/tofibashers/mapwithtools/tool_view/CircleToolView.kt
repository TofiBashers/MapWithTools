package com.gmail.tofibashers.mapwithtools.tool_view

import android.content.Context
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.Dimension
import android.support.annotation.IdRes
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.gmail.tofibashers.mapwithtools.R
import com.gmail.tofibashers.mapwithtools.internal.MapWithToolsDsl
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.PatternItem
import com.google.maps.android.SphericalUtil


/**
 * Created by TofiBashers on 20.01.2019.
 */
class CircleToolView : ToolView {

    private sealed class State {
        class WaitingState : State()
        class DrawingState(val currentlyDrawedCircle: Circle) : State()
    }

    private var state: State = State.WaitingState()

    private var isFigureClickable: Boolean = DEFAULT_IS_FIGURE_CLICKABLE
    private var strokeWidth: Float = 0f
    private var strokePattern: List<PatternItem>? = null
    private var strokeColor: Int = 0
    private var fillColor: Int = 0
    private var figureZIndex: Float = DEFAULT_FIGURE_Z_INDEX

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs, defStyleAttr, defStyleRes)
    }

    private constructor(context: Context,
                        @IdRes id: Int,
                        image: Drawable,
                        isFigureClickable: Boolean,
                        strokeWidth: Float,
                        strokePattern: List<PatternItem>?,
                        strokeColor: Int,
                        fillColor: Int,
                        figureZIndex: Float) : super(context, id, image) {
        this.isFigureClickable = isFigureClickable
        this.strokeWidth = strokeWidth
        this.strokePattern = strokePattern
        this.strokeColor = strokeColor
        this.fillColor = fillColor
        this.figureZIndex = figureZIndex
    }

    fun init(attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CircleToolView,
                defStyleAttr,
                defStyleRes).apply {

            try {
                buttonDrawable = null
                background = getDrawable(R.styleable.CircleToolView_android_background) ?:
                        createDefaultToolImageWithStates(context.resources,
                                DEFAULT_BACKGROUND_CHECKED_IMAGE_RES_ID,
                                DEFAULT_BACKGROUND_NON_CHECKED_IMAGE_RES_ID)
                strokeWidth = getDimension(R.styleable.CircleToolView_strokeWidth,
                        resources.getDimension(DEFAULT_STROKE_WIDTH_RES_ID))
                strokeColor = getColor(R.styleable.CircleToolView_strokeColor,
                        ResourcesCompat.getColor(resources, DEFAULT_STROKE_COLOR_RES_ID, null))
                fillColor = getColor(R.styleable.CircleToolView_fillColor,
                        ResourcesCompat.getColor(resources, DEFAULT_FILL_COLOR_RES_ID, null))
                isFigureClickable = getBoolean(R.styleable.CircleToolView_figureClickable,
                        DEFAULT_IS_FIGURE_CLICKABLE)
                figureZIndex = getFloat(R.styleable.CircleToolView_figureZIndex,
                        DEFAULT_FIGURE_Z_INDEX)
            }
            finally {
                recycle()
            }
        }
    }

    override fun onToolEnabled(drawingGesturesOverlayView: View, mapView: MapView) {
        drawingGesturesOverlayView.isClickable = true
        drawingGesturesOverlayView.isFocusable = true
        drawingGesturesOverlayView.setOnTouchListener { view, motionEvent ->
            val unmaskedEvent = motionEvent.actionMasked and MotionEvent.ACTION_MASK
            when (unmaskedEvent) {
                MotionEvent.ACTION_DOWN -> {
                    if (state is State.WaitingState) {
                        val centerLatLng = map?.projection?.fromScreenLocation(
                                Point(motionEvent.x.toInt(), motionEvent.y.toInt()))
                        map?.addCircle(
                                CircleOptions().center(centerLatLng))
                                ?.let { state = State.DrawingState(it) }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (state is State.DrawingState &&
                            !isSamePointWithLastInHistory(motionEvent)) {
                        val drawedCircle = (state as State.DrawingState).currentlyDrawedCircle
                        val drawedCenter = drawedCircle.center
                        val pointOnCircle = map?.projection?.fromScreenLocation(
                                Point(motionEvent.x.toInt(), motionEvent.y.toInt()))
                        val radius = SphericalUtil.computeDistanceBetween(drawedCenter, pointOnCircle)
                        drawedCircle.remove()
                        map?.addCircle(
                                CircleOptions()
                                        .center(drawedCenter)
                                        .clickable(isFigureClickable)
                                        .strokeColor(strokeColor)
                                        .strokePattern(strokePattern)
                                        .strokeWidth(strokeWidth)
                                        .fillColor(fillColor)
                                        .zIndex(figureZIndex)
                                        .radius(radius))
                                ?.let { state = State.DrawingState(it) }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (state is State.DrawingState) {
                        state = State.WaitingState()
                    }
                }
            }
            return@setOnTouchListener true
        }
    }

    open class DefaultCircleToolViewBuilder(context: Context): BaseToolViewBuilder<CircleToolView>(context) {

        override var _id = R.id.circle_tool_default

        override var _image: Drawable = createDefaultToolImageWithStates(context.resources,
                DEFAULT_BACKGROUND_CHECKED_IMAGE_RES_ID,
                DEFAULT_BACKGROUND_NON_CHECKED_IMAGE_RES_ID)

        protected var _isFigureClickable: Boolean = DEFAULT_IS_FIGURE_CLICKABLE

        protected var _figureStrokeWidth: Float = context.resources.getDimension(DEFAULT_STROKE_WIDTH_RES_ID)

        protected var _figureStrokePattern: List<PatternItem>? = null

        protected var _figureStrokeColor: Int? = null

        protected var _figureStrokeColorRes: Int = DEFAULT_STROKE_COLOR_RES_ID

        protected var _figureFillColor: Int? = null

        protected var _figureFillColorRes: Int = DEFAULT_FILL_COLOR_RES_ID

        protected var _figureZIndex: Float = DEFAULT_FIGURE_Z_INDEX

        @PublishedApi
        override fun build(): CircleToolView = CircleToolView(context,
                _id,
                _image,
                _isFigureClickable,
                _figureStrokeWidth,
                _figureStrokePattern,
                _figureStrokeColor ?:
                        ResourcesCompat.getColor(context.resources, _figureStrokeColorRes, null),
                _figureFillColor ?:
                        ResourcesCompat.getColor(context.resources, _figureFillColorRes, null),
                _figureZIndex)
    }

    /**
     * This is preferred way for creation [CircleToolView] from Java code.
     */
    class Builder(context: Context): DefaultCircleToolViewBuilder(context) {

        /**
         * Sets view id. If not, [R.id.circle_tool_default] is default value.
         */
        fun setId(@IdRes id: Int) = apply { this._id = id }

        /**
         * Sets image of drawed tool, recommended to be a state-list drawable, for "checked" and "non-checked" states
         * If not set, uses default drawable
         * at [DEFAULT_BACKGROUND_CHECKED_IMAGE_RES_ID] and [DEFAULT_BACKGROUND_NON_CHECKED_IMAGE_RES_ID].
         */
        fun setImage(image: Drawable) = apply { this._image = image }

        /**
         * Clickability of every drawn circle
         * Default value is false.
         */
        fun setFigureClickable(clickable: Boolean) = apply { this._isFigureClickable = clickable}

        /**
         * Sets the width (in screen pixels) of the 's outline.
         * It must be zero or greater. If it is zero then no outline is drawn.
         */
        fun setFigureStrokeWidth(strokeWidth: Float) = apply { this._figureStrokeWidth = strokeWidth }

        /**
         * Sets stroke pattern of the circle's outline.
         * Default value is null (simple line)
         */
        fun setFigureStrokePattern(strokePattern: List<PatternItem>) =
                apply { this._figureStrokePattern = strokePattern }

        /**
         * Sets the stroke color.
         * The stroke color is the color of this circle's outline, in the integer format specified by Color.
         * If TRANSPARENT is used then no outline is drawn.
         */
        fun setFigureStrokeColor(@ColorInt strokeColor: Int) = apply { this._figureStrokeColor = strokeColor }

        /**
         * Sets the stroke color.
         * The stroke color is the color of this circle's outline, in the integer format specified by Color.
         * If TRANSPARENT is used then no outline is drawn.
         */
        fun setFigureStrokeColorRes(@ColorRes strokeColorRes: Int) =
                apply { this._figureStrokeColorRes = strokeColorRes }

        /**
         * Sets the color inside every drawed circle's, in the integer format.
         * If TRANSPARENT is used then no outline is drawn.
         */
        fun setFigureFillColor(@ColorInt fillColor: Int): Builder =
                apply { this._figureFillColor = fillColor }

        /**
         * Sets the color inside every drawed circle's, in the integer format.
         * If TRANSPARENT is used then no outline is drawn.
         */
        fun setFigureFillColorRes(@ColorRes fillColorRes: Int): Builder =
                apply { this._figureFillColorRes = fillColorRes }

        /**
         * Sets the zIndex.
         * Default value is 0.0
         */
        fun setFigureZIndex(zIndex: Float): Builder = apply { this._figureZIndex = zIndex }

        override public fun build(): CircleToolView = super.build()
    }

    /**
     * This is preferred way for creation [CircleToolView] from Kotlin code.
     */
    @MapWithToolsDsl
    class DslBuilder(context: Context): DefaultCircleToolViewBuilder(context) {

        /**
         * View id. If not set, [R.id.circle_tool_default] is default value.
         */
        var id: Int
            @IdRes get() = _id
            set(@IdRes value) { _id = value }

        /**
         * Image of drawed tool, recommended to be a state-list drawable, for "checked" and "non-checked" states
         * If not set, uses default drawable
         * at [DEFAULT_BACKGROUND_CHECKED_IMAGE_RES_ID] and [DEFAULT_BACKGROUND_NON_CHECKED_IMAGE_RES_ID].
         */
        var image: Drawable
            get() = _image
            set(value) { _image = image }

        /**
         * Clickability of every drawed circle
         * Default value is false.
         */
        var isFigureClickable: Boolean
            get() = _isFigureClickable
            set(value) { _isFigureClickable = value }

        /**
         * Width (in screen pixels) of the circle's outline.
         * It must be zero or greater. If it is zero then no outline is drawn.
         */
        var figureStrokeWidth: Float

            @Dimension
            get() = _figureStrokeWidth

            set(@Dimension value) { _figureStrokeWidth = value }

        /**
         * Stroke pattern of the circle's outline.
         * Default value is null (simple line)
         */
        var figureStrokePattern: List<PatternItem>?
            get() = _figureStrokePattern
            set(value) { _figureStrokePattern = value }

        /**
         * Color of every drawed circle's outline, in the integer format.
         * If TRANSPARENT is used then no outline is drawn.
         */
        var figureStrokeColor: Int?

            @ColorInt
            get() = _figureStrokeColor

            set(@ColorInt value) { _figureStrokeColor = value }

        /**
         * Color of every drawed circle's outline, in the integer format.
         * If TRANSPARENT is used then no outline is drawn.
         */
        var figureStrokeColorRes: Int

            @ColorRes get() = _figureStrokeColorRes
            set(@ColorRes value) { _figureStrokeColorRes = value }

        /**
         * Color inside every drawed circle's, in the integer format.
         * If TRANSPARENT is used then no outline is drawn.
         */
        var figureFillColor: Int?

            @ColorInt
            get() = _figureFillColor

            set(@ColorInt value) { _figureFillColor = value }

        /**
         * Color inside every drawed circle's, in the integer format.
         * If TRANSPARENT is used then no outline is drawn.
         */
        var figureFillColorRes: Int

            @ColorRes get() = _figureFillColorRes
            set(@ColorRes value) { _figureFillColorRes = value }

        /**
         * Sets the zIndex.
         * Default value is 0.0
         */
        var figureZIndex: Float
            get() = _figureZIndex
            set(value) { _figureZIndex = value }

    }

    companion object {

        @JvmField
        val DEFAULT_BACKGROUND_CHECKED_IMAGE_RES_ID = R.drawable.ic_default_circle_tool_checked

        @JvmField
        val DEFAULT_BACKGROUND_NON_CHECKED_IMAGE_RES_ID = R.drawable.ic_default_circle_tool_unchecked

        @JvmField
        val DEFAULT_STROKE_WIDTH_RES_ID = R.dimen.stroke_width_default

        @JvmField
        val DEFAULT_STROKE_COLOR_RES_ID = R.color.stroke_default

        @JvmField
        val DEFAULT_FILL_COLOR_RES_ID = R.color.fill_default

        const val DEFAULT_IS_FIGURE_CLICKABLE = false

        const val DEFAULT_FIGURE_Z_INDEX = 0.0f
    }
}