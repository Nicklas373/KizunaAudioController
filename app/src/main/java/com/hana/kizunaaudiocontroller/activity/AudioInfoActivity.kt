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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.hana.kizunaaudiocontroller.*
import com.hana.kizunaaudiocontroller.audioUtils.AudioInfo
import com.hana.kizunaaudiocontroller.datasource.AudioNode
import com.hana.kizunaaudiocontroller.audioUtils.AudioProc
import com.hana.kizunaaudiocontroller.audioUtils.AudioUtils
import com.hana.kizunaaudiocontroller.databinding.*

class AudioInfoActivity : AppCompatActivity() {
    // Late binding
    private lateinit var contentInfoBinding: ContentAudioInfoBinding

    // Separate environment
    private lateinit var po: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding
        val binding = ActivityAudioInfoBinding.inflate(layoutInflater)
        contentInfoBinding = binding.detailContent
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
            contentInfoBinding.cvAchannel.setCardBackgroundColor(nightColor)
            contentInfoBinding.cvAformat.setCardBackgroundColor(nightColor)
            contentInfoBinding.cvAsession.setCardBackgroundColor(nightColor)
            contentInfoBinding.cvAstate.setCardBackgroundColor(nightColor)
            contentInfoBinding.cvAhal.setCardBackgroundColor(nightColor)
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
        ai.kaoReset(an.kaoFiles)

        // Begin less conditional bash processing
        ai.mediaFlinger(an.mediaflingerDump)
        ai.libraryEffects(an.mediaflingerDump, an.unfilteredLibrary, an.library)
        ai.limitAudioClient("Notification", an.mediaflingerDump, "3", an.audioClientTlInit)
        ai.limitAudioClient("Global", an.mediaflingerDump, "3", an.audioClientBlInit)

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
            val aClientTl = au.readFromFile(this, an.audioClientTlInitFile).toInt().plus(3)
            val aClientBl = au.readFromFile(this, an.audioClientBlInitFile).toInt().minus(1)
            ai.currentAudioClient10(
                aClientTl,
                aClientBl,
                an.mediaflingerDump,
                an.audioClientInit
            )
        } else {
            val aClientTl = au.readFromFile(this, an.audioClientTlInitFile).toInt().plus(2)
            val aClientBl = au.readFromFile(this, an.audioClientBlInitFile).toInt().minus(1)
            ai.currentAudioClient11(
                aClientTl,
                aClientBl,
                an.mediaflingerDump,
                an.audioClientInit
            )
        }

        // Begin bash processing for specific condition
        ai.kaoAudioInit("DIRECT", an.mediaflingerDump, "1", an.kaoAudioInit)
        val audioState = au.readFromFile(this, an.kaoInitFile).trim()

        /* If AudioState is empty then direct route to use MIXER path instead, but
         * If not then it should check whether value is dash or not, since dash is actually
         * introduced on Android 11 Audio API as "Historical Audio Session", which aren't need
         * to debug, so we should skip and router to MIXER path if this condition happen, however
         * if not then we will re-filter audio state and correct state after it.
         */
        if (audioState.isEmpty()) {
            ai.kaoLimitAudioClient(
                "MIXER",
                an.mediaflingerDump,
                "1",
                "+3",
                an.kaoAclientTopLimit
            )
            ai.kaoLimitAudioClient(
                "Suspended",
                an.mediaflingerDump,
                "1",
                "+3",
                an.kaoAclientBottomLimit
            )
            val kaoTopLimit = au.readFromFile(this, an.kaoTopLimitFile).toInt().plus(1)
            val kaoBottomLimit = au.readFromFile(this, an.kaoBottomLimitFile).toInt().minus(2)
            ai.kaoAudioClient(kaoTopLimit, kaoBottomLimit, an.mediaflingerDump, an.kaoAclient)
            ap.kaoProcString("Output", an.kaoAclient, "1", "20", an.kaoOutStreamInit)
        } else if (audioState.isNotEmpty()) {
            if (audioState == "-") {
                ai.kaoAudioInitExt("MIXER", an.mediaflingerDump, "1", "+63", an.kaoAudioInit)
                ai.kaoLimitAudioClient(
                    "MIXER",
                    an.mediaflingerDump,
                    "1",
                    "+3",
                    an.kaoAclientTopLimit
                )
                ai.kaoLimitAudioClient(
                    "Suspended",
                    an.mediaflingerDump,
                    "1",
                    "+3",
                    an.kaoAclientBottomLimit
                )
                val kaoTopLimit = au.readFromFile(this, an.kaoTopLimitFile).toInt().plus(1)
                val kaoBottomLimit =
                    au.readFromFile(this, an.kaoBottomLimitFile).toInt().minus(2)
                ai.kaoAudioClient(kaoTopLimit, kaoBottomLimit, an.mediaflingerDump, an.kaoAclient)
                ap.kaoProcString("Output", an.kaoAclient, "1", "20", an.kaoOutStreamInit)
            } else {
                ai.kaoAudioInitExt("DIRECT", an.mediaflingerDump, "1", "+64", an.kaoAudioInit)
                val audioStateAlt = au.readFromFile(this, an.kaoInitFile).trim()
                if (state1 == audioStateAlt || state3 == audioStateAlt || state4 == audioStateAlt) {
                    ai.kaoLimitAudioClient(
                        "DIRECT",
                        an.mediaflingerDump,
                        "1",
                        "+3",
                        an.kaoAclientTopLimit
                    )
                    ai.kaoLimitAudioClient(
                        "DIRECT",
                        an.mediaflingerDump,
                        "2",
                        "+3",
                        an.kaoAclientBottomLimit
                    )
                    val kaoTopLimit = au.readFromFile(this, an.kaoTopLimitFile).toInt().plus(1)
                    val kaoBottomLimit = au.readFromFile(this, an.kaoBottomLimitFile).toInt()
                    ai.kaoAudioClient(
                        kaoTopLimit,
                        kaoBottomLimit,
                        an.mediaflingerDump,
                        an.kaoAclient
                    )
                    ap.kaoProcString("Output", an.kaoAclient, "1", "20", an.kaoOutStreamInit)
                } else if (audioStateAlt == state2) {
                    ai.kaoLimitAudioClient(
                        "MIXER",
                        an.mediaflingerDump,
                        "1",
                        "+3",
                        an.kaoAclientTopLimit
                    )
                    ai.kaoLimitAudioClient(
                        "Suspended",
                        an.mediaflingerDump,
                        "1",
                        "+3",
                        an.kaoAclientBottomLimit
                    )
                    val kaoTopLimit = au.readFromFile(this, an.kaoTopLimitFile).toInt().plus(1)
                    val kaoBottomLimit =
                        au.readFromFile(this, an.kaoBottomLimitFile).toInt().minus(2)
                    ai.kaoAudioClient(
                        kaoTopLimit,
                        kaoBottomLimit,
                        an.mediaflingerDump,
                        an.kaoAclient
                    )
                    ap.kaoProcString("Output", an.kaoAclient, "1", "20", an.kaoOutStreamInit)
                }
            }
        }

        // Take several value & Put it on textview
        ap.kaoIOHandle("I/O", an.kaoAclient, "13", an.kaoIoInit)
        ap.kaoProcString("Standby", an.kaoAclient, "1", "9", an.kaoStandbyInit)
        ap.kaoProcInt("rate", an.kaoAclient, "1", an.kaoSmpRateInit)
        ap.kaoProcInt("count", an.kaoAclient, "1", an.kaoFrmCntInit)
        ap.kaoProcString("format", an.kaoAclient, "1", "16", an.kaoFrmBitInit)
        ap.kaoProcInt("Channel", an.kaoAclient, "1", an.kaoChnCntInit)
        ap.kaoProcString("Channel", an.kaoAclient, "2", "25", an.kaoChnMaskInit)
        ap.kaoProcString("Processing", an.kaoAclient, "1", "23", an.kaoPrcFrmInit)
        ap.kaoProcInt("Processing", an.kaoAclient, "2", an.kaoPrcBitInit)
        contentInfoBinding.audioOut.text = au.readFromFile(this, an.kaoOutStreamInitFile)
        contentInfoBinding.audioPid.text = au.readFromFile(this, an.kaoIoInitFile)
        contentInfoBinding.audioStandby.text = au.readFromFile(this, an.kaoStandbyInitFile)
        contentInfoBinding.audioSampleRate.text = au.readFromFile(this, an.kaoRateInitFile)
        contentInfoBinding.audioHalFrame.text = au.readFromFile(this, an.kaoFrmCntInitFile)
        contentInfoBinding.audioHalFormat.text =
            ap.kaoBitDetect(au.readFromFile(this, an.kaoFrmBitInitFile).trim())
        contentInfoBinding.audioChannelCount.text = au.readFromFile(this, an.kaoChnCntInitFile)
        contentInfoBinding.audioChannelMask.text = au.readFromFile(this, an.kaoChnMaskInitFile)
        contentInfoBinding.audioFormat.text =
            ap.kaoBitDetect(au.readFromFile(this, an.kaoPrcFrmInitFile).trim())
        contentInfoBinding.audioFrame.text = au.readFromFile(this, an.kaoPrcBitInitFile)
        contentInfoBinding.audioFlags.text = au.readFromFile(this, an.kaoInitFile)

        binding.cvAppMenu2.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            val sharedView: View = binding.cvAppMenu2
            val transitionName = getString(R.string.app_main_menu_2)
            val transitionActivityOptions =
                ActivityOptions.makeSceneTransitionAnimation(this, sharedView, transitionName)
            startActivity(i, transitionActivityOptions.toBundle())
        }

        contentInfoBinding.btnAudioDsp.setOnClickListener {
            po = Dialog(this)

            // Call dialog
            showPopup(
                resources.getString(R.string.audio_effects),
                resources.getString(R.string.audio_effects_details),
                resources.getString(R.string.audio_effects_list),
                au.readFromFile(this, an.libraryFiles).trim()
            )
        }

        contentInfoBinding.btnAudioSession.setOnClickListener {
            po = Dialog(this)

            // Call dialog
            showPopup(
                resources.getString(R.string.audio_session),
                resources.getString(R.string.audio_session_details),
                resources.getString(R.string.audio_session_list),
                au.readFromFile(this, an.audioClientInitFile).trim()
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
        // Initialize binding
        val poBinding = ActivityPopUpInfoBinding.inflate(layoutInflater)
        po.setContentView(poBinding.root)

        // Set binding
        poBinding.textPopUp1.text = text_1
        poBinding.textPopUpDesc1.text = text_2
        poBinding.textPopUpDesc2.text = text_3
        poBinding.textPopUpDesc3.text = text_4

        // sharedPreference begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)

        // Getting sharedPreference value if exist
        // Configure theme interface
        val nightMode = pref.getBoolean("MODE_NIGHT", false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            val nightColor = Color.parseColor("#607d8b")
            poBinding.cvTitle1.setCardBackgroundColor(nightColor)
            poBinding.cvTitle2.setCardBackgroundColor(nightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            val dayColor = Color.parseColor("#2286c3")
            poBinding.cvTitle1.setCardBackgroundColor(dayColor)
            poBinding.cvTitle2.setCardBackgroundColor(dayColor)
        }

        poBinding.cvPopUp1.setOnClickListener { po.dismiss() }
        po.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        po.show()
    }

    private fun showPopupAdd(text_1: String?, text_2: String?, text_3: String) {
        // Initialize binding
        val poBinding = ActivityPopUpInfoDetailsBinding.inflate(layoutInflater)
        po.setContentView(poBinding.root)

        // Set binding
        poBinding.textPopUp1.text = text_1
        poBinding.textPopUpDesc1.text = text_2
        poBinding.textPopUpDesc2.text = text_3

        // sharedPreference begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)

        // Getting sharedPreference value if exist
        // Configure theme interface
        val nightMode = pref.getBoolean("MODE_NIGHT", false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            val nightColor = Color.parseColor("#607d8b")
            poBinding.cvTitle1.setCardBackgroundColor(nightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            val dayColor = Color.parseColor("#2286c3")
            poBinding.cvTitle1.setCardBackgroundColor(dayColor)
        }

        poBinding.cvPopUp1.setOnClickListener { po.dismiss() }
        po.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        po.show()
    }
}