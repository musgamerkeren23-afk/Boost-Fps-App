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
import android.util.Log
import androidx.appcompat.view.ContextThemeWrapper

class FloatingWidgetService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var params: WindowManager.LayoutParams? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            
            // FIX: Menggunakan referensi style AppCompat yang valid untuk menghindari InflateException pada SwitchCompat
            val themedContext = ContextThemeWrapper(this, androidx.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar)
            floatingView = LayoutInflater.from(themedContext).inflate(R.layout.floating_layout, null)

            val LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }

            // Tetap gunakan FLAG_NOT_FOCUSABLE agar game Roblox di belakangnya tidak terganggu / pause
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

            val bubbleBtn = floatingView?.findViewById<Button>(R.id.bubbleBtn)
            val menuLayout = floatingView?.findViewById<LinearLayout>(R.id.menuLayout)
            val switchAA = floatingView?.findViewById<SwitchCompat>(R.id.switchBubbleAA)
            val btnBoost = floatingView?.findViewById<Button>(R.id.btnBubbleBoost)

            // Logika Touch & Click yang aman dan responsif
            var isDragging = false
            var initialX = 0
            var initialY = 0
            var initialTouchX = 0f
            var initialTouchY = 0f

            bubbleBtn?.setOnTouchListener { _, event ->
                try {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = params?.x ?: 0
                            initialY = params?.y ?: 0
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            isDragging = false
                            true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val dx = (event.rawX - initialTouchX).toInt()
4                            val dy = (event.rawY - initialTouchY).toInt()
                            
                            if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
                                isDragging = true
                            }

                            params?.x = initialX + dx
                            params?.y = initialY + dy
                            windowManager?.updateViewLayout(floatingView, params)
                            true
                        }
                        MotionEvent.ACTION_UP -> {
                            if (!isDragging) {
                                // Toggle buka/tutup menu
                                if (menuLayout?.visibility == View.VISIBLE) {
                                    menuLayout.visibility = View.GONE
                                } else {
                                    menuLayout?.visibility = View.VISIBLE
                                }
                            }
                            true
                        }
                        else -> false
                    }
                } catch (e: Exception) {
                    Log.e("FloatingTouch", "Error: ${e.message}")
                    false
                }
            }

            // Tombol Boost Shizuku / Root
            btnBoost?.setOnClickListener {
                try {
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

                    if (result.contains("ERROR") || result.contains("EXCEPTION") || result.contains("denied")) {
                        Toast.makeText(this, "Gagal Boost: $result", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "🚀 Berhasil Boost 120 FPS!", Toast.LENGTH_SHORT).show()
                        menuLayout?.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Gagal mengeksekusi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

        } catch (e: Exception) {
            Log.e("FloatingService", "Gagal inisialisasi: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (floatingView != null) {
                windowManager?.removeView(floatingView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
