package com.gmail.tofibashers.mapwithtools.zoom

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.IdRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import com.gmail.tofibashers.mapwithtools.BaseBuilder
import com.gmail.tofibashers.mapwithtools.R
import com.gmail.tofibashers.mapwithtools.internal.MapWithToolsDsl
import kotlinx.android.synthetic.main.view_zoom_rangebar.view.*


/**
 * Created by TofiBashers on 07.04.2019.
 *
 * This class represents zooming view with manual setting zoom levels count.
 */
class ZoomRangeBarView : LinearLayout {

    private lateinit var zoomMaxImage: Drawable
    private lateinit var zoomMinImage: Drawable

    private val onSeekbarChangeListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            onZoomLevelsChangeListener?.onZoomLevelChanged(seekbar_range.progress, levelsCount)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    }

    var levelsCount: Int = DEFAULT_LEVELS_COUNT
        private set

    var onZoomLevelsChangeListener: OnZoomLevelChangeListener? = null

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
                        zoomMaxImage: Drawable,
                        zoomMinImage: Drawable,
                        levelsCount: Int) : super(context) {
        this.id = id
        this.zoomMaxImage = zoomMaxImage
        this.zoomMinImage = zoomMinImage
        this.levelsCount = levelsCount
        initRangeBarWithImages()
    }

    /**
     * Manually set progress level without calling any listeners.
     */
    fun setProgressWithoutNotify(progress: Int) {
        seekbar_range.setOnSeekBarChangeListener(null)
        seekbar_range.progress = progress
        seekbar_range.setOnSeekBarChangeListener(onSeekbarChangeListener)
    }

    private fun init(attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        zoomMaxImage = ContextCompat.getDrawable(context, DEFAULT_ZOOM_HIGH_IMAGE_ID)!!
        zoomMinImage = ContextCompat.getDrawable(context, DEFAULT_ZOOM_LOW_IMAGE_ID)!!
        levelsCount = DEFAULT_LEVELS_COUNT
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ZoomRangeBarView,
                defStyleAttr,
                defStyleRes).apply {

            try {
                zoomMaxImage = getDrawable(R.styleable.ZoomRangeBarView_zoomMaxImage) ?: zoomMaxImage
                zoomMinImage = getDrawable(R.styleable.ZoomRangeBarView_zoomMinImage) ?: zoomMinImage
                levelsCount = getInteger(R.styleable.ZoomRangeBarView_levelsCount, DEFAULT_LEVELS_COUNT)
            }
            finally {
                recycle()
            }
        }
        initRangeBarWithImages()
    }

    private fun initRangeBarWithImages() {
        val zoomRangeBarView = View.inflate(context, R.layout.view_zoom_rangebar, null)
        zoomRangeBarView.layoutParams = FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT)

        addView(zoomRangeBarView)

        seekbar_range.max = levelsCount
        seekbar_range.setOnSeekBarChangeListener(onSeekbarChangeListener)

        image_zoom_min.setImageDrawable(zoomMinImage)
        image_zoom_min.setOnClickListener {
            seekbar_range.incrementProgressBy(-1)
        }
        image_zoom_min.setOnLongClickListener {
            seekbar_range.progress = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) seekbar_range.min
            else 0
            return@setOnLongClickListener true
        }

        image_zoom_max.setImageDrawable(zoomMaxImage)
        image_zoom_max.setOnClickListener {
            seekbar_range.incrementProgressBy(1)
        }
        image_zoom_max.setOnLongClickListener {
            seekbar_range.progress = seekbar_range.max
            return@setOnLongClickListener true
        }
    }

    interface OnZoomLevelChangeListener {
        fun onZoomLevelChanged(zoomLevel: Int, zoomLevelsCount: Int)
    }

    open class DefaultZoomRangeBarViewBuilder(context: Context): BaseBuilder<ZoomRangeBarView>(context) {

        override var _id = R.id.zoom_rangebar_default

        protected var _zoomHighImage: Drawable = ContextCompat.getDrawable(context, DEFAULT_ZOOM_HIGH_IMAGE_ID)!!
        protected var _zoomLowImage: Drawable = ContextCompat.getDrawable(context, DEFAULT_ZOOM_LOW_IMAGE_ID)!!
        protected var _levelsCount: Int = DEFAULT_LEVELS_COUNT

        @PublishedApi
        override fun build(): ZoomRangeBarView = ZoomRangeBarView(context,
                _id,
                _zoomHighImage,
                _zoomLowImage,
                _levelsCount)
    }

    /**
     * This is preferred way for creation [ZoomRangeBarView] from Java code.
     */
    class Builder(context: Context): DefaultZoomRangeBarViewBuilder(context) {

        /**
         * Sets view id. If not, [R.id.zoom_rangebar_default] is default value.
         */
        fun setId(@IdRes id: Int) = apply { this._id = id }

        /**
         * Sets zoom highest image. Default image is [DEFAULT_ZOOM_HIGH_IMAGE_ID]
         */
        fun setZoomHighImage(zoomHighImage: Drawable) = apply { this._zoomHighImage = zoomHighImage }

        /**
         * Sets zoom lowest image. Default image is [DEFAULT_ZOOM_LOW_IMAGE_ID]
         */
        fun setZoomLowImage(zoomLowImage: Drawable) = apply { this._zoomLowImage = zoomLowImage }

        /**
         * Set zooming levels count. Default value is [DEFAULT_LEVELS_COUNT]
         */
        fun setLevelsCount(levelsCount: Int) = apply { this._levelsCount = levelsCount }

        override public fun build(): ZoomRangeBarView = super.build()
    }

    /**
     * This is preferred way for creation [ZoomRangeBarView] from Kotlin code.
     */
    @MapWithToolsDsl
    class DslBuilder(context: Context): DefaultZoomRangeBarViewBuilder(context) {

        /**
         * View id. If not set, [R.id.zoom_rangebar_default] is default value.
         */
        var id: Int
            @IdRes get() = _id
            set(@IdRes value) { _id = value }

        /**
         * Zoom highest image. Default image is [DEFAULT_ZOOM_HIGH_IMAGE_ID]
         */
        var zoomHighImage: Drawable
            get() = _zoomHighImage
            set(value) { _zoomHighImage = value }

        /**
         * Zoom lowest image. Default image is [DEFAULT_ZOOM_LOW_IMAGE_ID]
         */
        var zoomLowImage: Drawable
            get() = _zoomLowImage
            set(value) { _zoomLowImage = value}

        /**
         * Levels count of zoom view. Default value is [DEFAULT_LEVELS_COUNT]
         */
        var levelsCount: Int
            get() = _levelsCount
            set(value) { _levelsCount = value }
    }

    companion object {
        private val DEFAULT_ZOOM_HIGH_IMAGE_ID = R.drawable.ic_default_plus
        private val DEFAULT_ZOOM_LOW_IMAGE_ID = R.drawable.ic_default_minus
        private const val DEFAULT_LEVELS_COUNT = 5
    }
}