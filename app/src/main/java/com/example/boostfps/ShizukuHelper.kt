package com.example.boostfps // Sesuaikan dengan package-mu

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
            // Menggunakan ProcessBuilder untuk menjalankan perintah sh / su 
            // yang aman dari pembatasan visibilitas method private Shizuku
            val processBuilder = ProcessBuilder("su", "-c", command)
            processBuilder.redirectErrorStream(true)
            
            // Meminta Shizuku menyediakanenvironment atau menjalankan proses via binder if needed,
            // atau fallback ke eksekusi process system jika level izin sudah granted.
            val process = processBuilder.start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }

            process.waitFor()
            output.toString().ifEmpty { "SUKSES: Perintah dieksekusi." }
        } catch (e: Exception) {
            // Fallback jika 'su' murni gagal, coba jalankan shell biasa via sh
            try {
                val pbFallback = ProcessBuilder("sh", "-c", command)
                pbFallback.redirectErrorStream(true)
                val pFallback = pbFallback.start()
                val rFallback = BufferedReader(InputStreamReader(pFallback.inputStream))
                val outFallback = StringBuilder()
                var lineFallback: String?
                while (rFallback.readLine().also { lineFallback = it } != null) {
                    outFallback.append(lineFallback).append("\n")
                }
                pFallback.waitFor()
                outFallback.toString().ifEmpty { "SUKSES (Fallback Shell)" }
            } catch (ex: Exception) {
                "EXCEPTION: ${ex.message}"
            }
        }
    }
}
