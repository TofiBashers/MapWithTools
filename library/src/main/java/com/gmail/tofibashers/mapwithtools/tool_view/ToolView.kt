package com.gmail.tofibashers.mapwithtools.tool_view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.support.annotation.IdRes
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.View
import android.widget.RadioButton
import com.gmail.tofibashers.mapwithtools.BaseBuilder
import com.gmail.tofibashers.mapwithtools.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView


/**
 * Created by TofiBashers on 20.01.2019.
 */
abstract class ToolView : RadioButton {

    protected var map: GoogleMap? = null

    constructor(context: Context) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes)

    protected constructor(context: Context,
                          @IdRes id: Int,
                          image: Drawable) : super(context) {
        this.id = id
        buttonDrawable = null
        val checkedDrawble = LayerDrawable(arrayOf(
                ColorDrawable(ResourcesCompat.getColor(context.resources, R.color.tool_pressed, null)),
                image))
        val unCheckedDrawble = LayerDrawable(arrayOf(
                ColorDrawable(ResourcesCompat.getColor(context.resources, R.color.tool_default, null)),
                image))
        background = StateListDrawable().apply {
            addState(IntArray(1) {android.R.attr.state_checked}, checkedDrawble)
            addState(IntArray(1) {-android.R.attr.state_checked}, unCheckedDrawble)
        }
        setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING)
    }

    fun onMapReady(map: GoogleMap,
                   drawingGesturesOverlayView: View,
                   mapView: MapView) {
        this.map = map
        if (isChecked) {
            onToolEnabled(drawingGesturesOverlayView, mapView)
        }
        this.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                onToolEnabled(drawingGesturesOverlayView, mapView)
            }
        }
    }

    abstract fun onToolEnabled(drawingGesturesOverlayView: View, mapView: MapView)

    abstract class BaseToolViewBuilder<out T: ToolView>(context: Context): BaseBuilder<T>(context) {

        abstract protected var _image: Drawable
    }

    companion object {

        const val DEFAULT_PADDING = 10
    }
}