package com.hana.kizunaaudiocontroller

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
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
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import java.util.*

class AudioConfActivity : AppCompatActivity() {
    // Declaring controller
    lateinit var this_activity: ConstraintLayout
    lateinit var cv_title: CardView
    lateinit var cv_uhqa: CardView
    lateinit var cv_hph: CardView
    lateinit var cv_amp: CardView
    lateinit var cv_impedance: CardView
    lateinit var switch_uhqa: SwitchCompat
    lateinit var switch_hph: SwitchCompat
    lateinit var switch_amp: SwitchCompat
    lateinit var switch_impedance: SwitchCompat
    lateinit var button_uhqa: Button
    lateinit var button_hph: Button
    lateinit var button_amp: Button
    lateinit var button_impedance: Button
    lateinit var po: Dialog
    lateinit var title: TextView
    lateinit var desc: TextView
    lateinit var desc_ext: TextView
    lateinit var uhqa_stats: TextView
    lateinit var hph_stats: TextView
    lateinit var amp_stats: TextView
    lateinit var impedance_stats: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_conf)

        // Bind controller
        this_activity = findViewById(R.id.activity_audio_conf)
        cv_title = findViewById(R.id.cv_app_menu_1)
        cv_uhqa = findViewById(R.id.cv_app_menu_1_1)
        cv_hph = findViewById(R.id.cv_app_menu_1_2)
        cv_amp = findViewById(R.id.cv_app_menu_1_3)
        cv_impedance = findViewById(R.id.cv_app_menu_1_4)
        uhqa_stats = findViewById(R.id.switch_app_menu_1_1_text)
        hph_stats = findViewById(R.id.switch_app_menu_1_2_1_text)
        amp_stats = findViewById(R.id.switch_app_menu_1_3_1_text)
        impedance_stats = findViewById(R.id.switch_app_menu_1_4_1_text)
        switch_uhqa = findViewById(R.id.switch_app_menu_1)
        switch_hph = findViewById(R.id.switch_app_menu_1_2)
        switch_amp = findViewById(R.id.switch_app_menu_1_3)
        switch_impedance = findViewById(R.id.switch_app_menu_1_4)
        button_uhqa = findViewById(R.id.button_app_menu_1)
        button_hph = findViewById(R.id.button_app_menu_1_2)
        button_amp = findViewById(R.id.button_app_menu_1_3)
        button_impedance = findViewById(R.id.button_app_menu_1_4)

        // Hide title bar
        Objects.requireNonNull(supportActionBar)?.hide()

        // Set an animation transition
        window.enterTransition = Explode()
        window.returnTransition = Fade()

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
        if (kernel_ver?.compareTo(upstream)!! > 0) {
            au.DumpFile(an.uhqa_kernel_4_x, an.uhqa_force_file)
            au.DumpFile(an.amp_kernel_4_x, an.amp_force_file)

            // Not supported on 4.9 Kernel since techpack aren't
            // use older than wcd9335
            au.WriteToFile("0", an.hph_force_file)
            au.WriteToFile("0", an.impedance_force_file)
        } else if (kernel_ver.compareTo(legacy) > 0) {
            au.DumpFile(an.uhqa_kernel_3_x, an.uhqa_force_file)
            au.DumpFile(an.hph_kernel_3_x, an.hph_force_file)
            au.DumpFile(an.amp_kernel_3_x, an.amp_force_file)
            au.DumpFile(an.impedance_kernel_3_x, an.impedance_file)
        }

        // Set switch value on init
        val uhqa_value = au.readFromFile(this, an.uhqa_file)
        val hph_value = au.readFromFile(this, an.hph_file)
        val amp_value = au.readFromFile(this, an.amp_file)
        val impedance_value = au.readFromFile(this, an.impedance_file)

        switch_uhqa.isChecked = uhqa_value?.compareTo("1")!! > 0
        switch_hph.isChecked = hph_value?.compareTo("1")!! > 0
        switch_amp.isChecked = amp_value?.compareTo("1")!! > 0
        switch_impedance.isChecked = impedance_value?.compareTo("1")!! > 0

        cv_title.setOnClickListener {
            val i = Intent(this@AudioConfActivity, MainActivity::class.java)
            val sharedView: View = cv_title
            val transitionName = getString(R.string.PLACEHOLDER)
            val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this@AudioConfActivity, sharedView, transitionName)
            startActivity(i, transitionActivityOptions.toBundle())
        }

        switch_uhqa.setOnClickListener {
            if (kernel_ver.compareTo(upstream) > 0) {
                if (switch_uhqa.isChecked) {
                    au.WriteToFile("1", an.uhqa_kernel_4_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_1_toast_uhqa_scs))
                    uhqa_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_enable))
                } else {
                    au.WriteToFile("0", an.uhqa_kernel_4_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_1_toast_uhqa_fid))
                    uhqa_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_disable))
                }
            } else if (kernel_ver.compareTo(legacy) > 0) {
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
            if (kernel_ver.compareTo(upstream) > 0) {
                switch_hph.isChecked = false
                switch_hph.isClickable = false
                switch_hph.isEnabled = false
                setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_2_toast_hph_ne))
                hph_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_unsupport), resources.getString(R.string.state_disable))
            } else if (kernel_ver.compareTo(legacy) > 0) {
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
            if (kernel_ver.compareTo(upstream) > 0) {
                if (switch_amp.isChecked) {
                    au.WriteToFile("1", an.amp_kernel_4_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_3_toast_amp_scs))
                    amp_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_enable))
                } else {
                    au.WriteToFile("0", an.amp_kernel_4_x)
                    setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_3_toast_amp_fid))
                    amp_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_support), resources.getString(R.string.state_disable))
                }
            } else if (kernel_ver.compareTo(legacy) > 0) {
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
            if (kernel_ver.compareTo(upstream) > 0) {
                switch_impedance.isChecked = false
                switch_impedance.isClickable = false
                switch_impedance.isEnabled = false
                setSnackBar(findViewById(android.R.id.content), resources.getString(R.string.app_menu_4_toast_impedance_ne))
                impedance_stats.text = String.format("%s %s | %s", resources.getString(R.string.state_info), resources.getString(R.string.state_unsupport), resources.getString(R.string.state_disable))
            } else if (kernel_ver.compareTo(legacy) > 0) {
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

        button_uhqa.setOnClickListener {
            // Initiate dialog
            po = Dialog(this@AudioConfActivity)

            // Call dialog
            ShowPopup(resources.getString(R.string.app_menu_1_text), resources.getString(R.string.app_menu_1_desc), resources.getString(R.string.app_menu_1_desc_ext))
        }

        button_hph.setOnClickListener {
            // Initiate dialog
            po = Dialog(this@AudioConfActivity)

            // Call dialog
            ShowPopup(resources.getString(R.string.app_menu_2_text), resources.getString(R.string.app_menu_2_desc), resources.getString(R.string.app_menu_2_desc_ext))
        }

        button_amp.setOnClickListener {
            // Initiate dialog
            po = Dialog(this@AudioConfActivity)

            // Call dialog
            ShowPopup(resources.getString(R.string.app_menu_3_text), resources.getString(R.string.app_menu_3_desc), resources.getString(R.string.app_menu_3_desc_ext))
        }

        button_impedance.setOnClickListener {
            // Initiate dialog
            po = Dialog(this@AudioConfActivity)

            // Call dialog
            ShowPopup(resources.getString(R.string.app_menu_4_text), resources.getString(R.string.app_menu_4_desc), resources.getString(R.string.app_menu_4_desc_ext))
        }
    }

    private fun ShowPopup(text_1: String?, text_2: String?, text_3: String?) {
        po.setContentView(R.layout.activity_pop_up)
        title = po.findViewById(R.id.text_pop_up_1)
        desc = po.findViewById(R.id.text_pop_up_desc_1)
        desc_ext = po.findViewById(R.id.text_pop_up_desc_2)
        title.text = text_1
        desc.text = text_2
        desc_ext.text = text_3
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