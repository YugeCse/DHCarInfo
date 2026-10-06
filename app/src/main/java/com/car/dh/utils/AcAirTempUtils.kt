package com.car.dh.utils

import android.annotation.SuppressLint
import com.car.dh.ui.screen.air.AirController

object AcAirTempUtils {

    @JvmStatic
    @SuppressLint("DefaultLocale")
    fun formatTemp(temp: Float): String = when {
        temp == AirController.TEMP_MIN -> "LO"
        temp == AirController.TEMP_MAX -> "HI"
        temp <= -2.5f -> "HI"
        temp <= -1.5f -> "LO"
        temp <= -0.5f -> "--"
        else -> String.format("%.1f", temp)
            .replace("\\.0$".toRegex(), "")
    }

}