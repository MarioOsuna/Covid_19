package com.example.covid_19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MenuActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    Button buttonOff, buttonOP1, buttonOP2, buttonOP3;
    TextView textView;
    private GoogleApiClient googleApiClient;
   public static String EMAIL_INICIADO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        buttonOff = findViewById(R.id.buttonOff);
        buttonOP1 = findViewById(R.id.buttonPrevencion);
        buttonOP2 = findViewById(R.id.buttonSintomas);
        buttonOP3 = findViewById(R.id.buttonMapa);
        textView = findViewById(R.id.textViewNombre);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, MenuActivity.this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                /*.addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)*/
                .build();
        Bundle datos = this.getIntent().getExtras();

        String email = datos.getString("email");
        EMAIL_INICIADO=email;

        textView.setText(email);

        buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*  Intent i=new Intent(MenuActivity.this, LoginActivity.class);
                startActivity(i);*/
                LoginManager.getInstance().logOut();

                if (LoginActivity.iniciado) {
                    Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull com.google.android.gms.common.api.Status status) {
                            if (status.isSuccess()) {

                            } else {
                                Toast.makeText(MenuActivity.this, "Sesión no cerrada", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
