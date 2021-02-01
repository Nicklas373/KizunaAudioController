package com.hana.kizunaaudiocontroller;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import static com.hana.kizunaaudiocontroller.AudioUtils.readFromFile;

public class AudioConfActivity extends AppCompatActivity {

    // Declaring controller
    ConstraintLayout this_activity;
    CardView cv_title, cv_uhqa, cv_hph, cv_amp, cv_impedance;
    SwitchCompat switch_uhqa, switch_hph, switch_amp, switch_impedance;
    Button button_uhqa, button_hph, button_amp, button_impedance;
    Dialog po;
    TextView title, desc, desc_ext, uhqa_stats, hph_stats, amp_stats, impedance_stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_conf);

        // Bind controller
        this_activity = findViewById(R.id.activity_audio_conf);
        cv_title = findViewById(R.id.cv_app_menu_1);
        cv_uhqa = findViewById(R.id.cv_app_menu_1_1);
        cv_hph = findViewById(R.id.cv_app_menu_1_2);
        cv_amp = findViewById(R.id.cv_app_menu_1_3);
        cv_impedance = findViewById(R.id.cv_app_menu_1_4);
        uhqa_stats = findViewById(R.id.switch_app_menu_1_1_text);
        hph_stats = findViewById(R.id.switch_app_menu_1_2_1_text);
        amp_stats = findViewById(R.id.switch_app_menu_1_3_1_text);
        impedance_stats = findViewById(R.id.switch_app_menu_1_4_1_text);
        switch_uhqa = findViewById(R.id.switch_app_menu_1);
        switch_hph = findViewById(R.id.switch_app_menu_1_2);
        switch_amp = findViewById(R.id.switch_app_menu_1_3);
        switch_impedance = findViewById(R.id.switch_app_menu_1_4);
        button_uhqa = findViewById(R.id.button_app_menu_1);
        button_hph = findViewById(R.id.button_app_menu_1_2);
        button_amp = findViewById(R.id.button_app_menu_1_3);
        button_impedance = findViewById(R.id.button_app_menu_1_4);

        // Hide title bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Set an animation transition
        getWindow().setEnterTransition(new Explode());
        getWindow().setReturnTransition(new Fade());

        // Call necessary class
        AudioNode an = new AudioNode();
        AudioUtils au = new AudioUtils();

        // Read kernel files
        au.ExportKernelFile(an.kernel_dump);
        String kernel_ver = readFromFile(this, an.kernel_file);

        // Declare static string for kernel version
        String upstream = "4.9";
        String legacy = "3.18";

        // Check kernel version
        if (kernel_ver.compareTo(upstream) > 0) {
            au.DumpFile(an.uhqa_kernel_4_x, an.uhqa_force_file);
            au.DumpFile(an.amp_kernel_4_x, an.amp_force_file);

            // Not supported on 4.9 Kernel since techpack aren't
            // use older than wcd9335
            au.WriteToFile("0", an.hph_force_file);
            au.WriteToFile("0", an.impedance_force_file);
        } else if (kernel_ver.compareTo(legacy) > 0) {
            au.DumpFile(an.uhqa_kernel_3_x, an.uhqa_force_file);
            au.DumpFile(an.hph_kernel_3_x, an.hph_force_file);
            au.DumpFile(an.amp_kernel_3_x, an.amp_force_file);
            au.DumpFile(an.impedance_kernel_3_x, an.impedance_file);
        }

        // Set switch value on init
        String uhqa_value = readFromFile(this, an.uhqa_file);
        String hph_value = readFromFile(this, an.hph_file);
        String amp_value = readFromFile(this, an.amp_file);
        String impedance_value = readFromFile(this, an.impedance_file);

        switch_uhqa.setChecked(uhqa_value.compareTo("1") > 0);
        switch_hph.setChecked(hph_value.compareTo("1") > 0);
        switch_amp.setChecked(amp_value.compareTo("1") > 0);
        switch_impedance.setChecked(impedance_value.compareTo("1") > 0);

        cv_title.setOnClickListener(v -> {
            Intent i = new Intent(AudioConfActivity.this, MainActivity.class);

            View sharedView = cv_title;
            String transitionName = getString(R.string.PLACEHOLDER);

            ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(AudioConfActivity.this, sharedView, transitionName);
            startActivity(i, transitionActivityOptions.toBundle());
        });

        switch_uhqa.setOnClickListener(v -> {
            if (kernel_ver.compareTo(upstream) > 0) {
                if (switch_uhqa.isChecked()) {
                    au.WriteToFile("1", an.uhqa_kernel_4_x);
                    setSnackBar(findViewById(android.R.id.content), getResources().getString(R.string.app_menu_1_toast_uhqa_scs));
                    uhqa_stats.setText(String.format("%s %s | %s", getResources().getString(R.string.state_info), getResources().getString(R.string.state_support), getResources().getString(R.string.state_enable)));
                } else {
                    au.WriteToFile("0", an.uhqa_kernel_4_x);
                    uhqa_stats.setText(String.format("%s %s | %s", getResources().getString(R.string.state_info), getResources().getString(R.string.state_support), getResources().getString(R.string.state_disable)));
                }
                uhqa_stats.setVisibility(View.VISIBLE);
                au.DropFile(an.uhqa_file);
            } else if (kernel_ver.compareTo(legacy) > 0) {
                if (switch_uhqa.isChecked()) {
                    au.WriteToFile("1", an.uhqa_kernel_3_x);
                    uhqa_stats.setText(String.format("%s %s | %s", getResources().getString(R.string.state_info), getResources().getString(R.string.state_support), getResources().getString(R.string.state_enable)));
                } else {
                    au.WriteToFile("0", an.uhqa_kernel_3_x);
                    uhqa_stats.setText(String.format("%s %s | %s", getResources().getString(R.string.state_info), getResources().getString(R.string.state_support), getResources().getString(R.string.state_disable)));
                }
                uhqa_stats.setVisibility(View.VISIBLE);
                au.DropFile(an.uhqa_file);
            }
        });

        switch_hph.setOnClickListener(v -> {
            if (kernel_ver.compareTo(upstream) > 0) {
                switch_hph.setChecked(false);
                switch_hph.setClickable(false);
                switch_hph.setEnabled(false);
                setSnackBar(findViewById(android.R.id.content), "Your kernel doesn't support HPH Module");
                hph_stats.setText(String.format("%s %s | %s", getResources().getString(R.string.state_info), getResources().getString(R.string.state_unsupport), getResources().getString(R.string.state_disable)));
                hph_stats.setVisibility(View.VISIBLE);
                au.DropFile(an.hph_file);
            } else if (kernel_ver.compareTo(legacy) > 0) {
                if (switch_hph.isChecked()) {
                    au.WriteToFile("1", an.hph_kernel_3_x);
                    hph_stats.setText(String.format("%s %s | %s", getResources().getString(R.string.state_info), getResources().getString(R.string.state_support), getResources().getString(R.string.state_enable)));
                    hph_stats.setVisibility(View.VISIBLE);
                    au.DropFile(an.hph_file);
                } else {
                    au.WriteToFile("0", an.hph_kernel_3_x);
                    hph_stats.setText(String.format("%s %s | %s", getResources().getString(R.string.state_info), getResources().getString(R.string.state_support), getResources().getString(R.string.state_disable)));
                    hph_stats.setVisibility(View.VISIBLE);
                    au.DropFile(an.hph_file);
                }
            }
        });

        switch_amp.setOnClickListener(v -> {
            if (kernel_ver.compareTo(upstream) > 0) {
                if (switch_amp.isChecked()) {
                    au.WriteToFile("1", an.amp_kernel_4_x);
                    amp_stats.setText(String.format("%s %s | %s", getResources().getString(R.string.state_info), getResources().getString(R.string.state_support), getResources().getString(R.string.state_enable)));
                    amp_stats.setVisibility(View.VISIBLE);
                    au.DropFile(an.amp_file);
                } else {
                    au.WriteToFile("0", an.amp_kernel_4_x);
                    amp_stats.setText(String.format("%s %s | %s", getResources().getString(R.string.state_info), getResources().getString(R.string.state_support), getResources().getString(R.string.state_disable)));
                    amp_stats.setVisibility(View.VISIBLE);
                    au.DropFile(an.amp_file);
                }
            } else if (kernel_ver.compareTo(legacy) > 0) {
                if (switch_amp.isChecked()) {
                    au.WriteToFile("1", an.amp_kernel_3_x);
                    amp_stats.setText(String.format("%s %s | %s", getResources().getString(R.string.state_info), getResources().getString(R.string.state_support), getResources().getString(R.string.state_enable)));
                    amp_stats.setVisibility(View.VISIBLE);
                    au.DropFile(an.amp_file);
                } else {
                    au.WriteToFile("0", an.amp_kernel_3_x);
                    amp_stats.setText(String.format("%s %s | %s", getResources().getString(R.string.state_info), getResources().getString(R.string.state_support), getResources().getString(R.string.state_disable)));
                    amp_stats.setVisibility(View.VISIBLE);
                    au.DropFile(an.amp_file);
                }
            }
        });

        switch_impedance.setOnClickListener(v -> {
            if (kernel_ver.compareTo(upstream) > 0) {
                switch_impedance.setChecked(false);
                switch_impedance.setClickable(false);
                switch_impedance.setEnabled(false);
                setSnackBar(findViewById(android.R.id.content), "Your kernel doesn't support Headphone Impedance Detection Module");
                impedance_stats.setText(String.format("%s %s | %s", getResources().getString(R.string.state_info), getResources().getString(R.string.state_unsupport), getResources().getString(R.string.state_disable)));
                impedance_stats.setVisibility(View.VISIBLE);
                au.DropFile(an.hph_file);
            } else if (kernel_ver.compareTo(legacy) > 0) {
                if (switch_impedance.isChecked()) {
                    au.WriteToFile("1", an.impedance_kernel_3_x);
                    impedance_stats.setText(String.format("%s %s | %s", getResources().getString(R.string.state_info), getResources().getString(R.string.state_support), getResources().getString(R.string.state_enable)));
                    impedance_stats.setVisibility(View.VISIBLE);
                    au.DropFile(an.impedance_file);
                } else {
                    au.WriteToFile("0", an.impedance_kernel_3_x);
                    impedance_stats.setText(String.format("%s %s | %s", getResources().getString(R.string.state_info), getResources().getString(R.string.state_support), getResources().getString(R.string.state_disable)));
                    impedance_stats.setVisibility(View.VISIBLE);
                    au.DropFile(an.impedance_file);
                }
            }
        });

        button_uhqa.setOnClickListener(v -> {
            // Initiate dialog
            po = new Dialog(AudioConfActivity.this);

            // Call dialog
            ShowPopup(button_uhqa, getResources().getString(R.string.app_menu_1_text), getResources().getString(R.string.app_menu_1_desc), getResources().getString(R.string.app_menu_1_desc_ext));
        });

        button_hph.setOnClickListener(v -> {
            // Initiate dialog
            po = new Dialog(AudioConfActivity.this);

            // Call dialog
            ShowPopup(button_hph,  getResources().getString(R.string.app_menu_2_text), getResources().getString(R.string.app_menu_2_desc), getResources().getString(R.string.app_menu_2_desc_ext));
        });

        button_amp.setOnClickListener(v -> {
            // Initiate dialog
            po = new Dialog(AudioConfActivity.this);

            // Call dialog
            ShowPopup(button_amp,  getResources().getString(R.string.app_menu_3_text), getResources().getString(R.string.app_menu_3_desc), getResources().getString(R.string.app_menu_3_desc_ext));
        });

        button_impedance.setOnClickListener(v -> {
            // Initiate dialog
            po = new Dialog(AudioConfActivity.this);

            // Call dialog
            ShowPopup(button_impedance,  getResources().getString(R.string.app_menu_4_text), getResources().getString(R.string.app_menu_4_desc), getResources().getString(R.string.app_menu_4_desc_ext));
        });
    }

    public void ShowPopup(View v, String text_1, String text_2, String text_3) {
        po.setContentView(R.layout.activity_pop_up);

        title = po.findViewById(R.id.text_pop_up_1);
        desc = po.findViewById(R.id.text_pop_up_desc_1);
        desc_ext = po.findViewById(R.id.text_pop_up_desc_2);

        title.setText(text_1);
        desc.setText(text_2);
        desc_ext.setText(text_3);

        title.setOnClickListener(v1 -> po.dismiss());
        po.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        po.show();
    }

    public void setSnackBar(View root, String snackTitle) {
        Snackbar snackbar = Snackbar.make(root, snackTitle, Snackbar.LENGTH_SHORT);
        snackbar.show();
        View view = snackbar.getView();
        TextView txtv = view.findViewById(com.google.android.material.R.id.snackbar_text);
        Typeface typeface = ResourcesCompat.getFont(AudioConfActivity.this, R.font.mandorin_font);
        txtv.setGravity(Gravity.START);
        txtv.setTypeface(typeface);
    }
}