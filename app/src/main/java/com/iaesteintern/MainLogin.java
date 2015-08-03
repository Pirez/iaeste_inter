package com.iaesteintern;
//package com.android.HelloWorld;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.*;
//import com.sun.org.apache.xml.internal.utils.StringVector;


public class MainLogin extends Activity {



    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {


        //setTheme(R.style.BlackTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        final TextView text_info = (TextView) findViewById(R.id.text_login_info);
        final EditText edit_u = (EditText) findViewById(R.id.edittext_brukernavn);
        final EditText edit_p = (EditText) findViewById(R.id.edittext_login_password);
        final Button button_login = (Button) findViewById(R.id.button_login_Login);
        final Button button_reset = (Button) findViewById(R.id.button_login_reset);
        final CheckBox checkBox_login = (CheckBox) findViewById(R.id.checkbox_login_husk);
        final SharedPreferences loginSettings = getSharedPreferences("LoginSettings", MODE_PRIVATE);
        final SharedPreferences innstillinger = getSharedPreferences("Innstillinger", MODE_PRIVATE);


        //Hvis personen har logget inn, så setter vi brukernavn og pass, inn i tekstfeltetet etterpå
        String tekst_01 = loginSettings.getString("user", "");
        String tekst_02 = loginSettings.getString("user", "");
        edit_u.setText(tekst_01);
        edit_p.setText(tekst_02);


        button_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Definere variabler
                Editable user_n = null;
                Editable pass_w = null;
                user_n = edit_u.getText();
                pass_w = edit_p.getText();
                String default_user = "test";
                String default_p_01 = "jump2011";
                String default_p_02 = "skihelga2011";
                String default_p_03 = "iaeste2011";

                String[] default_p_00 = new String[100];
                default_p_00[1] = "test";

                //Sjekker brukernavn og passrd
                if (pass_w.toString().matches(default_p_01) || pass_w.toString().matches(default_p_02) || pass_w.toString().matches(default_p_03)) {
                    //Setter default innsillinger
                    SharedPreferences.Editor inn = loginSettings.edit();
                    inn.putBoolean("autoBilde", false);
                    inn.commit();

                    Toast.makeText(getApplicationContext(), getString(R.string.login_19) + user_n.toString(),
                            Toast.LENGTH_SHORT).show();
                    //Legger de inn som GLOBALE variabler, slik at vi kan bruke de hvor som helst i programmet
                    SharedPreferences.Editor prefEditor = loginSettings.edit();
                    prefEditor.putBoolean("login", true);//Sier at login er gjennomført
                    prefEditor.putString("user", user_n.toString());
                    prefEditor.putString("pass", pass_w.toString());
                    prefEditor.commit();
                    //Går tilbake til hovedskjermen etter å ha gjennomført en sucess login
                    //Intent BACK = new Intent(MainLogin.this, MainHome.class);
                    //startActivity(BACK);

                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.login_03,
                            Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor prefEditor = loginSettings.edit();
                    prefEditor.putBoolean("login", false);
                    prefEditor.putString("username", "");
                    prefEditor.commit();
                    edit_p.setText("");
                }
            }
        });

        button_reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Resert alt, gjør alt tomt
                text_info.setText("");
                SharedPreferences.Editor prefEditor = loginSettings.edit();
                prefEditor.putBoolean("login", false);
                prefEditor.putString("user", "");
                prefEditor.putString("pass", "");
                prefEditor.commit();
                checkBox_login.setClickable(true);
                button_login.setClickable(true);
                edit_u.setText("");
                edit_p.setText("");

                Toast.makeText(getApplicationContext(), R.string.login_04,
                        Toast.LENGTH_SHORT).show();

                if (loginSettings.getBoolean("login", true)) {
                    //Restarter activity'en

                    Intent RESET = new Intent(MainLogin.this, MainLogin.class);
                    startActivity(RESET);
                }
            }
        });

        if (loginSettings.getBoolean("login", true)) {
            //Hvis du er logget inn, skal det ikkje være mulig å trykke på login knappen eller bruke checkbox
            text_info.setText(R.string.login_05);
            checkBox_login.setChecked(true);
            checkBox_login.setClickable(false);
            button_login.setClickable(false);
        }
    }


}


