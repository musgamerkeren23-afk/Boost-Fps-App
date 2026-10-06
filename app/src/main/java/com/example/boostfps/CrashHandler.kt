package com.example.boostfps

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import java.io.PrintWriter
import java.io.StringWriter

class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        // Extract stacktrace lengkap dari error
        val stringWriter = StringWriter()
        throwable.printStackTrace(PrintWriter(stringWriter))
        val errorLog = stringWriter.toString()

        // Salin log error ke clipboard otomatis
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Crash Log", errorLog)
            clipboard.setPrimaryClip(clip)

            // Tampilkan Toast di UI thread
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "App Crash! Log error berhasil disalin ke Clipboard.", Toast.LENGTH_LONG).show()
            }
            Thread.sleep(2000) // Beri waktu Toast agar terlihat sebelum app keluar
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Serahkan kembali ke handler bawaan
        defaultHandler?.uncaughtException(thread, throwable)
    }

    companion object {
        fun init(context: Context) {
            Thread.setDefaultUncaughtExceptionHandler(CrashHandler(context.applicationContext))
        }
    }
}
