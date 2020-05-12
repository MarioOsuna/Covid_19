package com.mario.covid_19;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HospitalActivity extends AppCompatActivity implements OnMapReadyCallback {
    TextView direccion;
    Button button;
    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        direccion = findViewById(R.id.Direccion);
        button = findViewById(R.id.button2);
        direccion.setText("Paraje Cerro del Camello, s/n, 14200 Peñarroya-Pueblonuevo, Córdoba");
        mMapView.getMapAsync(this);
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(HospitalActivity.this);
        dialogo1.setTitle(R.string.Indicios_enfermedad);
        dialogo1.setIcon(R.drawable.advertencia);
        dialogo1.setMessage(R.string.direccion_enfermo);
        dialogo1.setCancelable(true);
        dialogo1.show();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(HospitalActivity.this, MenuActivity.class);
                i.putExtra("email",MenuActivity.EMAIL_INICIADO);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(new LatLng(38.3085072, -5.2704065)).title("Hospital de alta resolución"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(38.3085072, -5.2704065), 16));
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}
