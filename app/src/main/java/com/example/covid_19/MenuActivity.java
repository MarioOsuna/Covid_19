package com.example.covid_19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

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

        Bundle datos = this.getIntent().getExtras();

        String email = datos.getString("email");

        textView.setText(email);

        buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*  Intent i=new Intent(MenuActivity.this, LoginActivity.class);
                startActivity(i);*/
                LoginManager.getInstance().logOut();
                Intent i = new Intent(MenuActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
        buttonOP1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, PrevencionActivity.class);
                startActivity(i);
            }
        });
        buttonOP2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, TestActivity.class);
                startActivity(i);
            }
        });
        buttonOP3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

    }
}
