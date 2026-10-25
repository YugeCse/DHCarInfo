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
            runCatching {
                _instance.carDriveMode = 0 //默认设置回舒适模式
                CarBaseController.singleton().apply {
                    setLanguage(_instance.carSystemLanguage)
                    setDriveMode2(_instance.carDriveMode)
                }
            }.onFailure { ex -> ex.printStackTrace() }
        }

    }

    /** 是否是编辑模式 **/
    var isEditMode = false

    private val prefs by lazy { PrefsUtils.singleton() }

    /** 获取或设置车机系统语言 **/
    var carSystemLanguage: Int = 0
        set(value) {
            if (isEditMode) {
                field = value
                return
            }
            prefs.put("carSystemLanguage", value)
        }
        get() {
            if (isEditMode) return field
            return prefs.getInt("carSystemLanguage", 0)
        }

    /** 获取或设置驾驶模式 **/
    var carDriveMode: Int = 0
        set(value) {
            if (isEditMode) {
                field = value
                return
            }
            prefs.put("carDriveMode", value)
        }
        get() {
            if (isEditMode) return field
            return prefs.getInt("carDriveMode", 0)
        }

    /** 空调小组件是否有背景色支持 **/
    var isAirAppWidgetRenderBackground: Boolean = false
        set(value) {
            if (isEditMode) field = value
            else prefs.put("isAirAppWidgetRenderBackground", value)
            CarDataBusDataObserver.notifyDataChanged()
        }
        get() {
            if (isEditMode) return field
            return prefs.getBoolean("isAirAppWidgetRenderBackground", false)
        }

    /** 空调小组件的刻度数字的颜色 **/
    var airAppWidgetMarkNumberColor: Color = DHCarInfoTheme.subText
        set(value) {
            if (isEditMode) field = value
            else prefs.put("airAppWidgetMarkNumberColor", value.toHexCode())
            CarDataBusDataObserver.notifyDataChanged()
        }
        get() {
            if (isEditMode) return field
            val hexCode = prefs.getString(
                "airAppWidgetMarkNumberColor",
                DHCarInfoTheme.subText.toHexCode()
            )
            return Color(android.graphics.Color.parseColor(hexCode))
        }

    /** 空调小组件的刻度数字的选中颜色 **/
    var airAppWidgetMarkNumberSelectColor: Color = DHCarInfoTheme.accent
        set(value) {
            if (isEditMode) field = value
            else prefs.put("airAppWidgetMarkNumberSelectColor", value.toHexCode())
            CarDataBusDataObserver.notifyDataChanged()
        }
        get() {
            if (isEditMode) return field
            val hexCode = prefs.getString(
                "airAppWidgetMarkNumberSelectColor",
                DHCarInfoTheme.accent.toHexCode()
            )
            return Color(android.graphics.Color.parseColor(hexCode))
        }

    /** 空调小组件的刻度线的颜色 **/
    var airAppWidgetMarkOvalColor: Color = DHCarInfoTheme.inactive
        set(value) {
            if (isEditMode) field = value
            else prefs.put("airAppWidgetMarkOvalColor", value.toHexCode())
            CarDataBusDataObserver.notifyDataChanged()
        }
        get() {
            if (isEditMode) return field
            val hexCode = prefs.getString(
                "airAppWidgetMarkOvalColor",
                DHCarInfoTheme.inactive.toHexCode()
            )
            return Color(android.graphics.Color.parseColor(hexCode))
        }

    /** 空调小组件的刻度线的选中颜色 **/
    var airAppWidgetMarkOvalSelectColor: Color = DHCarInfoTheme.accent
        set(value) {
            if (isEditMode) field = value
            else prefs.put("airAppWidgetMarkOvalSelectColor", value.toHexCode())
            CarDataBusDataObserver.notifyDataChanged()
        }
        get() {
            if (isEditMode) return field
            val hexCode = prefs.getString(
                "airAppWidgetMarkOvalSelectColor",
                DHCarInfoTheme.accent.toHexCode()
            )
            return Color(android.graphics.Color.parseColor(hexCode))
        }

    /** 空调小组件的温度数字颜色 **/
    var airAppWidgetTempTextColor: Color = DHCarInfoTheme.accent
        set(value) {
            if (isEditMode) field = value
            else prefs.put("airAppWidgetTempTextColor", value.toHexCode())
            CarDataBusDataObserver.notifyDataChanged()
        }
        get() {
            if (isEditMode) return field
            return prefs.getString(
                "airAppWidgetTempTextColor",
                DHCarInfoTheme.accent.toHexCode()
            )?.toColor() ?: DHCarInfoTheme.accent
        }

    /** 空调小组件子文本颜色 **/
    var airAppWidgetSubTextColor: Color = DHCarInfoTheme.subText
        set(value) {
            if (isEditMode) field = value
            else prefs.put("airAppWidgetSubTextColor", value.toHexCode())
            CarDataBusDataObserver.notifyDataChanged()
        }
        get() {
            if (isEditMode) return field
            return prefs.getString(
                "airAppWidgetSubTextColor",
                DHCarInfoTheme.subText.toHexCode()
            )?.toColor() ?: DHCarInfoTheme.subText
        }

}

/** LocalGlobalConfig对象 **/
val LocalGlobalConfig = staticCompositionLocalOf { GlobalConfig.singleton() }