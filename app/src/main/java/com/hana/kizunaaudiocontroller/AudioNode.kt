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
    var audio_client_tl_init_file = "audio_client_tl_init.txt"
    var audio_client_bl_init_file = "audio_client_bl_init.txt"
    var audio_client_init_file = "audio_client_init.txt"
    var library_files = "library.txt"
    var kao_init_file = "kao_audio_init.txt"
    var kao_top_limit_file = "kao_aclient_tl.txt"
    var kao_bottom_limit_file = "kao_aclient_bl.txt"
    var kao_io_init_file = "kao_io.txt"
    var kao_standby_init_file = "kao_standby.txt"
    var kao_rate_init_file = "kao_smp_rate.txt"
    var kao_frm_cnt_init_file = "kao_frm_cnt.txt"
    var kao_frm_bit_init_file = "kao_frm_bit.txt"
    var kao_chn_cnt_init_file = "kao_chn_cnt.txt"
    var kao_chn_mask_init_file = "kao_chn_mask.txt"
    var kao_prc_bit_init_file = "kao_prc_bit.txt"
    var kao_prc_frm_init_file = "kao_prc_frm.txt"
    var kao_out_stream_init_file = "kao_out_stream.txt"
    var kao_backup_dir = "/sdcard/Download"
    var kao_backup_files = "KAO_BACKUP.gz"
    var kao_files = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/"
    var uhqa_force_file = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/uhqa.txt"
    var hph_force_file = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/hph.txt"
    var amp_force_file = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/amp.txt"
    var impedance_force_file = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/impedance.txt"
    var gating_force_file = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/gating.txt"
    var kernel_dump = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kdump.txt"
    var mediaflinger_dump = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/afl.txt"
    var unfiltered_library = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/un_library.txt"
    var library = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/library.txt"
    var audio_client_tl_init = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/audio_client_tl_init.txt"
    var audio_client_bl_init = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/audio_client_bl_init.txt"
    var audio_client_init = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/audio_client_init.txt"
    var kao_audio_init = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_audio_init.txt"
    var kao_aclient = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_aclient.txt"
    var kao_aclient_top_limit = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_aclient_tl.txt"
    var kao_aclient_bottom_limit = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_aclient_bl.txt"
    var kao_io_init = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_io.txt"
    var kao_standby_init = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_standby.txt"
    var kao_smp_rate_init = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_smp_rate.txt"
    var kao_frm_cnt_init = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_frm_cnt.txt"
    var kao_frm_bit_init = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_frm_bit.txt"
    var kao_chn_cnt_init = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_chn_cnt.txt"
    var kao_chn_mask_init = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_chn_mask.txt"
    var kao_prc_bit_init = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_prc_bit.txt"
    var kao_prc_frm_init = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_prc_frm.txt"
    var kao_out_stream_init = "/sdcard/Android/data/com.hana.kizunaaudiocontroller/files/Download/kao_out_stream.txt"
}