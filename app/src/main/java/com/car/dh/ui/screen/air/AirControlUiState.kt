package com.car.dh.ui.screen.air

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.car.dh.canbus.AirHelper
import com.car.dh.canbus.DataCanbus
import com.car.dh.canbus.IUiNotify
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

// ============================================================
// UI 状态（只保留会从 DATA 变化中受益的字段）
// ============================================================

@Stable
class AirControlUiState internal constructor() {
    var power by mutableIntStateOf(0); internal set
    var ac by mutableIntStateOf(0); internal set
    var cycle by mutableIntStateOf(0); internal set
    var frontDefrost by mutableIntStateOf(0); internal set
    var modeUp by mutableIntStateOf(0); internal set
    var modeBody by mutableIntStateOf(0); internal set
    var modeFoot by mutableIntStateOf(0); internal set

    internal fun syncAll(c: AirController) {
        power = c.getPower()
        ac = c.getAc()
        cycle = c.getCycle()
        frontDefrost = c.getFrontDefrost()
        modeUp = c.getBlowUp()
        modeBody = c.getBlowBody()
        modeFoot = c.getBlowFoot()
    }
}


@Composable
fun rememberYuanjingAirUiState(
    controller: AirController
): AirControlUiState {
    val state = remember(controller) { AirControlUiState() }

    DisposableEffect(controller) {
        AirHelper.disableAirWindowLocal(true)
        state.syncAll(controller)

        val notify = IUiNotify { _, _, _, _ -> state.syncAll(controller) }

        for (id in AirController.OBSERVED_IDS) {
            DataCanbus.NOTIFY_EVENTS[id]?.addNotify(notify, 1)
        }

        onDispose {
            for (id in AirController.OBSERVED_IDS) {
                DataCanbus.NOTIFY_EVENTS[id]?.removeNotify(notify)
            }
            AirHelper.disableAirWindowLocal(false)
        }
    }

    // 兜底轮询：只用来同步开关类状态，不涉及温度/风量
    LaunchedEffect(controller) {
        while (true) {
            state.syncAll(controller)
            delay(300.milliseconds)
        }
    }

    return state
}