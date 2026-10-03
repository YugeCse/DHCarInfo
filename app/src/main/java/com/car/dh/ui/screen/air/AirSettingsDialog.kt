package com.car.dh.ui.screen.air

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.car.dh.ui.theme.DHCarInfoTheme

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
                viewModel::setUseAppWidgetDefaultBackground
        )
    }
}

@Composable
private fun AirSettingsDialogView(
    settings: AirSettingsInfo = AirSettingsInfo.default(),
    onChangeUseAppWidgetBackground: (Boolean) -> Unit = {}
) {
    Box(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .sizeIn(maxWidth = 460.dp)
            .run {
                if (!LocalInspectionMode.current)
                    fillMaxWidth(0.8f)
                else defaultMinSize(minWidth = 460.dp)
            },
        Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    DHCarInfoTheme.bg,
                    RoundedCornerShape(12.dp)
                ),
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
                    text = "小组件使用默认背景",
                    fontSize = 16.sp,
                    color = DHCarInfoTheme.text
                )
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = settings.useAppWidgetBackground,
                    onCheckedChange = onChangeUseAppWidgetBackground
                )
            }
        }
    }
}


@Composable
@Preview(device = "id:pixel_9a")
private fun AirSettingsDialogViewPreview() = DHCarInfoTheme { AirSettingsDialogView() }