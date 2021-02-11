package com.hana.kizunaaudiocontroller

import com.jaredrummler.android.shell.Shell

class AudioProc {

    // Audio Depth Variable
    var pcm_float = "(AUDIO_FORMAT_PCM_FLOAT)"
    var pcm_8_bit = "(AUDIO_FORMAT_PCM_8_BIT)"
    var pcm_16_bit = "(AUDIO_FORMAT_PCM_16_BIT)"
    var pcm_24_bit = "(AUDIO_FORMAT_PCM_24_BIT_PACKED)"
    var pcm_8_24_bit = "(AUDIO_FORMAT_PCM_8_24_BIT)"
    var pcm_32_bit = "(AUDIO_FORMAT_PCM_32_BIT)"
    var res_float = "PCM Float"
    var res_8_bit = "8 Bit"
    var res_16_bit = "16 Bit"
    var res_24_bit = "24 Bit"
    var res_8_24_bit = "8.24 Bit"
    var res_32_bit = "32 Bit"

    fun KAOIOHandle(type: String, aclient: String, tail: String, ioclient: String) {
       Shell.SU.run("grep -w $type $aclient | tail -c +$tail > $ioclient")
    }

    fun KAOProcInt(type: String, aclient: String, sed: String,  proc: String) {
       Shell.SU.run("grep -w $type $aclient | sed -n '$sed p' | sed 's/[^0-9,.]*//g' > $proc")
    }

    fun KAOProcString(type: String, aclient: String, sed: String,  cut: String, proc: String) {
       Shell.SU.run("grep -w $type $aclient | sed -n '$sed p' | sed 's/^.\\{$cut\\}//'  > $proc")
    }

    fun KAOExpensiveProc(type: String, proc: String, aclient: String, sed: String, tail: String) {
      Shell.SU.run("grep -w $type $aclient | sed -n '$sed p' | tail -c $tail")
    }

    fun KAOBitDetect(input: String): String {
        var out = "NULL"
        if (input.equals(pcm_float)) {
            out = res_float
            return out
        } else if (input.equals(pcm_8_bit)) {
            out = res_8_bit
            return out
        } else if (input.equals(pcm_16_bit)) {
            out = res_16_bit
            return out
        } else if (input.equals(pcm_24_bit)) {
            out = res_24_bit
            return out
        } else if (input.equals(pcm_8_24_bit)) {
            out = res_8_24_bit
            return out
        } else if (input.equals(pcm_32_bit)) {
            out = res_32_bit
            return out
        } else {
            return out
        }
    }
}