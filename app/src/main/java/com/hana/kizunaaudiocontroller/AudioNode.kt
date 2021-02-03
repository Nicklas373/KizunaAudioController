package com.hana.kizunaaudiocontroller

class AudioNode {
    // Kernel audio path
    var uhqa_kernel_3_x = "/sys/module/snd_soc_wcd9335/parameters/huwifi_mode"
    var uhqa_kernel_4_x = "/sys/module/wcd9335_dlkm/parameters/huwifi_mode"
    var hph_kernel_3_x = "/sys/module/snd_soc_wcd9330/parameters/high_perf_mode"
    var amp_kernel_3_x = "/sys/module/snd_soc_wcd9335/parameters/low_distort_amp"
    var amp_kernel_4_x = "/sys/module/wcd9335_dlkm/parameters/low_distort_amp"
    var impedance_kernel_3_x = "/sys/module/snd_soc_wcd9xxx/parameters/impedance_detect_en"
    var gating_kernel_3_x = "/sys/module/snd_soc_wcd9335/parameters/dig_core_collapse_enable"
    var gating_kernel_4_x = "/sys/module/wcd9335_dlkm/parameters/dig_core_collapse_enable"
    var uhqa_file = "uhqa.txt"
    var hph_file = "hph.txt"
    var amp_file = "amp.txt"
    var impedance_file = "impedance.txt"
    var gating_file = "gating.txt"
    var kernel_file = "kdump.txt"
    var uhqa_force_file = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/uhqa.txt"
    var hph_force_file = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/hph.txt"
    var amp_force_file = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/amp.txt"
    var impedance_force_file = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/impedance.txt"
    var gating_force_file = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/gating.txt"
    var kernel_dump = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kdump.txt"
}