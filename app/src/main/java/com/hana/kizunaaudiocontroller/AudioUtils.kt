package com.hana.kizunaaudiocontroller

import android.content.Context
import android.os.Environment
import android.util.Log
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
        try {
            val write = Shell.SU.run("echo $value > $data")
        } catch (e: Exception) {
            Log.e("Exception", "File write failed: $e")
        }
    }

    fun DumpFile(filename: String, local_dir: String) {
        try {
            val dump = Shell.SU.run("cp $filename $local_dir")
        } catch (e: Exception) {
            Log.e("Exception", "File read failed: $e")
        }
    }

    fun DropFile(local_dir: String) {
        try {
            val drop = Shell.SU.run("rm $local_dir")
        } catch (e: Exception) {
            Log.e("Exception", "File drop failed: $e")
        }
    }

    fun ExportKernelFile(local_dir: String) {
        try {
            val rkf = Shell.SU.run("uname -r | head -c 3 > $local_dir")
        } catch (e: Exception) {
            Log.e("Exception", "File drop failed: $e")
        }
    }
}