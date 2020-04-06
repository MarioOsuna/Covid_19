package com.example.covid_19;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class RegistrarActivity extends AppCompatActivity {
    EditText editTextName, editTextEmail, editTextAp, editTextDNI, editTextPass1, editTextPass2;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        editTextName = findViewById(R.id.editTextNombre);
        editTextAp = findViewById(R.id.editTextAp);
        editTextEmail = findViewById(R.id.editTextE_mail);
        editTextDNI = findViewById(R.id.editTextDni);
        editTextPass1 = findViewById(R.id.editTextpass);
        editTextPass2 = findViewById(R.id.editTextpass2);
        button = findViewById(R.id.buttonRegistrar);

    }
}
