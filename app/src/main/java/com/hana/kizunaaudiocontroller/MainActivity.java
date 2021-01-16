package com.hana.kizunaaudiocontroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Declare controller
    CardView placeholder_1;

    // Declare variables
    private static final int REQUEST_PERMISSIONS = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind controller
        placeholder_1 = findViewById(R.id.placeholder_1);

        // Ask necessary permission for storage access
        ask_perm();

        // Ask necessary root access
        AudioRoot ar = new AudioRoot();
        ar.run(ar::checkRooted);

        placeholder_1.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AudioConfActivity.class);

            View sharedView = placeholder_1;
            String transitionName = getString(R.string.PLACEHOLDER);

            ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, sharedView, transitionName);
            startActivity(i, transitionActivityOptions.toBundle());
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