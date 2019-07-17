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
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions


/**
 * Created by TofiBashers on 06.05.2019.
 */
class PolylineToolView : ToolView {

    private sealed class State {
        class WaitingState : State()
        class DrawingState(val currentlyDrawedPolyline: Polyline) : State()
    }

    private var state: State = State.WaitingState()

    private var isFigureClickable: Boolean = DEFAULT_IS_FIGURE_CLICKABLE
    private var width: Float = 0f
    private var strokePattern: List<PatternItem>? = null
    private var color: Int = 0
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
                        width: Float,
                        strokePattern: List<PatternItem>?,
                        color: Int,
                        isGeodesic: Boolean,
                        figureZIndex: Float) : super(context, id, image) {
        this.isFigureClickable = isFigureClickable
        this.width = width
        this.strokePattern = strokePattern
        this.color = color
        this.isGeodesic = isGeodesic
        this.figureZIndex = figureZIndex
    }

    fun init(attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.PolylineToolView,
                defStyleAttr,
                defStyleRes).apply {

            try {
                buttonDrawable = null
                background = getDrawable(R.styleable.PolylineToolView_android_background) ?:
                        createDefaultToolImageWithStates(context.resources,
                                DEFAULT_BACKGROUND_CHECKED_IMAGE_RES_ID,
                                DEFAULT_BACKGROUND_NON_CHECKED_IMAGE_RES_ID)
                isFigureClickable = getBoolean(R.styleable.PolylineToolView_figureClickable,
                        DEFAULT_IS_FIGURE_CLICKABLE)
                isGeodesic = getBoolean(R.styleable.PolylineToolView_isGeodesic,
                        DEFAULT_IS_FIGURE_GEODESIC)
                figureZIndex = getFloat(R.styleable.PolylineToolView_figureZIndex,
                        DEFAULT_FIGURE_Z_INDEX)
                width = getDimension(R.styleable.PolylineToolView_strokeWidth,
                        resources.getDimension(RectangleToolView.DEFAULT_STROKE_WIDTH_RES_ID))
                color = getColor(R.styleable.PolylineToolView_color,
                        ResourcesCompat.getColor(resources, RectangleToolView.DEFAULT_STROKE_COLOR_RES_ID, null))
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
            if (unmaskedEvent == MotionEvent.ACTION_UP) {
                when (state) {
                    is State.WaitingState -> {
                        val firstPointLatLng = map?.projection?.fromScreenLocation(
                                Point(motionEvent.x.toInt(), motionEvent.y.toInt()))
                        map?.addPolyline(PolylineOptions().add(firstPointLatLng))
                                ?.let { state = State.DrawingState(it) }
                    }
                    is State.DrawingState -> {
                        val drawingState = state as State.DrawingState
                        drawingState.currentlyDrawedPolyline.remove()
                        val currentPointLatLng = map?.projection?.fromScreenLocation(
                                Point(motionEvent.x.toInt(), motionEvent.y.toInt()))
                        map?.addPolyline(
                                PolylineOptions()
                                        .addAll(drawingState.currentlyDrawedPolyline.points)
                                        .add(currentPointLatLng)
                                        .clickable(isFigureClickable)
                                        .width(width)
                                        .color(color)
                                        .geodesic(isGeodesic)
                                        .zIndex(figureZIndex))
                                ?.let { state = State.DrawingState(it) }
                    }
                }
            }
            return@setOnTouchListener true
        }
    }

    open class DefaultPolylineToolViewBuilder(context: Context): BaseToolViewBuilder<PolylineToolView>(context) {

        override var _id = R.id.polyline_tool_default

        override var _image: Drawable = createDefaultToolImageWithStates(context.resources,
                DEFAULT_BACKGROUND_CHECKED_IMAGE_RES_ID,
                DEFAULT_BACKGROUND_NON_CHECKED_IMAGE_RES_ID)

        protected var _isFigureClickable: Boolean = DEFAULT_IS_FIGURE_CLICKABLE

        protected var _figureWidth: Float = context.resources.getDimension(DEFAULT_FIGURE_WIDTH_RES_ID)

        protected var _figureStrokePattern: List<PatternItem>? = null

        protected var _figureColor: Int? = null

        protected var _figureColorRes: Int = DEFAULT_COLOR_RES_ID

        protected var _figureZIndex: Float = DEFAULT_FIGURE_Z_INDEX

        protected var _figureIsGeodesic: Boolean = DEFAULT_IS_FIGURE_GEODESIC

        @PublishedApi
        override fun build(): PolylineToolView = PolylineToolView(context,
                _id,
                _image,
                _isFigureClickable,
                _figureWidth,
                _figureStrokePattern,
                _figureColor ?:
                        ResourcesCompat.getColor(context.resources, _figureColorRes, null),
                _figureIsGeodesic,
                _figureZIndex)
    }

    /**
     * This is preferred way for creation [RectangleToolView] from Java code.
     */
    class Builder(context: Context): DefaultPolylineToolViewBuilder(context) {

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
        fun setFigureWidth(width: Float) = apply { this._figureWidth = width }

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
        fun setFigureColor(@ColorInt color: Int) = apply { this._figureColor = color }

        /**
         * Sets the stroke color.
         * The stroke color is the color of this rectangle's outline, in the integer format specified by Color.
         * If TRANSPARENT is used then no outline is drawn.
         */
        fun setFigureColorRes(@ColorRes colorRes: Int) =
                apply { this._figureColor = ResourcesCompat.getColor(context.resources, colorRes, null) }

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

        override public fun build(): PolylineToolView = super.build()
    }

    /**
     * This is preferred way for creation [RectangleToolView] from Kotlin code.
     */
    @MapWithToolsDsl
    class DslBuilder(context: Context): DefaultPolylineToolViewBuilder(context) {

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
            get() = _figureWidth

            set(@Dimension value) { _figureWidth = value }

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
        var figureColor: Int?

            @ColorInt get() = _figureColor
            set(@ColorInt value) { _figureColor = value }

        /**
         * Color of every drawed rectangle's outline, in the integer format.
         * If TRANSPARENT is used then no outline is drawn.
         */
        var figureColorRes: Int
            @ColorRes get() = _figureColorRes
            set(@ColorRes value) { _figureColorRes = value }

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
        val DEFAULT_BACKGROUND_CHECKED_IMAGE_RES_ID = R.drawable.ic_default_polyline_checked

        @JvmField
        val DEFAULT_BACKGROUND_NON_CHECKED_IMAGE_RES_ID = R.drawable.ic_default_polyline_unchecked

        @JvmField
        val DEFAULT_FIGURE_WIDTH_RES_ID = R.dimen.stroke_width_default

        @JvmField
        val DEFAULT_COLOR_RES_ID = R.color.stroke_default

        const val DEFAULT_IS_FIGURE_CLICKABLE = false

        const val DEFAULT_IS_FIGURE_GEODESIC = false

        const val DEFAULT_FIGURE_Z_INDEX = 0.0f
    }
}