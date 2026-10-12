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
        
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_layout, null)

        val LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

                params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            // HAPUS FLAG_NOT_FOCUSABLE dan ganti jadi 0 (supaya toggle & tombol bisa disentuh aman)
            0,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }


        windowManager?.addView(floatingView, params)

        val bubbleBtn = floatingView?.findViewById<Button>(R.id.bubbleBtn)
        val menuLayout = floatingView?.findViewById<LinearLayout>(R.id.menuLayout)
        val switchAA = floatingView?.findViewById<SwitchCompat>(R.id.switchBubbleAA)
        val btnBoost = floatingView?.findViewById<Button>(R.id.btnBubbleBoost)

        // Perbaikan logika geser dan klik agar menu pasti keluar
        bubbleBtn?.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            private var startTime = 0L

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params!!.x
                        initialY = params!!.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        startTime = System.currentTimeMillis()
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = (event.rawX - initialTouchX).toInt()
                        val dy = (event.rawY - initialTouchY).toInt()
                        
                        params!!.x = initialX + dx
                        params!!.y = initialY + dy
                        windowManager?.updateViewLayout(floatingView, params)
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        // Jika durasi sentuhan cepat dan pergerakan sangat kecil, anggap sebagai Klik
                        val duration = System.currentTimeMillis() - startTime
                        val dx = Math.abs((event.rawX - initialTouchX).toInt())
                        val dy = Math.abs((event.rawY - initialTouchY).toInt())

                        if (duration < 200 && dx < 15 && dy < 15) {
                            if (menuLayout?.visibility == View.VISIBLE) {
                                menuLayout.visibility = View.GONE
                            } else {
                                menuLayout?.visibility = View.VISIBLE
                            }
                        }
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
