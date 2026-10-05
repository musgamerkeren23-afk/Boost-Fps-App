package com.example.boostfps

import rikka.shizuku.Shizuku

object ShizukuManager {

    // Cek apakah Shizuku aktif dan memiliki izin
    fun isShizukuAvailable(): Boolean {
        return try {
            Shizuku.pingBinder() && Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            false
        }
    }

    // Eksekusi baris perintah shell via Shizuku
    fun executeCommand(command: String): Boolean {
        return try {
            val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
            process.waitFor()
            process.exitValue() == 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
