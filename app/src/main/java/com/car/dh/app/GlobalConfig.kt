package com.car.dh.app

import com.car.dh.ui.screen.air.AirStateDataChange
import com.car.dh.utils.PrefsUtils

/** 全局配置类 **/
class GlobalConfig private constructor() {

    companion object {

        private val _instance by lazy { GlobalConfig() }

        @JvmStatic
        fun singleton(): GlobalConfig = _instance

    }

    private val prefs by lazy { PrefsUtils.singleton() }

    /** 空调小组件是否有背景色支持 **/
    var isAirAppWidgetRenderBackground: Boolean
        set(value) {
            prefs.put("isAirAppWidgetRenderBackground", value)
            AirStateDataChange.notifyDataChanged()
        }
        get() = prefs.getBoolean("isAirAppWidgetRenderBackground", false)


}