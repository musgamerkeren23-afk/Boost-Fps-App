package com.example.boostfps

object FpsBoosterEngine {

    fun applyQuickBoost(): Boolean {
        return try {
            ShizukuManager.executeCommand("settings put global window_animation_scale 0.5")
            ShizukuManager.executeCommand("settings put global transition_animation_scale 0.5")
            ShizukuManager.executeCommand("settings put global animator_duration_scale 0.5")
            ShizukuManager.executeCommand("cmd package compile -m speed-profile -a")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun setFpsCap(fps: Int): Boolean {
        return try {
            ShizukuManager.executeCommand("settings put system peak_refresh_rate $fps.0")
            ShizukuManager.executeCommand("settings put system user_refresh_rate $fps.0")
            ShizukuManager.executeCommand("settings put global peak_refresh_rate $fps.0")
            true
        } catch (e: Exception) {
            false
        }
    }

    fun setFastWifi(enabled: Boolean): Boolean {
        val mode = if (enabled) "1" else "0"
        return ShizukuManager.executeCommand("settings put global wifi_scan_always_enabled $mode") != null
    }

    fun setForceGpu(enabled: Boolean): Boolean {
        // Langsung jalankan command tanpa menyimpan variabel gantung
        return ShizukuManager.executeCommand("setprop debug.composition.type gpu") != null
    }

    fun setResolutionHD(): Boolean {
        return ShizukuManager.executeCommand("wm size 720x1280") != null
    }

    fun resetResolution(): Boolean {
        return ShizukuManager.executeCommand("wm size reset") != null
    }

    fun resetAll(): Boolean {
        ShizukuManager.executeCommand("settings put global window_animation_scale 1.0")
        ShizukuManager.executeCommand("settings put global transition_animation_scale 1.0")
        ShizukuManager.executeCommand("settings put global animator_duration_scale 1.0")
        ShizukuManager.executeCommand("wm size reset")
        return true
    }
}
