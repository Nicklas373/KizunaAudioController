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
import com.hana.kizunaaudiocontroller.datasource.AudioNode
import com.hana.kizunaaudiocontroller.audioUtils.AudioUtils
import com.hana.kizunaaudiocontroller.R
import com.hana.kizunaaudiocontroller.databinding.ActivityAudioSettingsBinding
import com.hana.kizunaaudiocontroller.databinding.ActivityPopUpBackupInfoBinding
import java.io.File
import java.util.*

class AudioSettingsActivity : AppCompatActivity() {

    // Binding
    private lateinit var binding: ActivityAudioSettingsBinding

    private lateinit var po: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAudioSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide title bar
        supportActionBar?.hide()

        // Set an animation transition
        window.enterTransition = Explode()
        window.returnTransition = Fade()

        // sharedPreference begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)
        val save = pref.edit()

        // Configure theme interface
        val nightMode = pref.getBoolean("MODE_NIGHT", false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding.themeSwitcher.isChecked = true

            val colorDrawable = ColorDrawable(Color.parseColor("#2286c3"))
            val nightColor = Color.parseColor("#2286c3")
            val textNightColor = Color.parseColor("#FFFFFFFF")

            supportActionBar?.setBackgroundDrawable(colorDrawable)
            binding.cvTitle.setCardBackgroundColor(nightColor)
            binding.settingInfo.setTextColor(textNightColor)
            binding.settingInfo1Ans.setTextColor(textNightColor)
            binding.settingInfo1.setTextColor(textNightColor)
            binding.settingInfo2.setTextColor(textNightColor)
            binding.settingInfo2Ans.setTextColor(textNightColor)
            binding.settingLanguage.setTextColor(textNightColor)
            binding.settingTheme.setTextColor(textNightColor)
            binding.settingBackup.setTextColor(textNightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            val colorDrawable = ColorDrawable(Color.parseColor("#64b5f6"))
            supportActionBar?.setBackgroundDrawable(colorDrawable)
        }

        // Configure language interface
        val lang = pref.getString("lang", "en")
        if (lang != null) {
            if (lang == "id") {
                binding.languageSwitcherId.isChecked = true
                binding.languageSwitcherEn.isChecked = false
                setLang("id")
            } else if (lang == "en") {
                binding.languageSwitcherId.isChecked = false
                binding.languageSwitcherEn.isChecked = true
                setLang("en")
            }
        } else {
            binding.languageSwitcherId.isChecked = false
            binding.languageSwitcherEn.isChecked = false
            setLang("en")
        }

        // Call necessary class
        val an = AudioNode()
        val au = AudioUtils()

        // Read kernel files
        au.exportFullKernelFile(an.kernel_dump)

        // Fill textview
        binding.settingInfo1Ans.text = Build.VERSION.RELEASE
        binding.settingInfo2Ans.text = au.readFromFile(this, an.kernel_file).trim()

        binding.cvTitle.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            val sharedView: View = binding.cvTitle
            val transitionName = getString(R.string.app_main_menu_3)
            val transitionActivityOptions =
                ActivityOptions.makeSceneTransitionAnimation(this, sharedView, transitionName)
            startActivity(i, transitionActivityOptions.toBundle())
        }

        binding.themeSwitcher.setOnClickListener {
            if (binding.themeSwitcher.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                save.putBoolean("MODE_NIGHT", true)
                save.apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                save.putBoolean("MODE_NIGHT", false)
                save.apply()
            }
        }

        binding.languageSwitcherId.setOnClickListener {
            if (binding.languageSwitcherId.isChecked) {
                save.putString("lang", "id")
                save.apply()
                setLang("id")
                this.recreate()
                binding.languageSwitcherId.isChecked = true
                binding.languageSwitcherEn.isChecked = false
            } else {
                binding.languageSwitcherId.isChecked = false
                binding.languageSwitcherEn.isChecked = false
            }
        }

        binding.languageSwitcherEn.setOnClickListener {
            if (binding.languageSwitcherId.isChecked) {
                save.putString("lang", "en")
                save.apply()
                setLang("en")
                this.recreate()
                binding.languageSwitcherId.isChecked = false
                binding.languageSwitcherEn.isChecked = true
            } else {
                binding.languageSwitcherId.isChecked = false
                binding.languageSwitcherEn.isChecked = false
            }
        }

        binding.buttonBackup.setOnClickListener {
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

    private fun setLang(language: String) {
        val locale = Locale(language)
        val config = baseContext.resources.configuration
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    private fun showPopup(
        text_1: String?,
        text_2: String?,
        text_3: String?,
        text_4: String?,
        text_5: String?,
        text_6: String?
    ) {
        po.setContentView(R.layout.activity_pop_up_backup_info)

        // Binding
        val pob: ActivityPopUpBackupInfoBinding =
            ActivityPopUpBackupInfoBinding.inflate(layoutInflater)

        // Call necessary class
        val an = AudioNode()
        val au = AudioUtils()

        val backupTime = DateFormat.format("yyyy-MM-dd hh:mm:ss", Date())

        pob.textPopUp1.text = text_1
        pob.textPopUpDesc1.text = text_2
        pob.textPopUpDesc2.text = text_3
        pob.textPopUpDesc3.text = text_4
        pob.textPopUpDesc4.text = text_5
        pob.textPopUpDesc5.text = text_6

        pob.cvTitle2.setOnClickListener {
            au.backupLogFiles(an.kao_files, an.kao_backup_files)
            val file = File(an.kao_files + an.kao_backup_files)
            if (file.exists()) {
                au.exportLogFiles(an.kao_files, an.kao_backup_files, an.kao_backup_dir)
                val file2 = File(an.kao_backup_dir + "/" + an.kao_backup_files)
                if (file2.exists()) {
                    val backupDir = an.kao_backup_dir + "/" + an.kao_backup_files
                    pob.textPopUpDesc3.text = backupDir
                    pob.textPopUpDesc5.text = backupTime.toString()
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.audio_settings_4_1_1_1)
                    )
                } else {
                    pob.textPopUpDesc3.text = resources.getString(R.string.audio_settings_4_1_1_4)
                    pob.textPopUpDesc5.text = resources.getString(R.string.audio_settings_4_1_1_4)
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.audio_settings_4_1_1_2)
                    )
                }
            } else {
                pob.textPopUpDesc3.text = resources.getString(R.string.audio_settings_4_1_1_4)
                pob.textPopUpDesc5.text = resources.getString(R.string.audio_settings_4_1_1_4)
                setSnackBar(
                    findViewById(android.R.id.content),
                    resources.getString(R.string.audio_settings_4_1_1_3)
                )
            }
        }

        pob.textPopUp1.setOnClickListener { po.dismiss() }
        po.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        po.show()
    }

    private fun setSnackBar(root: View?, snackTitle: String?) {
        val snackBar = Snackbar.make(root!!, snackTitle!!, Snackbar.LENGTH_SHORT)
        snackBar.show()
        val view = snackBar.view
        val txtv = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        val typeface = ResourcesCompat.getFont(this@AudioSettingsActivity, R.font.mandorin_font)
        txtv.gravity = Gravity.START
        txtv.typeface = typeface
    }
}