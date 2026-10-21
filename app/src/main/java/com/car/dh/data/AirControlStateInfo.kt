package com.car.dh.data

import androidx.compose.runtime.Stable
import com.car.dh.ui.screen.air.AirController

/**
 * 空调控制状态数据实体类
 * @param isPower 是否打开电源
 * @param isAcOn AC 是否打开
 * @param cycle 循环模式，1-内循环，其他-外循环
 * @param frontDefrost 前窗除雾开关
 * @param modeUp 吹头
 * @param modeBody 吹身体
 * @param modeFoot 吹脚
 * @param windLevel 风力
 * @param temp 温度
 */
@Stable
data class AirControlStateInfo(
    val isPower: Boolean = false,
    val isAcOn: Boolean = false,
    val cycle: Int,
    val frontDefrost: Int,
    val modeUp: Int,
    val modeBody: Int,
    val modeFoot: Int,
    val windLevel: Int = 0,
    val temp: Float = AirController.TEMP_MIN
) {

    companion object {

        @JvmStatic
        fun default(): AirControlStateInfo {
            return AirControlStateInfo(
                isPower = false,
                isAcOn = false,
                cycle = 0,
                frontDefrost = 0,
                modeUp = 0,
                modeBody = 0,
                modeFoot = 0,
                windLevel = 0
            )
        }

    }

}