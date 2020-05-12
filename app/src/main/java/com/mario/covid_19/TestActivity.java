package com.mario.covid_19;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class TestActivity extends AppCompatActivity {
    CheckBox checkBox, checkBox1, checkBox2, checkBox3, checkBox4;
    Button button, button2;
    static String SERVIDOR = "http://tfgcovid19.000webhostapp.com/";
    static String INSERTARENFERMO = "insertarEnfermosPOST.php";
    static String LISTARUSUARIO = "listadoCSVUsuario.php";
    LocationManager locationManager;
    LocationListener locationListener;
    int tiempoRefresco = 500;
    int PEDI_PERMISO_GPS = 1;
    public static String Latitud = "0";
    public static String Longitud = "0";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        button2 = findViewById(R.id.button4);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

        } else {
            iniciarLocalizacion();
        }


        checkBox = findViewById(R.id.checkBox);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);
        button = findViewById(R.id.button);
        //locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TestActivity.this, MenuActivity.class);
                i.putExtra("email", MenuActivity.EMAIL_INICIADO);
                startActivity(i);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conexion()) {
                    if (checkIfLocationOpened()) {

                        if (checkBox1.isChecked() && checkBox.isChecked() && checkBox2.isChecked() && checkBox3.isChecked()) {

                            ComprobarUsuario comprobarUsuario = new ComprobarUsuario();
                            comprobarUsuario.execute(Latitud, Longitud);

                            Intent i = new Intent(TestActivity.this, HospitalActivity.class);
                            startActivity(i);
                            //Toast.makeText(TestActivity.this, "Latitud: " + Latitud + " Longitud: " + Longitud, Toast.LENGTH_SHORT).show();
                            Log.i("Coordenadas", "Latitud: " + Latitud + " Longitud: " + Longitud);
                        } else {
                            if (checkBox1.isChecked() && checkBox4.isChecked()) {
                                //   Toast.makeText(TestActivity.this, "Estás enfermo", Toast.LENGTH_SHORT).show();

                                ComprobarUsuario comprobarUsuario = new ComprobarUsuario();
                                comprobarUsuario.execute(Latitud, Longitud);

                                Intent i = new Intent(TestActivity.this, HospitalActivity.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(TestActivity.this, R.string.No_enfermo, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else{
                        AlertDialog.Builder dialogo2 = new AlertDialog.Builder(TestActivity.this);
                        dialogo2.setTitle("Error");
                        dialogo2.setMessage(R.string.ubicación);
                        dialogo2.setCancelable(true);
                        dialogo2.show();
                    }
                } else {
                    AlertDialog.Builder dialogo2 = new AlertDialog.Builder(TestActivity.this);
                    dialogo2.setTitle("Error");
                    dialogo2.setMessage(R.string.error_servidor);
                    dialogo2.setCancelable(true);
                    dialogo2.show();
                }

            }
        });


    }

    private boolean checkIfLocationOpened() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        System.out.println("Provider contains=> " + provider);
        if (provider.contains("gps") || provider.contains("network")) {
            return true;
        }
        return false;
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


    //Obtener los datos de la tabla usuario e introducirlos en la tabla enfermo
    private class ComprobarUsuario extends AsyncTask<String, Void, Void> {
        String total = "";
        String Mail = "";
        String Email = MenuActivity.EMAIL_INICIADO;
        String Lat;
        String Long;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String[] lineas = total.split("\n");
            for (String lin : lineas) {
                String[] campos = lin.split(",");
                if (campos[0].toString().equals(Email)) {
                    Mail = campos[0].toString();


                }
                System.out.println(campos[0]);
            }
            Insertar(Mail, Lat, Long);


        }

        @Override
        protected Void doInBackground(String... strings) {
            //String script = strings[0];
            Lat = strings[0];
            Long = strings[1];

            URL url = null;
            HttpURLConnection httpURLConnection = null;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                url = new URL(SERVIDOR + LISTARUSUARIO);
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
            } catch (RuntimeException e) {
                e.printStackTrace();
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(TestActivity.this);
                dialogo1.setTitle("Error");
                dialogo1.setMessage(R.string.error_servidor);
                dialogo1.setCancelable(true);
                dialogo1.show();
            }

            Log.i("CONEXION", total);

            return null;
        }
    }

    private void Insertar(String Mail, String lat, String lon) {
        String script = null;

        script = SERVIDOR + INSERTARENFERMO;

        String contenido = "";
        try {
            System.out.println(script);
            URLConnection conexion = null;

            conexion = new URL(script).openConnection();
            //conexion.connect();
            conexion.setDoOutput(true);

            PrintStream ps = new PrintStream(conexion.getOutputStream());

            ps.print("Mail_enfermo=" + Mail);
            ps.print("&lat=" + lat);
            ps.print("&long=" + lon);

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
        } catch (RuntimeException r) {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(TestActivity.this);
            dialogo1.setTitle("Error");
            dialogo1.setMessage(R.string.error_servidor);
            dialogo1.setCancelable(true);
            dialogo1.show();
        }

    }

    private void iniciarLocalizacion() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Localizacion localizacion = new Localizacion();
        // localizacion.setMainActivity(this, tvMensaje);


        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, localizacion);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, localizacion);

        //  tvMensaje.setText("Localizacion agregada");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarLocalizacion();
                return;
            }
        }

    }


}

class Localizacion implements LocationListener {


    @Override
    public void onLocationChanged(Location location) {
        // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas

        TestActivity.Latitud = "" + location.getLatitude();
        TestActivity.Longitud = "" + location.getLongitude();


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Toast.makeText(Localizacion2.this, "Estás enfermo", Toast.LENGTH_SHORT).show();
        //.setText("GPS Activado");
    }

    @Override
    public void onProviderDisabled(String provider) {
        //tvMensaje.setText("GPS Desactivado");
    }
}
