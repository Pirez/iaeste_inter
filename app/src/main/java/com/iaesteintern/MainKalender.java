package com.iaesteintern;
//package com.android.HelloWorld;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Vector;

public class MainKalender extends Activity {
    private PopupWindow pw;
    private String[][][] event_data = new String[2][12][5];
    int spinner_pos = 0;
    int list_pos = 0;
    int i0;
    int kalender_active = 0; //sjekker hvis den er i list (0) eller popup (1)
    int lengden_kalender = 30;
    private String[][] kalender_data = new String[lengden_kalender][7];   //PASS PÅ LENGDEN AV DATA DEN FÅR INN, aka 7 LK, 5 DATA INFORMASJON, 100 MEDLEMMER
    String[] kalenderNavn;
    private Vector<String> kalender_balle = new Vector<String>();
    ArrayList<Integer> pos_liste;
    Typeface iaesteFont;
    Typeface iaesteFontBold;
    ArrayAdapter<String> liste;
    int ii;

    public static String getWord(String str, char seperator, int no) {
        int eind = 0;
        int bind = 0;
        int found = 0;
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (no == 0 && found == 1)
                return str.substring(0, i - 1);
            if (found == no && bind == 0) {
                bind = i;
            } else if (found == no + 1 || i == len - 1) {
                eind = i - 1;
                if (i == len - 1)
                    eind = i + 1;
                break;
            }
            if (str.charAt(i) == seperator)
                found++;
        }
        return str.substring(bind, eind);
    }

    public static String convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
                                  int hex_len = hex.length();
        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex_len - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);

            temp.append(decimal);
        }

        return sb.toString();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.main_kalender);
                getActionBar().setDisplayHomeAsUpEnabled(true);
                iaesteFont = Typeface.createFromAsset(getAssets(), "fonts/iaesteFont.ttf");
                iaesteFontBold = Typeface.createFromAsset(getAssets(), "fonts/iaesteFontBold.ttf");


        try {
                    String[] file_data = getResources().getStringArray(R.array.filename_list);   //Medlem data filen
                    FileInputStream fis = openFileInput(file_data[2]);
                    BufferedReader r = new BufferedReader(new InputStreamReader(fis));
                    String line;
                    String data;

                    ii = 0;

                    //Leser av teskstfilen, og putter data inn i array, og sorteres etter LK, og deretter informasjon om personern, eks.navn,tlf...
                    while ((line = r.readLine()) != null) {
                        data = (getWord(line, ';', 1));                            //Leser fra første kolonne datasett
                        // j = data, i = personer
                        Integer words = 7; //Hvor mangen kolonner den skal lese inn

                        for (int i = 0; i < words; i++) {

                            kalender_data[ii][i] = convertHexToString(getWord(data, '*', i));
                        }
                        ii++;

                    }



            Log.d("et tall:" , lengden_kalender + "");
                    fis.close();


                } catch (FileNotFoundException e) {

                    Toast.makeText(getApplicationContext(), R.string.update_text_02, Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    //Teksten er koruppt
                    Toast.makeText(getApplicationContext(), "IO feil! Kode: 01",
                            Toast.LENGTH_SHORT).show();
                }

        SpannableString s = new SpannableString("Kalender");
        s.setSpan(new TypefaceSpan(this, "iaesteFontBold.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);




                final SharedPreferences spinner_memory = getSharedPreferences("Innstillinger", MODE_PRIVATE);
                int spinner_pos_mem = spinner_memory.getInt("pos", 0);
                Spinner spinner = (Spinner) findViewById(R.id.widget44);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.valg_LK, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                //Setteer spinner på den posisjonen brukeren var inni, slik at den ikje restetter
                spinner.setSelection(spinner_pos_mem);

                //listview
                ListView kalender_list = (ListView) findViewById(R.id.widget45);


        /*String[] kalender_list = new String[i0];
        System.arraycopy(kalender_data[0][0], 0, kalender_list, 0, i0);
        setListAdapter(new CustomArrayAdapterKalender(this, kalender_list));
        setActionBarTitle(getString(R.string.lk_1) + " Bergen " + Integer.toString(navneliste_bergen.length) + " " + getString(R.string.medlem_1));*/




                adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                pos_liste = new ArrayList<Integer>();
                kalenderNavn = new String[ii];

                int index = 0;
                for (int i = 0; i < ii; i++) {
                    kalenderNavn[index++] = (kalender_data[i][6]);
                }
                if (kalenderNavn == null) {
                    Toast.makeText(this, "Ingen arrangementer er lagt inn i Portalen", Toast.LENGTH_SHORT).show();

                }

                liste = new CustomArrayAdapterKalender(this, kalenderNavn);
                kalender_list.setAdapter(liste);

                /*
               ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                       this, R.array.string_kalender_events_nasjonal, android.R.layout.simple_list_item_1);
               kalender_list.setAdapter(adapter2);
                */


                class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int pos, long id) {
                        spinner_pos = pos;
                        if (pos == 0) {
                            /*
                            ListView kalender_list = (ListView) findViewById(R.id.widget45);
                            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                                    MainKalender.this, R.array.string_kalender_events_nasjonal, android.R.layout.simple_list_item_1);
                            kalender_list.setAdapter(adapter2); */
                        }

                        if (pos == 1) {
                            ListView kalender_list = (ListView) findViewById(R.id.widget45);
                            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                                    MainKalender.this, R.array.string_kalender_events_weekends, android.R.layout.simple_list_item_1);
                            kalender_list.setAdapter(adapter2);
                        }


                    }

                    public void onNothingSelected(AdapterView parent) {
                    }

                }

                spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());


                kalender_list.setOnItemClickListener(new AdapterView.OnItemClickListener()

                {

                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        kalender_active = 1;

                        Intent i = new Intent(MainKalender.this, MainKalendarEvent.class);

                        i.putExtra("fraDato", kalender_data[position][1]);
                        i.putExtra("tilDato", kalender_data[position][2]);
                        i.putExtra("eventNavn", kalender_data[position][6]);
                        i.putExtra("eventSted", kalender_data[position][5]);
                        i.putExtra("eventInfo", kalender_data[position][4]);
                        startActivity(i);


                        //String dato = kalender_data[position][1] + " - " + kalender_data[position][2];

                        //popup_window(kalender_data[position][6], kalender_data[position][5], dato, kalender_data[position][4]);

                    }


                });
    }




    @Override
    public void onBackPressed() {
        /*
        Sjekk hvis du er inne i "VALG LK"  hvis den er det returnere den til main_home
        eller "MEDLEMLISTER" hvis den er det returnere tilbake til "VALG LK", lk_list()
         */
        Intent intent = new Intent(MainKalender.this, MainHomeNav.class);       //Går tilbake til MainHomeNav
        startActivity(intent);
        finish();


    }

}
