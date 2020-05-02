package com.example.covid_19;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    TextView registrar;
    EditText editTextMail, editTextPass;
    Button buttonInicio;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private GoogleApiClient googleApiClient;

    private SignInButton signInButton;
    private int SIGN_INT_CODE = 777;
    LocationManager locationManager;
    LocationListener locationListener;
    int tiempoRefresco = 500;
    int PEDI_PERMISO_GPS = 1;
    static String SERVIDOR = "http://tfgcovid19.000webhostapp.com/";
    static String LISTADOUSU = "listadoCSVUsuario.php";
    ProgressDialog progressDialog = null;
    Boolean existe, correcto;
    String email;
    public static Boolean iniciado = false;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        registrar = findViewById(R.id.textViewRegistrar);
        editTextMail = findViewById(R.id.editTextEmail);
        editTextPass = findViewById(R.id.editTextPass);
        buttonInicio = findViewById(R.id.buttonLogin);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.loginButton);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                /*.addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)*/
                .build();

        signInButton = (SignInButton) findViewById(R.id.signInButton);


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(i, SIGN_INT_CODE);
                iniciado = true;


            }
        });


        loginButton.setReadPermissions(Arrays.asList("email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getFaceBookProfileDetails(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, R.string.Cancelar, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });


        buttonInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!editTextPass.getText().toString().equals("") && !editTextMail.getText().toString().equals("")) {
                    ComprobarDatos comprobarDatos = new ComprobarDatos();
                    comprobarDatos.execute(LISTADOUSU);
                } else {
                    Toast.makeText(LoginActivity.this, "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show();
                }

            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegistrarActivity.class);
                startActivity(i);
            }
        });

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

               /* textViewAltitud.setText("Altitud: " + location.getAltitude());
                textViewLatitud.setText("Latitud: " + location.getLatitude());
                textViewLongitud.setText("Longitud: " + location.getLongitude());*/

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PEDI_PERMISO_GPS);

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, tiempoRefresco, 0, locationListener);
    }

    private void getFaceBookProfileDetails(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            //object retorna lo indicado en paramters.putString("fields", "email") en este caso, solo contiene el email
            @Override
            public void onCompleted(final JSONObject object, GraphResponse response) {
                try {

                    //Profile clase que contiene las características báscias de la cuenta de facebook (No retorna email)
                    Profile profileDefault = Profile.getCurrentProfile();
                    //Librería usada para poder mostrar la foto de perfil de facebook con una transformación circular
                    //   Picasso.with(MainActivity.this).load(profileDefault.getProfilePictureUri(100,100)).transform(new CircleTransform()).into(imageViewPhoto);
                    // textViewEmail.setText(object.getString("email"));
                    email = object.getString("email");
                    //Compruebo directamente el email aquí:
                    ComprobarDatos comprobarDatos = new ComprobarDatos();
                    comprobarDatos.execute(email);

                } catch (Exception e) {
                    Log.e("E-MainActivity", "getFaceBook" + e.toString());
                }
            }
        });
        Bundle parameters = new Bundle();
        //solicitando el campo email
        parameters.putString("fields", "email");
        request.setParameters(parameters);
        request.executeAsync();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PEDI_PERMISO_GPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {
                    return;

                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, tiempoRefresco, 0, locationListener);
                }
            }
        } else {
            Toast.makeText(this, "Debes concederme permiso para usar el GPS!!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_INT_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    //Inicio de sesión con google
    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            // Intent i = new Intent(LoginActivity.this, MenuActivity.class);
            //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
           /* GoogleSignInAccount acct = result.getSignInAccount();
            acct.getEmail();*/
            //startActivity(i);
            GoogleSignInAccount acct = result.getSignInAccount();
            email = acct.getEmail();

            ComprobarDatos comprobarDatos = new ComprobarDatos();
            comprobarDatos.execute(email);

        } else {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Error de conexión!", Toast.LENGTH_SHORT).show();
        Log.e("GoogleSignIn", "OnConnectionFailed: " + connectionResult);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //Comprobar si los datos existen
    private class ComprobarDatos extends AsyncTask<String, Void, Void> {
        String total = "";
        String mail = " ";
        Boolean Inicio = false;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            existe = false;
            correcto = false;
            try {
                String[] lineas = total.split("\n");
                for (String lin : lineas) {
                    String[] campos = lin.split(",");

                    if (campos[0].toString().equals(editTextMail.getText().toString()) || campos[0].toString().equals(mail)) {
                        mail = campos[0].toString();
                        existe = true;
                        System.out.println(campos[0]);
                    }
                    if (existe) {
                        if (campos[5].toString().equals(editTextPass.getText().toString())) {
                            correcto = true;
                            System.out.println(campos[0]);
                        }

                    }
                    if (mail != " ") {
                        Inicio = true;
                    }
                    // Toast.makeText(LoginActivity.this, "Valor de "+mail, Toast.LENGTH_SHORT).show();
                    System.out.println("Existe: " + existe);
                    System.out.println(campos[0]);
                }

                if ((existe && correcto) || (Inicio && existe)) {

                    //Toast.makeText(LoginActivity.this, "Usuario ya está registrado", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(LoginActivity.this, MenuActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("email", mail);
                    startActivity(i);
                } else {
                    if (Inicio) {
                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(LoginActivity.this);
                        dialogo1.setTitle("Inicio fallido");
                        dialogo1.setMessage("La cuenta de facebook o google con la que intenta iniciar sesión no ha sido registrada");
                        dialogo1.setCancelable(true);
                        dialogo1.show();
                        if (iniciado) {
                            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<com.google.android.gms.common.api.Status>() {
                                @Override
                                public void onResult(@NonNull com.google.android.gms.common.api.Status status) {
                                    if (status.isSuccess()) {
                                        iniciado = false;
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        // LoginManager.getInstance().logOut();
                    } else {
                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(LoginActivity.this);
                        dialogo1.setTitle("Error");
                        dialogo1.setMessage("Email o contraseña no reconocidas, pruebe de nuevo o bien si no está registrado por favor registrese");
                        dialogo1.setCancelable(true);
                        dialogo1.show();
                    }
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(LoginActivity.this);
                dialogo1.setTitle("Error");
                dialogo1.setMessage("No se puede conectar con el servidor, porfavor compruebe su conexión a internet");
                dialogo1.setCancelable(true);
                dialogo1.show();
            }

        }

        @Override
        protected Void doInBackground(String... strings) {
            //String script = strings[0];
            mail = strings[0];

            URL url = null;
            HttpURLConnection httpURLConnection = null;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                url = new URL(SERVIDOR + LISTADOUSU);
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
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(LoginActivity.this);
                dialogo1.setTitle("Error");
                dialogo1.setMessage("No se puede conectar con el servidor, porfavor compruebe su conexión a internet");
                dialogo1.setCancelable(true);
                dialogo1.show();
            } catch (RuntimeException r) {

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(LoginActivity.this);
                dialogo1.setTitle("Error");
                dialogo1.setMessage("No se puede conectar con el servidor, porfavor compruebe su conexión a internet");
                dialogo1.setCancelable(true);
                dialogo1.show();
            }

            Log.i("CONEXION", total);

            return null;
        }
    }


}
