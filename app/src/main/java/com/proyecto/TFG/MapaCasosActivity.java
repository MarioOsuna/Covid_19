package com.proyecto.TFG;

import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

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
import java.util.List;
import java.util.Locale;

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
                Geocoder geocoder;
                List<Address> direccion;
                geocoder = new Geocoder(MapaCasosActivity.this, Locale.getDefault());


                int i = 0;
                for (String lin : lineas) {


                    String[] campos = lin.split(",");


                    Double la = Double.parseDouble(campos[1].toString());
                    Double lo = Double.parseDouble(campos[2].toString());
                   direccion = geocoder.getFromLocation(la, lo, 1);
                    LatLng coordenadas = new LatLng(la, lo);
                    Log.i("Coordenadas", "" + coordenadas);
                    mMap.addMarker(new MarkerOptions().position(coordenadas).title(direccion.get(0).getLocality()+", "+direccion.get(0).getAdminArea()+", "+direccion.get(0).getCountryName()));
                   // mMap.addMarker(new MarkerOptions().position(coordenadas).title("enfermos"));



                    i++;
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(MapaCasosActivity.this);
                dialogo1.setTitle(R.string.mapa);
                dialogo1.setMessage(R.string.cero_casos);
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton(R.string.Confirmar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        //aceptar();
                        dialogo1.dismiss();
                    }
                });
                dialogo1.show();
            } catch (IOException e) {
                e.printStackTrace();
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
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton(R.string.Confirmar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        //aceptar();
                        dialogo1.dismiss();
                    }
                });
                dialogo1.show();
            } catch (RuntimeException r) {

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(MapaCasosActivity.this);
                dialogo1.setTitle("Error");
                dialogo1.setIcon(R.drawable.out);
                dialogo1.setMessage(R.string.error_servidor);
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton(R.string.Confirmar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        //aceptar();
                        dialogo1.dismiss();
                    }
                });
                dialogo1.show();
            }

            Log.i("CONEXION", total);


            return null;
        }
    }
}
