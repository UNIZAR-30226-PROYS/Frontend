package com.example.ivansantamaria.appproffrontend;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rubenbros on 14/04/2017.
 */

public class Registro2 extends AppCompatActivity {

    private Facade facade;
    private EditText username;
    private EditText password;
    private EditText confirmPassword;
    private EditText ciudad;
    private EditText experiencia;
    private EditText email;
    private EditText tlf;
    private int prof;

    private API api;
    private JSONObject respuesta;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prof = getIntent().getExtras().getInt("persona");
        if (prof == 1) setContentView(R.layout.activity_registro2_profesor);
        else setContentView(R.layout.activity_registro2_alumno);
        username = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirm);
        if (prof == 1) {
            email = (EditText) findViewById(R.id.email);
            tlf = (EditText) findViewById(R.id.telefono);
            ciudad = (EditText) findViewById(R.id.ciudadProfesorReg);
            experiencia = (EditText) findViewById(R.id.experienciaProfesorReg);
            Button siguiente = (Button) findViewById(R.id.siguiente);
            final Intent i = new Intent(this, Registro3.class);
            siguiente.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    final int code = guardarEnBd(prof);
                    if (code == -1) startActivity(i);
                    else error(code);
                }
            });
        } else {
            Button registro = (Button) findViewById(R.id.register);
            final Intent i = new Intent(this, Busqueda_Profesores.class);
            registro.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    final int code = guardarEnBd(prof);
                    if (code == -1) startActivity(i);
                    else error(code);
                }
            });
        }

        api = new API("http://10.0.2.2:8080", this);
    }

    private int guardarEnBd(int prof) {
        String usr = username.getText().toString();
        String psw = password.getText().toString();
        String cpsw = confirmPassword.getText().toString();
        if (usr.isEmpty()) return 0;
        else if (psw.isEmpty()) return 1;
        else if (cpsw.isEmpty()) return 2;
        else if (!cpsw.equals(psw)) return 3;
        if (prof == 1) {
            String mail = email.getText().toString();
            String phone = tlf.getText().toString();
            String city = ciudad.getText().toString();
            String experience = experiencia.getText().toString();
            if (mail.isEmpty()) return 4;
            else if (!mail.matches("[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+")) return 5;
            else if (phone.isEmpty()) return 6;
            else if (!phone.matches("[1-9]{9}")) return 7;
            else if (city.isEmpty()) return 9;

            facade = new Facade(api);
            try {
                return facade.registro_profesor(new ProfesorVO(usr,psw,phone,mail,city,null,null,
                        null,-1.0f,experience,null));
            } catch (APIexception ex) { respuesta = ex.json; return 10; }

        } else {
            //Guardar en base de datos
            CheckBox tyc = (CheckBox) findViewById(R.id.TyC);
            if (!tyc.isEnabled()) return 8;

            facade = new Facade(api);
            try {
                return facade.registro_alumno(new AlumnoVO(usr,psw));
            } catch (APIexception ex) { respuesta = ex.json; return 10; }
        }
    }

    private void error(int code) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        switch (code) {
            case 0:
                dlgAlert.setMessage("Rellene el campo de usuario");
                break;
            case 1:
                dlgAlert.setMessage("Rellene el campo de contrasena");
                break;
            case 2:
                dlgAlert.setMessage("Rellene el campo de confirmacion de contrasena");
                break;
            case 3:
                dlgAlert.setMessage("Las contrasenas no coinciden");
                break;
            case 4:
                dlgAlert.setMessage("Rellene el campo de e-mail");
                break;
            case 5:
                dlgAlert.setMessage("Introduzca un e-mail valido");
                break;
            case 6:
                dlgAlert.setMessage("Rellene el campo de telefono");
                break;
            case 7:
                dlgAlert.setMessage("Introduzca un telefono valido");
                break;
            case 8:
                dlgAlert.setMessage("Acepte los terminos y condiciones");
                break;
            case 9:
                dlgAlert.setMessage("Rellene el campo de Ciudad");
            case 10:
                try {
                    dlgAlert.setMessage("Error durante el registro: \n" + respuesta.getString("message"));
                } catch (JSONException ex) {dlgAlert.setMessage("Error durante el registro:");}
        }
        dlgAlert.setTitle("Error...");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
    }
}