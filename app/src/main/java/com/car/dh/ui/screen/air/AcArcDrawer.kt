package com.car.dh.ui.screen.air

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import com.car.dh.app.GlobalConfig
import com.car.dh.ui.theme.DHCarInfoTheme
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

object AcArcDrawer {

    /**
     * 绘制空调半圆弧、档位和温度
     * @param width 图片宽度
     * @param height 图片高度
     * @param currentTemp 当前温度 (例如 24.5)
     * @param totalGears 总档位数 (例如 7)
     * @param activeGear 当前激活的档位 (从 1 开始)
     */
    fun drawAcArc(
        globalConfig: GlobalConfig,
        active: Boolean,
        currentTemp: Float,
        totalGears: Int,
        activeGear: Int,
        width: Int,
        height: Int,
        padding: Float,
        arcStrokeWidth: Float,
        gearTextSize: Float,
        tempTextSize: Float,
    ): Bitmap {
        val canvasSize = min(width, height)
        val bitmap = createBitmap(canvasSize, (canvasSize * 0.6f).toInt())
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR) // 清除背景

        // 1. 初始化画笔
        val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = arcStrokeWidth
            strokeCap = Paint.Cap.ROUND
            color = globalConfig.airAppWidgetMarkOvalColor.toArgb()
        }

        val activeArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = arcStrokeWidth * 1.1f
            strokeCap = Paint.Cap.ROUND
            color = globalConfig.airAppWidgetMarkOvalSelectColor.toArgb()
        }

        val gearPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = gearTextSize
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            color = globalConfig.airAppWidgetMarkOvalColor.toArgb()
        }

        val tempPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = tempTextSize
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            color = (when {
                active -> globalConfig.airAppWidgetTempTextColor
                else -> globalConfig.airAppWidgetSubTextColor
            }).toArgb()
        }

        // 2. 计算圆弧边界 (留出 padding 和文字空间)
        val reqSize = min(width - arcStrokeWidth * 2f - gearTextSize * 2, height.toFloat())
        val radius = reqSize / 2f
        val offsetX = (width - padding * 2 - reqSize) / 2f
        val startX = padding + offsetX
        val startY = arcStrokeWidth + gearTextSize
        val endX = startX + reqSize
        val endY = startY + reqSize
        val rect = RectF(startX, startY, endX, endY)

        // 半圆从 180度 到 360度 (从左到右)
        val startAngle = 180f
        val sweepAngle = 180f

        // 3. 绘制背景圆弧
        canvas.drawArc(rect, startAngle, sweepAngle, false, arcPaint)

        // 4. 绘制激活进度圆弧
        if (totalGears > 1) {
            val progressSweep = (activeGear.toFloat() / (totalGears - 1)) * sweepAngle
            canvas.drawArc(rect, startAngle, progressSweep, false, activeArcPaint)
        }
        // 5. 绘制档位刻度
        val centerX = width / 2f
        val centerY = startY + radius
        for (i in 0 until totalGears) {
            // 计算当前档位的角度 (从 180 到 360)
            val angle = startAngle + (i.toFloat() / (totalGears - 1)) * sweepAngle
            val rad = Math.toRadians(angle.toDouble())

            // 计算刻度线起点和终点
            val lineStartRadius = radius - 10f
            val lineEndRadius = radius + 6f
            val x1 = centerX + (lineStartRadius * cos(rad)).toFloat()
            val y1 = centerY + (lineStartRadius * sin(rad)).toFloat()
            val x2 = centerX + (lineEndRadius * cos(rad)).toFloat()
            val y2 = centerY + (lineEndRadius * sin(rad)).toFloat()

            // 绘制刻度线
            val currentGearPaint = Paint(arcPaint).apply {
                strokeWidth = arcStrokeWidth * 0.5f
                color = (when {
                    i < activeGear ->
                        globalConfig.airAppWidgetMarkOvalSelectColor

                    else -> globalConfig.airAppWidgetMarkOvalColor
                }).toArgb()
            }
            canvas.drawLine(x1, y1, x2, y2, currentGearPaint)

            // 绘制档位数字 (在刻度线外侧)
            val textRadius = radius + 25f
            val textX = centerX + (textRadius * cos(rad)).toFloat()
            val textY = centerY + (textRadius * sin(rad)).toFloat() + 8f // 8f 用于垂直居中微调
            val newTextPaint = gearPaint.apply {
                color = when {
                    i > activeGear - 1 ->
                        globalConfig.airAppWidgetMarkNumberColor.toArgb()

                    else -> globalConfig.airAppWidgetMarkNumberSelectColor.toArgb()
                }
            }
            canvas.drawText("${i + 1}", textX, textY, newTextPaint)
        }

        // 6. 绘制中心温度
        val tempText = when (currentTemp) {
            currentTemp.toInt().toFloat() ->
                "${currentTemp.toInt()}°"

            else -> "${currentTemp}°"
        }
        canvas.drawText(tempText, centerX, centerY, tempPaint) // 20f 用于垂直居中微调

        return bitmap
    }
}