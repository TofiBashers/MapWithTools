package com.gmail.tofibashers.mapwithtools.tool_view

import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.support.annotation.DrawableRes
import android.support.v4.content.res.ResourcesCompat
import android.view.MotionEvent
import com.gmail.tofibashers.mapwithtools.R


/**
 * Created by TofiBashers on 30.04.2019.
 */

internal fun createDefaultToolImageWithStates(resources: Resources,
                                              @DrawableRes selectedImageResId: Int,
                                              @DrawableRes nonSelectedImageResId: Int): StateListDrawable =
        StateListDrawable().apply {
            val checkedDrawble = LayerDrawable(arrayOf(
                    ColorDrawable(ResourcesCompat.getColor(resources, R.color.tool_pressed, null)),
                    ResourcesCompat.getDrawable(resources,
                            selectedImageResId, null)))
            val unCheckedDrawble = LayerDrawable(arrayOf(
                    ResourcesCompat.getDrawable(resources,
                            nonSelectedImageResId, null)))
            addState(IntArray(1) {android.R.attr.state_checked}, checkedDrawble)
            addState(IntArray(1) {-android.R.attr.state_checked}, unCheckedDrawble)
        }

internal fun isSamePointWithLastInHistory(motionEvent: MotionEvent): Boolean =
        motionEvent.historySize != 0 &&
                motionEvent.x == motionEvent.getHistoricalX(motionEvent.historySize - 1) &&
                motionEvent.y == motionEvent.getHistoricalY(motionEvent.historySize - 1)