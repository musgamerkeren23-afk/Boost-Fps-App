package com.example.boostfps

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStartBubble = findViewById<Button>(R.id.btnApplyBoost)
        btnStartBubble.text = "🟢 Aktifkan Floating Bubble"

        btnStartBubble.setOnClickListener {
            if (checkOverlayPermission()) {
                val intent = Intent(this, FloatingWidgetService::class.java)
                startService(intent)
                Toast.makeText(this, "Floating Bubble diaktifkan! Buka game kamu.", Toast.LENGTH_LONG).show()
                finish() // Tutup app utama supaya langsung main game
            }
        }
    }

    private fun checkOverlayPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, 1234)
            Toast.makeText(this, "Izinkan aplikasi tampil di atas aplikasi lain dulu!", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}
