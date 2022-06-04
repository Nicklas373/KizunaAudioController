package com.hana.kizunaaudiocontroller.activity

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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar
import com.hana.kizunaaudiocontroller.R
import com.hana.kizunaaudiocontroller.audioUtils.AudioUtils
import com.hana.kizunaaudiocontroller.databinding.ActivityAudioConfBinding
import com.hana.kizunaaudiocontroller.databinding.ActivityPopUpConfBinding
import com.hana.kizunaaudiocontroller.databinding.ActivityPopUpInfoDetailsBinding
import com.hana.kizunaaudiocontroller.databinding.ContentAudioConfBinding
import com.hana.kizunaaudiocontroller.datasource.AudioNode

class AudioConfActivity : AppCompatActivity() {
    // Late binding
    private lateinit var confBinding: ContentAudioConfBinding

    // Separate environment
    private lateinit var po: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding
        val binding = ActivityAudioConfBinding.inflate(layoutInflater)
        confBinding = binding.detailContent
        setContentView(binding.root)

        supportActionBar?.hide()

        // Set an animation transition
        window.enterTransition = Explode()
        window.returnTransition = Fade()

        // sharedPreference begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)
        val save = pref.edit()

        // Getting sharedPreference value if exist
        val exp = pref.getBoolean("EXP_FEATURES", false)
        if (exp) {
            confBinding.switchAppMenu15.isChecked = true
            TransitionManager.beginDelayedTransition(confBinding.cvAppMenu16, AutoTransition())
            confBinding.cvAppMenu16.visibility = View.VISIBLE
        } else {
            confBinding.switchAppMenu15.isChecked = false
            TransitionManager.beginDelayedTransition(confBinding.cvAppMenu16, AutoTransition())
            confBinding.cvAppMenu16.visibility = View.INVISIBLE
        }

        // Getting sharedPreference value if exist
        // Configure theme interface
        val nightMode = pref.getBoolean("MODE_NIGHT", false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            val nightColor = Color.parseColor("#2286c3")

            binding.cvAppMenu1.setCardBackgroundColor(nightColor)
            confBinding.cvAppMenu11.setCardBackgroundColor(nightColor)
            confBinding.cvAppMenu12.setCardBackgroundColor(nightColor)
            confBinding.cvAppMenu13.setCardBackgroundColor(nightColor)
            confBinding.cvAppMenu14.setCardBackgroundColor(nightColor)
            confBinding.cvAppMenu15.setCardBackgroundColor(nightColor)
            confBinding.cvAppMenu16.setCardBackgroundColor(nightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            val colorDrawable = ColorDrawable(Color.parseColor("#64b5f6"))
            supportActionBar?.setBackgroundDrawable(colorDrawable)
        }

        // Call necessary class
        val an = AudioNode()
        val au = AudioUtils()

        // Read kernel files
        au.exportKernelFile(an.kernelDump)
        val kernelVer = au.readFromFile(this, an.kernelFile)

        // Declare static string for kernel version
        val upstream = "4.9"
        val midstream = "4.4"
        val legacy = "3.18"

        // Check kernel version
        if (kernelVer == upstream || kernelVer == midstream) {
            au.dumpFile(an.uhqaKernelUpstream, an.uhqaForceFile)
            au.dumpFile(an.ampKernelUpstream, an.ampForceFile)
            au.dumpFile(an.gatingKernelUpstream, an.gatingForceFile)

            // Not supported on 4.9 Kernel since techpack aren't
            // use older than wcd9335
            au.writeToFile("0", an.hphForceFile)
            au.writeToFile("0", an.impedanceForceFile)
        } else if (kernelVer == legacy) {
            au.dumpFile(an.uhqaKernelLegacy, an.uhqaForceFile)
            au.dumpFile(an.hphKernelLegacy, an.hphForceFile)
            au.dumpFile(an.ampKernelLegacy, an.ampForceFile)
            au.dumpFile(an.impedanceKernelLegacy, an.impedanceForceFile)
            au.dumpFile(an.gatingKernelLegacy, an.gatingForceFile)
        }

        // Set switch value on init
        val uhqaValue = au.readFromFile(this, an.uhqaFile)
        val hphValue = au.readFromFile(this, an.hphFile)
        val ampValue = au.readFromFile(this, an.ampFile)
        val impedanceValue = au.readFromFile(this, an.impedanceFile)
        val gatingValue = au.readFromFile(this, an.gatingFile)

        confBinding.switchAppMenu1.isChecked = uhqaValue > "1"
        confBinding.switchAppMenu12.isChecked = hphValue > "1"
        confBinding.switchAppMenu13.isChecked = ampValue > "1"
        confBinding.switchAppMenu14.isChecked = impedanceValue > "1"
        confBinding.switchAppMenu16.isChecked = gatingValue > "1"

        binding.cvAppMenu1.setOnClickListener {
            val i = Intent(this@AudioConfActivity, MainActivity::class.java)
            val sharedView: View = binding.cvAppMenu1
            val transitionName = getString(R.string.app_main_menu_1)
            val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(
                this@AudioConfActivity,
                sharedView,
                transitionName
            )
            startActivity(i, transitionActivityOptions.toBundle())
        }

        confBinding.switchAppMenu1.setOnClickListener {
            if (kernelVer == upstream || kernelVer == midstream) {
                if (confBinding.switchAppMenu1.isChecked) {
                    au.writeToFile("1", an.uhqaKernelUpstream)
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.app_menu_1_toast_uhqa_scs)
                    )
                    confBinding.switchAppMenu11Text.text = String.format(
                        "%s %s | %s",
                        resources.getString(R.string.state_info),
                        resources.getString(R.string.state_support),
                        resources.getString(R.string.state_enable)
                    )
                } else {
                    au.writeToFile("0", an.uhqaKernelUpstream)
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.app_menu_1_toast_uhqa_fid)
                    )
                    confBinding.switchAppMenu11Text.text = String.format(
                        "%s %s | %s",
                        resources.getString(R.string.state_info),
                        resources.getString(R.string.state_support),
                        resources.getString(R.string.state_disable)
                    )
                }
            } else if (kernelVer == legacy) {
                if (confBinding.switchAppMenu1.isChecked) {
                    au.writeToFile("1", an.uhqaKernelLegacy)
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.app_menu_1_toast_uhqa_scs)
                    )
                    confBinding.switchAppMenu11Text.text = String.format(
                        "%s %s | %s",
                        resources.getString(R.string.state_info),
                        resources.getString(R.string.state_support),
                        resources.getString(R.string.state_enable)
                    )
                } else {
                    au.writeToFile("0", an.uhqaKernelLegacy)
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.app_menu_1_toast_uhqa_fid)
                    )
                    confBinding.switchAppMenu11Text.text = String.format(
                        "%s %s | %s",
                        resources.getString(R.string.state_info),
                        resources.getString(R.string.state_support),
                        resources.getString(R.string.state_disable)
                    )
                }
            }
            confBinding.switchAppMenu11Text.visibility = View.VISIBLE
            au.dropFile(an.uhqaFile)
        }

        confBinding.switchAppMenu12.setOnClickListener {
            if (kernelVer == upstream || kernelVer == midstream) {
                confBinding.switchAppMenu12.isChecked = false
                confBinding.switchAppMenu12.isClickable = false
                confBinding.switchAppMenu12.isEnabled = false
                setSnackBar(
                    findViewById(android.R.id.content),
                    resources.getString(R.string.app_menu_2_toast_hph_ne)
                )
                confBinding.switchAppMenu121Text.text = String.format(
                    "%s %s | %s",
                    resources.getString(R.string.state_info),
                    resources.getString(R.string.state_unsupport),
                    resources.getString(R.string.state_disable)
                )
            } else if (kernelVer == legacy) {
                if (confBinding.switchAppMenu12.isChecked) {
                    au.writeToFile("1", an.hphKernelLegacy)
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.app_menu_2_toast_hph_scs)
                    )
                    confBinding.switchAppMenu121Text.text = String.format(
                        "%s %s | %s",
                        resources.getString(R.string.state_info),
                        resources.getString(R.string.state_support),
                        resources.getString(R.string.state_enable)
                    )
                } else {
                    au.writeToFile("0", an.hphKernelLegacy)
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.app_menu_2_toast_hph_fid)
                    )
                    confBinding.switchAppMenu121Text.text = String.format(
                        "%s %s | %s",
                        resources.getString(R.string.state_info),
                        resources.getString(R.string.state_support),
                        resources.getString(R.string.state_disable)
                    )
                }
            }
            confBinding.switchAppMenu121Text.visibility = View.VISIBLE
            au.dropFile(an.hphFile)
        }

        confBinding.switchAppMenu13.setOnClickListener {
            if (kernelVer == upstream || kernelVer == midstream) {
                if (confBinding.switchAppMenu13.isChecked) {
                    au.writeToFile("1", an.ampKernelUpstream)
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.app_menu_3_toast_amp_scs)
                    )
                    confBinding.switchAppMenu131Text.text = String.format(
                        "%s %s | %s",
                        resources.getString(R.string.state_info),
                        resources.getString(R.string.state_support),
                        resources.getString(R.string.state_enable)
                    )
                } else {
                    au.writeToFile("0", an.ampKernelUpstream)
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.app_menu_3_toast_amp_fid)
                    )
                    confBinding.switchAppMenu131Text.text = String.format(
                        "%s %s | %s",
                        resources.getString(R.string.state_info),
                        resources.getString(R.string.state_support),
                        resources.getString(R.string.state_disable)
                    )
                }
            } else if (kernelVer == legacy) {
                if (confBinding.switchAppMenu13.isChecked) {
                    au.writeToFile("1", an.ampKernelLegacy)
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.app_menu_3_toast_amp_scs)
                    )
                    confBinding.switchAppMenu131Text.text = String.format(
                        "%s %s | %s",
                        resources.getString(R.string.state_info),
                        resources.getString(R.string.state_support),
                        resources.getString(R.string.state_enable)
                    )
                } else {
                    au.writeToFile("0", an.ampKernelLegacy)
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.app_menu_3_toast_amp_fid)
                    )
                    confBinding.switchAppMenu131Text.text = String.format(
                        "%s %s | %s",
                        resources.getString(R.string.state_info),
                        resources.getString(R.string.state_support),
                        resources.getString(R.string.state_disable)
                    )
                }
            }
            confBinding.switchAppMenu131Text.visibility = View.VISIBLE
            au.dropFile(an.ampFile)
        }

        confBinding.switchAppMenu14.setOnClickListener {
            if (kernelVer == upstream || kernelVer == midstream) {
                confBinding.switchAppMenu14.isChecked = false
                confBinding.switchAppMenu14.isClickable = false
                confBinding.switchAppMenu14.isEnabled = false
                setSnackBar(
                    findViewById(android.R.id.content),
                    resources.getString(R.string.app_menu_4_toast_impedance_ne)
                )
                confBinding.switchAppMenu141Text.text = String.format(
                    "%s %s | %s",
                    resources.getString(R.string.state_info),
                    resources.getString(R.string.state_unsupport),
                    resources.getString(R.string.state_disable)
                )
            } else if (kernelVer == legacy) {
                if (confBinding.switchAppMenu14.isChecked) {
                    au.writeToFile("1", an.impedanceKernelLegacy)
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.app_menu_4_toast_impedance_scs)
                    )
                    confBinding.switchAppMenu141Text.text = String.format(
                        "%s %s | %s",
                        resources.getString(R.string.state_info),
                        resources.getString(R.string.state_support),
                        resources.getString(R.string.state_enable)
                    )
                } else {
                    au.writeToFile("0", an.impedanceKernelLegacy)
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.app_menu_4_toast_impedance_fid)
                    )
                    confBinding.switchAppMenu141Text.text = String.format(
                        "%s %s | %s",
                        resources.getString(R.string.state_info),
                        resources.getString(R.string.state_support),
                        resources.getString(R.string.state_disable)
                    )
                }
            }
            confBinding.switchAppMenu141Text.visibility = View.VISIBLE
            au.dropFile(an.impedanceFile)
        }

        confBinding.switchAppMenu15.setOnClickListener {
            if (confBinding.switchAppMenu15.isChecked) {
                confBinding.cvAppMenu16.visibility = View.VISIBLE
                save.putBoolean("EXP_FEATURES", true)
                save.apply()
            } else {
                confBinding.cvAppMenu16.visibility = View.INVISIBLE
                save.putBoolean("EXP_FEATURES", false)
                save.apply()
            }
        }

        confBinding.switchAppMenu16.setOnClickListener {
            if (kernelVer == upstream || kernelVer == midstream) {
                if (confBinding.switchAppMenu16.isChecked) {
                    au.writeToFile("1", an.gatingKernelUpstream)
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.app_menu_6_toast_gating_scs)
                    )
                    confBinding.switchAppMenu161Text.text = String.format(
                        "%s %s | %s",
                        resources.getString(R.string.state_info),
                        resources.getString(R.string.state_support),
                        resources.getString(R.string.state_enable)
                    )
                } else {
                    au.writeToFile("0", an.gatingKernelUpstream)
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.app_menu_6_toast_gating_fid)
                    )
                    confBinding.switchAppMenu161Text.text = String.format(
                        "%s %s | %s",
                        resources.getString(R.string.state_info),
                        resources.getString(R.string.state_support),
                        resources.getString(R.string.state_disable)
                    )
                }
            } else if (kernelVer == legacy) {
                if (confBinding.switchAppMenu16.isChecked) {
                    au.writeToFile("1", an.gatingKernelLegacy)
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.app_menu_6_toast_gating_scs)
                    )
                    confBinding.switchAppMenu16.text = String.format(
                        "%s %s | %s",
                        resources.getString(R.string.state_info),
                        resources.getString(R.string.state_support),
                        resources.getString(R.string.state_enable)
                    )
                } else {
                    au.writeToFile("0", an.gatingKernelLegacy)
                    setSnackBar(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.app_menu_6_toast_gating_fid)
                    )
                    confBinding.switchAppMenu161Text.text = String.format(
                        "%s %s | %s",
                        resources.getString(R.string.state_info),
                        resources.getString(R.string.state_support),
                        resources.getString(R.string.state_disable)
                    )
                }
            }
            confBinding.switchAppMenu161Text.visibility = View.VISIBLE
            au.dropFile(an.gatingFile)
        }

        confBinding.buttonAppMenu1.setOnClickListener {
            // Initiate dialog
            po = Dialog(this@AudioConfActivity)

            // Call dialog
            showPopup(
                resources.getString(R.string.app_menu_1_text),
                resources.getString(R.string.app_menu_1_desc)
            )
        }

        confBinding.buttonAppMenu12.setOnClickListener {
            // Initiate dialog
            po = Dialog(this@AudioConfActivity)

            // Call dialog
            showPopup(
                resources.getString(R.string.app_menu_2_text),
                resources.getString(R.string.app_menu_2_desc)
            )
        }

        confBinding.buttonAppMenu13.setOnClickListener {
            // Initiate dialog
            po = Dialog(this@AudioConfActivity)

            // Call dialog
            showPopup(
                resources.getString(R.string.app_menu_3_text),
                resources.getString(R.string.app_menu_3_desc)
            )
        }

        confBinding.buttonAppMenu14.setOnClickListener {
            // Initiate dialog
            po = Dialog(this@AudioConfActivity)

            // Call dialog
            showPopup(
                resources.getString(R.string.app_menu_4_text),
                resources.getString(R.string.app_menu_4_desc)
            )
        }

        confBinding.buttonAppMenu15.setOnClickListener {
            // Initiate dialog
            po = Dialog(this@AudioConfActivity)

            // Call dialog
            showPopup(
                resources.getString(R.string.app_menu_5_text),
                resources.getString(R.string.app_menu_5_desc)
            )
        }

        confBinding.buttonAppMenu16.setOnClickListener {
            // Initiate dialog
            po = Dialog(this@AudioConfActivity)

            // Call dialog
            showPopup(
                resources.getString(R.string.app_menu_6_text),
                resources.getString(R.string.app_menu_6_desc)
            )
        }

        binding.buttonTitle.setOnClickListener {
            // Initiate dialog
            po = Dialog(this@AudioConfActivity)

            // Call dialog
            showPopupAdd(
                resources.getString(R.string.app_menu_1_info_title),
                resources.getString(R.string.app_menu_1_info_details),
                resources.getString(R.string.app_menu_1_info_ext)
            )
        }
    }

    private fun showPopup(text_1: String?, text_2: String?) {
        // Initialize binding
        val poBinding = ActivityPopUpConfBinding.inflate(layoutInflater)
        po.setContentView(poBinding.root)

        // Set binding
        poBinding.textPopUp1.text = text_1
        poBinding.textPopUpDesc1.text = text_2

        // sharedPreference begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)

        // Getting sharedPreference value if exist
        // Configure theme interface
        val nightMode = pref.getBoolean("MODE_NIGHT", false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            val nightColor = Color.parseColor("#607d8b")
            poBinding.cvTitle1.setCardBackgroundColor(nightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            val dayColor = Color.parseColor("#2286c3")
            poBinding.cvTitle1.setCardBackgroundColor(dayColor)
        }

        poBinding.cvPopUp1.setOnClickListener { po.dismiss() }
        po.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        po.show()
    }

    private fun showPopupAdd(text_1: String?, text_2: String?, text_3: String) {
        // Initialize binding
        val poBinding = ActivityPopUpInfoDetailsBinding.inflate(layoutInflater)
        po.setContentView(poBinding.root)

        // Set binding
        poBinding.textPopUp1.text = text_1
        poBinding.textPopUpDesc1.text = text_2
        poBinding.textPopUpDesc2.text = text_3

        // sharedPreference begin
        val pref = applicationContext.getSharedPreferences("KAO_MAIN_PREF", 0)

        // Getting sharedPreference value if exist
        // Configure theme interface
        val nightMode = pref.getBoolean("MODE_NIGHT", false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            val nightColor = Color.parseColor("#607d8b")
            poBinding.cvTitle1.setCardBackgroundColor(nightColor)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            val dayColor = Color.parseColor("#2286c3")
            poBinding.cvTitle1.setCardBackgroundColor(dayColor)
        }

        poBinding.cvPopUp1.setOnClickListener { po.dismiss() }
        po.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        po.show()
    }

    private fun setSnackBar(root: View?, snackTitle: String?) {
        val snackBar = Snackbar.make(root!!, snackTitle!!, Snackbar.LENGTH_SHORT)
        snackBar.show()
        val view = snackBar.view
        val txtV = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        val typeface = ResourcesCompat.getFont(this@AudioConfActivity, R.font.mandorin_font)
        txtV.gravity = Gravity.START
        txtV.typeface = typeface
    }
}