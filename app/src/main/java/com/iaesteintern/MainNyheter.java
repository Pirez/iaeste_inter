package com.iaesteintern;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: johanpbj
 * Date: 05.02.13
 * Time: 21:30
 * To change this template use File | Settings | File Templates.
 */
public class MainNyheter extends Activity{

    int lengden_nyheter = 20;
    int kolonne_nyheter = 5;
    String urlLink;



    private String[][] nyhet_data = new String[lengden_nyheter][kolonne_nyheter];   //PASS PÅ LENGDEN AV DATA DEN FÅR INN, aka 7 LK, 5 DATA INFORMASJON, 100 MEDLEMMER


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_nyheter);
        getActionBar().setDisplayHomeAsUpEnabled(true);


        try {
            String[] file_data = getResources().getStringArray(R.array.filename_list);   //Medlem data filen
            FileInputStream fis = openFileInput(file_data[4]);
            BufferedReader r = new BufferedReader(new InputStreamReader(fis));
            String line;
            String data;

            int ii = 0;

            //Leser av teskstfilen, og putter data inn i array, og sorteres etter LK, og deretter informasjon om personern, eks.navn,tlf...
            while ((line = r.readLine()) != null) {
                data = (getWord(line, ';', 1));                            //Leser fra første kolonne datasett
                // j = data, i = personer
                ; //Hvor mangen kolonner den skal lese inn

                for (int i = 0; i < kolonne_nyheter ; i++) {
                    nyhet_data[ii][i] = convertHexToString(getWord(data, '*', i));
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
       // ScrollView nyhetsScroll = (ScrollView) findViewById(R.id.nyhets_scroll);
        LinearLayout nyhetsView = (LinearLayout) findViewById(R.id.nyhetsview);
        LinearLayout mainNyhet = (LinearLayout) findViewById(R.id.mainnyhetView);


        String tmp_overskrift;
        String tmp_nyhet;
        String tmp_bildelink;
        String tmp_created;
        String tmp_navn;


        for (int i = 0; i < lengden_nyheter; i++) {
            tmp_overskrift = nyhet_data[i][0];
            tmp_nyhet = nyhet_data[i][1];
            tmp_bildelink = nyhet_data[i][2];
            tmp_created = nyhet_data[i][3];
            tmp_navn = nyhet_data[i][4];


            String[] link = tmp_bildelink.split(",");
            Log.d("bildeurl", link + "");

            View inflatedView = getLayoutInflater().inflate(R.layout.nyhets_view, nyhetsView, false);

            TextView nyhetOverskrift = (TextView) inflatedView.findViewById(R.id.main_nyhet_overskrift);
            nyhetOverskrift.setText(tmp_overskrift);

            urlLink = getString(R.string.url_pictures_news) + link[0];

            TextView nyhetTekst = (TextView) inflatedView.findViewById(R.id.main_nyhet_innhold);
            nyhetTekst.setText(Html.fromHtml(tmp_nyhet));

            TextView nyhetCreated = (TextView) inflatedView.findViewById(R.id.main_nyhet_created);
            nyhetCreated.setText(tmp_created);

            TextView nyhetNavn = (TextView) inflatedView.findViewById(R.id.main_nyhet_navn);
            nyhetNavn.setText(tmp_navn);

            ImageView nyhetBilde = (ImageView) inflatedView.findViewById(R.id.main_nyhet_bilde);



            //new DownloadImageTask((ImageView) findViewById(R.id.main_nyhet_bilde)).execute(urlLink);
            Picasso.with(this)
                    .load(urlLink)
                    .placeholder(R.drawable.people)
                    .error(R.drawable.iaesteorange)
                    .fit()
                    .into(nyhetBilde);

            /*nyhetBilde.setScaleType(ImageView.ScaleType.FIT_XY);
            nyhetBilde.setAdjustViewBounds(true);*/

            mainNyhet.addView(inflatedView);

        }

}

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

    public String convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...

        int hex_len = hex.length();

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

    }



