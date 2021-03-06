package com.hana.kizunaaudiocontroller

import android.content.Context
import android.os.Environment
import com.jaredrummler.android.shell.Shell
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream


class AudioUtils {
    fun readFromFile(context: Context, filename: String): String {
            val bufferedReader: BufferedReader = FileInputStream(File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename)).bufferedReader()
            val inputString = bufferedReader.use { it.readText() }
            return inputString
    }

    fun WriteToFile(value: String, data: String) {
       Shell.SU.run("echo $value > $data")
    }

    fun DumpFile(filename: String, local_dir: String) {
       Shell.SU.run("cp $filename $local_dir")
    }

    fun DropFile(local_dir: String) {
      Shell.SU.run("rm $local_dir")
    }

    fun BackupLogFiles(bak_dir: String, name: String) {
        Shell.SU.run("cd $bak_dir && tar -czf $name *.txt")
    }

    fun ExportKernelFile(local_dir: String) {
        Shell.SU.run("uname -r | head -c 3 > $local_dir")
    }

    fun ExportFullKernelFile(local_dir: String) {
        Shell.SU.run("uname -a > $local_dir")
    }

    fun ExportLogFiles(bak_dir: String, name: String, dest_dir: String) {
        Shell.SU.run("cd $bak_dir && mv $name $dest_dir")
    }
}