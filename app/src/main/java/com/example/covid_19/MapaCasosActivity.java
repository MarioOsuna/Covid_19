package com.example.covid_19;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapaCasosActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    static String SERVIDOR = "http://tfgcovid19.000webhostapp.com/";
    static String LISTADOENFERMOS = "listadoCSVEnfermo.php";
    DatosEnfermos DatosEnfermos = new DatosEnfermos();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // DatosEnfermos.execute("");
        setContentView(R.layout.activity_mapa_casos);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Toast.makeText(this, "DatosEnfermos.total_enfermos: " + DatosEnfermos.total_enfermos, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        DatosEnfermos.execute("");


    }

    //Comprobar si los datos existen
    private class DatosEnfermos extends AsyncTask<String, Void, Void> {
       // int total_enfermos = 0;
        String total = "";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                String[] lineas = total.split("\n");



                int i = 0;
                for (String lin : lineas) {


                    String[] campos = lin.split(",");


                    Double la = Double.parseDouble(campos[1].toString());
                    Double lo = Double.parseDouble(campos[2].toString());
                    LatLng coordenadas = new LatLng(la, lo);
                    Log.i("Coordenadas",""+coordenadas);
                    mMap.addMarker(new MarkerOptions().position(coordenadas).title("Enfermo " + (i + 1)));


                    i++;
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(MapaCasosActivity.this);
                dialogo1.setTitle("Covid 19");
                dialogo1.setMessage(R.string.cero_casos);
                dialogo1.setCancelable(true);
                dialogo1.show();
            }

        }

        @Override
        protected Void doInBackground(String... strings) {
            String scripts = strings[0];
            // mail = strings[0];

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
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(MapaCasosActivity.this);
                dialogo1.setTitle("0 casos");
                dialogo1.setMessage("No se ha encontrado ningún caso registrado en la app");
                dialogo1.setCancelable(true);
                dialogo1.show();
            } catch (RuntimeException r) {

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(MapaCasosActivity.this);
                dialogo1.setTitle("Error");
                dialogo1.setMessage(R.string.error_servidor);
                dialogo1.setCancelable(true);
                dialogo1.show();
            }

            Log.i("CONEXION", total);


            return null;
        }
    }
}
