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

class FloatingWidgetService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var params: WindowManager.LayoutParams? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        
        // Anti-Crash Global Exception Handler untuk Service ini
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            Log.e("FloatingServiceCrash", "Terjadi error tak terduga: ${throwable.message}", throwable)
            try {
                // Tampilkan pesan error ringkas via Toast (jika UI thread mengizinkan)
                android.os.Handler(mainLooper).post {
                    Toast.makeText(applicationContext, "⚠️ Service Error: ${throwable.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        try {
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

            // Fungsi aman untuk memperbarui fokus window
            fun updateWindowFocus(isFocusable: Boolean) {
                try {
                    if (params != null) {
                        if (isFocusable) {
                            params?.flags = params?.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
                        } else {
                            params?.flags = params?.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        }
                        windowManager?.updateViewLayout(floatingView, params)
                    }
                } catch (e: Exception) {
                    Log.e("FocusError", "Gagal ubah fokus: ${e.message}")
                }
            }

            // Touch Listener dengan proteksi try-catch
            bubbleBtn?.setOnTouchListener(object : View.OnTouchListener {
                private var initialX = 0
                private var initialY = 0
                private var initialTouchX = 0f
                private var initialTouchY = 0f
                private var startTime = 0L

                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    try {
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                initialX = params?.x ?: 0
                                initialY = params?.y ?: 0
                                initialTouchX = event.rawX
                                initialTouchY = event.rawY
                                startTime = System.currentTimeMillis()
                                return true
                            }
                            MotionEvent.ACTION_MOVE -> {
                                val dx = (event.rawX - initialTouchX).toInt()
                                val dy = (event.rawY - initialTouchY).toInt()
                                
                                params?.x = initialX + dx
                                params?.y = initialY + dy
                                windowManager?.updateViewLayout(floatingView, params)
                                return true
                            }
                            MotionEvent.ACTION_UP -> {
                                val duration = System.currentTimeMillis() - startTime
                                val dx = Math.abs((event.rawX - initialTouchX).toInt())
                                val dy = Math.abs((event.rawY - initialTouchY).toInt())

                                if (duration < 200 && dx < 15 && dy < 15) {
                                    if (menuLayout?.visibility == View.VISIBLE) {
                                        menuLayout.visibility = View.GONE
                                        updateWindowFocus(false)
                                    } else {
                                        menuLayout?.visibility = View.VISIBLE
                                        updateWindowFocus(true)
                                    }
                                }
                                return true
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("TouchError", "Error pada touch listener: ${e.message}")
                    }
                    return false
                }
            })

            // Tombol Boost dengan proteksi error command
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

                    if (result.contains("ERROR") || result.contains("EXCEPTION")) {
                        Toast.makeText(this, "Gagal Boost: $result", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "🚀 Berhasil Boost 120 FPS!", Toast.LENGTH_SHORT).show()
                        menuLayout?.visibility = View.GONE
                        updateWindowFocus(false)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Terjadi kesalahan saat boost: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

        } catch (e: Exception) {
            Log.e("ServiceCreateError", "Gagal menginisialisasi Floating Widget: ${e.message}")
            Toast.makeText(this, "Gagal memuat Floating UI: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
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
