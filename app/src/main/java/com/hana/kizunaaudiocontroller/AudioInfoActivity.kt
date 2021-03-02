package com.hana.kizunaaudiocontroller

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import java.util.*


class AudioInfoActivity : AppCompatActivity() {

    lateinit var po: Dialog
    lateinit var title: TextView
    lateinit var title_1: CardView
    lateinit var title_2: CardView
    lateinit var desc: TextView
    lateinit var desc_ant: TextView
    lateinit var desc_ext: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_info)

        // Bind controller
        val cv_title: CardView = findViewById(R.id.cv_app_menu_2)
        val cv_1: CardView = findViewById(R.id.cv_achannel)
        val cv_2: CardView = findViewById(R.id.cv_aformat)
        val cv_3: CardView = findViewById(R.id.cv_asession)
        val cv_4: CardView = findViewById(R.id.cv_astate)
        val cv_5: CardView = findViewById(R.id.cv_ahal)
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
        val audio_flags: TextView = findViewById(R.id.audio_flags)
        val audio_dsp: Button = findViewById(R.id.btn_audio_dsp)
        val audio_session: Button = findViewById(R.id.btn_audio_session)
        val audio_info: Button = findViewById(R.id.button_title)

        // Hide title bar
        Objects.requireNonNull(supportActionBar)?.hide()

        // Lock rotation to potrait by default
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        // Set an animation transition
        window.enterTransition = Explode()
        window.returnTransition = Fade()

        // Sharedprefences begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)

        // Getting sharedpreferences value if exist
        // Configure theme interface
        val night_mode = pref.getBoolean("MODE_NIGHT", false)
        if (night_mode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            val nightColor = Color.parseColor("#2286c3")

            cv_title.setCardBackgroundColor(nightColor)
            cv_1.setCardBackgroundColor(nightColor)
            cv_2.setCardBackgroundColor(nightColor)
            cv_3.setCardBackgroundColor(nightColor)
            cv_4.setCardBackgroundColor(nightColor)
            cv_5.setCardBackgroundColor(nightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            val colorDrawable = ColorDrawable(Color.parseColor("#64b5f6"))
            Objects.requireNonNull(supportActionBar)?.setBackgroundDrawable(colorDrawable)
        }

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

        // Reset old cache at beginning
        ai.KAOReset(an.kao_files)

        // Begin less conditional bash processing
        ai.MediaFlinger(an.mediaflinger_dump)
        ai.LibraryEffects(an.mediaflinger_dump, an.unfiltered_library, an.library)
        ai.LimitAudioClient("Notification", an.mediaflinger_dump, "3", an.audio_client_tl_init)
        ai.LimitAudioClient("Global", an.mediaflinger_dump, "3", an.audio_client_bl_init)
        val aclient_tl = au.readFromFile(this, an.audio_client_tl_init_file).toInt().plus(2)
        val aclient_bl = au.readFromFile(this, an.audio_client_bl_init_file).toInt().minus(1)
        ai.CurrentAudioClient(aclient_tl, aclient_bl, an.mediaflinger_dump, an.audio_client_init)

        // Begin bash processing for specific condition
        ai.KAOAudioInit("DIRECT", an.mediaflinger_dump, "1", an.kao_audio_init)
        val AudioState = au.readFromFile(this, an.kao_init_file).trim()

        /* If AudioState is empty then direct route to use MIXER path instead, but
         * If not then it should check whether value is dash or not, since dash is actually
         * introduced on Android 11 Audio API as "Historical Audio Session", which aren't need
         * to debug, so we should skip and router to MIXER path if this condition happen, however
         * if not then we will re-filter audiostate and correct state after it.
         */
        if (AudioState.isEmpty()) {
            ai.KAOLimitAudioClient("MIXER", an.mediaflinger_dump, "1", "+3", an.kao_aclient_top_limit)
            ai.KAOLimitAudioClient("Suspended", an.mediaflinger_dump, "1", "+3", an.kao_aclient_bottom_limit)
            val KAOTopLimit = au.readFromFile(this, an.kao_top_limit_file).toInt().plus(1)
            val KAOBottomLimit = au.readFromFile(this, an.kao_bottom_limit_file).toInt().minus(2)
            ai.KAOAudioClient(KAOTopLimit, KAOBottomLimit, an.mediaflinger_dump, an.kao_aclient)
            ap.KAOProcString("Output", an.kao_aclient, "1", "20", an.kao_out_stream_init)
        } else if (AudioState.isNotEmpty()) {
            if (AudioState.equals("-")) {
                ai.KAOAudioInitExt("MIXER", an.mediaflinger_dump, "1", "+63", an.kao_audio_init)
                ai.KAOLimitAudioClient("MIXER", an.mediaflinger_dump, "1", "+3", an.kao_aclient_top_limit)
                ai.KAOLimitAudioClient("Suspended", an.mediaflinger_dump, "1", "+3", an.kao_aclient_bottom_limit)
                val KAOTopLimit = au.readFromFile(this, an.kao_top_limit_file).toInt().plus(1)
                val KAOBottomLimit = au.readFromFile(this, an.kao_bottom_limit_file).toInt().minus(2)
                ai.KAOAudioClient(KAOTopLimit, KAOBottomLimit, an.mediaflinger_dump, an.kao_aclient)
                ap.KAOProcString("Output", an.kao_aclient, "1", "20", an.kao_out_stream_init)
            } else {
                ai.KAOAudioInitExt("DIRECT", an.mediaflinger_dump, "1", "+64", an.kao_audio_init)
                val AudioStateAlt = au.readFromFile(this, an.kao_init_file).trim()
                if (state_1.equals(AudioStateAlt) || state_3.equals(AudioStateAlt) || state_4.equals(AudioStateAlt)) {
                    ai.KAOLimitAudioClient("DIRECT", an.mediaflinger_dump, "1", "+3", an.kao_aclient_top_limit)
                    ai.KAOLimitAudioClient("DIRECT", an.mediaflinger_dump, "2", "+3", an.kao_aclient_bottom_limit)
                    val KAOTopLimit = au.readFromFile(this, an.kao_top_limit_file).toInt().plus(1)
                    val KAOBottomLimit = au.readFromFile(this, an.kao_bottom_limit_file).toInt()
                    ai.KAOAudioClient(KAOTopLimit, KAOBottomLimit, an.mediaflinger_dump, an.kao_aclient)
                    ap.KAOProcString("Output", an.kao_aclient, "1", "20", an.kao_out_stream_init)
                } else if (AudioStateAlt.equals(state_2)) {
                    ai.KAOLimitAudioClient("MIXER", an.mediaflinger_dump, "1", "+3", an.kao_aclient_top_limit)
                    ai.KAOLimitAudioClient("Suspended", an.mediaflinger_dump, "1", "+3", an.kao_aclient_bottom_limit)
                    val KAOTopLimit = au.readFromFile(this, an.kao_top_limit_file).toInt().plus(1)
                    val KAOBottomLimit = au.readFromFile(this, an.kao_bottom_limit_file).toInt().minus(2)
                    ai.KAOAudioClient(KAOTopLimit, KAOBottomLimit, an.mediaflinger_dump, an.kao_aclient)
                    ap.KAOProcString("Output", an.kao_aclient, "1", "20", an.kao_out_stream_init)
                }
            }
        }

        // Take several value & Put it on textview
        ap.KAOIOHandle("I/O", an.kao_aclient, "13", an.kao_io_init)
        ap.KAOProcString("Standby", an.kao_aclient, "1", "9", an.kao_standby_init)
        ap.KAOProcInt("rate", an.kao_aclient, "1", an.kao_smp_rate_init)
        ap.KAOProcInt("count", an.kao_aclient, "1", an.kao_frm_cnt_init)
        ap.KAOProcString("format", an.kao_aclient, "1", "16", an.kao_frm_bit_init)
        ap.KAOProcInt("Channel", an.kao_aclient, "1", an.kao_chn_cnt_init)
        ap.KAOProcString("Channel", an.kao_aclient, "2", "25", an.kao_chn_mask_init)
        ap.KAOProcString("Processing", an.kao_aclient, "1", "23", an.kao_prc_frm_init)
        ap.KAOProcInt("Processing", an.kao_aclient, "2", an.kao_prc_bit_init)
        audio_out.setText(au.readFromFile(this, an.kao_out_stream_init_file))
        audio_pid.setText(au.readFromFile(this, an.kao_io_init_file))
        audio_standby.setText(au.readFromFile(this, an.kao_standby_init_file))
        audio_sample_rate.setText(au.readFromFile(this, an.kao_rate_init_file))
        audio_hal_frame.setText(au.readFromFile(this, an.kao_frm_cnt_init_file))
        audio_hal_format.setText(ap.KAOBitDetect(au.readFromFile(this, an.kao_frm_bit_init_file).trim()))
        audio_channel_count.setText(au.readFromFile(this, an.kao_chn_cnt_init_file))
        audio_channel_mask.setText(au.readFromFile(this, an.kao_chn_mask_init_file))
        audio_format.setText(ap.KAOBitDetect(au.readFromFile(this, an.kao_prc_frm_init_file).trim()))
        audio_frame.setText(au.readFromFile(this, an.kao_prc_bit_init_file))
        audio_flags.setText(au.readFromFile(this, an.kao_init_file))

        cv_title.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            val sharedView: View = cv_title
            val transitionName = getString(R.string.app_main_menu_2)
            val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this, sharedView, transitionName)
            startActivity(i, transitionActivityOptions.toBundle())
        }

        audio_dsp.setOnClickListener{
            po = Dialog(this)

            // Call dialog
            ShowPopup(resources.getString(R.string.audio_effects), resources.getString(R.string.audio_effects_details), resources.getString(R.string.audio_effects_list), au.readFromFile(this, an.library_files).trim())
        }

        audio_session.setOnClickListener{
            po = Dialog(this)

            // Call dialog
            ShowPopup(resources.getString(R.string.audio_session), resources.getString(R.string.audio_session_details),resources.getString(R.string.audio_session_list),  au.readFromFile(this, an.audio_client_init_file).trim())
        }

        audio_info.setOnClickListener{
            po = Dialog(this)

            // Call dialog
            ShowPopupAdd(resources.getString(R.string.app_menu_2_info_title), resources.getString(R.string.app_menu_2_info_details),resources.getString(R.string.app_menu_2_info_ext))
        }
    }

    private fun ShowPopup(text_1: String?, text_2: String?, text_3: String?, text_4: String?) {
        po.setContentView(R.layout.activity_pop_up_info)

        title_1 = po.findViewById(R.id.cv_title_1)
        title_2 = po.findViewById(R.id.cv_title_2)
        title = po.findViewById(R.id.text_pop_up_1)
        desc = po.findViewById(R.id.text_pop_up_desc_1)
        desc_ant = po.findViewById(R.id.text_pop_up_desc_2)
        desc_ext = po.findViewById(R.id.text_pop_up_desc_3)
        title.text = text_1
        desc.text = text_2
        desc_ant.text = text_3
        desc_ext.text = text_4

        // Sharedprefences begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)

        // Getting sharedpreferences value if exist
        // Configure theme interface
        val night_mode = pref.getBoolean("MODE_NIGHT", false)
        if (night_mode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            val nightColor = Color.parseColor("#607d8b")
            title_1.setCardBackgroundColor(nightColor)
            title_2.setCardBackgroundColor(nightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            val dayColor = Color.parseColor("#2286c3")
            title_1.setCardBackgroundColor(dayColor)
            title_2.setCardBackgroundColor(dayColor)
        }

        title.setOnClickListener { po.dismiss() }
        po.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        po.show()
    }

    private fun ShowPopupAdd(text_1: String?, text_2: String?, text_3: String) {
        po.setContentView(R.layout.activity_pop_up_info_details)
        title_1 = po.findViewById(R.id.cv_title_1)
        title = po.findViewById(R.id.text_pop_up_1)
        desc = po.findViewById(R.id.text_pop_up_desc_1)
        desc_ext = po.findViewById(R.id.text_pop_up_desc_2)
        title.text = text_1
        desc.text = text_2
        desc_ext.text = text_3

        // Sharedprefences begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)

        // Getting sharedpreferences value if exist
        // Configure theme interface
        val night_mode = pref.getBoolean("MODE_NIGHT", false)
        if (night_mode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            val nightColor = Color.parseColor("#607d8b")
            title_1.setCardBackgroundColor(nightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            val dayColor = Color.parseColor("#2286c3")
            title_1.setCardBackgroundColor(dayColor)
        }

        title.setOnClickListener { po.dismiss() }
        po.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        po.show()
    }
}