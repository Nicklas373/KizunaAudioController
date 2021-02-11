package com.hana.kizunaaudiocontroller

import com.jaredrummler.android.shell.Shell

class AudioInfo {
    fun MediaFlinger(data: String) {
       Shell.SU.run("dumpsys media.audio_flinger > $data")
    }

    fun LibraryEffects(aflinger: String, unfiltered_library:String, library:String){
        Shell.SU.run("grep -w Library $aflinger > $unfiltered_library && sed -i -e 's/ Library//g' $unfiltered_library && sed 's/[^ ]* //' $unfiltered_library > $library")
    }

    fun LimitAudioClient(filter: String, aflinger: String, head: String, aclient:String){
        Shell.SU.run("grep -n $filter $aflinger | head -c $head > $aclient")
    }

    fun CurrentAudioClient(top_limit: Int, bottom_limit: Int, aflinger:String, aclient:String){
        Shell.SU.run("sed -n '$top_limit,$bottom_limit p' $aflinger | sed 's/[^a-z,.]*//g' > $aclient")
    }

    fun KAOAudioInit(type: String, aflinger: String,  sed: String, audio: String){
        Shell.SU.run("grep -w $type $aflinger | sed -n '$sed p' | cut -c 1 > $audio")
    }

    fun KAOAudioInitExt(type: String, aflinger: String,  sed: String, tail: String, audio: String){
        Shell.SU.run("grep -w $type $aflinger | sed -n '$sed p' | tail -c $tail | sed 's/.\$//' > $audio")
    }

    fun KAOLimitAudioClient(type: String, aflinger: String, sed: String, trim: String, aclient: String){
        Shell.SU.run("grep -n $type $aflinger | sed -n '$sed p' | head -c$trim | tr -d \'[:space:]' > $aclient")
    }

    fun KAOAudioClient(top_limit: Int, bottom_limit: Int, aflinger: String, dir: String){
        Shell.SU.run("sed -n '$top_limit,$bottom_limit p' $aflinger | sed 's/^[[:space:]]*//' > $dir")
    }

    fun KAOReset(files: String) {
        Shell.SU.run("rm * $files")
    }
}