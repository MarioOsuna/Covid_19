package com.proyecto.TFG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
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

public class RegistrarActivity extends AppCompatActivity {

    EditText editTextName, editTextEmail, editTextAp, editTextDNI, editTextPass1, editTextPass2, editTextTlf;
    Button button;
    static String SERVIDOR = "http://tfgcovid19.000webhostapp.com/";
    static String INSERTARUSUARIO = "insertarUsuariosPOST.php";
    static String LISTADOUSU = "listadoCSVUsuario.php";
    ProgressDialog progressDialog = null;
    Boolean existe;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

                if (conexion()) {
                    if (!editTextName.getText().toString().equals("") && !editTextEmail.getText().toString().equals("") && !editTextAp.getText().toString().equals("")
                            && !editTextPass1.getText().toString().equals("") && !editTextPass2.getText().toString().equals("")
                    ) {

                        if (!editTextEmail.getText().toString().contains("@")) {
                            editTextEmail.setText("");
                            editTextEmail.setHintTextColor(Color.rgb(203, 67, 53));

                            Toast.makeText(RegistrarActivity.this, R.string.toast_NoMail, Toast.LENGTH_SHORT).show();

                        } else {
                            if (editTextPass1.getText().toString().length() < 8) {

                                editTextPass1.setTextColor(Color.rgb(203, 67, 53));

                                //  editTextPass2.setHintTextColor(Color.rgb(203, 67, 53));

                                Toast.makeText(RegistrarActivity.this, R.string.toast_caracteres, Toast.LENGTH_SHORT).show();

                            } else {
                                if (editTextPass1.getText().toString().equals(editTextPass2.getText().toString())) {
                                    // Toast.makeText(RegistrarActivity.this, "Registrando", Toast.LENGTH_SHORT).show();

                                    ComprobarDatos descargarCSV = new ComprobarDatos();
                                    descargarCSV.execute(LISTADOUSU);
                                    //Insertar(editTextEmail.getText().toString(),editTextDNI.getText().toString(),editTextName.getText().toString(),editTextAp.getText().toString(),Integer.parseInt(editTextTlf.getText().toString()),editTextPass1.getText().toString());

                                } else {
                                    editTextPass1.setHintTextColor(Color.rgb(203, 67, 53));

                                    editTextPass2.setHintTextColor(Color.rgb(203, 67, 53));
                                    Toast.makeText(RegistrarActivity.this, R.string.error_pass1, Toast.LENGTH_SHORT).show();

                                }
                            }
                        }


                    } else {
                        editTextEmail.setHintTextColor(Color.rgb(203, 67, 53));
                        editTextPass1.setHintTextColor(Color.rgb(203, 67, 53));
                        editTextName.setHintTextColor(Color.rgb(203, 67, 53));
                        editTextPass2.setHintTextColor(Color.rgb(203, 67, 53));
                        editTextAp.setHintTextColor(Color.rgb(203, 67, 53));

                        Toast.makeText(RegistrarActivity.this, R.string.toast_campos, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    AlertDialog.Builder dialogo2 = new AlertDialog.Builder(RegistrarActivity.this);
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
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(RegistrarActivity.this);
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

    }


    private class ComprobarDatos extends AsyncTask<String, Void, Void> {
        String total = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(RegistrarActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle(R.string.Registrando_usuario);
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
                Toast.makeText(RegistrarActivity.this, R.string.Usuario_registrado, Toast.LENGTH_SHORT).show();

                Intent i = new Intent(RegistrarActivity.this, MenuActivity.class);
                i.putExtra("email", editTextEmail.getText().toString());
                startActivity(i);
            } else {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(RegistrarActivity.this);
                dialogo1.setTitle(R.string.Usuario_registrado);
                dialogo1.setMessage(R.string.mail_existente);
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
            } catch (RuntimeException e) {
                e.printStackTrace();
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(RegistrarActivity.this);
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

    public boolean conexion() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

}
