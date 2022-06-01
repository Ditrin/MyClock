package com.example.myclock

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import java.util.*

class ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var hour: Int = 0
    private var minute: Int = 0
    private var second2: Int = 0
    private var refreshThread: Thread? = null
    

    private var handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> {
                    invalidate()
                }
            }
        }
    }

    private fun getCurrentTime() {
        val calendar = Calendar.getInstance()
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        second2 = calendar.get(Calendar.SECOND)
    }

    private val paint = Paint().apply {
        strokeWidth = 15f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private val paintSecond = Paint().apply {
        strokeWidth = 10f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private val paintMinute = Paint().apply {
        strokeWidth = 15f
        style = Paint.Style.STROKE
        isAntiAlias = true
        color = Color.BLUE
    }
    private val paintHour = Paint().apply {
        strokeWidth = 20f
        style = Paint.Style.STROKE
        isAntiAlias = true
        color = Color.RED
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.translate(width / 2f, height / 2f)
        getCurrentTime()
        canvas?.drawCircle(0f, 0f, 360.0f, paint)
        for (i in 0..11) {
            canvas?.drawLine(
                0f,
                -360f,
                0f,
                -300f,
                paint
            )
            canvas?.rotate(
                360 / 12f,
                0f,
                0f
            )
        }
        drawHour(canvas, paintHour)
        drawMinute(canvas, paintMinute)
        drawSecond(canvas, paintSecond)
    }

    private fun drawSecond(canvas: Canvas?, paint: Paint) {
        val longR = 298f
        val shortR = 60f
        val startX = (-shortR * Math.sin(second2.times(Math.PI / 30))).toFloat()
        val startY = (shortR * Math.cos(second2.times(Math.PI / 30))).toFloat()
        val endX = (longR * Math.sin(second2.times(Math.PI / 30))).toFloat()
        val endY = (-longR * Math.cos(second2.times(Math.PI / 30))).toFloat()
        canvas?.drawLine(startX, startY, endX, endY, paint)
    }

    private fun drawMinute(canvas: Canvas?, paint: Paint) {
        val longR = 225f
        val shortR = 50f
        val startX = (-shortR * Math.sin(minute.times(Math.PI / 30))).toFloat()
        val startY = (shortR * Math.cos(minute.times(Math.PI / 30))).toFloat()
        val endX = (longR * Math.sin(minute.times(Math.PI / 30))).toFloat()
        val endY = (-longR * Math.cos(minute.times(Math.PI / 30))).toFloat()
        canvas?.drawLine(startX, startY, endX, endY, paint)
    }

    private fun drawHour(canvas: Canvas?, paint: Paint) {
        val longR = 180f
        val shortR = 40f
        val startX = (-shortR * Math.sin(hour.times(Math.PI / 6))).toFloat()
        val startY = (shortR * Math.cos(hour.times(Math.PI / 6))).toFloat()
        val endX = (longR * Math.sin(hour.times(Math.PI / 6))).toFloat()
        val endY = (-longR * Math.cos(hour.times(Math.PI / 6))).toFloat()
        canvas?.drawLine(startX, startY, endX, endY, paint)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refreshThread = Thread(Runnable {
            while (true) {
                try {
                    Thread.sleep(1000)
                    handler.sendEmptyMessage(0)
                } catch (e: InterruptedException) {
                    break
                }
            }
        })
        refreshThread?.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacksAndMessages(null)
        refreshThread?.interrupt()
    }
}