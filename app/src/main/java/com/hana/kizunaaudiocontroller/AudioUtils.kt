package com.hana.kizunaaudiocontroller

import android.content.Context
import android.os.Environment
import android.util.Log
import com.jaredrummler.android.shell.Shell
import java.io.*

class AudioUtils {
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

    companion object {
        fun readFromFile(context: Context, filename: String?): String? {
            var line: String? = null
            try {
                val fileInputStream = FileInputStream(File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename))
                val inputStreamReader = InputStreamReader(fileInputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line + System.getProperty("line.separator"))
                }
                fileInputStream.close()
                line = stringBuilder.toString()
                bufferedReader.close()
            } catch (fnfe: IOException) {
                Log.e("Exception", "File not found $fnfe")
            }
            return line
        }
    }
}