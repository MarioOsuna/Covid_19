package com.mario.covid_19;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Recuperar_1Activity extends AppCompatActivity {
    EditText editText;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar1);
        editText = findViewById(R.id.EmailPrimero);
        button = findViewById(R.id.ButtonEnviar);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals("")) {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Recuperar_1Activity.this);
                    dialogo1.setTitle(R.string.New_pass);
                    dialogo1.setMessage("Si pulsa Confirmar recibirá un mensaje con una nueva contraseña");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            aceptar();
                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            cancelar();
                        }
                    });
                    dialogo1.show();
                } else {
                    Toast.makeText(Recuperar_1Activity.this, R.string.toast_campos, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void aceptar() {
        String pass = PasswordGenerator.getPassword(
                PasswordGenerator.MINUSCULAS +
                        PasswordGenerator.MAYUSCULAS +
                        PasswordGenerator.ESPECIALES, 8);
        Intent i = new Intent(Recuperar_1Activity.this, Recuperar_2_Activity.class);
        i.putExtra("email", editText.getText().toString());
        i.putExtra("pass", pass);


        startActivity(i);
        new MailJob("asistentecovidtfg@gmail.com", "11junio2020").execute(
                new MailJob.Mail("asistentecovidtfg@gmail.com", editText.getText().toString(), "New Password", "Su nueva contraseña:\n\n" + pass));
    }

    public void cancelar() {
        finish();
        Intent i = new Intent(Recuperar_1Activity.this, LoginActivity.class);
        startActivity(i);

    }


}