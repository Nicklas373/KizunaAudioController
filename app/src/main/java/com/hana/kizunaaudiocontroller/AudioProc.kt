package com.hana.kizunaaudiocontroller

import android.util.Log
import com.jaredrummler.android.shell.Shell

class AudioProc {

    fun KAOIOHandle(type: String, aclient: String, tail: String, ioclient: String) {
        try {
            val dump = Shell.SU.run("grep -w $type $aclient | tail -c +$tail > $ioclient")
        } catch (e: Exception) {
            Log.e("Exception", "File dump failed: $e")
        }
    }

    fun KAOProcInt(type: String, aclient: String, sed: String,  proc: String) {
        try {
            val dump = Shell.SU.run("grep -w $type $aclient | sed -n '$sed p' | sed 's/[^0-9,.]*//g' > $proc")
        } catch (e: Exception) {
            Log.e("Exception", "File dump failed: $e")
        }
    }

    fun KAOProcString(type: String, aclient: String, sed: String,  cut: String, proc: String) {
        try {
            val dump = Shell.SU.run("grep -w $type $aclient | sed -n '$sed p' | sed 's/^.\\{$cut\\}//'  > $proc")
        } catch (e: Exception) {
            Log.e("Exception", "File dump failed: $e")
        }
    }

    fun KAOExpensiveProc(type: String, proc: String, aclient: String, sed: String, tail: String) {
        try {
            val dump = Shell.SU.run("grep -w $type $aclient | sed -n '$sed p' | tail -c $tail")
        } catch (e: Exception) {
            Log.e("Exception", "File dump failed: $e")
        }
    }
}