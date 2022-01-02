package com.hana.kizunaaudiocontroller.activity

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hana.kizunaaudiocontroller.audioUtils.AudioRoot
import com.hana.kizunaaudiocontroller.R
import com.hana.kizunaaudiocontroller.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {
    // Declare variables
    private val requestPermission = 20

    // Binding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Binding root class
        val ar = AudioRoot()

        // Ask necessary permission for storage access
        ar.run(Runnable { askPerm() })

        // Ask necessary root access
        ar.run(Runnable { ar.checkRooted() })

        // sharedPreference begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)

        // Getting sharedPreference value if exist
        // Configure theme interface
        val nightMode = pref.getBoolean("MODE_NIGHT", false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            val colorDrawable = ColorDrawable(Color.parseColor("#2286c3"))
            val nightColor = Color.parseColor("#2286c3")

            supportActionBar?.setBackgroundDrawable(colorDrawable)
            binding.cvAppMainMenu1.setCardBackgroundColor(nightColor)
            binding.cvAppMainMenu2.setCardBackgroundColor(nightColor)
            binding.cvAppMainMenu3.setCardBackgroundColor(nightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            val colorDrawable = ColorDrawable(Color.parseColor("#64b5f6"))
            supportActionBar?.setBackgroundDrawable(colorDrawable)
        }

        // Set an animation transition
        window.enterTransition = Explode()
        window.returnTransition = Fade()

        binding.cvAppMainMenu1.setOnClickListener {
            val i = Intent(this@MainActivity, AudioConfActivity::class.java)
            val sharedView: View = binding.cvAppMainMenu1
            val transitionName = getString(R.string.app_main_menu_1)
            val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(
                this@MainActivity,
                sharedView,
                transitionName
            )
            startActivity(i, transitionActivityOptions.toBundle())
        }

        binding.cvAppMainMenu2.setOnClickListener {
            val i = Intent(this@MainActivity, AudioInfoActivity::class.java)
            val sharedView: View = binding.cvAppMainMenu2
            val transitionName = getString(R.string.app_main_menu_2)
            val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(
                this@MainActivity,
                sharedView,
                transitionName
            )
            startActivity(i, transitionActivityOptions.toBundle())
        }

        binding.cvAppMainMenu3.setOnClickListener {
            val i = Intent(this@MainActivity, AudioSettingsActivity::class.java)
            val sharedView: View = binding.cvAppMainMenu3
            val transitionName = getString(R.string.audio_settings_title)
            val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(
                this@MainActivity,
                sharedView,
                transitionName
            )
            startActivity(i, transitionActivityOptions.toBundle())
        }
    }

    private fun askPerm() {
        // start runtime permission
        val hasPermission =
            (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
        if (!hasPermission) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                requestPermission
            )
        }
    }
}