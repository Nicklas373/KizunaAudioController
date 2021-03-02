package com.hana.kizunaaudiocontroller

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar
import java.util.*

class AudioConfActivity : AppCompatActivity() {
    // Declaring controller
    lateinit var po: Dialog
    lateinit var title: TextView
    lateinit var title_1: CardView
    lateinit var desc: TextView
    lateinit var desc_ext: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_conf)

        // Bind controller
        val cv_title: CardView = findViewById(R.id.cv_app_menu_1)
        val cv_1: CardView = findViewById(R.id.cv_app_menu_1_1)
        val cv_2: CardView = findViewById(R.id.cv_app_menu_1_2)
        val cv_3: CardView = findViewById(R.id.cv_app_menu_1_3)
        val cv_4: CardView = findViewById(R.id.cv_app_menu_1_4)
        val cv_5: CardView = findViewById(R.id.cv_app_menu_1_5)
        val cv_gating: CardView = findViewById(R.id.cv_app_menu_1_6)
        val switch_uhqa: SwitchCompat = findViewById(R.id.switch_app_menu_1)
        val switch_hph: SwitchCompat = findViewById(R.id.switch_app_menu_1_2)
        val switch_amp: SwitchCompat = findViewById(R.id.switch_app_menu_1_3)
        val switch_impedance: SwitchCompat = findViewById(R.id.switch_app_menu_1_4)
        val switch_ef: SwitchCompat = findViewById(R.id.switch_app_menu_1_5)
        val switch_gating: SwitchCompat = findViewById(R.id.switch_app_menu_1_6)
        val button_info: Button = findViewById(R.id.button_title)
        val button_uhqa: Button = findViewById(R.id.button_app_menu_1)
        val button_hph: Button = findViewById(R.id.button_app_menu_1_2)
        val button_amp: Button = findViewById(R.id.button_app_menu_1_3)
        val button_impedance: Button = findViewById(R.id.button_app_menu_1_4)
        val button_ef: Button = findViewById(R.id.button_app_menu_1_5)
        val button_gating: Button = findViewById(R.id.button_app_menu_1_6)
        val uhqa_stats: TextView = findViewById(R.id.switch_app_menu_1_1_text)
        val hph_stats: TextView = findViewById(R.id.switch_app_menu_1_2_1_text)
        val amp_stats: TextView = findViewById(R.id.switch_app_menu_1_3_1_text)
        val impedance_stats: TextView = findViewById(R.id.switch_app_menu_1_4_1_text)
        val gating_stats: TextView = findViewById(R.id.switch_app_menu_1_6_1_text)

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
        val exp = pref.getBoolean("EXP_FEATURES", false)
        if (exp) {
            switch_ef.isChecked = true
            TransitionManager.beginDelayedTransition(cv_gating, AutoTransition())
            cv_gating.visibility = View.VISIBLE
        } else {
            switch_ef.isChecked = false
            TransitionManager.beginDelayedTransition(cv_gating, AutoTransition())
            cv_gating.visibility = View.INVISIBLE
        }

        // Getting sharedpreferences value if exist
        // Configure theme interface
        val night_mode = pref.getBoolean("MODE_NIGHT", false)
        if (night_mode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            val nightColor = Color.parseColor("#2286c3")

            cv_title.setCardBackgroundColor(nightColor)
            cv_1.setCardBackgroundColor(nightColor)
            cv_2.setCardBackgroundColor(nightColor)
            cv_3.setCardBackgroundColor(nightColor)
            cv_4.setCardBackgroundColor(nightColor)
            cv_5.setCardBackgroundColor(nightColor)
            cv_gating.setCardBackgroundColor(nightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            val colorDrawable = ColorDrawable(Color.parseColor("#64b5f6"))
            Objects.requireNonNull(supportActionBar)?.setBackgroundDrawable(colorDrawable)
        }

        // Call necessary class
        val an = AudioNode()
        val au = AudioUtils()

        // Read kernel files
        au.ExportKernelFile(an.kernel_dump)
        val kernel_ver = au.readFromFile(this, an.kernel_file)

        // Declare static string for kernel version
        val upstream = "4.9"
        val legacy = "3.18"

        // Check kernel version
        if (kernel_ver.equals(upstream)) {
            au.DumpFile(an.uhqa_kernel_4_x, an.uhqa_force_file)
            au.DumpFile(an.amp_kernel_4_x, an.amp_force_file)
            au.DumpFile(an.gating_kernel_4_x, an.gating_force_file)

            // Not supported on 4.9 Kernel since techpack aren't
            // use older than wcd9335
            au.WriteToFile("0", an.hph_force_file)
            au.WriteToFile("0", an.impedance_force_file)
        } else if (kernel_ver.equals(legacy)) {
            au.DumpFile(an.uhqa_kernel_3_x, an.uhqa_force_file)
            au.DumpFile(an.hph_kernel_3_x, an.hph_force_file)
            au.DumpFile(an.amp_kernel_3_x, an.amp_force_file)
            au.DumpFile(an.impedance_kernel_3_x, an.impedance_file)
            au.DumpFile(an.gating_kernel_3_x, an.gating_force_file)
        }

        // Set switch value on init
        val uhqa_value = au.readFromFile(this, an.uhqa_file)
        val hph_value = au.readFromFile(this, an.hph_file)
        val amp_value = au.readFromFile(this, an.amp_file)
        val impedance_value = au.readFromFile(this, an.impedance_file)
        val gating_value = au.readFromFile(this, an.gating_file)

        switch_uhqa.isChecked = uhqa_value.compareTo("1") > 0
        switch_hph.isChecked = hph_value.compareTo("1") > 0
        switch_amp.isChecked = amp_value.compareTo("1") > 0
        switch_impedance.isChecked = impedance_value.compareTo("1") > 0
        switch_gating.isChecked = gating_value.compareTo("1") > 0

        cv_title.setOnClickListener {
            val i = Intent(this@AudioConfActivity, MainActivity::class.java)
            val sharedView: View = cv_title
            val transitionName = getString(R.string.app_main_menu_1)
            val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this@AudioConfActivity, sharedView, transitionName)
            startActivity(i, transitionActivityOptions.toBundle())
        }

        switch_uhqa.setOnClickListener {
            if (kernel_ver.equals(upstream)) {
                if (switch_uhqa.isChecked) {
                    au.WriteToFile("1", an.uhqa_kernel_4_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_1_toast_uhqa_scs))
                    uhqa_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_enable))
                } else {
                    au.WriteToFile("0", an.uhqa_kernel_4_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_1_toast_uhqa_fid))
                    uhqa_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_disable))
                }
            } else if (kernel_ver.equals(legacy)) {
                if (switch_uhqa.isChecked) {
                    au.WriteToFile("1", an.uhqa_kernel_3_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_1_toast_uhqa_scs))
                    uhqa_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_enable))
                } else {
                    au.WriteToFile("0", an.uhqa_kernel_3_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_1_toast_uhqa_fid))
                    uhqa_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_disable))
                }
            }
            uhqa_stats.visibility = View.VISIBLE
            au.DropFile(an.uhqa_file)
        }

        switch_hph.setOnClickListener {
            if (kernel_ver.equals(upstream)) {
                switch_hph.isChecked = false
                switch_hph.isClickable = false
                switch_hph.isEnabled = false
                setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_2_toast_hph_ne))
                hph_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_unsupport), resources.getString(R.string.state_disable))
            } else if (kernel_ver.equals(legacy)) {
                if (switch_hph.isChecked) {
                    au.WriteToFile("1", an.hph_kernel_3_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_2_toast_hph_scs))
                    hph_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_enable))
                } else {
                    au.WriteToFile("0", an.hph_kernel_3_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_2_toast_hph_fid))
                    hph_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_disable))
                }
            }
            hph_stats.visibility = View.VISIBLE
            au.DropFile(an.hph_file)
        }

        switch_amp.setOnClickListener {
            if (kernel_ver.equals(upstream)) {
                if (switch_amp.isChecked) {
                    au.WriteToFile("1", an.amp_kernel_4_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_3_toast_amp_scs))
                    amp_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_enable))
                } else {
                    au.WriteToFile("0", an.amp_kernel_4_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_3_toast_amp_fid))
                    amp_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_disable))
                }
            } else if (kernel_ver.equals(legacy)) {
                if (switch_amp.isChecked) {
                    au.WriteToFile("1", an.amp_kernel_3_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_3_toast_amp_scs))
                    amp_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_enable))
                } else {
                    au.WriteToFile("0", an.amp_kernel_3_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_3_toast_amp_fid))
                    amp_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_disable))
                }
            }
            amp_stats.visibility = View.VISIBLE
            au.DropFile(an.amp_file)
        }

        switch_impedance.setOnClickListener {
            if (kernel_ver.equals(upstream)) {
                switch_impedance.isChecked = false
                switch_impedance.isClickable = false
                switch_impedance.isEnabled = false
                setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_4_toast_impedance_ne))
                impedance_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_unsupport), resources.getString(R.string.state_disable))
            } else if (kernel_ver.equals(legacy)) {
                if (switch_impedance.isChecked) {
                    au.WriteToFile("1", an.impedance_kernel_3_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_4_toast_impedance_scs))
                    impedance_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_enable))
                } else {
                    au.WriteToFile("0", an.impedance_kernel_3_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_4_toast_impedance_fid))
                    impedance_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_disable))
                }
            }
            impedance_stats.visibility = View.VISIBLE
            au.DropFile(an.impedance_file)
        }

        switch_ef.setOnClickListener {
             if (switch_ef.isChecked) {
                 TransitionManager.beginDelayedTransition(cv_gating, AutoTransition())
                 cv_gating.visibility = View.VISIBLE
                 save.putBoolean("EXP_FEATURES", true)
                 save.apply()
             } else {
                 TransitionManager.beginDelayedTransition(cv_gating, AutoTransition())
                 cv_gating.visibility = View.INVISIBLE
                 save.putBoolean("EXP_FEATURES", false)
                 save.apply()
             }
        }

        switch_gating.setOnClickListener{
            if (kernel_ver.equals(upstream)) {
                if (switch_gating.isChecked) {
                    au.WriteToFile("1", an.gating_kernel_4_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_6_toast_gating_scs))
                    gating_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_enable))
                } else {
                    au.WriteToFile("0", an.gating_kernel_4_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_6_toast_gating_fid))
                    gating_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_disable))
                }
            } else if (kernel_ver.equals(legacy)) {
                if (switch_gating.isChecked) {
                    au.WriteToFile("1", an.gating_kernel_3_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_6_toast_gating_scs))
                    gating_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_enable))
                } else {
                    au.WriteToFile("0", an.gating_kernel_3_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_6_toast_gating_fid))
                    gating_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_disable))
                }
            }
            gating_stats.visibility = View.VISIBLE
            au.DropFile(an.gating_file)
        }

        button_uhqa.setOnClickListener {
            // Initiate dialog
            po = Dialog(this@AudioConfActivity)

            // Call dialog
            ShowPopup(resources.getString(R.string.app_menu_1_text), resources.getString(R.string.app_menu_1_desc))
        }

        button_hph.setOnClickListener {
            // Initiate dialog
            po = Dialog(this@AudioConfActivity)

            // Call dialog
            ShowPopup(resources.getString(R.string.app_menu_2_text), resources.getString(R.string.app_menu_2_desc))
        }

        button_amp.setOnClickListener {
            // Initiate dialog
            po = Dialog(this@AudioConfActivity)

            // Call dialog
            ShowPopup(resources.getString(R.string.app_menu_3_text), resources.getString(R.string.app_menu_3_desc))
        }

        button_impedance.setOnClickListener {
            // Initiate dialog
            po = Dialog(this@AudioConfActivity)

            // Call dialog
            ShowPopup(resources.getString(R.string.app_menu_4_text), resources.getString(R.string.app_menu_4_desc))
        }

        button_ef.setOnClickListener {
            // Initiate dialog
            po = Dialog(this@AudioConfActivity)

            // Call dialog
            ShowPopup(resources.getString(R.string.app_menu_5_text), resources.getString(R.string.app_menu_5_desc))
        }

        button_gating.setOnClickListener {
            // Initiate dialog
            po = Dialog(this@AudioConfActivity)

            // Call dialog
            ShowPopup(resources.getString(R.string.app_menu_6_text), resources.getString(R.string.app_menu_6_desc))
        }

        button_info.setOnClickListener {
            // Initiate dialog
            po = Dialog(this@AudioConfActivity)

            // Call dialog
            ShowPopupAdd(resources.getString(R.string.app_menu_1_info_title), resources.getString(R.string.app_menu_1_info_details), resources.getString(R.string.app_menu_1_info_ext))
        }
    }

    private fun ShowPopup(text_1: String?, text_2: String?) {
        po.setContentView(R.layout.activity_pop_up_conf)
        title = po.findViewById(R.id.text_pop_up_1)
        title_1 = po.findViewById(R.id.cv_title_1)
        desc = po.findViewById(R.id.text_pop_up_desc_1)
        title.text = text_1
        desc.text = text_2

        // Sharedprefences begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)

        // Getting sharedpreferences value if exist
        // Configure theme interface
        val night_mode = pref.getBoolean("MODE_NIGHT", false)
        if (night_mode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            val nightColor = Color.parseColor("#607d8b")
            title_1.setCardBackgroundColor(nightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            val dayColor = Color.parseColor("#2286c3")
            title_1.setCardBackgroundColor(dayColor)
        }

        title.setOnClickListener { po.dismiss() }
        po.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        po.show()
    }

    private fun ShowPopupAdd(text_1: String?, text_2: String?, text_3: String) {
        po.setContentView(R.layout.activity_pop_up_info_details)
        title = po.findViewById(R.id.text_pop_up_1)
        title_1 = po.findViewById(R.id.cv_title_1)
        desc = po.findViewById(R.id.text_pop_up_desc_1)
        desc_ext = po.findViewById(R.id.text_pop_up_desc_2)
        title.text = text_1
        desc.text = text_2
        desc_ext.text = text_3

        // Sharedprefences begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)

        // Getting sharedpreferences value if exist
        // Configure theme interface
        val night_mode = pref.getBoolean("MODE_NIGHT", false)
        if (night_mode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            val nightColor = Color.parseColor("#607d8b")
            title_1.setCardBackgroundColor(nightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            val dayColor = Color.parseColor("#2286c3")
            title_1.setCardBackgroundColor(dayColor)
        }

        title.setOnClickListener { po.dismiss() }
        po.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        po.show()
    }

    fun setSnackBar(root: View?, snackTitle: String?) {
        val snackbar = Snackbar.make(root!!, snackTitle!!, Snackbar.LENGTH_SHORT)
        snackbar.show()
        val view = snackbar.view
        val txtv = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        val typeface = ResourcesCompat.getFont(this@AudioConfActivity, R.font.mandorin_font)
        txtv.gravity = Gravity.START
        txtv.typeface = typeface
    }
}