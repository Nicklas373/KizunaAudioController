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
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import com.hana.kizunaaudiocontroller.R
import com.hana.kizunaaudiocontroller.audioUtils.AudioRoot
import com.hana.kizunaaudiocontroller.databinding.ActivityMainBinding
import com.hana.kizunaaudiocontroller.databinding.ContentMainBinding

class MainActivity : AppCompatActivity() {
    // Late binding
    private lateinit var contentBinding: ContentMainBinding

    // Declare variables
    private val requestPermission = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        contentBinding = activityMainBinding.detailContent
        setContentView(activityMainBinding.root)

        // Binding root class
        val ar = AudioRoot()

        // SharedPreference begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)

        // Ask necessary permission for storage access
        ar.run(Runnable { askPerm() })

        // Ask necessary root access
        ar.run(Runnable { ar.checkRooted() })

        // Configure theme interface
        val nightMode = pref.getBoolean("MODE_NIGHT", false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            val colorDrawable = ColorDrawable(Color.parseColor("#2286c3"))
            val nightColor = Color.parseColor("#2286c3")

            supportActionBar?.setBackgroundDrawable(colorDrawable)
            contentBinding.cvAppMainMenu1.setCardBackgroundColor(nightColor)
            contentBinding.cvAppMainMenu2.setCardBackgroundColor(nightColor)
            contentBinding.cvAppMainMenu3.setCardBackgroundColor(nightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            val colorDrawable = ColorDrawable(Color.parseColor("#64b5f6"))
            supportActionBar?.setBackgroundDrawable(colorDrawable)
        }

        // Set an animation transition
        window.enterTransition = Explode()
        window.returnTransition = Fade()

        contentBinding.cvAppMainMenu1.setOnClickListener {
            if (ar.checkRooted()) {
                val i = Intent(this@MainActivity, AudioConfActivity::class.java)
                val sharedView: View = contentBinding.cvAppMainMenu1
                val transitionName = getString(R.string.app_main_menu_1)
                val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(
                    this@MainActivity,
                    sharedView,
                    transitionName
                )
                startActivity(i, transitionActivityOptions.toBundle())
                setSnackBar(
                    findViewById(android.R.id.content),
                    resources.getString(R.string.audio_root_access_true)
                )
            } else {
                setSnackBar(
                    findViewById(android.R.id.content),
                    resources.getString(R.string.audio_root_access_false)
                )
                setSnackBar(
                    findViewById(android.R.id.content),
                    resources.getString(R.string.audio_root_access_reason)
                )
            }
        }

        contentBinding.cvAppMainMenu2.setOnClickListener {
            if (ar.checkRooted()) {
                val i = Intent(this@MainActivity, AudioInfoActivity::class.java)
                val sharedView: View = contentBinding.cvAppMainMenu2
                val transitionName = getString(R.string.app_main_menu_2)
                val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(
                    this@MainActivity,
                    sharedView,
                    transitionName
                )
                startActivity(i, transitionActivityOptions.toBundle())
                setSnackBar(
                    findViewById(android.R.id.content),
                    resources.getString(R.string.audio_root_access_true)
                )
            } else {
                setSnackBar(
                    findViewById(android.R.id.content),
                    resources.getString(R.string.audio_root_access_false)
                )
                setSnackBar(
                    findViewById(android.R.id.content),
                    resources.getString(R.string.audio_root_access_reason)
                )
            }
        }

        contentBinding.cvAppMainMenu3.setOnClickListener {
            if (ar.checkRooted()) {
                val i = Intent(this@MainActivity, AudioSettingsActivity::class.java)
                val sharedView: View = contentBinding.cvAppMainMenu3
                val transitionName = getString(R.string.audio_settings_title)
                val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(
                    this@MainActivity,
                    sharedView,
                    transitionName
                )
                startActivity(i, transitionActivityOptions.toBundle())
                setSnackBar(
                    findViewById(android.R.id.content),
                    resources.getString(R.string.audio_root_access_true)
                )
            } else {
                setSnackBar(
                    findViewById(android.R.id.content),
                    resources.getString(R.string.audio_root_access_false)
                )
                setSnackBar(
                    findViewById(android.R.id.content),
                    resources.getString(R.string.audio_root_access_reason)
                )
            }
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

    private fun setSnackBar(root: View?, snackTitle: String?) {
        val snackBar = Snackbar.make(root!!, snackTitle!!, Snackbar.LENGTH_SHORT)
        snackBar.show()
        val view = snackBar.view
        val txtV = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        val typeface = ResourcesCompat.getFont(this, R.font.mandorin_font)
        txtV.gravity = Gravity.START
        txtV.typeface = typeface
    }
}