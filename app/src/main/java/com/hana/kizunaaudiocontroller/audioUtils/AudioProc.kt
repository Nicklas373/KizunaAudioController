package com.hana.kizunaaudiocontroller.audioUtils

import com.jaredrummler.android.shell.Shell

class AudioProc {

    // Audio Depth Variable
    private var pcmFloat = "(AUDIO_FORMAT_PCM_FLOAT)"
    private var pcm8Bit = "(AUDIO_FORMAT_PCM_8_BIT)"
    private var pcm16Bit = "(AUDIO_FORMAT_PCM_16_BIT)"
    private var pcm24Bit = "(AUDIO_FORMAT_PCM_24_BIT_PACKED)"
    private var pcm824Bit = "(AUDIO_FORMAT_PCM_8_24_BIT)"
    private var pcm32Bit = "(AUDIO_FORMAT_PCM_32_BIT)"
    private var resFloat = "PCM Float"
    private var res8Bit = "8 Bit"
    private var res16Bit = "16 Bit"
    private var res24Bit = "24 Bit"
    private var res824Bit = "8.24 Bit"
    private var res32Bit = "32 Bit"

    fun kaoIOHandle(type: String, aClient: String, tail: String, ioClient: String) {
        Shell.SU.run("grep -w $type $aClient | tail -c +$tail > $ioClient")
    }

    fun kaoProcInt(type: String, aClient: String, sed: String, proc: String) {
        Shell.SU.run("grep -w $type $aClient | sed -n '$sed p' | sed 's/[^0-9,.]*//g' > $proc")
    }

    fun kaoProcString(type: String, aClient: String, sed: String, cut: String, proc: String) {
        Shell.SU.run("grep -w $type $aClient | sed -n '$sed p' | sed 's/^.\\{$cut\\}//'  > $proc")
    }

    fun kaoExpensiveProc(type: String, aClient: String, sed: String, tail: String) {
        Shell.SU.run("grep -w $type $aClient | sed -n '$sed p' | tail -c $tail")
    }

    fun kaoBitDetect(input: String): String {
        var out = "NULL"
        when (input) {
            pcmFloat -> out = resFloat
            pcm8Bit -> out = res8Bit
            pcm16Bit -> out = res16Bit
            pcm24Bit -> out = res24Bit
            pcm824Bit -> out = res824Bit
            pcm32Bit -> out = res32Bit
        }
        return out
    }
}