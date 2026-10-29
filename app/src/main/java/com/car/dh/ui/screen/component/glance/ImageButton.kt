package com.car.dh.ui.screen.component.glance

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.car.dh.ui.theme.DHCarInfoTheme


/**
 * 图片按钮控件
 * @param modifier 修饰器
 * @param margin 外间距
 * @param title 标题
 * @param titleColor 标题颜色
 * @param active 是否是激活状态
 * @param useColorTint 是否使用颜色Tint，默认：true
 * @param activeIcon 激活时的图标显示
 * @param inactiveIcon 未激活时的图标显示
 */
@Composable
@SuppressLint("RestrictedApi")
fun ImageButton(
    modifier: GlanceModifier = GlanceModifier,
    margin: PaddingValues? = null,
    title: String? = null,
    titleColor: Color = DHCarInfoTheme.subText,
    active: Boolean,
    useColorTint: Boolean = true,
    @DrawableRes activeIcon: Int,
    @DrawableRes inactiveIcon: Int? = null,
    onClick: () -> Unit
) {
    val layoutDirection = LayoutDirection.Ltr
    val paddingStart =
        margin?.calculateStartPadding(layoutDirection) ?: 0.dp
    val paddingEnd =
        margin?.calculateEndPadding(layoutDirection) ?: 0.dp
    val paddingTop = margin?.calculateTopPadding() ?: 0.dp
    val paddingBottom = margin?.calculateBottomPadding() ?: 0.dp
    Column(
        modifier = GlanceModifier.padding(
            start = paddingStart,
            end = paddingEnd,
            top = paddingTop,
            bottom = paddingBottom,
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = GlanceModifier
                .then(modifier)
                .clickable(block = onClick),
            provider = ImageProvider(
                if (active || inactiveIcon == null)
                    activeIcon
                else inactiveIcon
            ),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            colorFilter = if (!useColorTint) null else ColorFilter.tint(ColorProvider(Color.White)),
        )
        if (!title?.trim().isNullOrEmpty()) {
            Spacer(GlanceModifier.height(3.dp))
            Text(
                text = title,
                style = TextStyle(fontSize = 11.sp, color = ColorProvider(titleColor))
            )
        }
    }
}