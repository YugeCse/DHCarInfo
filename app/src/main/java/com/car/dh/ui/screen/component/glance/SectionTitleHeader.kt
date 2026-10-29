package com.car.dh.ui.screen.component.glance

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.car.dh.ui.theme.DHCarInfoTheme


/**
 * 小组件分区标题：左侧强调色条 + 标题文字
 * @param title 标题
 * @param modifier 修饰器
 * @param fontSize 文字大小
 * @param titleColor 标题颜色
 * @param accentColor 引导线颜色
 */
@SuppressLint("RestrictedApi")
@Composable
fun SectionTitleHeader(
    title: String,
    modifier: GlanceModifier = GlanceModifier,
    fontSize: TextUnit = 13.sp,
    titleColor: Color = DHCarInfoTheme.subText,
    accentColor: Color = DHCarInfoTheme.accent,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = GlanceModifier
                .width(4.dp)
                .height(14.dp)
                .cornerRadius(2.dp)
                .background(accentColor),
            content = {}
        )
        Spacer(GlanceModifier.width(8.dp))
        Text(
            text = title,
            style = TextStyle(
                fontSize = fontSize,
                fontWeight = FontWeight.Medium,
                color = ColorProvider(titleColor)
            )
        )
    }
}
