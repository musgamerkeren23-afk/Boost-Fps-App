package com.example.boostfps // Pastikan ini sama persis dengan project kamu

import android.content.pm.PackageManager
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStreamReader

object ShizukuHelper {

    fun isShizukuReady(): Boolean {
        return try {
            if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) return false
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            false
        }
    }

    fun executeRootCommand(command: String): String {
        if (!isShizukuReady()) {
            return "ERROR: Shizuku belum aktif atau izin ditolak!"
        }

        return try {
            // Memanggil su -c via Shizuku menggunakan array parameter standar
            val process = Shizuku.newProcess(arrayOf("su", "-c", command), null, null)

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
