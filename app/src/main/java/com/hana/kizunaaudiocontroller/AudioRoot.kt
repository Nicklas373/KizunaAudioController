package com.hana.kizunaaudiocontroller

import java.io.DataOutputStream
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class AudioRoot {
    private val execute = AtomicBoolean()
    fun checkRooted(): Boolean {
        try {
            val p = Runtime.getRuntime().exec("su", null, File("/"))
            val os = DataOutputStream(p.outputStream)
            os.writeBytes("pwd\n")
            os.writeBytes("exit\n")
            os.flush()
            p.waitFor()
            p.destroy()
        } catch (e: Exception) {
            return false
        }
        return true
    }

    fun run(task: Runnable) {
        if (execute.get()) return
        if (execute.compareAndSet(false, true)) {
            task.run()
        }
    }
}