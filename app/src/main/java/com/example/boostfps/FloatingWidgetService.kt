package com.example.boostfps

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat

class FloatingWidgetService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var params: WindowManager.LayoutParams? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        // Inflate layout floating
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_layout, null)

        val LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        windowManager?.addView(floatingView, params)

        // Komponen UI di dalam floating
        val bubbleBtn = floatingView?.findViewById<Button>(R.id.bubbleBtn)
        val menuLayout = floatingView?.findViewById<LinearLayout>(R.id.menuLayout)
        val switchAA = floatingView?.findViewById<SwitchCompat>(R.id.switchBubbleAA)
        val btnBoost = floatingView?.findViewById<Button>(R.id.btnBubbleBoost)

        // Logika Klik Bubble (Buka/Tutup Menu)
        bubbleBtn?.setOnClickListener {
            if (menuLayout?.visibility == View.VISIBLE) {
                menuLayout.visibility = View.GONE
            } else {
                menuLayout?.visibility = View.VISIBLE
            }
        }

        // Logika Tombol Geser (Drag & Drop Bubble)
        bubbleBtn?.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params!!.x
                        initialY = params!!.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params!!.x = initialX + (event.rawX - initialTouchX).toInt()
                        params!!.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager?.updateViewLayout(floatingView, params)
                        return true
                    }
                }
                return false
            }
        })

        // Logika Tombol Boost di dalam Menu Melayang
        btnBoost?.setOnClickListener {
            val targetFilePath = "/sdcard/Android/data/com.roblox.client/files/UserSettings.xml"
            val aaValue = if (switchAA?.isChecked == true) "4" else "0"

            val configContent = """
                <Settings>
                    <Int name="FramerateLimit">120</Int>
                    <Int name="AntiAliasingQuality">$aaValue</Int>
                </Settings>
            """.trimIndent()

            val bashCommand = "echo '$configContent' > $targetFilePath"
            val result = ShizukuHelper.executeRootCommand(bashCommand)

            if (result.contains("ERROR") || result.contains("EXCEPTION")) {
                Toast.makeText(this, "Gagal Boost: $result", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "🚀 Berhasil Boost dari In-Game!", Toast.LENGTH_SHORT).show()
                menuLayout?.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (floatingView != null) windowManager?.removeView(floatingView)
    }
}
