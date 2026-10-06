package com.example.boostfps

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.tabs.TabLayout
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var tabLayout: TabLayout
    
    // Layout Tab Containers
    private lateinit var tabDashboard: View
    private lateinit var tabConfig: View
    private lateinit var tabScript: View
    private lateinit var tabSettings: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        tabLayout = findViewById(R.id.tabLayout)

        tabDashboard = findViewById(R.id.tabDashboard)
        tabConfig = findViewById(R.id.tabConfig)
        tabScript = findViewById(R.id.tabScript)
        tabSettings = findViewById(R.id.tabSettings)

        setupTabs()
        setupDashboardUI()
        setupConfigUI()
        setupScriptUI()
        setupSettingsUI()

        checkShizukuStatus()
    }

    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Dashboard"))
        tabLayout.addTab(tabLayout.newTab().setText("Config"))
        tabLayout.addTab(tabLayout.newTab().setText("Script"))
        tabLayout.addTab(tabLayout.newTab().setText("Settings"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabDashboard.visibility = View.GONE
                tabConfig.visibility = View.GONE
                tabScript.visibility = View.GONE
                tabSettings.visibility = View.GONE

                when (tab?.position) {
                    0 -> tabDashboard.visibility = View.VISIBLE
                    1 -> tabConfig.visibility = View.VISIBLE
                    2 -> tabScript.visibility = View.VISIBLE
                    3 -> tabSettings.visibility = View.VISIBLE
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun checkShizukuStatus() {
        if (Shizuku.pingBinder()) {
            if (Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                statusText.text = "Status Shizuku: Terhubung & Diizinkan ✅"
            } else {
                statusText.text = "Status Shizuku: Meminta Izin..."
                Shizuku.requestPermission(0)
            }
        } else {
            statusText.text = "Status Shizuku: Tidak Aktif ❌"
        }
    }

    private fun setupDashboardUI() {
        findViewById<Button>(R.id.btnQuickBoost).setOnClickListener {
            if (FpsBoosterEngine.applyQuickBoost()) {
                Toast.makeText(this, "Quick Boost Berhasil!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal Boost. Cek Shizuku!", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<MaterialSwitch>(R.id.switchFastWifi).setOnCheckedChangeListener { _, isChecked ->
            FpsBoosterEngine.setFastWifi(isChecked)
        }

        findViewById<MaterialSwitch>(R.id.switchForceGpu).setOnCheckedChangeListener { _, isChecked ->
            FpsBoosterEngine.setForceGpu(isChecked)
        }
    }

    private fun setupConfigUI() {
        val inputFpsCap = findViewById<EditText>(R.id.inputFpsCap)
        findViewById<Button>(R.id.btnApplyFpsCap).setOnClickListener {
            val fpsStr = inputFpsCap.text.toString()
            if (fpsStr.isNotEmpty()) {
                val fps = fpsStr.toInt()
                FpsBoosterEngine.setFpsCap(fps)
                Toast.makeText(this, "FPS Cap diubah ke $fps FPS", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnRes720p).setOnClickListener {
            FpsBoosterEngine.setResolutionHD()
            Toast.makeText(this, "Resolusi diubah ke 720p", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnResReset).setOnClickListener {
            FpsBoosterEngine.resetResolution()
            Toast.makeText(this, "Resolusi di-reset", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupScriptUI() {
        val inputScript = findViewById<EditText>(R.id.inputCustomScript)
        findViewById<Button>(R.id.btnRunScript).setOnClickListener {
            val script = inputScript.text.toString()
            if (script.isNotEmpty()) {
                val result = ShizukuManager.executeCommand(script)
                if (result != null) {
                    Toast.makeText(this, "Script Berhasil Dieksekusi!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Gagal Mengeksekusi Script", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupSettingsUI() {
        findViewById<Button>(R.id.btnRecheckShizuku).setOnClickListener {
            checkShizukuStatus()
        }

        findViewById<Button>(R.id.btnResetAll).setOnClickListener {
            FpsBoosterEngine.resetAll()
            Toast.makeText(this, "Semua Pengaturan Di-reset", Toast.LENGTH_SHORT).show()
        }
    }
}
