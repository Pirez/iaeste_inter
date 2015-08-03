package com.iaesteintern;
//package com.android.HelloWorld;
//TODO: Fikse hele denne screen (men er ikkje kritisk!)

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.util.Locale;

public class MainInnstillinger extends Activity {

    Locale locale;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_inntillinger);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        Button back_home = (Button) findViewById(R.id.button_innstillinger_01);
        Button lagre = (Button) findViewById(R.id.button_innstillinger_lagre);
        final CheckBox autoBilde = (CheckBox) findViewById(R.id.check_innstilinger_autoBildehusk);


        final SharedPreferences innstillinger = getSharedPreferences("Innstillinger", MODE_PRIVATE);
        SharedPreferences.Editor inn = innstillinger.edit();

        if (innstillinger.getBoolean("autoBilde", true)) {
            autoBilde.setChecked(true);
        } else {
            autoBilde.setChecked(false);

        }


        Spinner valg_malform = (Spinner) findViewById(R.id.spinner_innstilliner_målform);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.malform, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        valg_malform.setAdapter(adapter);
        valg_malform.setSelection(innstillinger.getInt("lang", 0));

        class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {


                SharedPreferences.Editor inn = innstillinger.edit();


                if (pos == 0) {
                    locale = new Locale("no");
                    inn.putInt("lang", 0);
                } else if (pos == 1) {
                    locale = new Locale("nn");
                    inn.putInt("lang", 1);
                } else if (pos == 2) {
                    inn.putInt("lang", 2);
                    locale = new Locale("en");
                }
                inn.commit();


            }

            public void onNothingSelected(AdapterView parent) {
            }


        }


        valg_malform.setOnItemSelectedListener(new MyOnItemSelectedListener());


        lagre.setOnClickListener(new View.OnClickListener() {
            public void onClick(View a) {

                SharedPreferences.Editor inn = innstillinger.edit();

                if (autoBilde.isChecked()) {
                    inn.putBoolean("autoBilde", true);
                    inn.commit();
                } else {
                    inn.putBoolean("autoBilde", false);
                    inn.commit();
                }

                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());

                Toast.makeText(getApplicationContext(), R.string.instilinnger_05,
                        Toast.LENGTH_SHORT).show();
                //Intent in = new Intent(MainInnstillinger.this, MainHome.class);
                //startActivity(in);
                finish();
            }
        });

        back_home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View a) {
                // Intent in = new Intent(MainInnstillinger.this, MainHome.class);
                // startActivity(in);
                finish();
            }
        });


    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed
                // in the Action Bar.
                Intent parentActivityIntent = new Intent(this, MainHome.class);
                parentActivityIntent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(parentActivityIntent);
                finish();
                return true;


            default:
                return false;

        }}



}



