package com.example.boostfps

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.boostfps.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnApplyBoost = findViewById<Button>(R.id.btnApplyBoost)
        val switchAntiAliasing = findViewById<SwitchCompat>(R.id.switchAntiAliasing)

        btnApplyBoost.setOnClickListener {
            val targetFilePath = "/sdcard/Android/data/com.roblox.client/files/UserSettings.xml"
            
            // Cek status toggle Anti-Aliasing (0 = Matikan untuk performa/FPS tinggi, 1/4 = Aktifkan)
            val antiAliasingValue = if (switchAntiAliasing.isChecked) "4" else "0"
            
            // Buat isi konfigurasi yang mencakup FPS Limit dan Anti-Aliasing
            val configContent = """
                <Settings>
                    <Int name="FramerateLimit">120</Int>
                    <Int name="AntiAliasingQuality">$antiAliasingValue</Int>
                </Settings>
            """.trimIndent()

            // Perintah shell untuk menulis konfigurasi baru ke file target
            val bashCommand = "echo '$configContent' > $targetFilePath"

            val result = ShizukuHelper.executeRootCommand(bashCommand)

            if (result.contains("ERROR") || result.contains("EXCEPTION")) {
                Toast.makeText(this, "Gagal menembus folder: $result", Toast.LENGTH_LONG).show()
            } else {
                val aaStatus = if (switchAntiAliasing.isChecked) "ON" else "OFF"
                Toast.makeText(this, "🚀 Boost Diterapkan! (Anti-Aliasing: $aaStatus)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
