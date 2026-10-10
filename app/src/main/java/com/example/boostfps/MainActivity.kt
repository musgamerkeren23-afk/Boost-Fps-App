package com.example.boostfps // Pastikan sama persis

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.boostfps.R // <--- Tambahkan import R ini secara eksplisit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnApplyBoost = findViewById<Button>(R.id.btnApplyBoost)

        btnApplyBoost.setOnClickListener {
            val targetFilePath = "/sdcard/Android/data/com.roblox.client/files/UserSettings.xml"
            val bashCommand = "echo '<Settings><Int name=\"FramerateLimit\">120</Int></Settings>' > $targetFilePath"

            val result = ShizukuHelper.executeRootCommand(bashCommand)

            if (result.contains("ERROR") || result.contains("EXCEPTION") || result.contains("ERR:")) {
                Toast.makeText(this, "Gagal menembus folder: $result", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "🚀 Berhasil bypass & terapkan FPS!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
