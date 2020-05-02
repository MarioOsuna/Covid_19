package com.example.covid_19;

import androidx.annotation.NonNull;
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
    Button button;
    static String SERVIDOR = "http://tfgcovid19.000webhostapp.com/";
    static String INSERTARENFERMO = "insertarEnfermosPOST.php";
    static String LISTARUSUARIO = "listadoCSVUsuario.php";
    LocationManager locationManager;
    LocationListener locationListener;
    int tiempoRefresco = 500;
    int PEDI_PERMISO_GPS = 1;
    String Latitud="0";
    String Longitud="0";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        checkBox = findViewById(R.id.checkBox);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);
        button = findViewById(R.id.button);
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkBox1.isChecked() && checkBox.isChecked() && checkBox2.isChecked() && checkBox3.isChecked()) {
                    //Toast.makeText(TestActivity.this, "Estás enfermo", Toast.LENGTH_SHORT).show();

                    ComprobarUsuario comprobarUsuario = new ComprobarUsuario();
                    comprobarUsuario.execute(Latitud,Longitud);

                    Intent i = new Intent(TestActivity.this, HospitalActivity.class);
                    startActivity(i);
                    Toast.makeText(TestActivity.this, "Latitud: "+Latitud+" Longitud: "+Longitud, Toast.LENGTH_SHORT).show();

                } else {
                    if (checkBox1.isChecked() && checkBox4.isChecked()) {
                        //   Toast.makeText(TestActivity.this, "Estás enfermo", Toast.LENGTH_SHORT).show();

                        ComprobarUsuario comprobarUsuario = new ComprobarUsuario();
                        comprobarUsuario.execute(Latitud,Longitud);

                          Intent i = new Intent(TestActivity.this, HospitalActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(TestActivity.this, "No estás enfermo", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


        //Obtener Latitud y longitud
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Latitud = Double.toString(location.getLatitude());
                Longitud = Double.toString(location.getLongitude());

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


    //Obtener los datos de la tabla usuario e introducirlos en la tabla enfermo
    private class ComprobarUsuario extends AsyncTask<String, Void, Void> {
        String total = "";
        String Mail = "";
        String Dni = "";
        String nombre = "";
        String ap = "";
        String movil = "";
        String Email=MenuActivity.EMAIL_INICIADO;
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
                    Dni = campos[1].toString();
                    nombre = campos[2].toString();
                    ap = campos[3].toString();
                    movil = campos[4].toString();

                    //System.out.println(campos[0]);
                }
                System.out.println(campos[0]);
            }
            Insertar(Mail, Dni, nombre, ap, movil,Lat,Long);


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
                dialogo1.setMessage("No se puede conectar con el servidor, porfavor compruebe su conexión a internet");
                dialogo1.setCancelable(true);
                dialogo1.show();
            }

            Log.i("CONEXION", total);

            return null;
        }
    }

    private void Insertar(String Mail, String Dni, String nombre, String ap, String movil,String lat,String lon) {
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
            ps.print("&Dni_enfermo=" + Dni);
            ps.print("&Nombre_enfermo=" + nombre);
            ps.print("&Apellidos_enfermo=" + ap);
            ps.print("&Movil_enfermo=" + movil);
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
            dialogo1.setMessage("No se puede conectar con el servidor, porfavor compruebe su conexión a internet");
            dialogo1.setCancelable(true);
            dialogo1.show();
        }

    }


}
