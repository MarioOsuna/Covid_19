package com.example.covid_19;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegistrarActivity extends AppCompatActivity {

    EditText editTextName, editTextEmail, editTextAp, editTextDNI, editTextPass1, editTextPass2, editTextTlf;
    Button button;
    static String SERVIDOR = "http://tfgcovid19.000webhostapp.com/";
    static String INSERTARUSUARIO = "insertarUsuariosPOST.php";
    static String LISTADOUSU = "listadoCSVUsuario.php";
    ProgressDialog progressDialog = null;
    Boolean existe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        editTextName = findViewById(R.id.editTextNombre);
        editTextAp = findViewById(R.id.editTextAp);
        editTextEmail = findViewById(R.id.editTextE_mail);
        editTextDNI = findViewById(R.id.editTextDni);
        editTextPass1 = findViewById(R.id.editTextpass);
        editTextPass2 = findViewById(R.id.editTextpass2);
        editTextTlf = findViewById(R.id.editTextTLF);
        button = findViewById(R.id.buttonRegistrar);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextName.getText().toString().equals(" ") && !editTextEmail.getText().toString().equals(" ") && !editTextAp.getText().toString().equals(" ")
                        && !editTextDNI.getText().toString().equals(" ") && !editTextPass1.getText().toString().equals(" ") && !editTextPass2.getText().toString().equals(" ")
                        && !editTextTlf.getText().toString().equals(" ")) {

                    if (editTextPass1.getText().toString().equals(editTextPass2.getText().toString())) {
                        Toast.makeText(RegistrarActivity.this, " " + editTextTlf.getText().toString(), Toast.LENGTH_SHORT).show();
                        ComprobarDatos descargarCSV = new ComprobarDatos();
                        descargarCSV.execute(LISTADOUSU);
                        //Insertar(editTextEmail.getText().toString(),editTextDNI.getText().toString(),editTextName.getText().toString(),editTextAp.getText().toString(),Integer.parseInt(editTextTlf.getText().toString()),editTextPass1.getText().toString());

                    } else {
                        Toast.makeText(RegistrarActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();

                    }

                } else {
                    Toast.makeText(RegistrarActivity.this, "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

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
        }

    }


    private class ComprobarDatos extends AsyncTask<String, Void, Void> {
        String total = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(RegistrarActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Registrando usuario");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            existe = false;
            String[] lineas = total.split("\n");
            for (String lin : lineas) {
                String[] campos = lin.split(",");
                if (campos[0].toString().equals(editTextEmail.getText().toString())) {

                    existe = true;
                    System.out.println(campos[0]);
                }
                System.out.println("Existe: " + existe);
                System.out.println(campos[0]);
            }
            progressDialog.dismiss();
            if (!existe) {
                Insertar(editTextEmail.getText().toString(), editTextDNI.getText().toString(), editTextName.getText().toString(), editTextAp.getText().toString(), editTextTlf.getText().toString(), editTextPass1.getText().toString());
              /*  Insertar insertar = new Insertar();
                insertar.execute("Insertando");*/
                Toast.makeText(RegistrarActivity.this, "Usuario registrado", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(RegistrarActivity.this, MenuActivity.class);
                i.putExtra("email",editTextEmail.getText().toString() );
                startActivity(i);
            } else {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(RegistrarActivity.this);
                dialogo1.setTitle("Usuario registrado");
                dialogo1.setMessage("Ya existe un usuario con este email, por favor pruebe con otro correo");
                dialogo1.setCancelable(true);
                dialogo1.show();
            }

        }

        @Override
        protected Void doInBackground(String... strings) {
            String script = strings[0];

            URL url = null;
            HttpURLConnection httpURLConnection = null;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                url = new URL(SERVIDOR + script);
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
            }

            Log.i("CONEXION", total);

            return null;
        }
    }

}
