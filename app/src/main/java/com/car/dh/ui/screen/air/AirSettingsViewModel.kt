package com.car.dh.ui.screen.air

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import com.car.dh.app.GlobalConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


@Stable
data class AirSettingsInfo(
    val useAppWidgetBackground: Boolean
) {

    companion object {

        @JvmStatic
        fun default() = AirSettingsInfo(
            useAppWidgetBackground = false
        )

    }

}

class AirSettingsViewModel : ViewModel() {

    private val globalConfig by lazy { GlobalConfig.singleton() }

    private val _uiState = MutableStateFlow(
        AirSettingsInfo(
            useAppWidgetBackground = false
        )
    )

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
        AirStateDataChange.notifyDataChanged() //通知数据变更处理
    }

}