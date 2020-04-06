package com.example.covid_19;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {
    Button buttonOff, buttonOP1, buttonOP2, buttonOP3;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        buttonOff = findViewById(R.id.buttonOff);
        buttonOP1 = findViewById(R.id.buttonPrevencion);
        buttonOP2 = findViewById(R.id.buttonSintomas);
        buttonOP3 = findViewById(R.id.buttonMapa);
        textView = findViewById(R.id.textViewNombre);
    }
}
