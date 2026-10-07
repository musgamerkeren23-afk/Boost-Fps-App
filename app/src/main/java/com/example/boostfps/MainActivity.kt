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

        // Registrasi listener permission Shizuku
        Shizuku.addRequestPermissionResultListener(onRequestPermissionResultListener)

        binding.btnCheckShizuku.setOnClickListener {
            checkShizukuPermission()
        }

        binding.btnExecute.setOnClickListener {
            val command = binding.etCommand.text.toString().trim()
            if (command.isNotEmpty()) {
                executeShizukuCommand(command)
            } else {
                Toast.makeText(this, "Perintah tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
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
                Toast.makeText(this, "Shizuku Aktif & Memiliki Izin", Toast.LENGTH_SHORT).show()
            } else if (Shizuku.shouldShowRequestPermissionRationale()) {
                Toast.makeText(this, "Izin Shizuku Diperlukan untuk Fitur ini", Toast.LENGTH_SHORT).show()
            } else {
                Shizuku.requestPermission(REQUEST_CODE_SHIZUKU)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Shizuku belum berjalan/terinstall", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Memanggil Shizuku.newProcess via Reflection karena method newProcess bertipe private di Shizuku API terbaru.
     */
    private fun executeShizukuCommand(command: String) {
        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Izin Shizuku Belum Diberikan", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val cmdArray = arrayOf("sh", "-c", command)
            
            // Mengakses method private 'newProcess' via Reflection Java
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

            binding.tvOutput.text = if (output.isNotEmpty()) output else "Command Executed Successfully"
        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvOutput.text = "Error executing command: ${e.message}"
        }
    }
}
