package com.proyecto.TFG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Recuperar_1Activity extends AppCompatActivity {
    EditText editText;
    Button button;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar1);
        editText = findViewById(R.id.EmailPrimero);
        button = findViewById(R.id.ButtonEnviar);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conexion()) {
                    if (!editText.getText().toString().equals("")) {
                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Recuperar_1Activity.this);
                        dialogo1.setTitle(R.string.New_pass);
                        dialogo1.setMessage(R.string.Email_pass);
                        dialogo1.setCancelable(false);
                        dialogo1.setPositiveButton(R.string.Confirmar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                aceptar();
                                //finish();
                            }
                        });
                        dialogo1.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                cancelar();
                            }
                        });
                        dialogo1.show();
                    } else {
                        Toast.makeText(Recuperar_1Activity.this, R.string.toast_campos, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    AlertDialog.Builder dialogo2 = new AlertDialog.Builder(Recuperar_1Activity.this);
                    dialogo2.setTitle("Error");
                    dialogo2.setIcon(R.drawable.out);
                    dialogo2.setMessage(R.string.error_servidor);
                    dialogo2.setCancelable(false);
                    dialogo2.setPositiveButton(R.string.Confirmar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            //aceptar();
                            dialogo1.dismiss();
                        }
                    });
                    dialogo2.show();
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
                new MailJob.Mail("asistentecovidtfg@gmail.com", editText.getText().toString(), "New Password",
                        "Su nueva contraseña:\n\n" + pass+"\n\n\nPara cualquier problema por favor pongase en contacto " +
                                "con nosotros a través del siguiente correo: asistentecovidtfg@gmail.com,\nSaludos."));
    }

    public void cancelar() {
        finish();
        Intent i = new Intent(Recuperar_1Activity.this, LoginActivity.class);
        startActivity(i);

    }

    public boolean conexion() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


}