package com.car.dh.ui.common

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.car.dh.ui.theme.DHCarInfoTheme
import com.car.dh.utils.toHexCode
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.HueSlider
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

/** 颜色拾取弹窗 **/
@Composable
fun ColorPickDialog(
    visible: Boolean = false,
    initialColor: Color? = null,
    onColorPick: (Color) -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    if (!visible) return
    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        ),
        onDismissRequest = onDismissRequest
    ) {
        ColorPickDialogView(
            initialColor = initialColor,
            onColorPick = {
                onColorPick(it)
                onDismissRequest()
            }
        )
    }
}

@Composable
private fun ColorPickDialogView(
    initialColor: Color? = null,
    onColorPick: (Color) -> Unit = {}
) {
    val isLandscape = LocalConfiguration.current.orientation ==
            Configuration.ORIENTATION_LANDSCAPE
    Box(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .sizeIn(maxWidth = if (!isLandscape) 500.dp else 800.dp)
            .run {
                if (!LocalInspectionMode.current)
                    fillMaxWidth(0.9f)
                else defaultMinSize(minWidth = if (!isLandscape) 500.dp else 700.dp)
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val colorPickerController = rememberColorPickerController()
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HsvColorPicker(
                    modifier = Modifier.size(180.dp),
                    initialColor = initialColor,
                    controller = colorPickerController,
                    onColorPickingFinished = {

                    }
                )
                Column(Modifier.padding(top = 20.dp)) {
                    BrightnessSlider(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth()
                            .height(16.dp),
                        initialColor = initialColor,
                        controller = colorPickerController
                    )
                    HueSlider(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth()
                            .height(16.dp),
                        initialColor = initialColor,
                        controller = colorPickerController
                    )
                    AlphaSlider(
                        modifier = Modifier
                            .padding(top = 18.dp)
                            .fillMaxWidth()
                            .height(16.dp),
                        initialColor = initialColor,
                        controller = colorPickerController
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontSize = 16.sp,
                            color = DHCarInfoTheme.text,
                            text = "当前：",
                        )
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .size(36.dp)
                                .background(colorPickerController.selectedColor.value)
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DHCarInfoTheme.inactive)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            fontSize = 16.sp,
                            color = DHCarInfoTheme.text,
                            text = colorPickerController.selectedColor.value.toHexCode(),
                        )
                        Spacer(Modifier.weight(1f))
                        Button(
                            onClick = {
                                onColorPick(colorPickerController.selectedColor.value)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) { Text(text = "确认") }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(device = "spec:parent=pixel_5,orientation=landscape")
private fun ColorPickDialogViewPreview() = DHCarInfoTheme { ColorPickDialogView() }