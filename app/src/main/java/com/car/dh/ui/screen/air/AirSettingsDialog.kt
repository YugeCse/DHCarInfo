package com.car.dh.ui.screen.air

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.car.dh.R
import com.car.dh.app.LocalGlobalConfig
import com.car.dh.ui.common.ColorPickDialog
import com.car.dh.ui.theme.DHCarInfoTheme
import com.car.dh.utils.toColor
import com.car.dh.utils.toHexCode
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.HueSlider
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

/**
 * 空调设置弹窗
 * @param isVisible 是否可见
 * @param onDismissRequest 申请消失时的事件
 */
@Composable
fun AirSettingsDialog(
    isVisible: Boolean = true,
    onDismissRequest: () -> Unit
) {
    if (!isVisible) return
    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        ),
        onDismissRequest = onDismissRequest
    ) {
        val viewModel = viewModel<AirSettingsViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        AirSettingsDialogView(
            settings = uiState,
            onChangeUseAppWidgetBackground =
                viewModel::setUseAppWidgetDefaultBackground,
            onChangeAirAppWidgetMarkOvalColor =
                viewModel::setAirAppWidgetMarkOvalColor,
            onChangeAirAppWidgetMarkNumberColor =
                viewModel::setAirAppWidgetMarkNumberColor,
            onChangeAirAppWidgetMarkOvalSelectColor =
                viewModel::setAirAppWidgetMarkOvalSelectColor,
            onChangeAirAppWidgetMarkNumberSelectColor =
                viewModel::setAirAppWidgetMarkNumberSelectColor,
            onChangeAirAppWidgetTempTextColor =
                viewModel::setAirAppWidgetTempTextColor,
            onChangeAirAppWidgetSubTextColor = viewModel::setAirAppWidgetSubTextColor,
        )
    }
}

@Composable
private fun AirSettingsDialogView(
    settings: AirSettingsInfo = AirSettingsInfo.default(),
    onChangeUseAppWidgetBackground: (Boolean) -> Unit = {},
    onChangeAirAppWidgetMarkNumberColor: (Color) -> Unit = {},
    onChangeAirAppWidgetMarkNumberSelectColor: (Color) -> Unit = {},
    onChangeAirAppWidgetMarkOvalColor: (Color) -> Unit = {},
    onChangeAirAppWidgetMarkOvalSelectColor: (Color) -> Unit = {},
    onChangeAirAppWidgetTempTextColor: (Color) -> Unit = {},
    onChangeAirAppWidgetSubTextColor: (Color) -> Unit = {},
) {
    val isLandscape = LocalConfiguration.current.orientation ==
            Configuration.ORIENTATION_LANDSCAPE
    Box(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .sizeIn(maxWidth = if (!isLandscape) 460.dp else 600.dp)
            .run {
                if (!LocalInspectionMode.current)
                    fillMaxWidth(0.9f)
                else defaultMinSize(minWidth = if (!isLandscape) 460.dp else 520.dp)
            },
        Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    DHCarInfoTheme.bg,
                    RoundedCornerShape(12.dp)
                )
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(12.dp),
                text = "空调小组件设置",
                fontSize = 22.sp,
                color = DHCarInfoTheme.text,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "使用默认背景",
                    fontSize = 16.sp,
                    color = DHCarInfoTheme.text
                )
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = settings.useAppWidgetBackground,
                    onCheckedChange = onChangeUseAppWidgetBackground
                )
            }
            ColorPickItemView(
                title = "刻度数文本颜色",
                targetColor = settings.airAppWidgetMarkNumberColor
            ) { onChangeAirAppWidgetMarkNumberColor(it.toColor()) }
            ColorPickItemView(
                title = "刻度数文本选中颜色",
                targetColor = settings.airAppWidgetMarkNumberSelectColor
            ) { onChangeAirAppWidgetMarkNumberSelectColor(it.toColor()) }
            ColorPickItemView(
                title = "刻度线颜色",
                targetColor = settings.airAppWidgetMarkOvalColor
            ) { onChangeAirAppWidgetMarkOvalColor(it.toColor()) }
            ColorPickItemView(
                title = "刻度线选中颜色",
                targetColor = settings.airAppWidgetMarkOvalSelectColor
            ) { onChangeAirAppWidgetMarkOvalSelectColor(it.toColor()) }
            ColorPickItemView(
                title = "温度文本颜色",
                targetColor = settings.airAppWidgetTempTextColor
            ) { onChangeAirAppWidgetTempTextColor(it.toColor()) }
            ColorPickItemView(
                title = "子文本颜色",
                targetColor = settings.airAppWidgetSubTextColor
            ) { onChangeAirAppWidgetSubTextColor(it.toColor()) }
        }
    }
}

/**
 * 颜色拾取Item视图
 * @param title 标题
 * @param targetColor 目标色
 * @param onColorSelected 颜色选中事件
 */
@Composable
private fun ColorPickItemView(
    title: String,
    targetColor: Color,
    onColorSelected: (String) -> Unit
) {
    var colorPickDialogVisible by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp)
            .padding(horizontal = 20.dp)
            .clickable { colorPickDialogVisible = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = DHCarInfoTheme.text
        )
        Spacer(Modifier.weight(1f))
        var targetColorHex by remember(targetColor) {
            mutableStateOf(targetColor.toHexCode())
        }
        if (targetColorHex.isNotEmpty())
            Text(text = targetColorHex, fontSize = 14.sp, color = DHCarInfoTheme.subText)
        Image(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(20.dp),
            contentDescription = null,
            painter = painterResource(R.drawable.ic_nav_forward)
        )
    }
    ColorPickDialog(
        initialColor = targetColor,
        visible = colorPickDialogVisible,
        onColorPick = { onColorSelected(it.toHexCode()) },
        onDismissRequest = { colorPickDialogVisible = false }
    )
}

@Composable
@Preview(device = "id:pixel_9a")
private fun AirSettingsDialogViewPreview() = DHCarInfoTheme { AirSettingsDialogView() }