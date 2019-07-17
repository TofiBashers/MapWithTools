package com.gmail.tofibashers.mapwithtools

import android.content.Context
import android.os.Bundle
import android.support.annotation.IdRes
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.gmail.tofibashers.mapwithtools.internal.MapWithToolsDsl
import com.gmail.tofibashers.mapwithtools.zoom.UIZoomControlsType
import com.gmail.tofibashers.mapwithtools.zoom.ZoomRangeBarView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback


/**
 * Created by TofiBashers on 14.01.2019.
 *
 * This class is wrapper of google [MapView],
 * that has some opportunities as drawing by user, and zooming view with levels.
 */
class MapWithToolsView : FrameLayout, OnMapReadyCallback {

    private lateinit var googleMapView: MapView
    private lateinit var overlayView: FrameLayout
    private var drawingToolsBar: DrawingToolsBar? = null
    private var zoomLevelsView: ZoomRangeBarView? = null
    private var uiZoomControlsType = DEFAULT_UI_ZOOM_CONTROLS_TYPE

    private var currentZoom: Float = MAX_ZOOMING_LEVEL

    private var outCallback: OnMapReadyCallback? = null

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
                        zoomControlsType: UIZoomControlsType,
                        zoomLevelsViewBuilder: BaseBuilder<ZoomRangeBarView>?,
                        drawingToolsBarBuilder: BaseBuilder<DrawingToolsBar>?) : super(context) {
        this.id = id

        this.uiZoomControlsType = zoomControlsType

        this.googleMapView = MapView(context)
        addView(googleMapView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT))

        this.zoomLevelsView = zoomLevelsViewBuilder?.build() ?: ZoomRangeBarView(context)
        addView(zoomLevelsView, generateDefaultZoomLevelsViewParams())
        zoomLevelsView?.visibility = View.GONE

        this.drawingToolsBar = drawingToolsBarBuilder?.build() ?: DrawingToolsBar(context)
        addView(drawingToolsBar, generateDefaultDrawingToolbarParams())
        drawingToolsBar?.visibility = View.GONE
    }

    override fun onMapReady(map: GoogleMap) {
        overlayView = FrameLayout(context)
        googleMapView.addView(overlayView, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT))

        drawingToolsBar?.visibility = View.VISIBLE
        drawingToolsBar?.onMapReady(map, overlayView, googleMapView)

        currentZoom = map.cameraPosition.zoom
        setZoomLevelWithoutNotify(currentZoom)
        map.setOnCameraMoveListener {
            if (currentZoom != map.cameraPosition.zoom) {
                currentZoom = map.cameraPosition.zoom
                setZoomLevelWithoutNotify(currentZoom)
            }
        }
        zoomLevelsView?.onZoomLevelsChangeListener =
                object : ZoomRangeBarView.OnZoomLevelChangeListener {
                    override fun onZoomLevelChanged(zoomLevel: Int, zoomLevelsCount: Int) {
                        map.moveCamera(CameraUpdateFactory.zoomTo(
                                (MAX_ZOOMING_LEVEL - MIN_ZOOMING_LEVEL)*(
                                        zoomLevel.toFloat()/zoomLevelsCount) + MIN_ZOOMING_LEVEL))
                    }
                }

        when (uiZoomControlsType) {
            UIZoomControlsType.WITHOUT_LEVELS -> {
                map.uiSettings.isZoomControlsEnabled = true
                zoomLevelsView?.visibility = View.GONE
            }
            UIZoomControlsType.WITH_LEVELS -> {
                map.uiSettings.isZoomControlsEnabled = false
                zoomLevelsView?.visibility = View.VISIBLE
            }
            UIZoomControlsType.DISABLED -> {
                map.uiSettings.isZoomControlsEnabled = false
                zoomLevelsView?.visibility = View.GONE
            }
        }
        outCallback?.onMapReady(map)
    }

    override fun onFinishInflate() {
        this.zoomLevelsView = findViewByType() ?:
                ZoomRangeBarView(context).apply { layoutParams = generateDefaultZoomLevelsViewParams() }
        addView(zoomLevelsView)
        zoomLevelsView?.visibility = View.GONE

        drawingToolsBar = findViewByType() ?:
                DrawingToolsBar(context).apply { layoutParams = generateDefaultDrawingToolbarParams() }
        addView(drawingToolsBar)
        drawingToolsBar?.visibility = View.GONE
        super.onFinishInflate()
    }

    /**
     * @see MapView.getMapAsync
     */
    fun getMapAsync(callback: OnMapReadyCallback) {
        this.outCallback = callback
        googleMapView.getMapAsync(this)
    }

    /**
     * You must call this method from the parent Activity/Fragment's corresponding method.
     */
    fun onCreate(savedInstanceState: Bundle?) = googleMapView.onCreate(savedInstanceState)

    /**
     * You must call this method from the parent Activity/Fragment's corresponding method.
     */
    fun onDestroy() = googleMapView.onDestroy()

    /**
     * You must call this method from the parent WearableActivity's corresponding method.
     */
    fun onEnterAmbient(ambientDetails: Bundle) = googleMapView.onEnterAmbient(ambientDetails)

    /**
     * You must call this method from the parent WearableActivity's corresponding method.
     */
    fun onExitAmbient() = googleMapView.onExitAmbient()

    /**
     * You must call this method from the parent Activity/Fragment's corresponding method.
     */
    fun onLowMemory() = googleMapView.onLowMemory()

    /**
     * You must call this method from the parent Activity/Fragment's corresponding method.
     */
    fun onPause() = googleMapView.onPause()

    /**
     * You must call this method from the parent Activity/Fragment's corresponding method.
     */
    fun onResume() = googleMapView.onResume()

    /**
     * You must call this method from the parent Activity/Fragment's corresponding method.
     */
    fun onSaveInstanceState(outState: Bundle) = googleMapView.onSaveInstanceState(outState)

    /**
     * You must call this method from the parent Activity/Fragment's corresponding method.
     */
    fun onStart() = googleMapView.onStart()

    /**
     * You must call this method from the parent Activity/Fragment's corresponding method.
     */
    fun onStop() = googleMapView.onStop()

    private fun init(attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.MapWithToolsView,
                defStyleAttr,
                defStyleRes).apply {

            try {
                uiZoomControlsType = UIZoomControlsType.values()[
                        getInt(R.styleable.MapWithToolsView_uiZoomControlsType,
                                DEFAULT_UI_ZOOM_CONTROLS_TYPE.ordinal)]
            }
            finally {
                recycle()
            }
        }

        googleMapView = MapView(context)
        addView(googleMapView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT))

    }

    private fun generateDefaultZoomLevelsViewParams() =
            FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.BOTTOM or Gravity.RIGHT
                bottomMargin = resources.getDimensionPixelSize(R.dimen.zoom_levels_bottom_margin)
                rightMargin = -resources.getDimensionPixelSize(R.dimen.zoom_levels_right_margin)
            }

    private fun generateDefaultDrawingToolbarParams() =
            FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                topMargin = resources.getDimensionPixelSize(R.dimen.drawing_tools_top_margin)
            }

    private fun setZoomLevelWithoutNotify(zoomLevel: Float) {
        zoomLevelsView?.apply {
            setProgressWithoutNotify(
                    Math.round((zoomLevel/
                            (MAX_ZOOMING_LEVEL - MIN_ZOOMING_LEVEL))*levelsCount))
        }
    }

    private inline fun <reified T: View> findViewByType(): T? =
        (0..childCount-1)
                .map { getChildAt(it) }
                .find { it is T } as T?

    open class DefaultMapWithToolsViewBuilder(context: Context): BaseBuilder<MapWithToolsView>(context) {

        override var _id = R.id.map_with_tools_default

        protected var _zoomControlsType: UIZoomControlsType = DEFAULT_UI_ZOOM_CONTROLS_TYPE
        protected var _zoomLevelsViewBuilder: BaseBuilder<ZoomRangeBarView>? = null
        protected var _drawingToolsBarBuilder: BaseBuilder<DrawingToolsBar>? = null

        @PublishedApi
        override fun build(): MapWithToolsView = MapWithToolsView(context,
                _id,
                _zoomControlsType,
                _zoomLevelsViewBuilder,
                _drawingToolsBarBuilder)
    }

    /**
     * This is preferred way for creation [MapWithToolsView] from Java code.
     */
    class Builder(context: Context): DefaultMapWithToolsViewBuilder(context) {

        /**
         * Sets view id. If not, [R.id.map_with_tools_default] is default value.
         */
        fun setId(@IdRes id: Int) = apply { this._id = id }

        /**
         * Set type of zoom controls for usage in [MapWithToolsView] object.
         * If type is [UIZoomControlsType.WITH_LEVELS], and levels view not added
         * - uses default levels view with 5 levels and default icons.
         * Default value is [DEFAULT_UI_ZOOM_CONTROLS_TYPE]
         */
        fun setZoomControlsType(zoomControlsType: UIZoomControlsType) =
                apply { this._zoomControlsType = zoomControlsType }

        fun setZoomLevelsView(zoomLevelsViewBuilder: BaseBuilder<ZoomRangeBarView>) =
                apply { this._zoomLevelsViewBuilder = zoomLevelsViewBuilder }

        fun setDrawingToolsBar(drawingToolsBarBuilder: BaseBuilder<DrawingToolsBar>) =
                apply { this._drawingToolsBarBuilder = drawingToolsBarBuilder }

        override public fun build(): MapWithToolsView = super.build()
    }

    /**
     * This is preferred way for creation [MapWithToolsView] from Kotlin code.
     */
    @MapWithToolsDsl
    class DslBuilder(context: Context): DefaultMapWithToolsViewBuilder(context) {

        /**
         * View id. If not set, [R.id.map_with_tools_default] is default value.
         */
        var id: Int
        @IdRes get() = _id
        set(@IdRes value) { _id = value }

        /**
         * Type of zoom controls for usage in [MapWithToolsView] object.
         * If type is [UIZoomControlsType.WITH_LEVELS], and levels view not added
         * - uses default levels view with 5 levels and default icons.
         * Default value is [DEFAULT_UI_ZOOM_CONTROLS_TYPE]
         */
        var zoomControlsType: UIZoomControlsType
            get() = _zoomControlsType
            set(value) { _zoomControlsType = value }

        /**
         * Adds [DrawingToolsBar] to map.
         */
        fun drawingToolsBar(drawingToolsBar: DrawingToolsBar.DslBuilder.() -> Unit) {
            val builder = DrawingToolsBar.DslBuilder(context)
            builder.drawingToolsBar()
            _drawingToolsBarBuilder = builder
        }

        /**
         * Adds [ZoomRangeBarView] to map. If [ZoomRangeBarView] must be enabled -
         * also necessary add [zoomControlsType].
         */
        fun zoomRangeBar(zoomRangeBar: ZoomRangeBarView.DslBuilder.() -> Unit) {
            val builder = ZoomRangeBarView.DslBuilder(context)
            builder.zoomRangeBar()
            _zoomLevelsViewBuilder = builder
        }
    }

    companion object {
        private val DEFAULT_UI_ZOOM_CONTROLS_TYPE = UIZoomControlsType.WITHOUT_LEVELS

        private val MAX_ZOOMING_LEVEL = 21f

        private val MIN_ZOOMING_LEVEL = 2f
    }
}