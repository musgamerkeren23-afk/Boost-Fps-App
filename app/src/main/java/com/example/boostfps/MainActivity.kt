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

        // Pastikan ID ini sesuai dengan yang ada di activity_main.xml
        statusText = findViewById(R.id.statusText)
        boostButton = findViewById(R.id.boostButton)

        if (Shizuku.pingBinder()) {
            checkShizukuPermission()
        } else {
            statusText.text = "Status Shizuku: Tidak Aktif"
        }

        boostButton.setOnClickListener {
            if (Shizuku.pingBinder()) {
                val success = FpsBoosterEngine.applyFpsBoost()
                if (success) {
                    Toast.makeText(this, "FPS Boost Berhasil!", Toast.LENGTH_SHORT).show()
                    statusText.text = "Status: Teroptimasi"
                } else {
                    Toast.makeText(this, "Gagal mengaplikasikan boost", Toast.LENGTH_SHORT).show()
                }
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
}
