package com.example.boostfps

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.boostfps.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTabs()
        setupListeners()
        checkShizukuStatus()
    }

    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Dashboard"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Config"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Script"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Settings"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                hideAllTabs()
                when (tab?.position) {
                    0 -> binding.tabDashboard.visibility = View.VISIBLE
                    1 -> binding.tabConfig.visibility = View.VISIBLE
                    2 -> binding.tabScript.visibility = View.VISIBLE
                    3 -> binding.tabSettings.visibility = View.VISIBLE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun hideAllTabs() {
        binding.tabDashboard.visibility = View.GONE
        binding.tabConfig.visibility = View.GONE
        binding.tabScript.visibility = View.GONE
        binding.tabSettings.visibility = View.GONE
    }

    private fun setupListeners() {
        // Switch Wi-Fi - Hanya merespons jika disentuh langsung oleh user
        binding.switchFastWifi.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                val success = FpsBoosterEngine.setFastWifiScan(isChecked)
                showToast(if (success) "Fast Wi-Fi Scan diubah!" else "Gagal mengubah Fast Wi-Fi")
            }
        }

        // Switch Force GPU
        binding.switchForceGpu.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                val success = FpsBoosterEngine.setForceGpu(isChecked)
                showToast(if (success) "Force GPU Composition diubah!" else "Gagal mengubah Force GPU")
            }
        }

        // Tombol Quick Boost
        binding.btnQuickBoost.setOnClickListener {
            val success = FpsBoosterEngine.quickBoost()
            showToast(if (success) "Quick Boost Berhasil!" else "Gagal mengeksekusi Quick Boost")
        }

        // Tombol Config FPS & Resolusi
        binding.btnApplyFpsCap.setOnClickListener {
            val fpsText = binding.inputFpsCap.text.toString()
            if (fpsText.isNotEmpty()) {
                val success = FpsBoosterEngine.setFpsCap(fpsText.toInt())
                showToast(if (success) "FPS Cap diset ke $fpsText" else "Gagal set FPS Cap")
            } else {
                showToast("Masukkan angka FPS terlebih dahulu")
            }
        }

        binding.btnRes720p.setOnClickListener {
            val success = FpsBoosterEngine.setResolution720p()
            showToast(if (success) "Resolusi diubah ke 720p" else "Gagal mengubah resolusi")
        }

        binding.btnResReset.setOnClickListener {
            val success = FpsBoosterEngine.resetResolution()
            showToast(if (success) "Resolusi dikembalikan ke default" else "Gagal reset resolusi")
        }

        // Tombol Script Custom
        binding.btnRunScript.setOnClickListener {
            val script = binding.inputCustomScript.text.toString()
            if (script.isNotEmpty()) {
                val output = ShizukuManager.executeCommand(script)
                showToast(if (output != null) "Script berhasil dijalankan" else "Gagal menjalankan script")
            } else {
                showToast("Masukkan perintah script dulu")
            }
        }

        // Tombol Settings
        binding.btnRecheckShizuku.setOnClickListener {
            checkShizukuStatus()
        }

        binding.btnResetAll.setOnClickListener {
            FpsBoosterEngine.resetResolution()
            FpsBoosterEngine.setFastWifiScan(false)
            FpsBoosterEngine.setForceGpu(false)
            binding.switchFastWifi.isChecked = false
            binding.switchForceGpu.isChecked = false
            showToast("Semua pengaturan telah di-reset")
        }
    }

    private fun checkShizukuStatus() {
        if (ShizukuManager.isShizukuAvailable()) {
            binding.statusText.text = "Status Shizuku: Terhubung (Aktif)"
            binding.statusText.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            binding.statusText.text = "Status Shizuku: Tidak Terhubung / Butuh Izin"
            binding.statusText.setTextColor(getColor(android.R.color.holo_red_dark))
            ShizukuManager.requestPermission()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
