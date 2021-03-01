package com.hana.kizunaaudiocontroller

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*


class MainActivity : AppCompatActivity() {
    // Declare variables
    private val REQUEST_PERMISSIONS = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind controller
        val mm_1: CardView = findViewById(R.id.cv_app_main_menu_1)
        val mm_2: CardView = findViewById(R.id.cv_app_main_menu_2)
        val mm_3: CardView = findViewById(R.id.cv_app_main_menu_3)

        // Binding root class
        val ar = AudioRoot()

        // Ask necessary permission for storage access
        ar.run(Runnable { ask_perm() })

        // Ask necessary root access
        ar.run(Runnable { ar.checkRooted() })

        // Sharedprefences begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)

        // Getting sharedpreferences value if exist
        val night_mode = pref.getBoolean("MODE_NIGHT", false)
        if (night_mode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            val colorDrawable = ColorDrawable(Color.parseColor("#2286c3"))
            val nightColor = Color.parseColor("#2286c3")

            Objects.requireNonNull(supportActionBar)?.setBackgroundDrawable(colorDrawable)
            mm_1.setCardBackgroundColor(nightColor)
            mm_2.setCardBackgroundColor(nightColor)
            mm_3.setCardBackgroundColor(nightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            val colorDrawable = ColorDrawable(Color.parseColor("#64b5f6"))
            Objects.requireNonNull(supportActionBar)?.setBackgroundDrawable(colorDrawable)
        }

        // Lock rotation to potrait by default
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        // Set an animation transition
        window.enterTransition = Explode()
        window.returnTransition = Fade()

        mm_1.setOnClickListener {
            val i = Intent(this@MainActivity, AudioConfActivity::class.java)
            val sharedView: View = mm_1
            val transitionName = getString(R.string.app_main_menu_1)
            val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this@MainActivity, sharedView, transitionName)
            startActivity(i, transitionActivityOptions.toBundle())
        }

        mm_2.setOnClickListener {
            val i = Intent(this@MainActivity, AudioInfoActivity::class.java)
            val sharedView: View = mm_2
            val transitionName = getString(R.string.app_main_menu_2)
            val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this@MainActivity, sharedView, transitionName)
            startActivity(i, transitionActivityOptions.toBundle())
        }

        mm_3.setOnClickListener {
            val i = Intent(this@MainActivity, AudioSettingsActivity::class.java)
            startActivity(i)
        }
    }

    private fun ask_perm() {
        // start runtime permission
        val hasPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSIONS)
        }
    }
}