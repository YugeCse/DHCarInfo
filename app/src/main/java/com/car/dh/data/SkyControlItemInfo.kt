package com.car.dh.data

import androidx.annotation.DrawableRes

data class SkyControlItemInfo(
    @DrawableRes val iconRes: Int,
    val title: String,
    val control: () -> Unit
)
