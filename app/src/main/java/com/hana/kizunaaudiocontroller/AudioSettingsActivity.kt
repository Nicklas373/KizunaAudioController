package com.hana.kizunaaudiocontroller

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.*

class AudioSettingsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_settings)

        val theme_switcher: SwitchMaterial = findViewById(R.id.theme_switcher)

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

        // Getting sharedpreferences value if exist
        val night_mode = pref.getBoolean("MODE_NIGHT", false)
        if (night_mode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            theme_switcher.isChecked = true

            val colorDrawable = ColorDrawable(Color.parseColor("#2286c3"))
            Objects.requireNonNull(supportActionBar)?.setBackgroundDrawable(colorDrawable)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            val colorDrawable = ColorDrawable(Color.parseColor("#64b5f6"))
            Objects.requireNonNull(supportActionBar)?.setBackgroundDrawable(colorDrawable)
        }

        // Lock rotation to potrait by default
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

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
    }
}