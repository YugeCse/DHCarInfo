package com.car.dh.utils

import android.annotation.SuppressLint

object AcAirTempUtils {

    @JvmStatic
    @SuppressLint("DefaultLocale")
    fun formatTemp(temp: Float): String = when {
        temp <= -2.5f -> "HI"
        temp <= -1.5f -> "LO"
        temp <= -0.5f -> "--"
        else -> String.format("%.1f", temp)
            .replace("\\.0$".toRegex(), "")
    }

}