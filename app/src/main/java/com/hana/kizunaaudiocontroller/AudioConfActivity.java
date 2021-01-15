package com.hana.kizunaaudiocontroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.view.View;
import android.widget.TextView;

import java.util.Objects;

import static com.hana.kizunaaudiocontroller.AudioUtils.readFromFile;

public class AudioConfActivity extends AppCompatActivity {

    // Declaring controller
    ConstraintLayout this_activity;
    CardView placeholder_1_1, placeholder_1_2;
    SwitchCompat placeholder_switch_1_1;
    TextView placeholder_dump_text_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_conf);

        // Bind controller
        this_activity = findViewById(R.id.activity_audio_conf);
        placeholder_1_1 = findViewById(R.id.placeholder_1_1);
        placeholder_1_2 = findViewById(R.id.placeholder_1_2);
        placeholder_switch_1_1 = findViewById(R.id.placeholder_switch_1);
        placeholder_dump_text_1 = findViewById(R.id.placeholder_dump_1);

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

        // Import kernel files
        au.DumpFile(an.uhqa_kernel_4_x, an.uhqa_force_file);
        placeholder_dump_text_1.setText(readFromFile(this, an.uhqa_file));

        placeholder_switch_1_1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (placeholder_switch_1_1.isChecked()) {
                    au.writeToFile(an.uhqa_kernel_4_x, "1");
                    au.DropFile(an.uhqa_file);
                } else {
                    au.writeToFile(an.uhqa_kernel_4_x, "0");
                    au.DropFile(an.uhqa_file);
                }
            }
        });
    }
}