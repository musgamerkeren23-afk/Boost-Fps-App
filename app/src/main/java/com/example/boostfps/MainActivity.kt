package com.example.boostfps // Sesuaikan dengan package name project kamu

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.boostfps.databinding.ActivityMainBinding
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val SHIZUKU_CODE = 1001

    private val onRequestPermissionResultListener =
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode == SHIZUKU_CODE) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Akses Shizuku Diberikan!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Akses Shizuku Ditolak!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inisialisasi ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Register listener Shizuku
        Shizuku.addRequestPermissionResultListener(onRequestPermissionResultListener)

        setupUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(onRequestPermissionResultListener)
    }

    private fun setupUI() {
        // Tombol Request Shizuku
        binding.btnRequestShizuku?.setOnClickListener {
            checkAndRequestShizukuPermission()
        }

        // Tombol Quick Boost
        binding.btnQuickBoost?.setOnClickListener {
            quickBoost()
        }

        // Tombol Resolution 720p
        binding.btnRes720p?.setOnClickListener {
            setResolution720p()
        }

        // Tombol Fast Wi-Fi Scan
        binding.btnFastWifi?.setOnClickListener {
            setFastWifiScan()
        }
    }

    private fun isShizukuAvailable(): Boolean {
        return try {
            Shizuku.pingBinder()
        } catch (e: Exception) {
            false
        }
    }

    private fun checkAndRequestShizukuPermission() {
        if (!isShizukuAvailable()) {
            Toast.makeText(this, "Shizuku belum berjalan/terinstall!", Toast.LENGTH_SHORT).show()
            return
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Shizuku sudah aktif dan siap digunakan", Toast.LENGTH_SHORT).show()
        } else {
            Shizuku.requestPermission(SHIZUKU_CODE)
        }
    }

    // --- Fungsi Helper / Utilitas Optimization ---

    private fun quickBoost() {
        if (!isShizukuAvailable()) {
            Toast.makeText(this, "Shizuku belum aktif!", Toast.LENGTH_SHORT).show()
            return
        }
        // Tempatkan perintah shell boost kamu di sini
        executeShellCommand("settings put global process_limit 10")
        Toast.makeText(this, "Quick Boost Berhasil!", Toast.LENGTH_SHORT).show()
    }

    private fun setResolution720p() {
        if (!isShizukuAvailable()) {
            Toast.makeText(this, "Shizuku belum aktif!", Toast.LENGTH_SHORT).show()
            return
        }
        executeShellCommand("wm size 720x1600") // Sesuaikan rasio layar target
        Toast.makeText(this, "Resolusi diubah ke 720p", Toast.LENGTH_SHORT).show()
    }

    private fun setFastWifiScan() {
        if (!isShizukuAvailable()) {
            Toast.makeText(this, "Shizuku belum aktif!", Toast.LENGTH_SHORT).show()
            return
        }
        executeShellCommand("settings put global wifi_scan_throttle_enabled 0")
        Toast.makeText(this, "Wi-Fi Scan Throttling Dimatikan", Toast.LENGTH_SHORT).show()
    }

    private fun executeShellCommand(command: String) {
        try {
            Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal menjalankan perintah: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
