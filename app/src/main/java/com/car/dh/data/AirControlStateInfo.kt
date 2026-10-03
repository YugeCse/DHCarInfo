package com.car.dh.data

import androidx.compose.runtime.Stable

/**
 * 空调控制状态数据实体类
 */
@Stable
data class AirControlStateInfo(
    val power: Int,
    val ac: Int,
    val cycle: Int,
    val frontDefrost: Int,
    val modeUp: Int,
    val modeBody: Int,
    val modeFoot: Int
) {

    companion object {

        @JvmStatic
        fun default(): AirControlStateInfo {
            return AirControlStateInfo(
                power = 0,
                ac = 0,
                cycle = 0,
                frontDefrost = 0,
                modeUp = 0,
                modeBody = 0,
                modeFoot = 0
            )
        }

    }

}