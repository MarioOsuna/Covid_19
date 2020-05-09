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
import android.os.StrictMode;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    TextView registrar;
    EditText editTextMail, editTextPass;
    Button buttonInicio;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private GoogleApiClient googleApiClient;
    static String INSERTARUSUARIO = "insertarUsuariosPOST.php";

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
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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

    //Permisos para obtener el mail y nombre de facebook
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
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
                    comprobarDatos.execute(" ", " ", " ");
                } else {
                    Toast.makeText(LoginActivity.this, R.string.toast_campos, Toast.LENGTH_SHORT).show();
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
                   // Profile profileDefault = Profile.getCurrentProfile();
                    //Librería usada para poder mostrar la foto de perfil de facebook con una transformación circular

                    email = object.getString("email");

                    //Compruebo el email aquí,pasandole el nombre y el apellido de la persona:
                    ComprobarDatos comprobarDatos = new ComprobarDatos();
                    comprobarDatos.execute(email, object.getString("first_name"), object.getString("last_name"));

                } catch (Exception e) {
                    Log.e("E-MainActivity", "getFaceBook" + e.toString());
                }
            }
        });
        Bundle parameters = new Bundle();
        //solicitando el campo email
        parameters.putString("fields", "first_name,last_name,email");
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
            Toast.makeText(this, R.string.toast_gps, Toast.LENGTH_SHORT).show();
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

            GoogleSignInAccount acct = result.getSignInAccount();
            email = acct.getEmail();

            //Paso el mail para comprobar si existe en la bd y le paso el nombre y apellido del usuario para que en caso de que no exista insertarlo
            ComprobarDatos comprobarDatos = new ComprobarDatos();
            comprobarDatos.execute(email, acct.getGivenName(), acct.getFamilyName());

        } else {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
        Log.e("GoogleSignIn", "OnConnectionFailed: " + connectionResult);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //Comprobar si los datos existen
    private class ComprobarDatos extends AsyncTask<String, Void, Void> {
        String total = "";
        String mail = " ";
        String nom = " ";
        String Aps = " ";
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
                    //Se comprueba si algún correo de la tabla coincide con el correo introducido o bien si el mail obtenido del inicio con Google/Facebook coincide con alguno de la tabla
                    if (campos[0].toString().equals(editTextMail.getText().toString()) || campos[0].toString().equals(mail)) {

                        existe = true;
                        // System.out.println(campos[0]);
                    }
                    //Si el correo existe se comprueba si la contraseña existe
                    if (existe) {
                        if (campos[5].toString().equals(editTextPass.getText().toString())) {
                            correcto = true;
                            System.out.println(correcto);
                        }

                    }
                    //Mail solo está lleno si se inicia sesión con google/facebook, por lo que si está vacio no se ha seleccionado ninguna de estas dos opciones
                    if (!mail.equals(" ")) {
                        Inicio = true;
                    }
                    // Toast.makeText(LoginActivity.this, "Valor de "+mail, Toast.LENGTH_SHORT).show();
                    System.out.println("Existe: " + existe);
                    System.out.println("Contraseña: " + correcto);
                    System.out.println("Iniciado con google/facebook: " + Inicio);
                    System.out.println(campos[0]);
                }
                //Si existe el mail introducido y la contraseña es correcta o bien si se ha iniciado sesión con facebook y el correo existe, lanzamos el menu
                if ((existe && correcto) || (Inicio && existe)) {

                    Intent i = new Intent(LoginActivity.this, MenuActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("email", mail);
                    startActivity(i);
                } else {
                    //Si no coincide, pero si se ha iniciado sesión con Facebook/Google se inserta los datos obtenidos de este en la tabla y lanza el menú
                    if (Inicio) {


                        Insertar(mail, " ", nom, Aps, " ", PasswordGenerator.getPassword(
                                PasswordGenerator.MINUSCULAS +
                                        PasswordGenerator.MAYUSCULAS +
                                        PasswordGenerator.ESPECIALES, 8));
                        Intent i = new Intent(LoginActivity.this, MenuActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("email", mail);
                        startActivity(i);

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

                        //Por el contrario si no coincide, el mail o la contraseña son erroneas
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
                dialogo1.setMessage(R.string.error_servidor);
                dialogo1.setCancelable(true);
                dialogo1.show();
            }

        }

        @Override
        protected Void doInBackground(String... strings) {
            //String script = strings[0];
            //Obtenemos los datos que se le pasan al hilo, si es un inicio normal se le pasarán datos vacios
            mail = strings[0];
            nom = strings[1];
            Aps = strings[2];

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
                dialogo1.setMessage(R.string.error_servidor);
                dialogo1.setCancelable(true);
                dialogo1.show();
            } catch (RuntimeException r) {

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(LoginActivity.this);
                dialogo1.setTitle("Error");
                dialogo1.setMessage(R.string.error_servidor);
                dialogo1.setCancelable(true);
                dialogo1.show();
            }

            Log.i("CONEXION", total);

            return null;
        }
    }

    //Método para insertar datos en la tabla de usuario si se inicia sesión con google/facebook y no está registrado
    private void Insertar(String Mail, String Dni, String nombre, String ap, String movil, String pass) {
        String script = null;

        script = SERVIDOR + INSERTARUSUARIO;

        String contenido = "";
        try {
            System.out.println(script);
            URLConnection conexion = null;

            conexion = new URL(script).openConnection();
            //conexion.connect();
            conexion.setDoOutput(true);

            PrintStream ps = new PrintStream(conexion.getOutputStream());

            ps.print("Mail=" + Mail);
            ps.print("&Dni=" + Dni);
            ps.print("&Nombre=" + nombre);
            ps.print("&Apellidos=" + ap);
            ps.print("&Movil=" + movil);
            ps.print("&Pass=" + pass);

            InputStream inputStream = conexion.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String linea = "";

            while ((linea = br.readLine()) != null) {
                contenido += linea;
            }


            br.close();
            Log.i("Contenido: ", contenido);


        } catch (MalformedURLException ex) {
        } catch (IOException e) {
        } catch (RuntimeException a) {
            a.printStackTrace();
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(LoginActivity.this);
            dialogo1.setTitle("Error");
            dialogo1.setMessage(R.string.error_servidor);
            dialogo1.setCancelable(true);
            dialogo1.show();
        }

    }


}

//clase para generar una contraseña al azar a los usuarios que inician sesión con google/facebook, de manera que solo pueden entrar con una de estas opciones
class PasswordGenerator {

    public static String NUMEROS = "0123456789";

    public static String MAYUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";

    public static String ESPECIALES = "ñÑ";


    public static String getPinNumber() {
        return getPassword(NUMEROS, 4);
    }

    public static String getPassword() {
        return getPassword(8);
    }

    public static String getPassword(int length) {
        return getPassword(NUMEROS + MAYUSCULAS + MINUSCULAS, length);
    }

    public static String getPassword(String key, int length) {
        String pswd = "";

        for (int i = 0; i < length; i++) {
            pswd += (key.charAt((int) (Math.random() * key.length())));
        }

        return pswd;
    }
}
