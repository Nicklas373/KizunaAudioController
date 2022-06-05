package com.hana.kizunaaudiocontroller.activity

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.transition.Explode
import android.transition.Fade
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import com.hana.kizunaaudiocontroller.R
import com.hana.kizunaaudiocontroller.audioUtils.AudioUtils
import com.hana.kizunaaudiocontroller.databinding.ActivityAudioSettingsBinding
import com.hana.kizunaaudiocontroller.databinding.ActivityPopUpBackupInfoBinding
import com.hana.kizunaaudiocontroller.databinding.ContentAudioSettingsBinding
import com.hana.kizunaaudiocontroller.datasource.AudioNode
import java.io.File
import java.util.*

class AudioSettingsActivity : AppCompatActivity() {
    // Late binding
    private lateinit var contentSettingsBinding: ContentAudioSettingsBinding
    private lateinit var po: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding
        val activityAudioSettingsBinding = ActivityAudioSettingsBinding.inflate(layoutInflater)
        contentSettingsBinding = activityAudioSettingsBinding.detailContent
        setContentView(activityAudioSettingsBinding.root)

        // Hide title bar
        supportActionBar?.hide()

        // Set an animation transition
        window.enterTransition = Explode()
        window.returnTransition = Fade()

        // SharedPreference begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)
        val save = pref.edit()

        // Configure theme interface
        val nightMode = pref.getBoolean("MODE_NIGHT", false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            contentSettingsBinding.themeSwitcher.isChecked = true

            val colorDrawable = ColorDrawable(Color.parseColor("#2286c3"))
            val nightColor = Color.parseColor("#2286c3")
            val textNightColor = Color.parseColor("#FFFFFFFF")

            supportActionBar?.setBackgroundDrawable(colorDrawable)
            activityAudioSettingsBinding.cvTitle.setCardBackgroundColor(nightColor)
            contentSettingsBinding.settingInfo.setTextColor(textNightColor)
            contentSettingsBinding.settingInfo1Ans.setTextColor(textNightColor)
            contentSettingsBinding.settingInfo1.setTextColor(textNightColor)
            contentSettingsBinding.settingInfo2.setTextColor(textNightColor)
            contentSettingsBinding.settingInfo2Ans.setTextColor(textNightColor)
            contentSettingsBinding.settingTheme.setTextColor(textNightColor)
            contentSettingsBinding.settingBackup.setTextColor(textNightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            val colorDrawable = ColorDrawable(Color.parseColor("#64b5f6"))
            supportActionBar?.setBackgroundDrawable(colorDrawable)
        }

        // Call necessary class
        val an = AudioNode()
        val au = AudioUtils()

        // Read kernel files
        au.exportFullKernelFile(an.kernelDump)

        // Fill textview
        contentSettingsBinding.settingInfo1Ans.text = Build.VERSION.RELEASE
        contentSettingsBinding.settingInfo2Ans.text = au.readFromFile(this, an.kernelFile).trim()

        activityAudioSettingsBinding.cvTitle.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            val sharedView: View = activityAudioSettingsBinding.cvTitle
            val transitionName = getString(R.string.app_main_menu_3)
            val transitionActivityOptions =
                ActivityOptions.makeSceneTransitionAnimation(this, sharedView, transitionName)
            startActivity(i, transitionActivityOptions.toBundle())
        }

        contentSettingsBinding.themeSwitcher.setOnClickListener {
            if (contentSettingsBinding.themeSwitcher.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                save.putBoolean("MODE_NIGHT", true)
                save.apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                save.putBoolean("MODE_NIGHT", false)
                save.apply()
            }
        }

        contentSettingsBinding.buttonBackup.setOnClickListener {
            // Initiate dialog
            po = Dialog(this@AudioSettingsActivity)

            // Call dialog
            showPopup(
                resources.getString(R.string.audio_settings_4_1_1),
                resources.getString(R.string.audio_settings_4_1_2),
                resources.getString(R.string.audio_settings_4_1_3),
                resources.getString(R.string.PLACEHOLDER),
                resources.getString(R.string.audio_settings_4_1_4),
                resources.getString(R.string.PLACEHOLDER)
            )
        }
    }

    private fun showPopup(
        text_1: String?,
        text_2: String?,
        text_3: String?,
        text_4: String?,
        text_5: String?,
        text_6: String?
    ) {
        // Initialize binding
        val poBinding = ActivityPopUpBackupInfoBinding.inflate(layoutInflater)
        po.setContentView(poBinding.root)

        // Call necessary class
        val an = AudioNode()
        val au = AudioUtils()

        val backupTime = DateFormat.format("yyyy-MM-dd hh:mm:ss", Date())

        // Set binding
        poBinding.textPopUp1.text = text_1
        poBinding.textPopUpDesc1.text = text_2
        poBinding.textPopUpDesc2.text = text_3
        poBinding.textPopUpDesc3.text = text_4
        poBinding.textPopUpDesc4.text = text_5
        poBinding.textPopUpDesc5.text = text_6

        poBinding.cvTitle2.setOnClickListener {
            au.backupLogFiles(an.kaoFiles, an.kaoBackupFiles)
            val file = File(an.kaoFiles + an.kaoBackupFiles)
            if (file.exists()) {
                au.exportLogFiles(an.kaoFiles, an.kaoBackupFiles, an.kaoBackupDir)
                val file2 = File(an.kaoBackupDir + "/" + an.kaoBackupFiles)
                if (file2.exists()) {
                    val backupDir = an.kaoBackupDir + "/" + an.kaoBackupFiles
                    poBinding.textPopUpDesc3.text = backupDir
                    poBinding.textPopUpDesc5.text = backupTime.toString()
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.audio_settings_4_1_1_1)
                    )
                } else {
                    poBinding.textPopUpDesc3.text =
                        resources.getString(R.string.audio_settings_4_1_1_4)
                    poBinding.textPopUpDesc5.text =
                        resources.getString(R.string.audio_settings_4_1_1_4)
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.audio_settings_4_1_1_2)
                    )
                }
            } else {
                poBinding.textPopUpDesc3.text = resources.getString(R.string.audio_settings_4_1_1_4)
                poBinding.textPopUpDesc5.text = resources.getString(R.string.audio_settings_4_1_1_4)
                setSnackBar(
                    findViewById(android.R.id.content),
                    resources.getString(R.string.audio_settings_4_1_1_3)
                )
            }
        }

        poBinding.textPopUp1.setOnClickListener { po.dismiss() }
        po.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        po.show()
    }

    private fun setSnackBar(root: View?, snackTitle: String?) {
        val snackBar = Snackbar.make(root!!, snackTitle!!, Snackbar.LENGTH_SHORT)
        snackBar.show()
        val view = snackBar.view
        val txtV = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        val typeface = ResourcesCompat.getFont(this@AudioSettingsActivity, R.font.mandorin_font)
        txtV.gravity = Gravity.START
        txtV.typeface = typeface
    }
}