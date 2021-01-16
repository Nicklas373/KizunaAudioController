package com.hana.kizunaaudiocontroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import static com.hana.kizunaaudiocontroller.AudioUtils.readFromFile;

public class AudioConfActivity extends AppCompatActivity {

    // Declaring controller
    ConstraintLayout this_activity;
    CardView placeholder_1_1, placeholder_1_2;
    SwitchCompat placeholder_switch_1_1;
    Button placeholder_button_1;
    Dialog po;
    TextView title, desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_conf);

        // Bind controller
        this_activity = findViewById(R.id.activity_audio_conf);
        placeholder_1_1 = findViewById(R.id.placeholder_1_1);
        placeholder_1_2 = findViewById(R.id.placeholder_1_2);
        placeholder_switch_1_1 = findViewById(R.id.placeholder_switch_1);
        placeholder_button_1 = findViewById(R.id.placeholder_button_1);

        // Hide title bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Set an animation transition
        getWindow().setEnterTransition(new Explode());
        getWindow().setReturnTransition(new Fade());

        // Set color for dark mode
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                this_activity.setBackgroundColor(Color.parseColor("#29434e"));
                placeholder_1_1.setCardBackgroundColor(Color.parseColor("#1565c0"));
                placeholder_1_2.setCardBackgroundColor(Color.parseColor("#1565c0"));
                break;
        }

        // Call necessary class
        AudioNode an = new AudioNode();
        AudioUtils au = new AudioUtils();

        // Read kernel files
        au.ExportKernelFile(an.kernel_dump);
        String kernel_ver = readFromFile(this, an.kernel_file);

        if (!kernel_ver.equals("4.9.")) {
            Toast.makeText(AudioConfActivity.this, "Sucess ! | Kernel version : " + kernel_ver, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(AudioConfActivity.this, "Kernel version : " + kernel_ver, Toast.LENGTH_SHORT).show();
        }


        placeholder_switch_1_1.setOnClickListener(v -> {
            if (placeholder_switch_1_1.isChecked()) {
                au.writeToFile(an.uhqa_kernel_4_x, "1");
                au.DropFile(an.uhqa_file);
            } else {
                au.writeToFile(an.uhqa_kernel_4_x, "0");
                au.DropFile(an.uhqa_file);
            }
        });

        placeholder_button_1.setOnClickListener(v -> {
            // Initiate dialog
            po = new Dialog(AudioConfActivity.this);
            String title = getResources().getString(R.string.PLACEHOLDER);
            String desc = getResources().getString(R.string.PLACEHOLDER_LONG_DESC);

            // Call dialog
            ShowPopup(placeholder_button_1, title, desc);
        });
    }

    public void ShowPopup(View v, String text_1, String text_2) {
        po.setContentView(R.layout.activity_pop_up);

        title = po.findViewById(R.id.placeholder_pop_up_1);
        desc = po.findViewById(R.id.placeholder_pop_up_desc_1);

        title.setText(text_1);
        desc.setText(text_2);

        title.setOnClickListener(v1 -> po.dismiss());
        po.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        po.show();
    }
}