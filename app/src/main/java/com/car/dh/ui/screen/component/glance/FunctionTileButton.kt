package com.car.dh.ui.screen.component.glance

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.background
import androidx.glance.layout.padding
import androidx.glance.layout.size
import com.car.dh.R


/**
 * 功能开关块按钮
 * @param size 尺寸
 * @param title 标题
 * @param titleColor 标题颜色
 * @param active 是否被激活
 * @param icon 图标
 * @param onClick 点击事件
 */
@Composable
@SuppressLint("RestrictedApi")
fun FunctionTileButton(
    size: Dp,
    title: String = "",
    titleColor: Color,
    active: Boolean,
    icon: Int,
    onClick: () -> Unit,
) {
    val bg = remember(active) {
        ImageProvider(
            if (active)
                R.drawable.bg_btn_active
            else R.drawable.bg_btn_inactive
        )
    }
    ImageButton(
        modifier = GlanceModifier
            .background(bg)
            .size(size)
            .padding(size * 0.17f),
        title = title,
        titleColor = titleColor,
        active = true,
        activeIcon = icon,
        onClick = onClick,
    )
}