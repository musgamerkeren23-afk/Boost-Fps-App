package com.example.boostfps

object FpsBoosterEngine {

    fun applyFpsBoost(): Boolean {
        return try {
            // Jalankan perintah optimasi via ShizukuManager
            val cmd1 = ShizukuManager.executeCommand("echo 3 > /proc/sys/vm/drop_caches")
            val cmd2 = ShizukuManager.executeCommand("settings put global window_animation_scale 0.5")
            val cmd3 = ShizukuManager.executeCommand("settings put global transition_animation_scale 0.5")
            
            cmd1 != null && cmd2 != null && cmd3 != null
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
