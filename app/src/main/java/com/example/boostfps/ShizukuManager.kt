package com.example.boostfps

import rikka.shizuku.Shizuku

object ShizukuManager {

    fun executeCommand(command: String): Process? {
        return try {
            // Menggunakan refleksi untuk mengakses method private newProcess
            val newProcessMethod = Shizuku::class.java.getDeclaredMethod(
                "newProcess",
                Array<String>::class.java,
                Array<String>::class.java,
                String::class.java
            )
            newProcessMethod.isAccessible = true
            
            val cmdArray = arrayOf("sh", "-c", command)
            newProcessMethod.invoke(null, cmdArray, null, null) as Process
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
