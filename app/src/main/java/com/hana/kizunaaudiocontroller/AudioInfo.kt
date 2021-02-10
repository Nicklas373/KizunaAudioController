package com.hana.kizunaaudiocontroller

import android.util.Log
import com.jaredrummler.android.shell.Shell

class AudioInfo {
    fun MediaFlinger(data: String) {
        try {
            val dump = Shell.SU.run("dumpsys media.audio_flinger > $data")
        } catch (e: Exception) {
            Log.e("Exception", "File dump failed: $e")
        }
    }

    fun LibraryEffects(aflinger: String, unfiltered_library:String, library:String){
        try {
            val dump = Shell.SU.run("grep -w Library $aflinger > $unfiltered_library && sed -i -e 's/ Library//g' $unfiltered_library && sed 's/[^ ]* //' $unfiltered_library > $library")
        } catch (e: Exception) {
            Log.e("Exception", "File dump failed: $e")
        }
    }

    fun LimitAudioClient(aflinger: String, filter: String, trim: String, aclient:String){
        try {
            val dump = Shell.SU.run("grep -n $filter $aflinger | sed 's/.//$trim+g' > $aclient")
        } catch (e: Exception) {
            Log.e("Exception", "File dump failed: $e")
        }
    }

    fun CurrentAudioClient(top_limit:String, bottom_limit:String, aflinger:String, aclient:String){
        try {
            val dump = Shell.SU.run("sed -n '$top_limit,$bottom_limit+p' $aflinger | sed 's/[^a-z,.]*//g' > $aclient")
        } catch (e: Exception) {
            Log.e("Exception", "File dump failed: $e")
        }
    }

    fun KAOAudioInit(type: String, aflinger: String,  sed: String, audio: String){
        try {
            val dump = Shell.SU.run("grep -w $type $aflinger | sed -n '$sed p' | cut -c 1 > $audio")
        } catch (e: Exception) {
            Log.e("Exception", "File dump failed: $e")
        }
    }

    fun KAOAudioInitExt(type: String, aflinger: String,  sed: String, tail: String, audio: String){
        try {
            val dump = Shell.SU.run("grep -w $type $aflinger | sed -n '$sed p' | tail -c $tail | sed 's/.\$//' > $audio")
        } catch (e: Exception) {
            Log.e("Exception", "File dump failed: $e")
        }
    }

    fun KAOLimitAudioClient(type: String, aflinger: String, sed: String, trim: String, aclient: String){
        try {
            val dump = Shell.SU.run("grep -n $type $aflinger | sed -n '$sed p' | head -c$trim | tr -d \'[:space:]' > $aclient")
        } catch (e: Exception) {
            Log.e("Exception", "File dump failed: $e")
        }
    }

    fun KAOAudioClient(top_limit: Int, bottom_limit: Int, aflinger: String, dir: String){
        try {
            val dump = Shell.SU.run("sed -n '$top_limit,$bottom_limit p' $aflinger | sed 's/^[[:space:]]*//' > $dir")
        } catch (e: Exception) {
            Log.e("Exception", "File dump failed: $e")
        }
    }

}