package com.gmail.tofibashers.mapwithtools

import android.content.Context
import com.gmail.tofibashers.mapwithtools.tool_view.CircleToolView
import com.gmail.tofibashers.mapwithtools.tool_view.MapControlToolView
import com.gmail.tofibashers.mapwithtools.tool_view.PolylineToolView
import com.gmail.tofibashers.mapwithtools.tool_view.RectangleToolView
import com.gmail.tofibashers.mapwithtools.zoom.ZoomRangeBarView


/**
 * Created by TofiBashers on 20.04.2019.
 */

/**
 * Preferred method for creation [MapControlToolView] from Kotlin code.
 */
inline fun mapWithToolsView(context: Context,
                            buildMapWithTools: MapWithToolsView.DslBuilder.() -> Unit): MapWithToolsView {
    val builder = MapWithToolsView.DslBuilder(context)
    builder.buildMapWithTools()
    return builder.build()
}

/**
 * Preferred method for creation [DrawingToolsBar] from Kotlin code.
 */
inline fun drawingToolsBar(context: Context,
                           drawingToolsBar: DrawingToolsBar.DslBuilder.() -> Unit): DrawingToolsBar {
    val builder = DrawingToolsBar.DslBuilder(context)
    builder.drawingToolsBar()
    return builder.build()
}

/**
 * Preferred method for creation [ZoomRangeBarView] from Kotlin code.
 */
inline fun zoomRangeBar(context: Context,
                        zoomRangeBar: ZoomRangeBarView.DslBuilder.() -> Unit): ZoomRangeBarView {
    val builder = ZoomRangeBarView.DslBuilder(context)
    builder.zoomRangeBar()
    return builder.build()
}

/**
 * Preferred method for creation [PolylineToolView] from Kotlin code.
 */
inline fun polylineTool(context: Context,
                        polylineToolView: PolylineToolView.DslBuilder.() -> Unit): PolylineToolView {
    val builder = PolylineToolView.DslBuilder(context)
    builder.polylineToolView()
    return builder.build()
}

/**
 * Preferred method for creation [CircleToolView] from Kotlin code.
 */
inline fun circleTool(context: Context,
                      circleToolView: CircleToolView.DslBuilder.() -> Unit): CircleToolView {
    val builder = CircleToolView.DslBuilder(context)
    builder.circleToolView()
    return builder.build()
}

/**
 * Preferred method for creation [RectangleToolView] from Kotlin code.
 */
inline fun rectangleTool(context: Context,
                         rectangleToolView: RectangleToolView.DslBuilder.() -> Unit): RectangleToolView {
    val builder = RectangleToolView.DslBuilder(context)
    builder.rectangleToolView()
    return builder.build()
}

/**
 * Preferred method for creation [MapControlToolView] from Kotlin code.
 */
inline fun mapControlTool(context: Context,
                          mapControlToolView: MapControlToolView.DslBuilder.() -> Unit): MapControlToolView {
    val builder = MapControlToolView.DslBuilder(context)
    builder.mapControlToolView()
    return builder.build()
}