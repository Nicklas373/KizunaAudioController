package com.hana.kizunaaudiocontroller.audioUtils

import android.content.Context
import android.os.Environment
import com.jaredrummler.android.shell.Shell
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream

class AudioUtils {
    fun readFromFile(context: Context, filename: String): String {
        val bufferedReader: BufferedReader = FileInputStream(
            File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                filename
            )
        ).bufferedReader()
        return bufferedReader.use { it.readText() }
    }

    fun writeToFile(value: String, data: String) {
        Shell.SU.run("echo $value > $data")
    }

    fun dumpFile(filename: String, local_dir: String) {
        Shell.SU.run("cp $filename $local_dir")
    }

    fun dropFile(local_dir: String) {
        Shell.SU.run("rm $local_dir")
    }

    fun backupLogFiles(bak_dir: String, name: String) {
        Shell.SU.run("cd $bak_dir && tar -czf $name *.txt")
    }

    fun exportKernelFile(local_dir: String) {
        Shell.SU.run("uname -r | head -c 3 > $local_dir")
    }

    fun exportFullKernelFile(local_dir: String) {
        Shell.SU.run("uname -a > $local_dir")
    }

    fun exportLogFiles(bak_dir: String, name: String, dest_dir: String) {
        Shell.SU.run("cd $bak_dir && mv $name $dest_dir")
    }
}