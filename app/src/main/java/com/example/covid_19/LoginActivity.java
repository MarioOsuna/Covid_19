package com.example.covid_19;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    TextView textView, registrar;
    EditText editTextMail, editTextPass;
    Button buttonInicio, buttonFace, buttonGoog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registrar = findViewById(R.id.textViewRegistrar);
        editTextMail = findViewById(R.id.editTextEmail);
        editTextPass = findViewById(R.id.editTextPass);
        buttonInicio = findViewById(R.id.buttonLogin);
        buttonFace = findViewById(R.id.buttonLogFacebook);
        buttonGoog = findViewById(R.id.buttonLogGoogle);
    }
}
