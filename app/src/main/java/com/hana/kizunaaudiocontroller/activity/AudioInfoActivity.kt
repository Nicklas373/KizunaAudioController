package com.hana.kizunaaudiocontroller.activity

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.hana.kizunaaudiocontroller.*
import com.hana.kizunaaudiocontroller.audioUtils.AudioInfo
import com.hana.kizunaaudiocontroller.datasource.AudioNode
import com.hana.kizunaaudiocontroller.audioUtils.AudioProc
import com.hana.kizunaaudiocontroller.audioUtils.AudioUtils
import com.hana.kizunaaudiocontroller.databinding.ActivityAudioInfoBinding
import com.hana.kizunaaudiocontroller.databinding.ActivityPopUpInfoBinding
import com.hana.kizunaaudiocontroller.databinding.ActivityPopUpInfoDetailsBinding


class AudioInfoActivity : AppCompatActivity() {

    // Separate environment
    private lateinit var po: Dialog
    private lateinit var title: TextView
    private lateinit var desc: TextView
    private lateinit var descExt: TextView

    // Binding
    private lateinit var binding: ActivityAudioInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAudioInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Set an animation transition
        window.enterTransition = Explode()
        window.returnTransition = Fade()

        // sharedPreference begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)

        // Getting sharedPreference value if exist
        // Configure theme interface
        val nightMode = pref.getBoolean("MODE_NIGHT", false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            val nightColor = Color.parseColor("#2286c3")

            binding.cvAppMenu2.setCardBackgroundColor(nightColor)
            binding.cvAchannel.setCardBackgroundColor(nightColor)
            binding.cvAformat.setCardBackgroundColor(nightColor)
            binding.cvAsession.setCardBackgroundColor(nightColor)
            binding.cvAstate.setCardBackgroundColor(nightColor)
            binding.cvAhal.setCardBackgroundColor(nightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            val colorDrawable = ColorDrawable(Color.parseColor("#64b5f6"))
            supportActionBar?.setBackgroundDrawable(colorDrawable)
        }

        // Export required class object
        val ai = AudioInfo()
        val an = AudioNode()
        val ap = AudioProc()
        val au = AudioUtils()

        // Begin Audio Out Detection
        val state1 = " (DIRECT)"
        val state2 = "(MIXER)"
        val state3 = "1 (DIRECT)"
        val state4 = "(DIRECT)"

        // Reset old cache at beginning
        ai.kaoReset(an.kao_files)

        // Begin less conditional bash processing
        ai.mediaFlinger(an.mediaflinger_dump)
        ai.libraryEffects(an.mediaflinger_dump, an.unfiltered_library, an.library)
        ai.limitAudioClient("Notification", an.mediaflinger_dump, "3", an.audio_client_tl_init)
        ai.limitAudioClient("Global", an.mediaflinger_dump, "3", an.audio_client_bl_init)

        /* Show current app or services that was using audio services in the system
         *
         * NOTE: Android 10 or below doesn't support to show directly app name or service name
         * on Notification or Global usage in audioflinger services, instead show only pid number
         * without pid name just like on Android 11.
         *
         * So create decision to separate based on Android version to show prepare pid with number
         * or pid with name from the services in audioflinger.
         */
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.Q) {
            val aClientTl = au.readFromFile(this, an.audio_client_tl_init_file).toInt().plus(3)
            val aClientBl = au.readFromFile(this, an.audio_client_bl_init_file).toInt().minus(1)
            ai.currentAudioClient10(
                aClientTl,
                aClientBl,
                an.mediaflinger_dump,
                an.audio_client_init
            )
        } else {
            val aClientTl = au.readFromFile(this, an.audio_client_tl_init_file).toInt().plus(2)
            val aClientBl = au.readFromFile(this, an.audio_client_bl_init_file).toInt().minus(1)
            ai.currentAudioClient11(
                aClientTl,
                aClientBl,
                an.mediaflinger_dump,
                an.audio_client_init
            )
        }

        // Begin bash processing for specific condition
        ai.kaoAudioInit("DIRECT", an.mediaflinger_dump, "1", an.kao_audio_init)
        val audioState = au.readFromFile(this, an.kao_init_file).trim()

        /* If AudioState is empty then direct route to use MIXER path instead, but
         * If not then it should check whether value is dash or not, since dash is actually
         * introduced on Android 11 Audio API as "Historical Audio Session", which aren't need
         * to debug, so we should skip and router to MIXER path if this condition happen, however
         * if not then we will re-filter audio state and correct state after it.
         */
        if (audioState.isEmpty()) {
            ai.kaoLimitAudioClient(
                "MIXER",
                an.mediaflinger_dump,
                "1",
                "+3",
                an.kao_aclient_top_limit
            )
            ai.kaoLimitAudioClient(
                "Suspended",
                an.mediaflinger_dump,
                "1",
                "+3",
                an.kao_aclient_bottom_limit
            )
            val kaoTopLimit = au.readFromFile(this, an.kao_top_limit_file).toInt().plus(1)
            val kaoBottomLimit = au.readFromFile(this, an.kao_bottom_limit_file).toInt().minus(2)
            ai.kaoAudioClient(kaoTopLimit, kaoBottomLimit, an.mediaflinger_dump, an.kao_aclient)
            ap.kaoProcString("Output", an.kao_aclient, "1", "20", an.kao_out_stream_init)
        } else if (audioState.isNotEmpty()) {
            if (audioState == "-") {
                ai.kaoAudioInitExt("MIXER", an.mediaflinger_dump, "1", "+63", an.kao_audio_init)
                ai.kaoLimitAudioClient(
                    "MIXER",
                    an.mediaflinger_dump,
                    "1",
                    "+3",
                    an.kao_aclient_top_limit
                )
                ai.kaoLimitAudioClient(
                    "Suspended",
                    an.mediaflinger_dump,
                    "1",
                    "+3",
                    an.kao_aclient_bottom_limit
                )
                val kaoTopLimit = au.readFromFile(this, an.kao_top_limit_file).toInt().plus(1)
                val kaoBottomLimit =
                    au.readFromFile(this, an.kao_bottom_limit_file).toInt().minus(2)
                ai.kaoAudioClient(kaoTopLimit, kaoBottomLimit, an.mediaflinger_dump, an.kao_aclient)
                ap.kaoProcString("Output", an.kao_aclient, "1", "20", an.kao_out_stream_init)
            } else {
                ai.kaoAudioInitExt("DIRECT", an.mediaflinger_dump, "1", "+64", an.kao_audio_init)
                val audioStateAlt = au.readFromFile(this, an.kao_init_file).trim()
                if (state1 == audioStateAlt || state3 == audioStateAlt || state4 == audioStateAlt) {
                    ai.kaoLimitAudioClient(
                        "DIRECT",
                        an.mediaflinger_dump,
                        "1",
                        "+3",
                        an.kao_aclient_top_limit
                    )
                    ai.kaoLimitAudioClient(
                        "DIRECT",
                        an.mediaflinger_dump,
                        "2",
                        "+3",
                        an.kao_aclient_bottom_limit
                    )
                    val kaoTopLimit = au.readFromFile(this, an.kao_top_limit_file).toInt().plus(1)
                    val kaoBottomLimit = au.readFromFile(this, an.kao_bottom_limit_file).toInt()
                    ai.kaoAudioClient(
                        kaoTopLimit,
                        kaoBottomLimit,
                        an.mediaflinger_dump,
                        an.kao_aclient
                    )
                    ap.kaoProcString("Output", an.kao_aclient, "1", "20", an.kao_out_stream_init)
                } else if (audioStateAlt == state2) {
                    ai.kaoLimitAudioClient(
                        "MIXER",
                        an.mediaflinger_dump,
                        "1",
                        "+3",
                        an.kao_aclient_top_limit
                    )
                    ai.kaoLimitAudioClient(
                        "Suspended",
                        an.mediaflinger_dump,
                        "1",
                        "+3",
                        an.kao_aclient_bottom_limit
                    )
                    val kaoTopLimit = au.readFromFile(this, an.kao_top_limit_file).toInt().plus(1)
                    val kaoBottomLimit =
                        au.readFromFile(this, an.kao_bottom_limit_file).toInt().minus(2)
                    ai.kaoAudioClient(
                        kaoTopLimit,
                        kaoBottomLimit,
                        an.mediaflinger_dump,
                        an.kao_aclient
                    )
                    ap.kaoProcString("Output", an.kao_aclient, "1", "20", an.kao_out_stream_init)
                }
            }
        }

        // Take several value & Put it on textview
        ap.kaoIOHandle("I/O", an.kao_aclient, "13", an.kao_io_init)
        ap.kaoProcString("Standby", an.kao_aclient, "1", "9", an.kao_standby_init)
        ap.kaoProcInt("rate", an.kao_aclient, "1", an.kao_smp_rate_init)
        ap.kaoProcInt("count", an.kao_aclient, "1", an.kao_frm_cnt_init)
        ap.kaoProcString("format", an.kao_aclient, "1", "16", an.kao_frm_bit_init)
        ap.kaoProcInt("Channel", an.kao_aclient, "1", an.kao_chn_cnt_init)
        ap.kaoProcString("Channel", an.kao_aclient, "2", "25", an.kao_chn_mask_init)
        ap.kaoProcString("Processing", an.kao_aclient, "1", "23", an.kao_prc_frm_init)
        ap.kaoProcInt("Processing", an.kao_aclient, "2", an.kao_prc_bit_init)
        binding.audioOut.text = au.readFromFile(this, an.kao_out_stream_init_file)
        binding.audioPid.text = au.readFromFile(this, an.kao_io_init_file)
        binding.audioStandby.text = au.readFromFile(this, an.kao_standby_init_file)
        binding.audioSampleRate.text = au.readFromFile(this, an.kao_rate_init_file)
        binding.audioHalFrame.text = au.readFromFile(this, an.kao_frm_cnt_init_file)
        binding.audioHalFormat.text =
            ap.kaoBitDetect(au.readFromFile(this, an.kao_frm_bit_init_file).trim())
        binding.audioChannelCount.text = au.readFromFile(this, an.kao_chn_cnt_init_file)
        binding.audioChannelMask.text = au.readFromFile(this, an.kao_chn_mask_init_file)
        binding.audioFormat.text =
            ap.kaoBitDetect(au.readFromFile(this, an.kao_prc_frm_init_file).trim())
        binding.audioFrame.text = au.readFromFile(this, an.kao_prc_bit_init_file)
        binding.audioFlags.text = au.readFromFile(this, an.kao_init_file)

        binding.cvAppMenu2.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            val sharedView: View = binding.cvAppMenu2
            val transitionName = getString(R.string.app_main_menu_2)
            val transitionActivityOptions =
                ActivityOptions.makeSceneTransitionAnimation(this, sharedView, transitionName)
            startActivity(i, transitionActivityOptions.toBundle())
        }

        binding.btnAudioDsp.setOnClickListener {
            po = Dialog(this)

            // Call dialog
            showPopup(
                resources.getString(R.string.audio_effects),
                resources.getString(R.string.audio_effects_details),
                resources.getString(R.string.audio_effects_list),
                au.readFromFile(this, an.library_files).trim()
            )
        }

        binding.btnAudioSession.setOnClickListener {
            po = Dialog(this)

            // Call dialog
            showPopup(
                resources.getString(R.string.audio_session),
                resources.getString(R.string.audio_session_details),
                resources.getString(R.string.audio_session_list),
                au.readFromFile(this, an.audio_client_init_file).trim()
            )
        }

        binding.buttonTitle.setOnClickListener {
            po = Dialog(this)

            // Call dialog
            showPopupAdd(
                resources.getString(R.string.app_menu_2_info_title),
                resources.getString(R.string.app_menu_2_info_details),
                resources.getString(R.string.app_menu_2_info_ext)
            )
        }
    }

    private fun showPopup(text_1: String?, text_2: String?, text_3: String?, text_4: String?) {
        po.setContentView(R.layout.activity_pop_up_info)

        // Binding
        val pob: ActivityPopUpInfoBinding = ActivityPopUpInfoBinding.inflate(layoutInflater)

        title.text = text_1
        desc.text = text_2
        pob.textPopUpDesc2.text = text_3
        pob.textPopUpDesc3.text = text_4

        // sharedPreference begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)

        // Getting sharedPreference value if exist
        // Configure theme interface
        val nightMode = pref.getBoolean("MODE_NIGHT", false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            val nightColor = Color.parseColor("#607d8b")
            pob.cvTitle1.setCardBackgroundColor(nightColor)
            pob.cvTitle2.setCardBackgroundColor(nightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            val dayColor = Color.parseColor("#2286c3")
            pob.cvTitle1.setCardBackgroundColor(dayColor)
            pob.cvTitle2.setCardBackgroundColor(dayColor)
        }

        title.setOnClickListener { po.dismiss() }
        po.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        po.show()
    }

    private fun showPopupAdd(text_1: String?, text_2: String?, text_3: String) {
        po.setContentView(R.layout.activity_pop_up_info_details)

        // Binding
        val pob: ActivityPopUpInfoDetailsBinding =
            ActivityPopUpInfoDetailsBinding.inflate(layoutInflater)
        title.text = text_1
        desc.text = text_2
        descExt.text = text_3

        // sharedPreference begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)

        // Getting sharedPreference value if exist
        // Configure theme interface
        val nightMode = pref.getBoolean("MODE_NIGHT", false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            val nightColor = Color.parseColor("#607d8b")
            pob.cvTitle1.setCardBackgroundColor(nightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            val dayColor = Color.parseColor("#2286c3")
            pob.cvTitle1.setCardBackgroundColor(dayColor)
        }

        title.setOnClickListener { po.dismiss() }
        po.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        po.show()
    }
}