package com.example.boostfps

object FpsBoosterEngine {

    // 1. Quick FPS Boost (Clean RAM compile app & disable anim)
    fun applyQuickBoost(): Boolean {
        return try {
            ShizukuManager.executeCommand("settings put global window_animation_scale 0.5")
            ShizukuManager.executeCommand("settings put global transition_animation_scale 0.5")
            ShizukuManager.executeCommand("settings put global animator_duration_scale 0.5")
            // Force Compile Speed untuk mengurangi lag/stutter
            ShizukuManager.executeCommand("cmd package compile -m speed-profile -a")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // 2. Set Max Refresh Rate / FPS Cap
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

    // 3. Fast WiFi / Network Mode
    fun setFastWifi(enabled: Boolean): Boolean {
        val valStr = if (enabled) "1" else "0"
        return ShizukuManager.executeCommand("settings put global wifi_scan_always_enabled $valStr") != null
    }

    // 4. Force 2D GPU Rendering
    fun setForceGpu(enabled: Boolean): Boolean {
        val valStr = if (enabled) "true" else "false"
        return ShizukuManager.executeCommand("setprop debug.composition.type gpu") != null
    }

    // 5. Ubah Resolusi Layar ke 720p (HD) untuk FPS lebih tinggi
    fun setResolutionHD(): Boolean {
        return ShizukuManager.executeCommand("wm size 720x1280") != null
    }

    // 6. Reset Resolusi Layar
    fun resetResolution(): Boolean {
        return ShizukuManager.executeCommand("wm size reset") != null
    }

    // 7. Reset Animasi
    fun resetAll(): Boolean {
        ShizukuManager.executeCommand("settings put global window_animation_scale 1.0")
        ShizukuManager.executeCommand("settings put global transition_animation_scale 1.0")
        ShizukuManager.executeCommand("settings put global animator_duration_scale 1.0")
        ShizukuManager.executeCommand("wm size reset")
        return true
    }
}
