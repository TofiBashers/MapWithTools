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
import com.gmail.tofibashers.mapwithtools.tool_view.CircleToolView.Companion.DEFAULT_BACKGROUND_CHECKED_IMAGE_RES_ID
import com.gmail.tofibashers.mapwithtools.tool_view.CircleToolView.Companion.DEFAULT_BACKGROUND_NON_CHECKED_IMAGE_RES_ID
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions


/**
 * Created by TofiBashers on 20.01.2019.
 */
class RectangleToolView : ToolView {

    private sealed class State {
        class WaitingState : State()
        class DrawingState(val firstTapPoint: LatLng,
                           val currentlyDrawedPolygon: Polygon) : State()
    }

    private var state: State = State.WaitingState()

    private var isFigureClickable: Boolean = DEFAULT_IS_FIGURE_CLICKABLE
    private var strokeWidth: Float = 0f
    private var strokePattern: List<PatternItem>? = null
    private var strokeColor: Int = 0
    private var fillColor: Int = 0
    private var isGeodesic: Boolean = DEFAULT_IS_FIGURE_GEODESIC
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
                        isGeodesic: Boolean,
                        figureZIndex: Float) : super(context, id, image) {
        this.isFigureClickable = isFigureClickable
        this.strokeWidth = strokeWidth
        this.strokePattern = strokePattern
        this.strokeColor = strokeColor
        this.fillColor = fillColor
        this.isGeodesic = isGeodesic
        this.figureZIndex = figureZIndex
    }

    fun init(attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.RectangleToolView,
                defStyleAttr,
                defStyleRes).apply {

            try {
                buttonDrawable = null
                background = getDrawable(R.styleable.RectangleToolView_android_background) ?:
                        createDefaultToolImageWithStates(context.resources,
                                DEFAULT_BACKGROUND_CHECKED_IMAGE_RES_ID,
                                DEFAULT_BACKGROUND_NON_CHECKED_IMAGE_RES_ID)
                strokeWidth = getDimension(R.styleable.RectangleToolView_strokeWidth,
                        resources.getDimension(DEFAULT_STROKE_WIDTH_RES_ID))
                strokeColor = getColor(R.styleable.RectangleToolView_strokeColor,
                        ResourcesCompat.getColor(resources, DEFAULT_STROKE_COLOR_RES_ID, null))
                fillColor = getColor(R.styleable.RectangleToolView_fillColor,
                        ResourcesCompat.getColor(resources, DEFAULT_FILL_COLOR_RES_ID, null))
                isFigureClickable = getBoolean(R.styleable.RectangleToolView_figureClickable,
                        DEFAULT_IS_FIGURE_CLICKABLE)
                isGeodesic = getBoolean(R.styleable.RectangleToolView_isGeodesic,
                        DEFAULT_IS_FIGURE_GEODESIC)
                figureZIndex = getFloat(R.styleable.RectangleToolView_figureZIndex,
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
        state = State.WaitingState()
        drawingGesturesOverlayView.setOnTouchListener { view, motionEvent ->
            val unmaskedEvent = motionEvent.actionMasked and MotionEvent.ACTION_MASK
            when (unmaskedEvent) {
                MotionEvent.ACTION_DOWN -> {
                    if (state is State.WaitingState) {
                        val firstTapPoint = map?.projection?.fromScreenLocation(
                                Point(motionEvent.x.toInt(), motionEvent.y.toInt()))
                        if (firstTapPoint != null) {
                            map?.addPolygon(PolygonOptions().add(firstTapPoint))
                                    ?.let { state = State.DrawingState(firstTapPoint, it) }
                        }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (state is State.DrawingState && !isSamePointWithLastInHistory(motionEvent)) {
                        val drawingState = state as State.DrawingState
                        drawingState.currentlyDrawedPolygon.remove()
                        val motionPoint = map?.projection?.fromScreenLocation(
                                Point(motionEvent.x.toInt(), motionEvent.y.toInt()))
                        if (motionPoint != null) {
                            map?.addPolygon(
                                    PolygonOptions()
                                            .add(drawingState.firstTapPoint,
                                                    LatLng(drawingState.firstTapPoint.latitude, motionPoint.longitude),
                                                    motionPoint,
                                                    LatLng(motionPoint.latitude, drawingState.firstTapPoint.longitude))
                                            .clickable(isFigureClickable)
                                            .strokeColor(strokeColor)
                                            .strokePattern(strokePattern)
                                            .strokeWidth(strokeWidth)
                                            .fillColor(fillColor)
                                            .geodesic(isGeodesic)
                                            .zIndex(figureZIndex))
                                    ?.let { state = State.DrawingState(drawingState.firstTapPoint, it) }
                        }
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

    open class DefaultRectangleToolViewBuilder(context: Context): BaseToolViewBuilder<RectangleToolView>(context) {

        override var _id = R.id.rectangle_tool_default

        override var _image: Drawable = createDefaultToolImageWithStates(context.resources,
                DEFAULT_BACKGROUND_CHECKED_IMAGE_RES_ID,
                DEFAULT_BACKGROUND_NON_CHECKED_IMAGE_RES_ID)

        protected var _isFigureClickable: Boolean = DEFAULT_IS_FIGURE_CLICKABLE

        protected var _figureStrokeWidth: Float =
                context.resources.getDimension(CircleToolView.DEFAULT_STROKE_WIDTH_RES_ID)

        protected var _figureStrokePattern: List<PatternItem>? = null

        protected var _figureStrokeColor: Int? = null

        protected var _figureStrokeColorRes: Int = DEFAULT_STROKE_COLOR_RES_ID

        protected var _figureFillColor: Int? = null

        protected var _figureFillColorRes: Int = DEFAULT_FILL_COLOR_RES_ID

        protected var _figureZIndex: Float = 0f

        protected var _figureIsGeodesic: Boolean = false

        @PublishedApi
        override fun build(): RectangleToolView = RectangleToolView(context,
                _id,
                _image,
                _isFigureClickable,
                _figureStrokeWidth,
                _figureStrokePattern,
                _figureStrokeColor ?:
                        ResourcesCompat.getColor(context.resources, _figureStrokeColorRes, null),
                _figureFillColor ?:
                        ResourcesCompat.getColor(context.resources, _figureFillColorRes, null),
                _figureIsGeodesic,
                _figureZIndex)
    }

    /**
     * This is preferred way for creation [RectangleToolView] from Java code.
     */
    class Builder(context: Context): DefaultRectangleToolViewBuilder(context) {

        /**
         * Sets view id. If not, [R.id.map_control_tool_default] is default value.
         */
        fun setId(@IdRes id: Int) = apply { this._id = id }

        /**
         * Sets image of drawed tool, recommended to be a state-list drawable, for "checked" and "non-checked" states
         * If not set, uses default drawable
         * at [DEFAULT_BACKGROUND_CHECKED_IMAGE_RES_ID] and [DEFAULT_BACKGROUND_NON_CHECKED_IMAGE_RES_ID].
         */
        fun setImage(image: Drawable) = apply { this._image = image }

        /**
         * Clickability of every drawn rectangle
         * Default value is false.
         */
        fun setFigureClickable(clickable: Boolean) = apply { this._isFigureClickable = clickable}

        /**
         * Sets the width (in screen pixels) of the rectangle's outline.
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
         * The stroke color is the color of this rectangle's outline, in the integer format specified by Color.
         * If TRANSPARENT is used then no outline is drawn.
         */
        fun setFigureStrokeColor(@ColorInt strokeColor: Int) =
                apply { this._figureStrokeColor = strokeColor }

        /**
         * Sets the stroke color.
         * The stroke color is the color of this rectangle's outline, in the integer format specified by Color.
         * If TRANSPARENT is used then no outline is drawn.
         */
        fun setFigureStrokeColorRes(@ColorRes strokeColorRes: Int) =
                apply { this._figureStrokeColorRes = strokeColorRes }

        /**
         * Sets the color inside every drawed rectangle's, in the integer format.
         * If TRANSPARENT is used then no outline is drawn.
         */
        fun setFigureFillColor(@ColorInt fillColor: Int) = apply { this._figureFillColor = fillColor }

        /**
         * Sets the color inside every drawed rectangle's, in the integer format.
         * If TRANSPARENT is used then no outline is drawn.
         */
        fun setFigureFillColorRes(@ColorInt fillColorRes: Int) =
                apply { this._figureFillColorRes = fillColorRes }

        /**
         * Set the segments should be geodesic
         * Default value is false.
         */
        fun setFigureGeodesic(isGeodesic: Boolean) = apply { this._figureIsGeodesic = isGeodesic }

        /**
         * Set is the segments should be geodesic
         * Default value is 0.0
         */
        fun setFigureZIndex(zIndex: Float) = apply { this._figureZIndex = zIndex }

        override public fun build(): RectangleToolView = super.build()
    }

    /**
     * This is preferred way for creation [RectangleToolView] from Kotlin code.
     */
    @MapWithToolsDsl
    class DslBuilder(context: Context): DefaultRectangleToolViewBuilder(context) {

        /**
         * View id. If not set, [R.id.map_control_tool_default] is default value.
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
         * Clickability of every drawn rectangle
         * Default value is false.
         */
        var isFigureClickable: Boolean
            get() = _isFigureClickable
            set(value) { _isFigureClickable = value }

        /**
         * Width (in screen pixels) of the rectangle's outline.
         * It must be zero or greater. If it is zero then no outline is drawn.
         */
        var figureStrokeWidth: Float

            @Dimension
            get() = _figureStrokeWidth

            set(@Dimension value) { _figureStrokeWidth = value }

        /**
         * Stroke pattern of the rectangle's outline.
         * Default value is null (simple line)
         */
        var figureStrokePattern: List<PatternItem>?
            get() = _figureStrokePattern
            set(value) { _figureStrokePattern = value }

        /**
         * Color of every drawed rectangle's outline, in the integer format.
         * If TRANSPARENT is used then no outline is drawn.
         */
        var figureStrokeColor: Int?

            @ColorInt get() = _figureStrokeColor
            set(@ColorInt value) { _figureStrokeColor = value }

        /**
         * Color of every drawed rectangle's outline, in the integer format.
         * If TRANSPARENT is used then no outline is drawn.
         */
        var figureStrokeColorRes: Int

            @ColorRes get() = _figureStrokeColorRes
            set(@ColorRes value) { _figureStrokeColorRes = value }

        /**
         * Color inside every drawed rectangle's, in the integer format.
         * If TRANSPARENT is used then no outline is drawn.
         */
        var figureFillColor: Int?

            @ColorInt get() = _figureFillColor
            set(@ColorInt value) { _figureFillColor = value }

        /**
         * Color inside every drawed rectangle's, in the integer format.
         * If TRANSPARENT is used then no outline is drawn.
         */
        var figureFillColorRes: Int

            @ColorInt get() = _figureFillColorRes
            set(@ColorInt value) { _figureFillColorRes = value }

        /**
         * Is the segments should be geodesic
         * Default value is false.
         */
        var isFigureGeodesic: Boolean
            get() = _figureIsGeodesic
            set(value) { _figureIsGeodesic = value }

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
        val DEFAULT_BACKGROUND_CHECKED_IMAGE_RES_ID = R.drawable.ic_default_rectange_tool_checked

        @JvmField
        val DEFAULT_BACKGROUND_NON_CHECKED_IMAGE_RES_ID = R.drawable.ic_default_rectange_tool_unchecked

        @JvmField
        val DEFAULT_STROKE_WIDTH_RES_ID = R.dimen.stroke_width_default

        @JvmField
        val DEFAULT_STROKE_COLOR_RES_ID = R.color.stroke_default

        @JvmField
        val DEFAULT_FILL_COLOR_RES_ID = R.color.fill_default

        const val DEFAULT_IS_FIGURE_CLICKABLE = false

        const val DEFAULT_IS_FIGURE_GEODESIC = false

        const val DEFAULT_FIGURE_Z_INDEX = 0.0f
    }


}