package com.proyecto.TFG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class Recuperar_2_Activity extends AppCompatActivity {
    Button button;
    EditText editTextpass1, editTextmail;
    static String SERVIDOR = "http://tfgcovid19.000webhostapp.com/";
    static String LISTADOUSU = "listadoCSVUsuario.php";
    static String MODIFICARPASS = "ModificarPassPOST.php";
    String COMPROBAR_PASS;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar2);

        button = findViewById(R.id.button5);
        editTextmail = findViewById(R.id.editText);
        Bundle datos = this.getIntent().getExtras();
        String email = datos.getString("email");

        COMPROBAR_PASS = datos.getString("pass");

        // Toast.makeText(this, COMPROBAR_PASS, Toast.LENGTH_SHORT).show();
        editTextmail.setText(email);
        editTextpass1 = findViewById(R.id.editTextRecupPass1);
        // editTextpass2 = findViewById(R.id.editTextRecupPass2);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conexion()) {
                    if (editTextpass1.getText().toString().equals("") || editTextmail.getText().toString().equals("")) {
                        Toast.makeText(Recuperar_2_Activity.this, R.string.se_requiere, Toast.LENGTH_SHORT).show();
                    } else {
                        if (editTextpass1.getText().toString().equals(COMPROBAR_PASS)) {

                            ListarUsuario cambiarpass = new ListarUsuario();
                            cambiarpass.execute(COMPROBAR_PASS);
                        } else {
                            Toast.makeText(Recuperar_2_Activity.this,R.string.cod_inva, Toast.LENGTH_SHORT).show();
                        }


                    }
                } else {
                    AlertDialog.Builder dialogo2 = new AlertDialog.Builder(Recuperar_2_Activity.this);
                    dialogo2.setTitle("Error");
                    dialogo2.setIcon(R.drawable.out);
                    dialogo2.setMessage(R.string.error_servidor);
                    dialogo2.setCancelable(false);
                    dialogo2.setPositiveButton(R.string.Confirmar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            //aceptar();
                            dialogo1.dismiss();
                        }
                    });
                    dialogo2.show();
                }
            }
        });
    }

    private class ListarUsuario extends AsyncTask<String, Void, Void> {
        String total = "";
        boolean existe;
        String pass;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            existe = false;


            String[] lineas = total.split("\n");
            for (String lin : lineas) {
                String[] campos = lin.split(",");
                //Se comprueba si algún correo de la tabla coincide con el correo introducido o bien si el mail obtenido del inicio con Google/Facebook coincide con alguno de la tabla
                if (campos[0].toString().equals(editTextmail.getText().toString())) {

                    existe = true;
                    // System.out.println(campos[0]);
                }

                //Mail solo está lleno si se inicia sesión con google/facebook, por lo que si está vacio no se ha seleccionado ninguna de estas dos opciones

            }
            if (existe) {
                modificar(editTextmail.getText().toString(), COMPROBAR_PASS);

                Intent i = new Intent(Recuperar_2_Activity.this, MenuActivity.class);
                i.putExtra("email", editTextmail.getText().toString());
                startActivity(i);
            } else {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Recuperar_2_Activity.this);
                dialogo1.setTitle("Error");
                dialogo1.setMessage(R.string.EmailNoReconocido);
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton(R.string.Confirmar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        //aceptar();
                        dialogo1.dismiss();
                    }
                });
                dialogo1.show();
            }


        }

        @Override
        protected Void doInBackground(String... strings) {
            pass = strings[0];
            //Obtenemos los datos que se le pasan al hilo, si es un inicio normal se le pasarán datos vacios


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
            } catch (MalformedURLException e) {
                e.printStackTrace();
                AlertDialog.Builder dialogo2 = new AlertDialog.Builder(Recuperar_2_Activity.this);
                dialogo2.setTitle("Error");
                dialogo2.setIcon(R.drawable.out);
                dialogo2.setMessage(R.string.error_servidor);
                dialogo2.setCancelable(true);
                dialogo2.show();
                throw new RuntimeException(e);
            } catch (IOException e) {

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

    private void modificar(String Mail, String pass) {
        String script = null;

        script = SERVIDOR + MODIFICARPASS;

        String contenido = "";
        try {
            System.out.println(script);
            URLConnection conexion = null;

            conexion = new URL(script).openConnection();
            //conexion.connect();
            conexion.setDoOutput(true);

            PrintStream ps = new PrintStream(conexion.getOutputStream());

            ps.print("Mail=" + Mail);
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
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Recuperar_2_Activity.this);
            dialogo1.setTitle("Error");
            dialogo1.setIcon(R.drawable.out);
            dialogo1.setMessage(R.string.error_servidor);
            dialogo1.setCancelable(true);
            dialogo1.show();
        }

    }


}


