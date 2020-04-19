package com.example.covid_19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegistrarActivity extends AppCompatActivity {
    EditText editTextName, editTextEmail, editTextAp, editTextDNI, editTextPass1, editTextPass2, editTextTlf;
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
        editTextTlf = findViewById(R.id.editTextTLF);
        button = findViewById(R.id.buttonRegistrar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegistrarActivity.this, MenuActivity.class);
                startActivity(i);
            }
        });
    }
}
