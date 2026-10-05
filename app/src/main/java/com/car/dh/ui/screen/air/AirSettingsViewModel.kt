package com.car.dh.ui.screen.air

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.car.dh.app.GlobalConfig
import com.car.dh.ui.screen.base.CarDataBusDataObserver
import com.car.dh.ui.theme.DHCarInfoTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


@Stable
data class AirSettingsInfo(
    val useAppWidgetBackground: Boolean,
    val airAppWidgetMarkNumberColor: Color,
    val airAppWidgetMarkNumberSelectColor: Color,
    val airAppWidgetMarkOvalColor: Color,
    val airAppWidgetMarkOvalSelectColor: Color,
    val airAppWidgetTempTextColor: Color,
    val airAppWidgetSubTextColor: Color,

) {

    companion object {

        @JvmStatic
        fun default() = AirSettingsInfo(
            useAppWidgetBackground = false,
            airAppWidgetMarkNumberColor = DHCarInfoTheme.subText,
            airAppWidgetMarkNumberSelectColor = DHCarInfoTheme.accent,
            airAppWidgetMarkOvalColor = DHCarInfoTheme.inactive,
            airAppWidgetMarkOvalSelectColor = DHCarInfoTheme.accent,
            airAppWidgetTempTextColor = DHCarInfoTheme.accent,
            airAppWidgetSubTextColor = DHCarInfoTheme.subText,
        )

    }

}

class AirSettingsViewModel : ViewModel() {

    private val globalConfig by lazy { GlobalConfig.singleton() }

    private val _uiState by lazy {
        MutableStateFlow(
            AirSettingsInfo(
                useAppWidgetBackground = globalConfig.isAirAppWidgetRenderBackground,
                airAppWidgetTempTextColor = globalConfig.airAppWidgetTempTextColor,
                airAppWidgetMarkOvalColor = globalConfig.airAppWidgetMarkOvalColor,
                airAppWidgetMarkOvalSelectColor = globalConfig.airAppWidgetMarkOvalSelectColor,
                airAppWidgetMarkNumberColor = globalConfig.airAppWidgetMarkNumberColor,
                airAppWidgetMarkNumberSelectColor = globalConfig.airAppWidgetMarkNumberSelectColor,
                airAppWidgetSubTextColor = globalConfig.airAppWidgetSubTextColor,
            )
        )
    }

    val uiState: StateFlow<AirSettingsInfo> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(
            useAppWidgetBackground =
                globalConfig.isAirAppWidgetRenderBackground
        )
    }

    /**
     * 设置小组件使用默认的背景
     * @param value 是否可用
     */
    fun setUseAppWidgetDefaultBackground(value: Boolean) {
        _uiState.value = _uiState.value.copy(
            useAppWidgetBackground = value
        )
        globalConfig.isAirAppWidgetRenderBackground = value
        CarDataBusDataObserver.notifyDataChanged() //通知数据变更处理
    }

    /**
     * 设置空调温度文本的颜色
     */
    fun setAirAppWidgetTempTextColor(color: Color) {
        _uiState.value = _uiState.value.copy(
            airAppWidgetTempTextColor = color
        )
        globalConfig.airAppWidgetTempTextColor = color
    }

    fun setAirAppWidgetMarkOvalColor(color: Color) {
        _uiState.value = _uiState.value.copy(
            airAppWidgetMarkOvalColor = color
        )
        globalConfig.airAppWidgetMarkOvalColor = color
    }

    fun setAirAppWidgetMarkOvalSelectColor(color: Color) {
        _uiState.value = _uiState.value.copy(
            airAppWidgetMarkOvalSelectColor = color
        )
        globalConfig.airAppWidgetMarkOvalSelectColor = color
    }

    fun setAirAppWidgetMarkNumberColor(color: Color) {
        _uiState.value = _uiState.value.copy(
            airAppWidgetMarkNumberColor = color
        )
        globalConfig.airAppWidgetMarkNumberColor = color
    }

    fun setAirAppWidgetMarkNumberSelectColor(color: Color) {
        _uiState.value = _uiState.value.copy(
            airAppWidgetMarkNumberSelectColor = color
        )
        globalConfig.airAppWidgetMarkNumberSelectColor = color
    }

    fun setAirAppWidgetSubTextColor(color: Color){
        _uiState.value = _uiState.value
            .copy(airAppWidgetSubTextColor = color)
        globalConfig.airAppWidgetSubTextColor = color
    }

}