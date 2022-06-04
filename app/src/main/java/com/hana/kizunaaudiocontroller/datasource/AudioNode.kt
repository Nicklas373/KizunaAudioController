package com.hana.kizunaaudiocontroller.datasource

import android.annotation.SuppressLint

class AudioNode {
    // Kernel audio path
    var uhqaKernelLegacy = "/sys/module/snd_soc_wcd9335/parameters/huwifi_mode"
    var uhqaKernelUpstream = "/sys/module/wcd9335_dlkm/parameters/huwifi_mode"
    var hphKernelLegacy = "/sys/module/snd_soc_wcd9330/parameters/high_perf_mode"
    var ampKernelLegacy = "/sys/module/snd_soc_wcd9335/parameters/low_distort_amp"
    var ampKernelUpstream = "/sys/module/wcd9335_dlkm/parameters/low_distort_amp"
    var impedanceKernelLegacy = "/sys/module/snd_soc_wcd9xxx/parameters/impedance_detect_en"
    var gatingKernelLegacy = "/sys/module/snd_soc_wcd9335/parameters/dig_core_collapse_enable"
    var gatingKernelUpstream = "/sys/module/wcd9335_dlkm/parameters/dig_core_collapse_enable"
    var uhqaFile = "uhqa.txt"
    var hphFile = "hph.txt"
    var ampFile = "amp.txt"
    var impedanceFile = "impedance.txt"
    var gatingFile = "gating.txt"
    var kernelFile = "kdump.txt"
    var audioClientTlInitFile = "audio_client_tlInit.txt"
    var audioClientBlInitFile = "audio_client_blInit.txt"
    var audioClientInitFile = "audio_clientInit.txt"
    var libraryFiles = "library.txt"
    var kaoInitFile = "kao_audioInit.txt"
    var kaoTopLimitFile = "kao_aclient_tl.txt"
    var kaoBottomLimitFile = "kao_aclient_bl.txt"
    var kaoIoInitFile = "kao_io.txt"
    var kaoStandbyInitFile = "kao_standby.txt"
    var kaoRateInitFile = "kao_smp_rate.txt"
    var kaoFrmCntInitFile = "kao_frm_cnt.txt"
    var kaoFrmBitInitFile = "kao_frm_bit.txt"
    var kaoChnCntInitFile = "kao_chn_cnt.txt"
    var kaoChnMaskInitFile = "kao_chn_mask.txt"
    var kaoPrcBitInitFile = "kao_prc_bit.txt"
    var kaoPrcFrmInitFile = "kao_prc_frm.txt"
    var kaoOutStreamInitFile = "kao_out_stream.txt"
    @SuppressLint("SdCardPath")
    var kaoBackupDir = "/sdcard/Download"
    var kaoBackupFiles = "KAO_BACKUP.gz"
    @SuppressLint("SdCardPath")
    var kaoFiles = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/"
    @SuppressLint("SdCardPath")
    var uhqaForceFile =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/uhqa.txt"
    @SuppressLint("SdCardPath")
    var hphForceFile =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/hph.txt"
    @SuppressLint("SdCardPath")
    var ampForceFile =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/amp.txt"
    @SuppressLint("SdCardPath")
    var impedanceForceFile =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/impedance.txt"
    @SuppressLint("SdCardPath")
    var gatingForceFile =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/gating.txt"
    @SuppressLint("SdCardPath")
    var kernelDump = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kdump.txt"
    @SuppressLint("SdCardPath")
    var mediaflingerDump =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/afl.txt"
    @SuppressLint("SdCardPath")
    var unfilteredLibrary =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/un_library.txt"
    @SuppressLint("SdCardPath")
    var library = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/library.txt"
    @SuppressLint("SdCardPath")
    var audioClientTlInit =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/audio_client_tlInit.txt"
    @SuppressLint("SdCardPath")
    var audioClientBlInit =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/audio_client_blInit.txt"
    @SuppressLint("SdCardPath")
    var audioClientInit =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/audio_clientInit.txt"
    @SuppressLint("SdCardPath")
    var kaoAudioInit =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_audioInit.txt"
    @SuppressLint("SdCardPath")
    var kaoAclient =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_aclient.txt"
    @SuppressLint("SdCardPath")
    var kaoAclientTopLimit =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_aclient_tl.txt"
    @SuppressLint("SdCardPath")
    var kaoAclientBottomLimit =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_aclient_bl.txt"
    @SuppressLint("SdCardPath")
    var kaoIoInit =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_io.txt"
    @SuppressLint("SdCardPath")
    var kaoStandbyInit =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_standby.txt"
    @SuppressLint("SdCardPath")
    var kaoSmpRateInit =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_smp_rate.txt"
    @SuppressLint("SdCardPath")
    var kaoFrmCntInit =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_frm_cnt.txt"
    @SuppressLint("SdCardPath")
    var kaoFrmBitInit =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_frm_bit.txt"
    @SuppressLint("SdCardPath")
    var kaoChnCntInit =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_chn_cnt.txt"
    @SuppressLint("SdCardPath")
    var kaoChnMaskInit =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_chn_mask.txt"
    @SuppressLint("SdCardPath")
    var kaoPrcBitInit =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_prc_bit.txt"
    @SuppressLint("SdCardPath")
    var kaoPrcFrmInit =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_prc_frm.txt"
    @SuppressLint("SdCardPath")
    var kaoOutStreamInit =
        "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_out_stream.txt"
}