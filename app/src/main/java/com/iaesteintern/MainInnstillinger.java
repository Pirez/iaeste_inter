package com.iaesteintern;
//package com.android.HelloWorld;
//TODO: Fikse hele denne screen (men er ikkje kritisk!)

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import java.util.Locale;

public class MainInnstillinger extends Activity {

    Locale locale;
    Typeface iaesteFont;
    Typeface iaesteFontBold;
    TextView textMalform;
    Button omAppen;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_inntillinger);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Button back_home = (Button) findViewById(R.id.button_innstillinger_01);
        Button lagre = (Button) findViewById(R.id.button_innstillinger_lagre);
        iaesteFont = Typeface.createFromAsset(getAssets(), "fonts/iaesteFont.ttf");
        iaesteFont = Typeface.createFromAsset(getAssets(), "fonts/iaesteFont.ttf");

        omAppen = (Button) findViewById(R.id.buttonOmOss);


        final CheckBox autoBilde = (CheckBox) findViewById(R.id.check_innstilinger_autoBildehusk);
        iaesteFont = Typeface.createFromAsset(getAssets(), "fonts/iaesteFont.ttf");

        iaesteFont = Typeface.createFromAsset(getAssets(), "fonts/iaesteFont.ttf");
        autoBilde.setTypeface(iaesteFont);
        omAppen.setTypeface(iaesteFont);
        lagre.setTypeface(iaesteFont );

        final SharedPreferences innstillinger = getSharedPreferences("Innstillinger", MODE_PRIVATE);
        SharedPreferences.Editor inn = innstillinger.edit();

        if (innstillinger.getBoolean("autoBilde", true)) {
            autoBilde.setChecked(true);
        } else {
            autoBilde.setChecked(false);

        }


        SpannableString s = new SpannableString("Innstillinger");
        s.setSpan(new TypefaceSpan(this, "iaesteFontBold.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.secondary));
        }

        textMalform = (TextView) findViewById(R.id.text_innstillinger_malform);
        textMalform.setTypeface(iaesteFontBold);
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


        omAppen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainInnstillinger.this, MainCredits.class);
                startActivity(intent);
                finish();
            }
        });

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
                Intent in = new Intent(MainInnstillinger.this, MainHomeNav.class);
                startActivity(in);
                finish();
            }
        });




    }




    @Override
    public boolean onOptionsItemSelected
            (MenuItem
                     item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // This is called when the Home (Up) button is pressed
                // in the Action Bar.
                onBackPressed();
                return true;



            default:
                return super.onOptionsItemSelected(item);

        }

    }



    @Override
    public void onBackPressed() {
        /*
        Sjekk hvis du er inne i "VALG LK"  hvis den er det returnere den til main_home
        eller "MEDLEMLISTER" hvis den er det returnere tilbake til "VALG LK", lk_list()
         */
        Intent intent = new Intent(MainInnstillinger.this, MainHomeNav.class);       //Går tilbake til MainHomeNav
        startActivity(intent);
        finish();


    }



}



