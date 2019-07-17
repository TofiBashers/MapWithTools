package com.gmail.tofibashers.mapwithtools

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.annotation.Dimension
import android.support.annotation.IdRes
import android.util.AttributeSet
import android.view.View
import android.widget.RadioGroup
import com.gmail.tofibashers.mapwithtools.internal.MapWithToolsDsl
import com.gmail.tofibashers.mapwithtools.tool_view.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView


/**
 * Created by TofiBashers on 27.01.2019.
 *
 * This class represents bar with drawing tools.
 * If no toolview added to tools bar,
 * by default added single [MapControlToolView].
 */
class DrawingToolsBar : RadioGroup {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private constructor(context: Context,
                        @Dimension toolHeight: Int,
                        toolBuilders: List<BaseBuilder<ToolView>>) : super(context) {
        orientation = RadioGroup.HORIZONTAL
        setBackgroundColor(Color.WHITE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevation = context.resources.getDimension(R.dimen.tool_elevation_default)
        }
        toolBuilders.forEach {
            addView(it.build(), RadioGroup.LayoutParams(toolHeight, toolHeight))
        }
        addMapControlToolIfNotAdded()
    }

    override fun onFinishInflate() {
        addMapControlToolIfNotAdded()
        super.onFinishInflate()
    }

    fun onMapReady(map: GoogleMap, drawingGesturesOverlayView: View, mapView: MapView) =
            (0 until childCount)
                    .map { getChildAt(it) as ToolView }
                    .forEach { it.onMapReady(map, drawingGesturesOverlayView, mapView) }

    private fun addMapControlToolIfNotAdded() {
        var defaultView = getDefaultToolViewChild()
        if (defaultView == null) {
            defaultView = MapControlToolView(context)
            val layoutParams = if(childCount == 0) {
                val defaultHeight = context.resources.getDimensionPixelSize(R.dimen.tool_height_default)
                RadioGroup.LayoutParams(defaultHeight, defaultHeight)
            }
            else {
                val lastChildParams = getChildAt(childCount - 1).layoutParams
                RadioGroup.LayoutParams(lastChildParams.width, lastChildParams.height)
            }
            addView(defaultView, layoutParams)
        }
        defaultView.isChecked = true
    }

    private fun addMapControlToolIfNotAdded(@Dimension toolHeight: Int) {
        var defaultView = getDefaultToolViewChild()
        if (defaultView == null) {
            defaultView = MapControlToolView(context)
            addView(defaultView, RadioGroup.LayoutParams(toolHeight, toolHeight))
        }
        defaultView.isChecked = true
    }

    private fun getDefaultToolViewChild() : MapControlToolView? {
        return (0 until childCount)
                .map { getChildAt(it) }
                .firstOrNull { it is MapControlToolView } as MapControlToolView
    }

    private fun getDefaultToolViewChildIndex() : Int? {
        return (0 until childCount)
                .firstOrNull { getChildAt(it) is MapControlToolView }
    }

    open class DefaultDrawingToolsBarBuilder(context: Context): BaseBuilder<DrawingToolsBar>(context) {

        override var _id = R.id.drawing_tools_bar_default

        protected val _toolBuilders: MutableList<BaseBuilder<ToolView>> = mutableListOf()

        @Dimension
        protected var _toolHeight: Int = context.resources.getDimensionPixelSize(R.dimen.tool_height_default)

        @PublishedApi
        override fun build(): DrawingToolsBar = DrawingToolsBar(context, _toolHeight, _toolBuilders)
    }

    /**
     * This is preferred way for creation [DrawingToolsBar] from Java code.
     */
    class Builder(context: Context): DefaultDrawingToolsBarBuilder(context) {

        /**
         * Sets view id. If not, [R.id.drawing_tools_bar_default] is default value.
         */
        fun setId(@IdRes id: Int) = apply { this._id = id }

        /**
         * Sets height of every added tool. If not set, [R.dimen.tool_height_default] used.
         */
        fun setToolHeight(@Dimension toolHeight: Int) = apply { this._toolHeight = toolHeight }

        /**
         * Adds any [ToolView] to drawing tools. If no toolview added to tools bar,
         * by default added single [MapControlToolView].
         */
        fun addTool(toolViewBuilder: ToolView.BaseToolViewBuilder<ToolView>) =
                apply { _toolBuilders.add(toolViewBuilder) }

        override public fun build(): DrawingToolsBar = super.build()
    }

    /**
     * This is preferred way for creation [DrawingToolsBar] from Kotlin code.
     */
    @MapWithToolsDsl
    class DslBuilder(context: Context): DefaultDrawingToolsBarBuilder(context) {

        /**
         * View id. If not set, [R.id.drawing_tools_bar_default] is default value.
         */
        var id: Int
            @IdRes get() = _id
            set(@IdRes value) { _id = value }

        /**
         * Height of every added tool. If not set, [R.dimen.tool_height_default] used.
         */
        var toolHeight
            @Dimension get() = _toolHeight
            set(@Dimension value) { this._toolHeight = value }

        /**
         * Adds [PolylineToolView] to drawing tools. If no toolview added to tools bar,
         * by default added single [MapControlToolView].
         */
        fun polylineTool(polylineToolView: PolylineToolView.DslBuilder.() -> Unit) {
            val builder = PolylineToolView.DslBuilder(context)
            builder.polylineToolView()
            _toolBuilders.add(builder)
        }

        /**
         * Adds [CircleToolView] to drawing tools. If no toolview added to tools bar,
         * by default added single [MapControlToolView].
         */
        fun circleTool(circleToolView: CircleToolView.DslBuilder.() -> Unit) {
            val builder = CircleToolView.DslBuilder(context)
            builder.circleToolView()
            _toolBuilders.add(builder)
        }

        /**
         * Adds [RectangleToolView] to drawing tools. If no toolview added to tools bar,
         * by default added single [MapControlToolView].
         */
        fun rectangleTool(rectangleToolView: RectangleToolView.DslBuilder.() -> Unit) {
            val builder = RectangleToolView.DslBuilder(context)
            builder.rectangleToolView()
            _toolBuilders.add(builder)
        }

        /**
         * Adds [MapControlToolView] to drawing tools. If no toolview added to tools bar,
         * by default added single [MapControlToolView].
         */
        fun mapControlTool(mapControlToolView: MapControlToolView.DslBuilder.() -> Unit) {
            val builder = MapControlToolView.DslBuilder(context)
            builder.mapControlToolView()
            _toolBuilders.add(builder)
        }
    }
}