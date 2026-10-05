package com.example.boostfps

object FpsBoosterEngine {

    // Perintah untuk menyalakan mode Performa FPS
    val enableBoostCommands = arrayOf(
        // Force GPU Rendering & Optimasi Compositer
        "settings put global surface_flinger.force_hw_ui 1",
        "setprop debug.sf.hw 1",
        
        // Nonaktifkan Throttling Ringan & Kinerja Maksimal
        "cmd power set-mode 0", // Performance Mode
        "setprop debug.performance.tuning 1",
        
        // Bebaskan Memory Cache untuk Game
        "sync && echo 3 > /proc/sys/vm/drop_caches"
    )

    // Perintah untuk mengembalikan ke mode Normal
    val disableBoostCommands = arrayOf(
        "settings put global surface_flinger.force_hw_ui 0",
        "cmd power set-mode 1" // Balanced / Normal Mode
    )

    fun applyBoost(): Boolean {
        var success = true
        for (cmd in enableBoostCommands) {
            if (!ShizukuManager.executeCommand(cmd)) {
                success = false
            }
        }
        return success
    }

    fun resetBoost(): Boolean {
        var success = true
        for (cmd in disableBoostCommands) {
            if (!ShizukuManager.executeCommand(cmd)) {
                success = false
            }
        }
        return success
    }
}
