package com.car.dh.app

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColor
import androidx.core.graphics.toColorLong
import com.car.dh.ui.screen.air.AirStateDataChange
import com.car.dh.ui.theme.DHCarInfoTheme
import com.car.dh.utils.PrefsUtils
import com.car.dh.utils.toColor
import com.car.dh.utils.toHexCode

/** 全局配置类 **/
class GlobalConfig private constructor() {

    companion object {

        private val _instance by lazy { GlobalConfig() }

        @JvmStatic
        fun singleton(): GlobalConfig = _instance

    }

    private val prefs by lazy { PrefsUtils.singleton() }

    /** 获取或设置车机系统语言 **/
    var carSystemLanguage: Int
        set(value) {
            prefs.put("carSystemLanguage", value)
        }
        get() = prefs.getInt("carSystemLanguage", 0)

    /** 获取或设置驾驶模式 **/
    var carDriveMode: Int
        set(value) = prefs.put("carDriveMode", value)
        get() = prefs.getInt("carDriveMode", 0)

    /** 空调小组件是否有背景色支持 **/
    var isAirAppWidgetRenderBackground: Boolean
        set(value) {
            prefs.put("isAirAppWidgetRenderBackground", value)
            AirStateDataChange.notifyDataChanged()
        }
        get() = prefs.getBoolean("isAirAppWidgetRenderBackground", false)

    /** 空调小组件的刻度数字的颜色 **/
    var airAppWidgetMarkNumberColor: Color
        set(value) {
            prefs.put("airAppWidgetMarkNumberColor", value.toHexCode())
            AirStateDataChange.notifyDataChanged()
        }
        get() {
            val hexCode = prefs.getString(
                "airAppWidgetMarkNumberColor",
                DHCarInfoTheme.subText.toHexCode()
            )
            return Color(android.graphics.Color.parseColor(hexCode))
        }

    /** 空调小组件的刻度数字的选中颜色 **/
    var airAppWidgetMarkNumberSelectColor: Color
        set(value) {
            prefs.put("airAppWidgetMarkNumberSelectColor", value.toHexCode())
            AirStateDataChange.notifyDataChanged()
        }
        get() {
            val hexCode = prefs.getString(
                "airAppWidgetMarkNumberSelectColor",
                DHCarInfoTheme.subText.toHexCode()
            )
            return Color(android.graphics.Color.parseColor(hexCode))
        }

    /** 空调小组件的刻度线的颜色 **/
    var airAppWidgetMarkOvalColor: Color
        set(value) {
            prefs.put("airAppWidgetMarkOvalColor", value.toHexCode())
            AirStateDataChange.notifyDataChanged()
        }
        get() {
            val hexCode = prefs.getString(
                "airAppWidgetMarkOvalColor",
                DHCarInfoTheme.inactive.toHexCode()
            )
            return Color(android.graphics.Color.parseColor(hexCode))
        }

    /** 空调小组件的刻度线的选中颜色 **/
    var airAppWidgetMarkOvalSelectColor: Color
        set(value) {
            prefs.put("airAppWidgetMarkOvalSelectColor", value.toHexCode())
            AirStateDataChange.notifyDataChanged()
        }
        get() {
            val hexCode = prefs.getString(
                "airAppWidgetMarkOvalSelectColor",
                DHCarInfoTheme.inactive.toHexCode()
            )
            return Color(android.graphics.Color.parseColor(hexCode))
        }

    /** 空调小组件的温度数字颜色 **/
    var airAppWidgetTempTextColor: Color
        set(value) {
            prefs.put("airAppWidgetTempTextColor", value.toHexCode())
            AirStateDataChange.notifyDataChanged()
        }
        get() = prefs.getString(
            "airAppWidgetTempTextColor",
            DHCarInfoTheme.accent.toHexCode()
        )?.toColor() ?: DHCarInfoTheme.accent

}

/** LocalGlobalConfig对象 **/
val LocalGlobalConfig = staticCompositionLocalOf { GlobalConfig.singleton() }