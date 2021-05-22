package com.hana.kizunaaudiocontroller

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import java.io.File
import java.util.*


class AudioSettingsActivity: AppCompatActivity() {

    lateinit var po: Dialog
    lateinit var title_1: TextView
    lateinit var title_2: TextView
    lateinit var cv_title_2: CardView
    lateinit var desc_title_1: TextView
    lateinit var desc_title_2: TextView
    lateinit var alt_title_1: TextView
    lateinit var alt_desc_title_1: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_settings)

        // Bind controller
        val cv_title: CardView = findViewById(R.id.cv_title)
        val txt_settings_1: TextView = findViewById(R.id.setting_info)
        val txt_settings_2: TextView = findViewById(R.id.setting_theme)
        val txt_settings_3: TextView = findViewById(R.id.setting_language)
        val txt_settings_4: TextView = findViewById(R.id.setting_backup)
        val txt_settings_1_ans: TextView = findViewById(R.id.setting_info_1)
        val txt_settings_1_qns: TextView = findViewById(R.id.setting_info_1_ans)
        val txt_settings_1_1_ans: TextView = findViewById(R.id.setting_info_2)
        val txt_settings_1_2_qns: TextView = findViewById(R.id.setting_info_2_ans)
        val theme_switcher: SwitchMaterial = findViewById(R.id.theme_switcher)
        val language_switcher: SwitchMaterial = findViewById(R.id.language_switcher)
        val backup_switcher: SwitchMaterial = findViewById(R.id.backup_switcher)

        // Hide title bar
        Objects.requireNonNull(supportActionBar)?.hide()

        // Lock rotation to potrait by default
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        // Set an animation transition
        window.enterTransition = Explode()
        window.returnTransition = Fade()

        // Sharedprefences begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)
        val save = pref.edit()

        // Configure theme interface
        val night_mode = pref.getBoolean("MODE_NIGHT", false)
        if (night_mode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            theme_switcher.isChecked = true

            val colorDrawable = ColorDrawable(Color.parseColor("#2286c3"))
            val nightColor = Color.parseColor("#2286c3")
            val textnightColor = Color.parseColor("#FFFFFFFF")

            Objects.requireNonNull(supportActionBar)?.setBackgroundDrawable(colorDrawable)
            cv_title.setCardBackgroundColor(nightColor)
            txt_settings_1.setTextColor(textnightColor)
            txt_settings_1_qns.setTextColor(textnightColor)
            txt_settings_1_ans.setTextColor(textnightColor)
            txt_settings_1_1_ans.setTextColor(textnightColor)
            txt_settings_1_2_qns.setTextColor(textnightColor)
            txt_settings_2.setTextColor(textnightColor)
            txt_settings_3.setTextColor(textnightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            val colorDrawable = ColorDrawable(Color.parseColor("#64b5f6"))
            Objects.requireNonNull(supportActionBar)?.setBackgroundDrawable(colorDrawable)
        }

        // Configure language interface
        val lang = pref.getString("lang", "en")
        if (lang != null) {
            if (lang.equals("id")) {
                language_switcher.isChecked = true
                language_switcher.setText(R.string.audio_settings_3_1)
                setLang("id")
            } else if (lang.equals("en")) {
                language_switcher.isChecked = false
                language_switcher.setText(R.string.audio_settings_3_2)
                setLang("en")
            }
        } else {
            language_switcher.isChecked = false
            language_switcher.setText(R.string.audio_settings_3_2)
        }

        // Call necessary class
        val an = AudioNode()
        val au = AudioUtils()

        // Read kernel files
        au.ExportFullKernelFile(an.kernel_dump)

        // Fill textview
        txt_settings_1_qns.setText(Build.VERSION.RELEASE)
        txt_settings_1_2_qns.setText(au.readFromFile(this, an.kernel_file).trim())

        cv_title.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            val sharedView: View = cv_title
            val transitionName = getString(R.string.app_main_menu_3)
            val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this, sharedView, transitionName)
            startActivity(i, transitionActivityOptions.toBundle())
        }

        theme_switcher.setOnClickListener {
            if (theme_switcher.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                save.putBoolean("MODE_NIGHT", true)
                save.apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                save.putBoolean("MODE_NIGHT", false)
                save.apply()
            }
        }

        language_switcher.setOnClickListener{
            if (language_switcher.isChecked()) {
                save.putString("lang", "id")
                save.apply()
                setLang("id")
                this.recreate()
                language_switcher.isChecked = true
            } else {
                save.putString("lang", "en")
                save.apply()
                setLang("en")
                this.recreate()
                language_switcher.isChecked = false
            }
        }

        backup_switcher.setOnClickListener{
            if (backup_switcher.isChecked) {
                // Initiate dialog
                po = Dialog(this@AudioSettingsActivity)

                // Call dialog
                ShowPopup(
                    "Backup Menu",
                    "Backup current log files from the app to your sdcard",
                    "Backup",
                    "PLACEHOLDER",
                    "Last Backup:",
                    "PLACEHOLDER"
                )
            }
        }
    }

    fun setLang(language: String) {
        val locale = Locale(language)
        val config = baseContext.resources.configuration
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    private fun ShowPopup(text_1: String?, text_2: String?, text_3: String?, text_4: String?, text_5: String?, text_6: String?) {
        po.setContentView(R.layout.activity_pop_up_backup_info)

        // Call necessary class
        val an = AudioNode()
        val au = AudioUtils()

        val currentTime = Calendar.getInstance().time

        title_1 = po.findViewById(R.id.text_pop_up_1)
        title_2 = po.findViewById(R.id.text_pop_up_desc_2)
        cv_title_2 = po.findViewById(R.id.cv_title_2)
        desc_title_1 = po.findViewById(R.id.text_pop_up_desc_1)
        desc_title_2 = po.findViewById(R.id.text_pop_up_desc_3)
        alt_title_1 = po.findViewById(R.id.text_pop_up_desc_4)
        alt_desc_title_1 = po.findViewById(R.id.text_pop_up_desc_5)
        title_1.text = text_1
        desc_title_1.text = text_2
        title_2.text = text_3
        desc_title_2.text = text_4
        alt_title_1.text = text_5
        alt_desc_title_1.text = text_6

        cv_title_2.setOnClickListener{
            au.BackupLogFiles(an.kao_files, "KAO_BACKUP.gz")
            val file = File(an.kao_files+"KAO_BACKUP.gz")
            if (file.exists()) {
                au.ExportLogFiles(an.kao_files,"KAO_BACKUP.gz", an.kao_backup)
                val file_2 = File(an.kao_backup+"/"+"KAO_BACKUP.gz")
                if (file_2.exists()) {
                    desc_title_2.text = an.kao_backup+"/"+"KAO_BACKUP.gz"
                    alt_desc_title_1.text = currentTime.toString()
                    setSnackBar(findViewById(android.R.id.content), "Data Backup Available on Download Folder")
                } else {
                    desc_title_2.text = "NULL"
                    alt_desc_title_1.text = "NULL"
                    setSnackBar(findViewById(android.R.id.content), "Data Backup Failed on Export")
                }
            } else {
                desc_title_2.text = "NULL"
                alt_desc_title_1.text = "NULL"
                setSnackBar(findViewById(android.R.id.content), "Data Backup Failed on Backup")
            }
        }

        title_1.setOnClickListener { po.dismiss() }
        po.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        po.show()
    }

    fun setSnackBar(root: View?, snackTitle: String?) {
        val snackbar = Snackbar.make(root!!, snackTitle!!, Snackbar.LENGTH_SHORT)
        snackbar.show()
        val view = snackbar.view
        val txtv = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        val typeface = ResourcesCompat.getFont(this@AudioSettingsActivity, R.font.mandorin_font)
        txtv.gravity = Gravity.START
        txtv.typeface = typeface
    }
}