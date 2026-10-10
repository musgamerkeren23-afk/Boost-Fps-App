package com.example.appbooster // Sesuaikan dengan package-mu

import android.content.pm.PackageManager
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStreamReader

object ShizukuHelper {

    // Cek apakah Shizuku terpasang dan diizinkan
    fun isShizukuReady(): Boolean {
        return try {
            if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) return false
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            false
        }
    }

    // Eksekusi perintah bypass
    fun executeRootCommand(command: String): String {
        if (!isShizukuReady()) {
            return "ERROR: Shizuku belum aktif atau izin ditolak!"
        }

        return try {
            // Menggunakan su -c via Shizuku untuk menembus proteksi folder Android/data
            val fullCommand = arrayOf("su", "-c", command)
            val process = Shizuku.newProcess(fullCommand, null, null)

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
            val output = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            while (errorReader.readLine().also { line = it } != null) {
                output.append("ERR: ").append(line).append("\n")
            }

            process.waitFor()
            output.toString().ifEmpty { "SUKSES: Perintah dieksekusi." }
        } catch (e: Exception) {
            "EXCEPTION: ${e.message}"
        }
    }
}
