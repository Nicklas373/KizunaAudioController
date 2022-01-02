package com.hana.kizunaaudiocontroller.audioUtils

import com.jaredrummler.android.shell.Shell

class AudioInfo {
    fun mediaFlinger(data: String) {
        Shell.SU.run("dumpsys media.audio_flinger > $data")
    }

    fun libraryEffects(aFlinger: String, unfiltered_library: String, library: String) {
        Shell.SU.run("grep -w Library $aFlinger > $unfiltered_library && sed -i -e 's/ Library//g' $unfiltered_library && sed 's/[^ ]* //' $unfiltered_library > $library")
    }

    fun limitAudioClient(filter: String, aFlinger: String, head: String, aClient: String) {
        Shell.SU.run("grep -n $filter $aFlinger | head -c $head > $aClient")
    }

    fun currentAudioClient10(
        top_limit: Int,
        bottom_limit: Int,
        aFlinger: String,
        aClient: String
    ) {
        Shell.SU.run("sed -n '$top_limit,$bottom_limit p' $aFlinger | sed 's/^[[:space:]]*//' > $aClient")
    }

    fun currentAudioClient11(
        top_limit: Int,
        bottom_limit: Int,
        aFlinger: String,
        aClient: String
    ) {
        Shell.SU.run("sed -n '$top_limit,$bottom_limit p' $aFlinger | sed 's/[^a-z,.]*//g' > $aClient")
    }

    fun kaoAudioInit(type: String, aFlinger: String, sed: String, audio: String) {
        Shell.SU.run("grep -w $type $aFlinger | sed -n '$sed p' | cut -c 1 > $audio")
    }

    fun kaoAudioInitExt(type: String, aFlinger: String, sed: String, tail: String, audio: String) {
        Shell.SU.run("grep -w $type $aFlinger | sed -n '$sed p' | tail -c $tail | sed 's/.\$//' > $audio")
    }

    fun kaoLimitAudioClient(
        type: String,
        aFlinger: String,
        sed: String,
        trim: String,
        aClient: String
    ) {
        Shell.SU.run("grep -n $type $aFlinger | sed -n '$sed p' | head -c$trim | tr -d \'[:space:]' > $aClient")
    }

    fun kaoAudioClient(top_limit: Int, bottom_limit: Int, aFlinger: String, dir: String) {
        Shell.SU.run("sed -n '$top_limit,$bottom_limit p' $aFlinger | sed 's/^[[:space:]]*//' > $dir")
    }

    fun kaoReset(files: String) {
        Shell.SU.run("rm * $files")
    }
}