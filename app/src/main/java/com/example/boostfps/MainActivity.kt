package com.example.boostfps

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var boostButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        boostButton = findViewById(R.id.boostButton)

        // Request Shizuku permission when starting
        if (Shizuku.pingBinder()) {
            checkShizukuPermission()
        } else {
            statusText.text = "Status Shizuku: Tidak Aktif"
        }

        boostButton.setOnClickListener {
            if (Shizuku.pingBinder()) {
                boostFps()
            } else {
                Toast.makeText(this, "Shizuku belum berjalan!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkShizukuPermission() {
        if (Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            statusText.text = "Status Shizuku: Terhubung & Diizinkan"
        } else {
            Shizuku.requestPermission(0)
        }
    }

    private fun boostFps() {
        // Eksekusi perintah via ShizukuManager (tanpa panggil Shizuku.newProcess langsung)
        val process = ShizukuManager.executeCommand("echo 3 > /proc/sys/vm/drop_caches")
        
        if (process != null) {
            Toast.makeText(this, "FPS Boost Berhasil Dijalankan!", Toast.LENGTH_SHORT).show()
            statusText.text = "Status: Optimasi Selesai"
        } else {
            Toast.makeText(this, "Gagal menjalankan optimasi FPS", Toast.LENGTH_SHORT).show()
        }
    }
}
