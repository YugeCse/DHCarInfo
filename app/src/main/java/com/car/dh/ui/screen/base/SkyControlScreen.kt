package com.car.dh.ui.screen.base

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.car.dh.R
import com.car.dh.data.SkyControlItemInfo
import com.car.dh.ui.theme.DHCarInfoTheme

@Composable
fun SkyControlScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.padding(top = 14.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .size(width = 3.dp, height = 13.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(DHCarInfoTheme.accent)
            ) {}
            Spacer(Modifier.width(7.dp))
            Text(
                text = "天窗控制",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DHCarInfoTheme.subText
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DHCarInfoTheme.panel)
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val itemSource = remember {
                listOf(
                    SkyControlItemInfo(
                        iconRes = R.drawable.ic_sky_door_open,
                        title = "打开",
                        control = SkyController::open
                    ),
                    SkyControlItemInfo(
                        iconRes = R.drawable.ic_sky_door_close,
                        title = "关闭",
                        control = SkyController::close
                    ),
                    SkyControlItemInfo(
                        iconRes = R.drawable.ic_sky_door_stop,
                        title = "停止",
                        control = SkyController::stop
                    ),
                    SkyControlItemInfo(
                        iconRes = R.drawable.ic_sky_door_fresh_air,
                        title = "透气",
                        control = SkyController::freshAir
                    ),
                )
            }
            repeat(itemSource.size) { index ->
                val item = itemSource[index]
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DHCarInfoTheme.inactive.copy(alpha = 0.35f))
                        .clickable(onClick = { item.control() })
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(item.iconRes),
                        contentDescription = null,
                        tint = DHCarInfoTheme.accent
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = DHCarInfoTheme.text
                    )
                }
            }
        }
    }
}
