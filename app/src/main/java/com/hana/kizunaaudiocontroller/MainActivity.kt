package com.hana.kizunaaudiocontroller

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {
    // Declare controller
    lateinit var mm_1: CardView
    lateinit var theme_switcher: SwitchMaterial

    // Declare variables
    private val REQUEST_PERMISSIONS = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind controller
        mm_1 = findViewById(R.id.cv_app_main_menu_1)
        theme_switcher = findViewById(R.id.theme_switcher)

        // Binding root class
        val ar = AudioRoot()

        // Ask necessary permission for storage access
        ar.run(Runnable { ask_perm() })

        // Ask necessary root access
        ar.run(Runnable { ar.checkRooted() })

        // Sharedprefences begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)
        val save = pref.edit()

        // Getting sharedpreferences value if exist
        val night_mode = pref.getBoolean("MODE_NIGHT", false)
        if (night_mode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            theme_switcher.isChecked = true
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Set an animation transition
        window.enterTransition = Explode()
        window.returnTransition = Fade()

        mm_1.setOnClickListener {
            val i = Intent(this@MainActivity, AudioConfActivity::class.java)
            val sharedView: View = mm_1
            val transitionName = getString(R.string.PLACEHOLDER)
            val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this@MainActivity, sharedView, transitionName)
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
    }

    private fun ask_perm() {
        // start runtime permission
        val hasPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSIONS)
        } else {
            Toast.makeText(this@MainActivity, "Permissions already granted :3", Toast.LENGTH_SHORT).show()
        }
    }
}