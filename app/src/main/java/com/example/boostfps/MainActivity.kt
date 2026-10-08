package com.example.boostfps

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.boostfps.databinding.ActivityMainBinding
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val REQUEST_CODE_SHIZUKU = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Toggle FPS Boost (120Hz Force)
        binding.toggleFpsBoost.setOnCheckedChangeListener { _, isChecked ->
            triggerHapticFeedback()
            if (isChecked) {
                applyShizukuCommand("settings put global peak_refresh_rate 120.0; settings put global user_refresh_rate 120.0")
                Toast.makeText(this, "⚡ 120Hz Refresh Rate Activated!", Toast.LENGTH_SHORT).show()
            } else {
                applyShizukuCommand("settings put global peak_refresh_rate 60.0; settings put global user_refresh_rate 60.0")
                Toast.makeText(this, "🔄 Refresh Rate Restored", Toast.LENGTH_SHORT).show()
            }
        }

        // 2. Toggle Anti-Aliasing (Force 4x MSAA)
        binding.toggleAntiAliasing.setOnCheckedChangeListener { _, isChecked ->
            triggerHapticFeedback()
            if (isChecked) {
                applyShizukuCommand("setprop debug.egl.force_msaa 1")
                Toast.makeText(this, "🎮 Anti-Aliasing (4x MSAA) ON", Toast.LENGTH_SHORT).show()
            } else {
                applyShizukuCommand("setprop debug.egl.force_msaa 0")
                Toast.makeText(this, "🎮 Anti-Aliasing OFF", Toast.LENGTH_SHORT).show()
            }
        }

        // 3. FITUR KEREN BARU: Game Governor Turbo Mode (Force Performance Mode)
        binding.toggleGameTurbo?.setOnCheckedChangeListener { _, isChecked ->
            triggerHapticFeedback()
            if (isChecked) {
                // Mengubah governor CPU ke performance & matikan thermal throttling
                applyShizukuCommand("setprop sys.use_fifo_ui 1; chmod 644 /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor; echo performance > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor")
                Toast.makeText(this, "🔥 GAME TURBO: MAXIMUM PERFORMANCE!", Toast.LENGTH_SHORT).show()
            } else {
                applyShizukuCommand("echo schedutil > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor")
                Toast.makeText(this, "❄️ Game Turbo Disabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Memberikan Efek Getar Haptic ala Cyberpunk saat menekan Toggle
     */
    private fun triggerHapticFeedback() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }

    /**
     * Memanggil Shizuku.newProcess via Reflection Java
     */
    private fun applyShizukuCommand(command: String) {
        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Izin Shizuku Belum Diberikan!", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val cmdArray = arrayOf("sh", "-c", command)
            val newProcessMethod = Shizuku::class.java.getDeclaredMethod(
                "newProcess",
                Array<String>::class.java,
                Array<String>::class.java,
                String::class.java
            )
            newProcessMethod.isAccessible = true
            val process = newProcessMethod.invoke(null, cmdArray, null, null) as Process

            val reader = process.inputStream.bufferedReader()
            val output = reader.readText()
            process.waitFor()

            binding.tvOutput.text = if (output.isNotEmpty()) "> SYSTEM LOG:\n$output" else "> COMMAND EXECUTED SUCCESSFULLY:\n$command"
        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvOutput.text = "> ERROR EXECUTING COMMAND:\n${e.message}"
        }
    }
}
