package com.gmail.tofibashers.mapwithtools.tool_view

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.IdRes
import android.util.AttributeSet
import android.view.View
import com.gmail.tofibashers.mapwithtools.R
import com.gmail.tofibashers.mapwithtools.internal.MapWithToolsDsl
import com.gmail.tofibashers.mapwithtools.tool_view.CircleToolView.Companion.DEFAULT_BACKGROUND_CHECKED_IMAGE_RES_ID
import com.gmail.tofibashers.mapwithtools.tool_view.CircleToolView.Companion.DEFAULT_BACKGROUND_NON_CHECKED_IMAGE_RES_ID
import com.google.android.gms.maps.MapView


/**
 * Created by TofiBashers on 26.01.2019.
 *
 * This view must replace default [MapView].
 */
class MapControlToolView : ToolView {

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

    private constructor(context: Context, @IdRes id: Int, image: Drawable) : super(context, id, image)

    fun init(attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.MapControlToolView,
                defStyleAttr,
                defStyleRes).apply {

            try {
                buttonDrawable = null
                background = getDrawable(R.styleable.MapControlToolView_android_background) ?:
                        createDefaultToolImageWithStates(context.resources,
                                RectangleToolView.DEFAULT_BACKGROUND_CHECKED_IMAGE_RES_ID,
                                RectangleToolView.DEFAULT_BACKGROUND_NON_CHECKED_IMAGE_RES_ID)
            }
            finally {
                recycle()
            }
        }
    }

    override fun onToolEnabled(drawingGesturesOverlayView: View, mapView: MapView) {
        drawingGesturesOverlayView.isClickable = false
        drawingGesturesOverlayView.isFocusable = false
        drawingGesturesOverlayView.setOnTouchListener(null)
    }

    /**
     * This is preferred way for creation [MapControlToolView] from Java code.
     */
    class Builder(context: Context): BaseToolViewBuilder<MapControlToolView>(context) {

        override var _id = R.id.map_control_tool_default

        override var _image: Drawable = createDefaultToolImageWithStates(context.resources,
                DEFAULT_BACKGROUND_CHECKED_IMAGE_RES_ID,
                DEFAULT_BACKGROUND_NON_CHECKED_IMAGE_RES_ID)

        /**
         * Sets view id. If not, [View.NO_ID] is default value.
         */
        fun setId(@IdRes id: Int) = apply { this._id = id }

        override public fun build(): MapControlToolView = MapControlToolView(context, _id, _image)
    }

    /**
     * This is preferred way for creation [MapControlToolView] from Kotlin code.
     */
    @MapWithToolsDsl
    class DslBuilder(context: Context): BaseToolViewBuilder<MapControlToolView>(context) {

        override var _id = R.id.map_control_tool_default

        override var _image: Drawable = createDefaultToolImageWithStates(context.resources,
                DEFAULT_BACKGROUND_CHECKED_IMAGE_RES_ID,
                DEFAULT_BACKGROUND_NON_CHECKED_IMAGE_RES_ID)

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

        @PublishedApi
        override internal fun build(): MapControlToolView = MapControlToolView(context,
                _id,
                _image)
    }

    companion object {

        @JvmField
        val DEFAULT_BACKGROUND_CHECKED_IMAGE_RES_ID = R.drawable.ic_default_pan_tool_checked

        @JvmField
        val DEFAULT_BACKGROUND_NON_CHECKED_IMAGE_RES_ID = R.drawable.ic_default_pan_tool_unchecked
    }
}