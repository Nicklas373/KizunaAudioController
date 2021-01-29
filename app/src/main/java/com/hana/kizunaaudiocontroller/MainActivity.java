package com.hana.kizunaaudiocontroller;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity {

    // Declare controller
    CardView mm_1;
    SwitchMaterial theme_switcher;

    // Declare variables
    private static final int REQUEST_PERMISSIONS = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind controller
        mm_1 = findViewById(R.id.cv_app_main_menu_1);
        theme_switcher = findViewById(R.id.theme_switcher);

        // Binding root class
        AudioRoot ar = new AudioRoot();

        // Ask necessary permission for storage access
        ar.run(this::ask_perm);

        // Ask necessary root access
        ar.run(ar::checkRooted);

        // Sharedprefences begin
        SharedPreferences pref = getApplicationContext().getSharedPreferences("KAO_MAIN_PREF", 0);
        SharedPreferences.Editor save = pref.edit();

        // Getting sharedpreferences value if exist
        boolean night_mode = pref.getBoolean("MODE_NIGHT", false);
        if (night_mode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Set an animation transition
        getWindow().setEnterTransition(new Explode());
        getWindow().setReturnTransition(new Fade());

        mm_1.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AudioConfActivity.class);

            View sharedView = mm_1;
            String transitionName = getString(R.string.PLACEHOLDER);

            ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, sharedView, transitionName);
            startActivity(i, transitionActivityOptions.toBundle());
        });

        theme_switcher.setOnClickListener(v -> {
            if (theme_switcher.isChecked()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                save.putBoolean("MODE_NIGHT", false);
                save.apply();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                save.putBoolean("MODE_NIGHT", true);
                save.apply();
            }
        });
    }

    private void ask_perm() {
        // start runtime permission
        boolean hasPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
        } else {
            Toast.makeText(MainActivity.this, "Permissions already granted :3", Toast.LENGTH_SHORT).show();
        }
    }

}