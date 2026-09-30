package com.car.dh.ui.screen.base

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.car.dh.data.SkyControlItemInfo
import com.car.dh.ui.theme.DHCarInfoTheme

@Composable
fun SkyControlScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "天窗控制", fontSize = 14.sp, color = DHCarInfoTheme.subText)
        Row(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val itemSource = remember {
                listOf(
                    SkyControlItemInfo(
                        title = "打开",
                        control = SkyController::open
                    ),
                    SkyControlItemInfo(
                        title = "关闭",
                        control = SkyController::close
                    ),
                    SkyControlItemInfo(
                        title = "透气",
                        control = SkyController::freshAir
                    ),
                    SkyControlItemInfo(
                        title = "停止",
                        control = SkyController::stop
                    )
                )
            }
            repeat(itemSource.size) { index ->
                val skyControlItem = itemSource[index]
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .weight(1f)
                        .height(58.dp)
                        .clickable(onClick = { skyControlItem.control() })
                        .border(
                            1.dp,
                            DHCarInfoTheme.inactive,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = skyControlItem.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DHCarInfoTheme.text
                    )
                }
            }
        }
    }
}