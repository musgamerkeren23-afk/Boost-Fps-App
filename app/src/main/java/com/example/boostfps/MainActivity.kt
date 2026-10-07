package com.example.boostfps

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.boostfps.databinding.ActivityMainBinding
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val REQUEST_CODE_SHIZUKU = 1001

    private val onRequestPermissionResultListener =
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode == REQUEST_CODE_SHIZUKU) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Izin Shizuku Diberikan!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Izin Shizuku Ditolak!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Shizuku.addRequestPermissionResultListener(onRequestPermissionResultListener)

        checkShizukuPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(onRequestPermissionResultListener)
    }

    private fun checkShizukuPermission() {
        try {
            if (Shizuku.isPreV11()) {
                Toast.makeText(this, "Versi Shizuku terlalu lama", Toast.LENGTH_SHORT).show()
                return
            }

            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Shizuku Aktif", Toast.LENGTH_SHORT).show()
            } else {
                Shizuku.requestPermission(REQUEST_CODE_SHIZUKU)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Shizuku belum berjalan", Toast.LENGTH_SHORT).show()
        }
    }

    fun runShellCommand(command: String): String {
        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            return "Izin Shizuku belum diberikan"
        }

        return try {
            val cmdArray = arrayOf("sh", "-c", command)

            // Memanggil method private 'newProcess' via Reflection
            val newProcessMethod = Shizuku::class.java.getDeclaredMethod(
                "newProcess",
                Array<String>::class.java,
                Array<String>::class.java,
                String::class.java
            )
            newProcessMethod.isAccessible = true

            val process = newProcessMethod.invoke(null, cmdArray, null, null) as Process

            val reader = process.inputStream.bufferedReader()
            val output = reader.readText()
            process.waitFor()

            if (output.isNotEmpty()) output else "Command executed"
        } catch (e: Exception) {
            e.printStackTrace()
            "Error: ${e.message}"
        }
    }
}
