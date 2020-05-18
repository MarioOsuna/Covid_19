package com.mario.covid_19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MenuActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    Button buttonOff, buttonOP1, buttonOP2, buttonOP3;
    TextView textView;
    private GoogleApiClient googleApiClient;
    public static String EMAIL_INICIADO;

    static String SERVIDOR = "http://tfgcovid19.000webhostapp.com/";
    static String LISTADOENFERMOS = "listadoCSVEnfermo.php";

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        EMAIL_INICIADO = email;

        textView.setText(email);

        buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                if (conexion()) {

                    ListarEnfermos listarEnfermos = new ListarEnfermos();
                    listarEnfermos.execute();
                } else {
                    AlertDialog.Builder dialogo2 = new AlertDialog.Builder(MenuActivity.this);
                    dialogo2.setTitle("Error");
                    dialogo2.setIcon(R.drawable.out);
                    dialogo2.setMessage(R.string.error_servidor);
                    dialogo2.setCancelable(true);
                    dialogo2.show();
                }


            }
        });
        buttonOP3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conexion()) {
                Intent i = new Intent(MenuActivity.this, MapaCasosActivity.class);
                startActivity(i);
                }else {
                    AlertDialog.Builder dialogo2 = new AlertDialog.Builder(MenuActivity.this);
                    dialogo2.setTitle("Error");
                    dialogo2.setIcon(R.drawable.out);
                    dialogo2.setMessage(R.string.error_servidor);
                    dialogo2.setCancelable(true);
                    dialogo2.show();
                }
            }
        });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class ListarEnfermos extends AsyncTask<String, Void, Void> {
        String total = "";
        String mail = " ";
        Boolean existe = false;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            existe = false;

            try {
                String[] lineas = total.split("\n");
                for (String lin : lineas) {
                    String[] campos = lin.split(",");

                    if (campos[0].toString().equals(EMAIL_INICIADO)) {

                        existe = true;

                    }

                }
                if (existe) {
                    Intent i = new Intent(MenuActivity.this, HospitalActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(MenuActivity.this, TestActivity.class);
                    startActivity(i);
                }


            } catch (RuntimeException e) {
                e.printStackTrace();
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(MenuActivity.this);
                dialogo1.setTitle("Error");
                dialogo1.setIcon(R.drawable.out);
                dialogo1.setMessage(R.string.error_servidor);
                dialogo1.setCancelable(true);
                dialogo1.show();
            }

        }

        @Override
        protected Void doInBackground(String... strings) {
            //String script = strings[0];
            //mail = strings[0];

            URL url = null;
            HttpURLConnection httpURLConnection = null;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                url = new URL(SERVIDOR + LISTADOENFERMOS);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                    String linea = "";

                    while ((linea = br.readLine()) != null) {
                        total += linea + "\n";
                    }

                    br.close();
                    inputStream.close();


                }
            } catch (IOException e) {
                e.printStackTrace();
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(MenuActivity.this);
                dialogo1.setTitle("Error");
                dialogo1.setIcon(R.drawable.out);
                dialogo1.setMessage(R.string.error_servidor);
                dialogo1.setCancelable(true);
                dialogo1.show();
            } catch (RuntimeException r) {

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(MenuActivity.this);
                dialogo1.setTitle("Error");
                dialogo1.setIcon(R.drawable.out);
                dialogo1.setMessage(R.string.error_servidor);
                dialogo1.setCancelable(true);
                dialogo1.show();
            }

            Log.i("CONEXION", total);

            return null;
        }
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
