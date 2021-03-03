package com.hana.kizunaaudiocontroller

import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.*


class AudioSettingsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_settings)

        // Bind controller
        val cv_title: CardView = findViewById(R.id.cv_title)
        val txt_settings_1: TextView = findViewById(R.id.setting_info)
        val txt_settings_2: TextView = findViewById(R.id.setting_theme)
        val txt_settings_3: TextView = findViewById(R.id.setting_language)
        val txt_settings_1_ans: TextView = findViewById(R.id.setting_info_1)
        val txt_settings_1_qns: TextView = findViewById(R.id.setting_info_1_ans)
        val txt_settings_1_1_ans: TextView = findViewById(R.id.setting_info_2)
        val txt_settings_1_2_qns: TextView = findViewById(R.id.setting_info_2_ans)
        val theme_switcher: SwitchMaterial = findViewById(R.id.theme_switcher)
        val language_switcher: SwitchMaterial = findViewById(R.id.language_switcher)

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
    }

    fun setLang(language: String) {
        val locale = Locale(language)
        val config = baseContext.resources.configuration
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

}