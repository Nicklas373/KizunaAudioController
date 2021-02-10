package com.hana.kizunaaudiocontroller

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.util.*


class AudioInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_info)

        // Bind controller
        val cv_title: CardView = findViewById(R.id.cv_app_menu_2)
        val audio_out: TextView = findViewById(R.id.audio_out)
        val audio_pid: TextView = findViewById(R.id.audio_pid)
        val audio_standby: TextView = findViewById(R.id.audio_standby)
        val audio_sample_rate: TextView = findViewById(R.id.audio_sample_rate)
        val audio_hal_frame: TextView = findViewById(R.id.audio_hal_frame)
        val audio_hal_format: TextView = findViewById(R.id.audio_hal_format)
        val audio_channel_count: TextView = findViewById(R.id.audio_channel_count)
        val audio_channel_mask: TextView = findViewById(R.id.audio_channel_mask)
        val audio_format: TextView = findViewById(R.id.audio_format)
        val audio_frame: TextView = findViewById(R.id.audio_frame)

        // Hide title bar
        Objects.requireNonNull(supportActionBar)?.hide()

        // Lock rotation to potrait by default
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        // Set an animation transition
        window.enterTransition = Explode()
        window.returnTransition = Fade()

        // Export required class object
        val ai = AudioInfo()
        val an = AudioNode()
        val ap = AudioProc()
        val au = AudioUtils()

        // Begin Audio Out Detection
        val state_1 = " (DIRECT)"
        val state_2 = "(MIXER)"
        val state_3 = "1 (DIRECT)"
        val state_4 = "(DIRECT)"

        // Begin bash processing
        ai.MediaFlinger(an.mediaflinger_dump)
        ai.LibraryEffects(an.mediaflinger_dump, an.unfiltered_library, an.library)

        // Begin bash processing for specific condition
        ai.KAOAudioInit("DIRECT", an.mediaflinger_dump, "1", an.kao_audio_init)
        val AudioState = au.readFromFile(this, an.kao_init_file).trim()
        if (AudioState.equals("-")) {
            ai.KAOAudioInitExt("MIXER", an.mediaflinger_dump, "1", "+64", an.kao_audio_init)
            ai.KAOLimitAudioClient("MIXER", an.mediaflinger_dump, "1", "+3", an.kao_aclient_top_limit)
            ai.KAOLimitAudioClient("Suspended", an.mediaflinger_dump, "1", "+3", an.kao_aclient_bottom_limit)
            val KAOTopLimit = au.readFromFile(this, an.kao_top_limit_file).toInt().plus(1)
            val KAOBottomLimit = au.readFromFile(this, an.kao_bottom_limit_file).toInt().minus(2)
            ai.KAOAudioClient(KAOTopLimit, KAOBottomLimit, an.mediaflinger_dump, an.kao_aclient)
            ap.KAOProcString("Output", an.kao_aclient, "1", "20", an.kao_out_stream_init)
        } else {
            ai.KAOAudioInitExt("DIRECT", an.mediaflinger_dump, "1", "+66", an.kao_audio_init)
            val AudioStateAlt = au.readFromFile(this, an.kao_init_file).trim()
            if (state_1.equals(AudioStateAlt) || state_3.equals(AudioStateAlt) || state_4.equals(AudioStateAlt)) {
                ai.KAOLimitAudioClient("DIRECT", an.mediaflinger_dump, "1", "+3", an.kao_aclient_top_limit)
                ai.KAOLimitAudioClient("DIRECT", an.mediaflinger_dump, "2", "+3", an.kao_aclient_bottom_limit)
                val KAOTopLimit = au.readFromFile(this, an.kao_top_limit_file).toInt().plus(1)
                val KAOBottomLimit = au.readFromFile(this, an.kao_bottom_limit_file).toInt()
                ai.KAOAudioClient(KAOTopLimit, KAOBottomLimit, an.mediaflinger_dump, an.kao_aclient)
                ap.KAOProcString("Output", an.kao_aclient, "1", "20", an.kao_out_stream_init)
            } else if (AudioStateAlt.equals(state_2, false)) {
                ai.KAOLimitAudioClient("MIXER", an.mediaflinger_dump, "1", "+3", an.kao_aclient_top_limit)
                ai.KAOLimitAudioClient("Suspended", an.mediaflinger_dump, "1", "+3", an.kao_aclient_bottom_limit)
                val KAOTopLimit = au.readFromFile(this, an.kao_top_limit_file).toInt().plus(1)
                val KAOBottomLimit = au.readFromFile(this, an.kao_bottom_limit_file).toInt().minus(2)
                ai.KAOAudioClient(KAOTopLimit, KAOBottomLimit, an.mediaflinger_dump, an.kao_aclient)
                ap.KAOProcString("Output", an.kao_aclient, "1", "20", an.kao_out_stream_init)
             }
       }

        ap.KAOIOHandle("I/O", an.kao_aclient, "13", an.kao_io_init)
        ap.KAOProcString("Standby", an.kao_aclient, "1", "9", an.kao_standby_init)
        ap.KAOProcInt("rate", an.kao_aclient, "1", an.kao_smp_rate_init)
        ap.KAOProcInt("count", an.kao_aclient, "1", an.kao_frm_cnt_init)
        ap.KAOProcString("format", an.kao_aclient, "1", "16", an.kao_frm_bit_init)
        ap.KAOProcInt("Channel", an.kao_aclient, "1", an.kao_chn_cnt_init)
        ap.KAOProcString("Channel", an.kao_aclient, "2", "25", an.kao_chn_mask_init)
        ap.KAOProcString("Processing", an.kao_aclient, "1", "22", an.kao_prc_frm_init)
        ap.KAOProcInt("Processing", an.kao_aclient, "2", an.kao_prc_bit_init)
        audio_out.setText(au.readFromFile(this, an.kao_out_stream_init_file))
        audio_pid.setText(au.readFromFile(this, an.kao_io_init_file))
        audio_standby.setText(au.readFromFile(this, an.kao_standby_init_file))
        audio_sample_rate.setText(au.readFromFile(this, an.kao_rate_init_file))
        audio_hal_frame.setText(au.readFromFile(this, an.kao_frm_cnt_init_file))
        audio_hal_format.setText(au.readFromFile(this, an.kao_frm_bit_init_file))
        audio_channel_count.setText(au.readFromFile(this, an.kao_chn_cnt_init_file))
        audio_channel_mask.setText(au.readFromFile(this, an.kao_chn_mask_init_file))
        audio_format.setText(au.readFromFile(this, an.kao_prc_frm_init_file))
        audio_frame.setText(au.readFromFile(this, an.kao_prc_bit_init_file))
    }
}