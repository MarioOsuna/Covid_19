package com.mario.covid_19;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PrevencionActivity extends AppCompatActivity {
    Button button;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prevencion);
        button = findViewById(R.id.button3);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PrevencionActivity.this, MenuActivity.class);
                i.putExtra("email", MenuActivity.EMAIL_INICIADO);
                startActivity(i);
            }
        });
    }
}
