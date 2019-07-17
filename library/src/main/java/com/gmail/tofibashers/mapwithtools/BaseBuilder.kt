package com.gmail.tofibashers.mapwithtools

import android.content.Context


/**
 * Created by TofiBashers on 26.04.2019.
 */
abstract class BaseBuilder<out T>(protected val context: Context) {

    abstract var _id: Int

    abstract internal fun build(): T
}