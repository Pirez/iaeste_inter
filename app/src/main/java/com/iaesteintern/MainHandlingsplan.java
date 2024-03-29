package com.iaesteintern;
//package com.android.HelloWorld;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.io.*;
import java.util.ArrayList;

public class MainHandlingsplan extends Activity {
    private PopupWindow pw;
    private String[][][] event_data = new String[2][12][5];
    int spinner_pos = 0;
    int list_pos = 0;
    int handlingsplan_active = 0; //sjekker hvis den er i list (0) eller popup (1)
    int lengden_kalender = 3000;
    private String[][] handling_data = new String[lengden_kalender][7];   //PASS PÅ LENGDEN AV DATA DEN FÅR INN, aka 7 LK, 5 DATA INFORMASJON, 100 MEDLEMMER
    ArrayList<Integer> pos_liste;

    public static String getWord(String str, char seperator, int no) {
        int eind = 0;
        int bind = 0;
        int found = 0;
        for (int i = 0; i < str.length(); i++) {
            if (no == 0 && found == 1)
                return str.substring(0, i - 1);
            if (found == no && bind == 0) {
                bind = i;
            } else if (found == no + 1 || i == str.length() - 1) {
                eind = i - 1;
                if (i == str.length() - 1)
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

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

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


        try {
            String[] file_data = getResources().getStringArray(R.array.filename_list);   //Medlem data filen
            FileInputStream fis = openFileInput(file_data[2]);
            BufferedReader r = new BufferedReader(new InputStreamReader(fis));
            String line;
            String data;

            int ii = 0;

            //Leser av teskstfilen, og putter data inn i array, og sorteres etter LK, og deretter informasjon om personern, eks.navn,tlf...
            while ((line = r.readLine()) != null) {
                data = (getWord(line, ';', 1));                            //Leser fra første kolonne datasett
                // j = data, i = personer
                Integer words = 7; //Hvor mangen kolonner den skal lese inn

                for (int i = 0; i < words; i++) {
                    handling_data[ii][i] = convertHexToString(getWord(data, '*', i));
                }
                ii++;

            }
            fis.close();


        } catch (FileNotFoundException e) {

            Toast.makeText(getApplicationContext(), R.string.update_text_02, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            //Teksten er koruppt
            Toast.makeText(getApplicationContext(), "IO feil! Kode: 01",
                    Toast.LENGTH_SHORT).show();
        }


        final SharedPreferences spinner_memory = getSharedPreferences("Innstillinger", MODE_PRIVATE);
        int spinner_pos_mem = spinner_memory.getInt("pos", 0);

        Spinner spinner = (Spinner) findViewById(R.id.widget44);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.valg_LK, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Setteer spinner på den posisjonen brukeren var inni, slik at den ikje restetter
        spinner.setSelection(spinner_pos_mem);


        ListView kalender_list = (ListView) findViewById(R.id.widget45);

        ArrayAdapter<CharSequence> liste =
                new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        pos_liste = new ArrayList<Integer>();

        for (int i = 0; i < lengden_kalender; i++) {
            String tmp_overskrift = handling_data[i][6];
            if (tmp_overskrift == null) {
            } else {
                liste.add(tmp_overskrift);
                pos_liste.add(i);
            }
        }
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
                            MainHandlingsplan.this, R.array.string_kalender_events_weekends, android.R.layout.simple_list_item_1);
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

                handlingsplan_active = 1;

                position = pos_liste.get(position);

                String dato = handling_data[position][1] + " - " + handling_data[position][2];

                popup_window(handling_data[position][6], handling_data[position][5], dato, handling_data[position][4]);

            }


        });
    }


    public void popup_window(String overskrift, String sted, String dato, String info) {


        final Dialog dialog = new Dialog(MainHandlingsplan.this);
        dialog.show();
        dialog.setContentView(R.layout.popup_event);
        dialog.setTitle(overskrift);
        dialog.setCancelable(true);


        //LayoutInflater inflater = (LayoutInflater) MainKalender.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //final View layout = inflater.inflate(R.layout.popup_event, (ViewGroup) findViewById(R.id.popup_event));
        //pw = new PopupWindow(layout, 300, 550, true);
        TextView popup_overskrift = (TextView) dialog.findViewById(R.id.text_popup_event_overskrift);
        TextView popup_dato = (TextView) dialog.findViewById(R.id.text_popup_event_dato);
        TextView popup_sted = (TextView) dialog.findViewById(R.id.text_popup_event_sted);
        TextView popup_info = (TextView) dialog.findViewById(R.id.text_popup_evet_info);

        final Button lukk = (Button) dialog.findViewById(R.id.button_popup_event_tilbake);

        popup_overskrift.setText(overskrift);
        popup_dato.setText("Sted: " + sted);
        popup_sted.setText("Dato: " + dato);
        popup_info.setText(info);


        lukk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vv) {
                // Lukker popup
                dialog.cancel();
            }

        });

    }

    @Override
    public void onBackPressed() {
        /*
        Sjekk hvis du er inne i "VALG LK"  hvis den er det returnere den til main_home
        eller "MEDLEMLISTER" hvis den er det returnere tilbake til "VALG LK", lk_list()
         */
        if (handlingsplan_active == 0) {
            finish();
        } else if (handlingsplan_active == 1) {
            final SharedPreferences spinner_memory = getSharedPreferences("Innstillinger", MODE_PRIVATE);
            SharedPreferences.Editor inn = spinner_memory.edit();
            inn.putInt("pos", spinner_pos);
            inn.commit();

            finish();
            Intent RESET = new Intent(MainHandlingsplan.this, MainHandlingsplan.class);
            startActivity(RESET);
            handlingsplan_active = 0;

        }
        return;
    }


}
