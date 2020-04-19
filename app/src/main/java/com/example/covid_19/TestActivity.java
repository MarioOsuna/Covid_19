package com.example.covid_19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class TestActivity extends AppCompatActivity {
    CheckBox checkBox, checkBox1, checkBox2, checkBox3, checkBox4;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        checkBox = findViewById(R.id.checkBox);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /*    Intent i=new Intent(TestActivity.this, MenuActivity.class);
                startActivity(i);*/
                if (checkBox1.isChecked() && checkBox.isChecked() && checkBox2.isChecked() && checkBox3.isChecked()) {
                    Toast.makeText(TestActivity.this, "Estás enfermo", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkBox1.isChecked() && checkBox4.isChecked()) {
                        Toast.makeText(TestActivity.this, "Estás enfermo", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TestActivity.this, "No estás enfermo", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
}
