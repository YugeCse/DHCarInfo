package com.car.dh.ui.screen.air

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.car.dh.data.AirControlStateInfo
import com.car.dh.ui.screen.base.CarDataBusDataObserver
import com.car.dh.ui.screen.widget.AirControlAppWidget
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds


/**
 * 空调控制 UI 状态类
 * @param airStateInfo 空调状态信息
 * @param isAirSettingsDialogVisible 是否显示设置弹窗
 * @param isInEditInspectorMode 是否是编辑模式，默认：false
 */
@Stable
data class AirControlUiState(
    val airStateInfo: AirControlStateInfo,
    val isAirSettingsDialogVisible: Boolean,
    val isInEditInspectorMode: Boolean = false,
) {

    companion object {

        /** 默认设置 **/
        @JvmStatic
        fun default(): AirControlUiState {
            return AirControlUiState(
                isInEditInspectorMode = false,
                isAirSettingsDialogVisible = false,
                airStateInfo = AirControlStateInfo.default(),
            )
        }

    }

}

/** 空调界面的ViewModel对象 **/
class AirControlViewModel : ViewModel() {

    private val controller by lazy { AirController.singleton() }

    private val _uiState = MutableStateFlow(AirControlUiState.default())

    val uiState: StateFlow<AirControlUiState> = _uiState.asStateFlow()

    /** 获取或设置空调设置弹窗是否可见 **/
    var isAirSettingDialogVisible: Boolean
        get() = _uiState.value.isAirSettingsDialogVisible
        set(value) {
            _uiState.value = _uiState.value.copy(isAirSettingsDialogVisible = value)
        }

    init {
        viewModelScope.launch {
            while (isActive) {
                syncAirStateData()
                delay(300.milliseconds)
            }
        }
    }

    /** 同步空调状态数据 **/
    private fun syncAirStateData() {
        if (isInEditInspectorMode) return
        updateUiState {
            return@updateUiState copy(
                airStateInfo = airStateInfo.copy(
                    isPower = controller.getPower() == 1,
                    isAcOn = controller.getAc() == 1,
                    cycle = controller.getCycle(),
                    frontDefrost = controller.getFrontDefrost(),
                    modeUp = controller.getBlowUp(),
                    modeBody = controller.getBlowBody(),
                    modeFoot = controller.getBlowFoot(),
                    windLevel = controller.getWindLevel(),
                    temp = controller.getTempLeft(),
                )
            )
        }
    }

    /**
     * 更新 UI状态数据
     * @param newUiState 状态应用
     */
    fun updateUiState(
        newUiState: AirControlUiState.() -> AirControlUiState
    ): AirControlUiState {
        return newUiState(_uiState.value)
            .also { result -> _uiState.value = result }
    }

    /**
     * 设置当前设置是否为编辑模式
     * @param value 是否是编辑模式
     */
    fun setInEditInspectorMode(value: Boolean) {
        _uiState.value = _uiState.value
            .copy(isInEditInspectorMode = value)
    }

    /** 判断当前是否是编辑模式 **/
    val isInEditInspectorMode: Boolean
        get() = _uiState.value.isInEditInspectorMode

    /** 切换电源 **/
    fun togglePower() {
        if (isInEditInspectorMode) {
            updateUiState {
                copy(
                    airStateInfo = airStateInfo
                        .copy(isPower = !airStateInfo.isPower)
                )
            }
        } else controller.togglePower()
    }

    /** 升高温度 **/
    fun increaseTemp() {
        if (isInEditInspectorMode) {
            updateUiState {
                copy(
                    airStateInfo = airStateInfo.copy(
                        temp = (airStateInfo.temp + 1)
                            .coerceAtMost(AirController.TEMP_MAX)
                    )
                )
            }
        } else controller.increaseTemp()
    }

    /** 降低温度 **/
    fun decreaseTemp() {
        if (isInEditInspectorMode) {
            updateUiState {
                copy(
                    airStateInfo = airStateInfo.copy(
                        temp = (airStateInfo.temp - 1)
                            .coerceAtLeast(AirController.TEMP_MIN)
                    )
                )
            }
        } else controller.decreaseTemp()
    }

    /** 温度打到最低 (LO) **/
    fun setTempMin() {
        if (isInEditInspectorMode) {
            updateUiState {
                copy(
                    airStateInfo = airStateInfo.copy(temp = AirController.TEMP_MIN)
                )
            }
        } else controller.setTempMin()
    }

    /** 温度打到最高 (HI) **/
    fun setTempMax() {
        if (isInEditInspectorMode) {
            updateUiState {
                copy(
                    airStateInfo = airStateInfo.copy(temp = AirController.TEMP_MAX)
                )
            }
        } else controller.setTempMax()
    }

    /** 设置风力 **/
    fun setWindLevel(level: Int) {
        if (isInEditInspectorMode) {
            updateUiState {
                copy(
                    airStateInfo = airStateInfo.copy(
                        windLevel = level.coerceIn(
                            minimumValue = AirController.WIND_MIN,
                            maximumValue = AirController.WIND_MAX
                        )
                    )
                )
            }
        } else controller.setWindLevel(level)
    }

    /** 增加风力 **/
    fun incrementWindLevel() {
        setWindLevel(
            (_uiState.value.airStateInfo.windLevel
                .coerceIn(
                    minimumValue = AirController.WIND_MIN,
                    maximumValue = AirController.WIND_MAX
                ) + 1).coerceIn(
                minimumValue = AirController.WIND_MIN,
                maximumValue = AirController.WIND_MAX
            )
        )
    }

    /** 减少风力 **/
    fun decrementWindLevel() {
        setWindLevel(
            (_uiState.value.airStateInfo.windLevel
                .coerceIn(
                    minimumValue = AirController.WIND_MIN,
                    maximumValue = AirController.WIND_MAX
                ) - 1).coerceIn(
                minimumValue = AirController.WIND_MIN,
                maximumValue = AirController.WIND_MAX
            )
        )
    }

    /** 切换前挡除雾 **/
    fun toggleFrontDefrost() {
        if (isInEditInspectorMode) {
            updateUiState {
                copy(
                    airStateInfo = airStateInfo.copy(
                        frontDefrost =
                            if (airStateInfo.frontDefrost == 1) 0 else 1
                    )
                )
            }
        } else controller.toggleFrontDefrost()
    }

    /** 切换循环模式 **/
    fun toggleCycle() {
        if (isInEditInspectorMode) {
            updateUiState {
                copy(
                    airStateInfo = airStateInfo.copy(
                        cycle = if (airStateInfo.cycle == 1) 0 else 1
                    )
                )
            }
        } else controller.toggleCycle()
    }

    /** 切换模式 **/
    fun toggleMode() = controller.toggleMode()

    /** 切换 AC 模式 **/
    fun toggleAc() {
        if (isInEditInspectorMode) {
            updateUiState {
                copy(
                    airStateInfo = airStateInfo.copy(
                        isAcOn = !airStateInfo.isAcOn
                    )
                )
            }
        } else controller.toggleAc()
    }

    /** 释放按键 **/
    fun releaseKey() {
        controller.releaseKey()
        viewModelScope.launch {
            AirControlAppWidget.updateAll()
            CarDataBusDataObserver.notifyDataChanged()
        }
    }

}