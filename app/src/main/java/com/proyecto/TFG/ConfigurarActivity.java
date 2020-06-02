package com.proyecto.TFG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ConfigurarActivity extends AppCompatActivity {
    EditText editTextNombre, editTextApellidos, editTextdni, editTextTLF, editTextPass;
    Button button;
    TextView Guardar, Eliminar;
    Switch switch1, switch2;

    static String SERVIDOR = "http://tfgcovid19.000webhostapp.com/";
    static String LISTADOUSU = "listadoCSVUsuario.php";
    static String MODIFICARDNI = "ModificarDniPOST.php";
    static String MODIFICARTLF = "ModificarTLFPOST.php";
    static String MODIFICARNOMBRE = "ModificarNombrePOST.php";
    static String MODIFICARAPELLIDOS = "ModificarApellidosPOST.php";
    static String MODIFICARPASS = "ModificarPassPOST.php";
    static String ELIMINAR = "EliminarGET.php";
    static String ELIMINARENFERMO = "EliminarEnfermoGET.php";
    static String EMAIL;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ComprobarDatos descargarCSV = new ComprobarDatos();
        descargarCSV.execute("no");

        EMAIL = MenuActivity.EMAIL_INICIADO;

        setContentView(R.layout.activity_configurar);
        editTextNombre = findViewById(R.id.editTextNombreConfig);
        editTextApellidos = findViewById(R.id.editTextApellidosConfig);
        editTextdni = findViewById(R.id.editTextDniConfig);
        editTextTLF = findViewById(R.id.editTexttlfConfig);

        editTextPass = findViewById(R.id.editTextNewpass);
        button = findViewById(R.id.buttonVolverConfig);
        Guardar = findViewById(R.id.textViewGuardar);
        Eliminar = findViewById(R.id.textViewEliminar);
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);

        editTextNombre.setEnabled(false);
        editTextApellidos.setEnabled(false);
        editTextdni.setEnabled(false);
        editTextTLF.setEnabled(false);
        editTextPass.setEnabled(false);

        editTextNombre.setText("");
        editTextApellidos.setText("");
        editTextdni.setText("");
        editTextTLF.setText("");
        editTextPass.setText("");

        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch1.isChecked()) {
                    editTextNombre.setEnabled(true);
                    editTextApellidos.setEnabled(true);
                    editTextdni.setEnabled(true);
                    editTextTLF.setEnabled(true);
                } else {

                    editTextNombre.setEnabled(false);
                    editTextApellidos.setEnabled(false);
                    editTextdni.setEnabled(false);
                    editTextTLF.setEnabled(false);
                }

            }
        });
        switch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch2.isChecked()) {

                    editTextPass.setEnabled(true);
                } else {

                    editTextPass.setEnabled(false);
                }

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ConfigurarActivity.this, MenuActivity.class);
                i.putExtra("email", MenuActivity.EMAIL_INICIADO);
                startActivity(i);
            }
        });

        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch1.isChecked() && switch2.isChecked()) {
                    //Toast.makeText(ConfigurarActivity.this, "Activado", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder dialogo2 = new AlertDialog.Builder(ConfigurarActivity.this);
                    dialogo2.setTitle(R.string.Configurar);
                    dialogo2.setIcon(R.drawable.configurar);
                    dialogo2.setMessage(R.string.Modificar_Datos);
                    dialogo2.setCancelable(false);
                    dialogo2.setPositiveButton(R.string.Confirmar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            modificar("Nombre", editTextNombre.getText().toString(), MODIFICARNOMBRE);
                            modificar("Apellidos", editTextApellidos.getText().toString(), MODIFICARAPELLIDOS);

                            if (!editTextdni.getText().toString().equals("")) {
                                modificar("Dni", editTextdni.getText().toString(), MODIFICARDNI);

                            }
                            if (!editTextTLF.getText().toString().equals("")) {
                                modificar("Movil", editTextTLF.getText().toString(), MODIFICARTLF);


                            }
                            ComprobarDatos descargarCSV = new ComprobarDatos();
                            descargarCSV.execute("");


                        }
                    });
                    dialogo2.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            dialogo1.dismiss();
                        }
                    });
                    dialogo2.show();
                } else {
                    if (switch1.isChecked()) {
                        AlertDialog.Builder dialogo2 = new AlertDialog.Builder(ConfigurarActivity.this);
                        dialogo2.setTitle(R.string.Configurar);
                        dialogo2.setIcon(R.drawable.configurar);
                        dialogo2.setMessage(R.string.Modificar_Datos);
                        dialogo2.setCancelable(false);
                        dialogo2.setPositiveButton(R.string.Confirmar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {

                                modificar("Nombre", editTextNombre.getText().toString(), MODIFICARNOMBRE);
                                modificar("Apellidos", editTextApellidos.getText().toString(), MODIFICARAPELLIDOS);

                                if (!editTextdni.getText().toString().equals("")) {
                                    modificar("Dni", editTextdni.getText().toString(), MODIFICARDNI);

                                }
                                if (!editTextTLF.getText().toString().equals("")) {
                                    modificar("Movil", editTextTLF.getText().toString(), MODIFICARTLF);


                                }

                                finish();

                            }
                        });
                        dialogo2.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                dialogo1.dismiss();
                            }
                        });
                        dialogo2.show();

                    } else {
                        if (switch2.isChecked()) {
                            AlertDialog.Builder dialogo2 = new AlertDialog.Builder(ConfigurarActivity.this);
                            dialogo2.setTitle(R.string.Configurar);
                            dialogo2.setIcon(R.drawable.configurar);
                            dialogo2.setMessage(R.string.Modificar_Datos);
                            dialogo2.setCancelable(false);
                            dialogo2.setPositiveButton(R.string.Confirmar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {

                                    ComprobarDatos descargarCSV = new ComprobarDatos();
                                    descargarCSV.execute("");


                                }
                            });
                            dialogo2.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    dialogo1.dismiss();
                                }
                            });
                            dialogo2.show();

                        }
                    }
                }


            }
        });
        Eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo2 = new AlertDialog.Builder(ConfigurarActivity.this);
                dialogo2.setTitle(R.string.Eliminar);
                dialogo2.setIcon(R.drawable.advertencia);
                dialogo2.setMessage(R.string.Eliminar_datos);
                dialogo2.setCancelable(false);
                dialogo2.setPositiveButton(R.string.Confirmar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        eliminamos(ELIMINARENFERMO);
                        eliminamos(ELIMINAR);
                        LoginManager.getInstance().logOut();
                        Intent i = new Intent(ConfigurarActivity.this, LoginActivity.class);
                        i.putExtra("email", MenuActivity.EMAIL_INICIADO);
                        startActivity(i);

                    }
                });
                dialogo2.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.dismiss();
                    }
                });
                dialogo2.show();
            }
        });
    }

    private class ComprobarDatos extends AsyncTask<String, Void, Void> {
        String total = "";

        String comprobamos="";
        Boolean Coincide = false;


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

                if (campos[0].toString().equals(EMAIL)) {
                    Log.i("EMIAL", EMAIL);
                    //  Inicio = true;
                    if (!campos[1].toString().equals(" ")) {
                        editTextdni.setText(campos[1]);

                    }
                    if (!campos[2].toString().equals(" ")) {
                        editTextNombre.setText(campos[2]);

                    }
                    if (!campos[3].toString().equals(" ")) {
                        editTextApellidos.setText(campos[3]);

                    }
                    if (!campos[4].toString().equals(" ")) {
                        editTextTLF.setText(campos[4]);

                    }

                    if (editTextPass.getText().toString().equals(campos[5])) {
                        Coincide = true;
                    }


                }


            }
            if (!comprobamos.equals("no")) {
                if (Coincide) {
                    Toast.makeText(ConfigurarActivity.this, R.string.Coincide, Toast.LENGTH_SHORT).show();
                } else {

                    if (editTextPass.getText().length() >= 8) {
                        modificar("Pass", editTextPass.getText().toString(), MODIFICARPASS);
                        finish();
                    } else {
                        Toast.makeText(ConfigurarActivity.this, R.string.toast_caracteres, Toast.LENGTH_SHORT).show();
                    }


                }
            }


        }

        @Override
        protected Void doInBackground(String... strings) {
            comprobamos = strings[0];
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
                AlertDialog.Builder dialogo2 = new AlertDialog.Builder(ConfigurarActivity.this);
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
                throw new RuntimeException(e);
            } catch (IOException e) {

            }

            Log.i("CONEXION", total);

            return null;
        }
    }

    private void modificar(String cambio, String nuevo, String ruta) {
        String script = null;

        script = SERVIDOR + ruta;

        String contenido = "";
        try {
            System.out.println(script);
            URLConnection conexion = null;

            conexion = new URL(script).openConnection();
            //conexion.connect();
            conexion.setDoOutput(true);

            PrintStream ps = new PrintStream(conexion.getOutputStream());

            ps.print("Mail=" + EMAIL);
            ps.print("&" + cambio + "=" + nuevo);

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
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(ConfigurarActivity.this);
            dialogo1.setTitle("Error");
            dialogo1.setIcon(R.drawable.out);
            dialogo1.setMessage(R.string.error_servidor);
            dialogo1.setCancelable(true);
            dialogo1.show();
        }

    }


    private void eliminamos(String php) {
        String script = SERVIDOR + php + "?Mail=" + EMAIL;
        String contenido = "";

        URL url = null;
        HttpURLConnection httpURLConnection = null;


        try {
            url = new URL(script);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                String linea = "";

                while ((linea = br.readLine()) != null) {
                    contenido += linea + "\n";
                }

                br.close();
                inputStream.close();
                Log.i("Contenido: ", contenido);

            }
        } catch (IOException e) {
            e.printStackTrace();

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(ConfigurarActivity.this);
            dialogo1.setTitle("Error");
            dialogo1.setIcon(R.drawable.out);
            dialogo1.setMessage(R.string.error_servidor);
            dialogo1.setCancelable(true);
            dialogo1.show();
        }
    }

}
