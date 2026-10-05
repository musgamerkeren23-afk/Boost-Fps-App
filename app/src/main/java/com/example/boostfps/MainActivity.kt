package com.example.boostfps

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnBoost = findViewById<Button>(R.id.btnBoost)
        btnBoost.setOnClickListener {
            if (Shizuku.pingBinder()) {
                executeFpsBoostCommands()
            } else {
                Toast.makeText(this, "Shizuku belum aktif!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun executeFpsBoostCommands() {
        // Menjalankan command booster tanpa inject (pure shell via Shizuku)
        val commands = arrayOf(
            "settings put global surface_flinger.force_hw_ui 1",
            "setprop debug.sf.showfps 0",
            "cmd power set-mode 0" // Set ke Performance mode
        )

        try {
            for (cmd in commands) {
                Shizuku.newProcess(arrayOf("sh", "-c", cmd), null, null)
            }
            Toast.makeText(this, "FPS Booster Berhasil Diaktifkan!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal menjalankan perintah Shizuku!", Toast.LENGTH_SHORT).show()
        }
    }
}
