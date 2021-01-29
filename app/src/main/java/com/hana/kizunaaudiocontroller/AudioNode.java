package com.hana.kizunaaudiocontroller;

public class AudioNode {

    // Kernel audio path
    public String uhqa_kernel_3_x = "/sys/module/snd_soc_wcd9335/parameters/huwifi_mode";
    public String uhqa_kernel_4_x = "/sys/module/wcd9335_dlkm/parameters/huwifi_mode";
    public String hph_kernel_3_x = "/sys/module/snd_soc_wcd9330/parameters/high_perf_mode";
    public String uhqa_file = "uhqa.txt";
    public String hph_file = "hph.txt";
    public String kernel_file = "kdump.txt";
    public String uhqa_force_file = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/uhqa.txt";
    public String hph_force_file = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/hph.txt";
    public String kernel_dump = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kdump.txt";
}
