package com.example.covid_19;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Service;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView textViewAltitud,textViewLatitud,textViewLongitud;
    LocationManager locationManager;
    LocationListener locationListener;
    int tiempoRefresco = 500;
    int PEDI_PERMISO_GPS = 1;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        textViewAltitud=findViewById(R.id.textView1);
        textViewLatitud=findViewById(R.id.textView2);
        textViewLongitud=findViewById(R.id.textView3);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                textViewAltitud.setText("Altitud: " + location.getAltitude());
                textViewLatitud.setText("Latitud: " + location.getLatitude());
                textViewLongitud.setText("Longitud: " + location.getLongitude());

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
}
