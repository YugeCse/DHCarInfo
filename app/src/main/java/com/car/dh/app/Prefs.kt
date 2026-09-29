package com.car.dh.app

import android.content.Context
import androidx.core.content.edit

class Prefs private constructor() {

    companion object {

        private val _instance by lazy { Prefs() }

        @JvmStatic
        fun singleton() = _instance

    }

    private val prefs by lazy {
        DHApplication.singleton()
            .getSharedPreferences("dh_config", Context.MODE_PRIVATE)
    }

    /** 获取或存储温度数据 **/
    var airTempValue: Float
        set(value) {
            prefs.edit(true) {
                putFloat("air_temp_value", value)
            }
        }
        get() = prefs.getFloat("air_temp_value", 17.0f)

}