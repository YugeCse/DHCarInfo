package com.car.dh.app

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.car.dh.ui.screen.base.CarBaseController
import com.car.dh.ui.screen.base.CarDataBusDataObserver
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

        /** 初始化配置数据 **/
        @JvmStatic
        fun initConfigs() {
            try {
                val carBaseController = CarBaseController.singleton()
                carBaseController.setLanguage(_instance.carSystemLanguage)
                carBaseController.setDriveMode2(_instance.carDriveMode)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

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
            CarDataBusDataObserver.notifyDataChanged()
        }
        get() = prefs.getBoolean("isAirAppWidgetRenderBackground", false)

    /** 空调小组件的刻度数字的颜色 **/
    var airAppWidgetMarkNumberColor: Color
        set(value) {
            prefs.put("airAppWidgetMarkNumberColor", value.toHexCode())
            CarDataBusDataObserver.notifyDataChanged()
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
            CarDataBusDataObserver.notifyDataChanged()
        }
        get() {
            val hexCode = prefs.getString(
                "airAppWidgetMarkNumberSelectColor",
                DHCarInfoTheme.accent.toHexCode()
            )
            return Color(android.graphics.Color.parseColor(hexCode))
        }

    /** 空调小组件的刻度线的颜色 **/
    var airAppWidgetMarkOvalColor: Color
        set(value) {
            prefs.put("airAppWidgetMarkOvalColor", value.toHexCode())
            CarDataBusDataObserver.notifyDataChanged()
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
            CarDataBusDataObserver.notifyDataChanged()
        }
        get() {
            val hexCode = prefs.getString(
                "airAppWidgetMarkOvalSelectColor",
                DHCarInfoTheme.accent.toHexCode()
            )
            return Color(android.graphics.Color.parseColor(hexCode))
        }

    /** 空调小组件的温度数字颜色 **/
    var airAppWidgetTempTextColor: Color
        set(value) {
            prefs.put("airAppWidgetTempTextColor", value.toHexCode())
            CarDataBusDataObserver.notifyDataChanged()
        }
        get() = prefs.getString(
            "airAppWidgetTempTextColor",
            DHCarInfoTheme.accent.toHexCode()
        )?.toColor() ?: DHCarInfoTheme.accent

    /** 空调小组件子文本颜色 **/
    var airAppWidgetSubTextColor: Color
        set(value) {
            prefs.put("airAppWidgetSubTextColor", value.toHexCode())
            CarDataBusDataObserver.notifyDataChanged()
        }
        get() = prefs.getString(
            "airAppWidgetSubTextColor",
            DHCarInfoTheme.subText.toHexCode()
        )?.toColor() ?: DHCarInfoTheme.subText

}

/** LocalGlobalConfig对象 **/
val LocalGlobalConfig = staticCompositionLocalOf { GlobalConfig.singleton() }