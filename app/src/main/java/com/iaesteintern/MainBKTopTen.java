package com.iaesteintern;
//package com.android.HelloWorld;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Vector;

public class MainBKTopTen extends Activity {
    /**
     * Called when the activity is first created.
     */
    int lk_active_ = 7;
    int i0 = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    int i5 = 0;
    int i6 = 0;
    int j = 0;
    //private int[] lk_active_len = new int[8];
    SharedPreferences app_preferences;


    private String[][][] bk_data = new String[8][5][210];   //PASS PÅ LENGDEN AV DATA DEN FÅR INN, aka 7 LK, 5 DATA INFORMASJON, 200 MEDLEMMER
    private Vector<String> bkliste_data = new Vector<String>();
    private Vector<String> bk_lk = new Vector<String>();
    private GestureDetector gestureDetector;
    private ProgressDialog progressDialog;
    Typeface iaesteFont ;
    Typeface iaesteFontBold;

    TextView BKTitle;
    TextView meanTitle;
    TextView hjelpeTekst;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainbk_topten);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //final long t0 = System.currentTimeMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.secondary));
        }


        //Definerer selve fonten og setter font på der de skal være
        iaesteFont = Typeface.createFromAsset(getAssets(), "fonts/iaesteFont.ttf");
        iaesteFontBold = Typeface.createFromAsset(getAssets(), "fonts/iaesteFontBold.ttf");

        BKTitle = (TextView) findViewById(R.id.text_mainbk_title);
        meanTitle = (TextView) findViewById(R.id.mean_title);
        hjelpeTekst = (TextView) findViewById(R.id.hjelpe_tekst);

        BKTitle.setTypeface(iaesteFontBold);
        meanTitle.setTypeface(iaesteFontBold);
        hjelpeTekst.setTypeface(iaesteFont);



        String[] lk_list = getResources().getStringArray(R.array.string_LK_BK);
        //setTitle(getString(R.string.title_05) + " - " + lk_list[lk_active_]);

        try {
            String[] file_data = getResources().getStringArray(R.array.filename_list);   //Medlem data filen
            FileInputStream fis = openFileInput(file_data[1]);
            BufferedReader r = new BufferedReader(new InputStreamReader(fis));
            String line;

            //Oversikt over verdiene til LK'ene i datasettet
            String oslo_hex = "(.*)4f736c6f";//
            String bergen_hex = "(.*)42657267656e";//
            String grimstad_hex = "(.*)4772696d73746164";//
            String stavanger_hex = "(.*)54726f6e646865696d";//"Dette er egentlig LK Trondheim";
            String tromso_hex = "(.*)54726f6d73(.*)";//Søker kun med lk-er med Troms i midten
            String trondheim_hex = "(.*)53746176616e676572";// Dette er egentlig LK Stavanger
            String aas_hex = "(.*)73";//Søker kun på lk-er med s på slutten

            //Leser av teskstfilen, og putter data inn i array, og sorteres etter LK, og deretter informasjon om personern, eks.navn,tlf...
            while ((line = r.readLine()) != null) {
                bkliste_data.add(getWord(line, ';', 1));                            //Leser fra første kolonne datasett
                bk_lk.add((getWord(bkliste_data.get(j), '*', 2)));                  //Lese LK og sortere dem etterpå da
                // j = data, i = personer
                Integer words = 4; //Hvor mangen kolonner den skal lese inn

                for (int j7 = 0; j7 < words; j7++) {
                    bk_data[7][j7][j] = convertHexToString(getWord(bkliste_data.get(j), '*', j7));
                }

                if (bk_lk.get(j).matches(oslo_hex)) {
                    //Oslo
                    for (int j2 = 0; j2 < words; j2++) {
                        bk_data[2][j2][i2] = convertHexToString(getWord(bkliste_data.get(j), '*', j2));
                        bk_data[2][2][i2] = lk_list[2];
                        bk_data[7][2][j] = lk_list[2];

                    }

                    i2++;
                }
                else if (bk_lk.get(j).matches(bergen_hex)) {
                    //Bergen
                    for (int j0 = 0; j0 < words; j0++)
                        bk_data[0][j0][i0] = convertHexToString(getWord(bkliste_data.get(j), '*', j0));
                    bk_data[0][2][i0] = lk_list[0];
                    bk_data[7][2][j] = lk_list[0];

                    i0++;

                }

                else if (bk_lk.get(j).matches(grimstad_hex)) {
                    //Grimstad
                    for (int j1 = 0; j1 < words; j1++)
                        bk_data[1][j1][i1] = convertHexToString(getWord(bkliste_data.get(j), '*', j1));
                    bk_data[1][2][i1] = lk_list[1];
                    bk_data[7][2][j] = lk_list[1];


                    i1++;
                }

                else if (bk_lk.get(j).matches(stavanger_hex)) {
                    //Stavanger
                    for (int j5 = 0; j5 < words; j5++)
                        bk_data[5][j5][i5] = convertHexToString(getWord(bkliste_data.get(j), '*', j5));
                    //Byttet string med Trondheim, 3 == stavanger
                    bk_data[5][2][i5] = lk_list[5];
                    bk_data[7][2][j] = lk_list[5];


                    i5++;
                }

                else if (bk_lk.get(j).matches(tromso_hex)) {
                    //Tromsø
                    for (int j4 = 0; j4 < words; j4++)
                        bk_data[4][j4][i4] = convertHexToString(getWord(bkliste_data.get(j), '*', j4));
                    bk_data[4][2][i4] = lk_list[4];
                    bk_data[7][2][j] = lk_list[4];


                    i4++;
                }

                else if (bk_lk.get(j).matches(trondheim_hex)) {
                    //Trondheim

                    for (int j3 = 0; j3 < words; j3++)
                        bk_data[3][j3][i3] = convertHexToString(getWord(bkliste_data.get(j), '*', j3));
                    //Byttet string med Stavanger, 5 == Trondheim
                    bk_data[3][2][i3] = lk_list[3];
                    bk_data[7][2][j] = lk_list[3];


                    i3++;
                }

                else if (bk_lk.get(j).matches(aas_hex)) {
                    //Ås
                    for (int j6 = 0; j6 < words; j6++)
                        bk_data[6][j6][i6] = convertHexToString(getWord(bkliste_data.get(j), '*', j6));
                    bk_data[6][2][i6] = lk_list[6];
                    bk_data[7][2][j] = lk_list[6];

                    i6++;
                }

                //NASJONALT - AKA Alle medlemmer i en vektor

                j++;
            }
            fis.close();


        } catch (FileNotFoundException e) {

            Toast.makeText(getApplicationContext(), "Error! Code: 02", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            //Teksten er koruppt
            Toast.makeText(getApplicationContext(), "IO fail! Code: 01",
                    Toast.LENGTH_SHORT).show();
        }

        int[] lk_active_len = {i0, i1, i2, i3, i4, i5, i6, j};

        top10(lk_active_, lk_active_len[lk_active_]);

        //Log.e("TESTBALLE", "Total time: " + Long.toString(System.currentTimeMillis()-t0));


        gestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override

                    public boolean onFling(MotionEvent e1, MotionEvent e2
                            , float velocityX, float velocityY) {

                        int LARGE_MOVE = 35;
                        int[] lk_active_len = {i0, i1, i2, i3, i4, i5, i6, j};
                        if (e2.getX() - e1.getX() > LARGE_MOVE) {

                            //Toast.makeText(getApplicationContext(), "IO feil! Kode: 01", Toast.LENGTH_SHORT).show();


                            if (lk_active_ > 0) {
                                //MainSpillere.this.overridePendingTransition(R.anim.left_right, R.anim.right_left);
                                lk_active_ -= 1;
                                top10(lk_active_, lk_active_len[lk_active_]);
                            } else {
                                lk_active_ = 7;
                                top10(lk_active_, lk_active_len[lk_active_]);
                            }

                        } else if (e1.getX() - e2.getX() > LARGE_MOVE) {
                            if (lk_active_ < 7) {
                                lk_active_ += 1;
                                top10(lk_active_, lk_active_len[lk_active_]);

                            } else {
                                lk_active_ = 0;
                                top10(lk_active_, lk_active_len[lk_active_]);
                            }
                        }

                        return false;
                    }
                });


    }

    public void top10(Integer lk_active, int len) {
        String[] lk_list = getResources().getStringArray(R.array.string_LK_BK);


        setActionBarTitle(getString(R.string.title_05) + " - " + Integer.toString(lk_active + 1) + "/8");
        TextView title_lk = (TextView) findViewById(R.id.text_mainbk_title);
        title_lk.setText(lk_list[lk_active]);


        int[] temp_poeng_i = new int[len];
        String[] temp_poeng_s = new String[len];
        int[] index_; // = new int[len];
        System.arraycopy(bk_data[lk_active][1], 0, temp_poeng_s, 0, len);
        for (int i = 0; i < len; i++) {
            temp_poeng_i[i] = Integer.parseInt(temp_poeng_s[i]);
        }

        index_ = sortIndex(temp_poeng_i, len);


        TextView mean_value = (TextView) findViewById(R.id.text_mainbk_gjennom);
        mean_value.setTypeface(iaesteFont);
        mean_value.setText(mean_valu(temp_poeng_i, len) + " poeng per medlem");


        TextView one_1 = (TextView) findViewById(R.id.text_mainbk_first_place);
        TextView one_2 = (TextView) findViewById(R.id.text_mainbk_first_place_lk);
        TextView two_1 = (TextView) findViewById(R.id.text_mainbk_second_place);
        TextView two_2 = (TextView) findViewById(R.id.text_mainbk_second_place_lk);
        TextView third_1 = (TextView) findViewById(R.id.text_mainbk_third_place);
        TextView third_2 = (TextView) findViewById(R.id.text_mainbk_third_place_lk);
        TextView fourth_1 = (TextView) findViewById(R.id.text_mainbk_fourth_place);
        TextView fourth_2 = (TextView) findViewById(R.id.text_mainbk_fourth_place_lk);
        TextView fith_1 = (TextView) findViewById(R.id.text_mainbk_fith_place);
        TextView fith_2 = (TextView) findViewById(R.id.text_mainbk_fith_place_lk);
        TextView sixth_1 = (TextView) findViewById(R.id.text_mainbk_sixth_place);
        TextView sixth_2 = (TextView) findViewById(R.id.text_mainbk_sixth_place_lk);
        TextView seventh_1 = (TextView) findViewById(R.id.text_mainbk_seventh_place);
        TextView seventh_2 = (TextView) findViewById(R.id.text_mainbk_seventh_place_lk);
        TextView eight_1 = (TextView) findViewById(R.id.text_mainbk_eight_place);
        TextView eight_2 = (TextView) findViewById(R.id.text_mainbk_eight_place_lk);
        TextView nine_1 = (TextView) findViewById(R.id.text_mainbk_nine_place);
        TextView nine_2 = (TextView) findViewById(R.id.text_mainbk_nine_place_lk);
        TextView ten_1 = (TextView) findViewById(R.id.text_mainbk_ten_place);
        TextView ten_2 = (TextView) findViewById(R.id.text_mainbk_ten_place_lk);

        one_1.setTypeface(iaesteFont);
        one_2.setTypeface(iaesteFont);
        two_1.setTypeface(iaesteFont);
        two_2.setTypeface(iaesteFont);
        third_1.setTypeface(iaesteFont);
        third_2.setTypeface(iaesteFont);
        fourth_1.setTypeface(iaesteFont);
        fourth_2.setTypeface(iaesteFont);
        fith_1.setTypeface(iaesteFont);
        fith_2.setTypeface(iaesteFont);
        sixth_1.setTypeface(iaesteFont);
        sixth_2.setTypeface(iaesteFont);
        seventh_1.setTypeface(iaesteFont);
        seventh_2.setTypeface(iaesteFont);
        eight_1.setTypeface(iaesteFont);
        eight_2.setTypeface(iaesteFont);
        nine_1.setTypeface(iaesteFont);
        nine_2.setTypeface(iaesteFont);
        ten_1.setTypeface(iaesteFont);
        ten_2.setTypeface(iaesteFont);


        one_1.setText(bk_data[lk_active][3][index_[len - 1]]);
        one_2.setText(bk_data[lk_active][2][index_[len - 1]]);
        if (len > 2) {

            two_1.setText(bk_data[lk_active][3][index_[len - 2]]);
            two_2.setText(bk_data[lk_active][2][index_[len - 2]]);
        }
        if (len > 3) {

            third_1.setText(bk_data[lk_active][3][index_[len - 3]]);
            third_2.setText(bk_data[lk_active][2][index_[len - 3]]);
        }
        if (len > 4) {

            fourth_1.setText(bk_data[lk_active][3][index_[len - 4]]);
            fourth_2.setText(bk_data[lk_active][2][index_[len - 4]]);
        }
        if (len > 5) {


            fith_1.setText(bk_data[lk_active][3][index_[len - 5]]);
            fith_2.setText(bk_data[lk_active][2][index_[len - 5]]);
        }
        if (len > 6) {


            sixth_1.setText(bk_data[lk_active][3][index_[len - 6]]);
            sixth_2.setText(bk_data[lk_active][2][index_[len - 6]]);
        }
        if (len > 7) {


            seventh_1.setText(bk_data[lk_active][3][index_[len - 7]]);
            seventh_2.setText(bk_data[lk_active][2][index_[len - 7]]);
        } else {
            seventh_1.setText("");
            seventh_2.setText("");
        }
        if (len > 8) {


            eight_1.setText(bk_data[lk_active][3][index_[len - 8]]);
            eight_2.setText(bk_data[lk_active][2][index_[len - 8]]);
        } else {
            eight_1.setText("");
            eight_2.setText("");
        }
        if (len > 9) {


            nine_1.setText(bk_data[lk_active][3][index_[len - 9]]);
            nine_2.setText(bk_data[lk_active][2][index_[len - 9]]);
        } else {
            nine_1.setText("");
            nine_2.setText("");
        }
        if (len > 10) {


            ten_1.setText(bk_data[lk_active][3][index_[len - 10]]);
            ten_2.setText(bk_data[lk_active][2][index_[len - 10]]);
        } else {
            ten_1.setText("");
            ten_2.setText("");
        }
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

    public String mean_valu(int[] temp_poeng_i, int len)

    {
        //Calculate the mean value
        int sum_total = 0;
        double m = 0;
        for (int i = 0; i < len; i++) {
            if (temp_poeng_i[i] != 0) {
                sum_total += temp_poeng_i[i];
                m += 1.;
            }
        }
        //Format the mean value to X.XX format
        double mean = sum_total / m;
        DecimalFormat mean_ = new DecimalFormat("#.##");
        return mean_.format(mean);
    }

    public String mean_valu_zero(int[] temp_poeng_i, int len)

    {
        //Calculate the mean value
        int sum_total = 0;
        double m = 0;
        for (int i = 0; i < len; i++) {
            sum_total += temp_poeng_i[i];
            m += 1.;
        }
        //Format the mean value to X.XX format
        double mean = sum_total / m;
        DecimalFormat mean_ = new DecimalFormat("#.##");
        return mean_.format(mean);
    }


    public int[] sortIndex(int[] vec, int len) {

        int[] index_ = new int[len];
        double min = vec[0];
        int pos = 0;
        for (int i = 0; i < len; i++) index_[i] = -1;

        for (int j = 0; j < len; j++) {
            for (int i = 0; i < len; i++) {
                Boolean bNext = false;
                for (int k = 0; k < j; k++)
                    if (index_[k] == i) bNext = true;
                if (vec[i] < min && !bNext) {
                    pos = i;
                    min = vec[i];
                }
            }
            index_[j] = pos;
            min = Double.MAX_VALUE;
        }
        return index_;
    }

    public int[] sortIndex_double(double[] vec, int len) {

        int[] index_ = new int[len];
        double min = vec[0];
        int pos = 0;
        for (int i = 0; i < len; i++) index_[i] = -1;

        for (int j = 0; j < len; j++) {
            for (int i = 0; i < len; i++) {
                Boolean bNext = false;
                for (int k = 0; k < j; k++)
                    if (index_[k] == i) bNext = true;
                if (vec[i] < min && !bNext) {
                    pos = i;
                    min = vec[i];
                }
            }
            index_[j] = pos;
            min = Double.MAX_VALUE;
        }
        return index_;
    }


    private void setActionBarTitle (String actionBarTitle) {
        SpannableString s = new SpannableString(actionBarTitle);
        s.setSpan(new TypefaceSpan(this, "iaesteFontBold.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);

    }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

    @Override
    public boolean onCreateOptionsMenu
            (Menu
                     menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bktop, menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected
            (MenuItem
                     item) {
        switch (item.getItemId()) {


            case R.id.dinPlassering:
                popup_window_dinplassering();
                return true;

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
    Intent intent = new Intent(MainBKTopTen.this, MainHomeNav.class);       //Går tilbake til MainHomeNav
    startActivity(intent);
    finish();

}

    public void reload() {
        //Funker denne? Usikker, sjekk ut animasjon senere
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    public void popup_window_dinplassering() {

        final Dialog
                dialog = new Dialog(MainBKTopTen.this);
        dialog.setContentView(R.layout.dialog_bkdinplassering);
        dialog.setTitle(getString(R.string.bk_04));
        dialog.setCancelable(true);
        dialog.show();

        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String Str_pass = app_preferences.getString("password", "0");
        TextView plassering = (TextView) dialog.findViewById(R.id.text_dinplass_01);
        final String id = convertHexToString(Str_pass);

        boolean finnes = false;
        int[] temp_poeng_i = new int[j];
        String[] temp_poeng_s = new String[j];
        int[] index_; // = new int[len];
        System.arraycopy(bk_data[7][1], 0, temp_poeng_s, 0, j);
        for (int i = 0; i < j; i++) {
            temp_poeng_i[i] = Integer.parseInt(temp_poeng_s[i]);
        }
        index_ = sortIndex(temp_poeng_i, j);
        for (int i = 0; i < j; i++) {
            if (bk_data[7][0][index_[i]].matches(id)) {
                plassering.setText(getString(R.string.bk_05) + " " + Integer.toString(j - i) + " " + getString(R.string.bk_06) + " " + Integer.toString(j) + " " + getString(R.string.bk_07));
                finnes = true;
                //String s = String.format("%s", getString(R.string.innstillinger_01));
                //plassering.setText(R.string.innstillinger_01);
            }
        }
        if (!finnes) {
            plassering.setText(" ");
            Toast.makeText(getApplicationContext(), R.string.bk_03,
                    Toast.LENGTH_SHORT).show();
        }

    }


    public void popup_window_top3lk() {

        final Dialog
                dialog = new Dialog(MainBKTopTen.this);
        dialog.setContentView(R.layout.dialog_bktop3lk);
        dialog.setTitle(getString(R.string.bk_01));
        dialog.setCancelable(true);
        dialog.show();

        final TextView first_lk = (TextView) dialog.findViewById(R.id.text_mainbk_first_place_lk_top3);
        final TextView second_lk = (TextView) dialog.findViewById(R.id.text_mainbk_second_place_lk_top3);
        final TextView third_lk = (TextView) dialog.findViewById(R.id.text_mainbk_third_place_lk_top3);
        final TextView fourth_lk = (TextView) dialog.findViewById(R.id.text_mainbk_fourth_place_lk_top3);
        final TextView five_lk = (TextView) dialog.findViewById(R.id.text_mainbk_five_place_lk_top3);

        final TextView first_lk_p = (TextView) dialog.findViewById(R.id.text_mainbk_first_place_poeng_top3);
        final TextView second_lk_p = (TextView) dialog.findViewById(R.id.text_mainbk_second_place_poeng_top3);
        final TextView third_lk_p = (TextView) dialog.findViewById(R.id.text_mainbk_third_place_poeng_top3);
        final TextView fourth_lk_p = (TextView) dialog.findViewById(R.id.text_mainbk_fourth_place_poeng_top3);
        final TextView five_lk_p = (TextView) dialog.findViewById(R.id.text_mainbk_five_place_poeng_top3);

        //Definere variabler
        int[] lk_active_len = {i0, i1, i2, i3, i4, i5, i6, j};
        String[] lk_list = getResources().getStringArray(R.array.string_LK_BK);
        int len_lk;
        int[] index_lk = new int[j];
        double[] mean_lk_all = new double[j];
        String[] temp_poeng_s = new String[j];
        //Går gjennom alle ll-ene, og finner gjennomsnittet
        for (int i = 0; i < lk_active_; i++) {
            double sum_lk = 0;
            //Definere variabler
            len_lk = lk_active_len[i];
            //Kopiere poenge til en ny vektor, med lik lengde som antall medlemmer i lk
            System.arraycopy(bk_data[i][1], 0, temp_poeng_s, 0, len_lk);
            //Summere opp total summen og finner gjennomsnitt verdien
            for (int ii = 0; ii < len_lk; ii++) {
                sum_lk += Double.parseDouble(temp_poeng_s[ii]); //temp_poeng_d[ii];
            }
            mean_lk_all[i] = sum_lk / len_lk;
        }

        //Sortere mean value fra alle lk, og finner indeksene
        index_lk = sortIndex_double(mean_lk_all, j);

        int len_index = index_lk.length;
        first_lk.setText(lk_list[index_lk[len_index - 1]]);
        second_lk.setText(lk_list[index_lk[len_index - 2]]);
        third_lk.setText(lk_list[index_lk[len_index - 3]]);
        fourth_lk.setText(lk_list[index_lk[len_index - 4]]);
        five_lk.setText(lk_list[index_lk[len_index - 5]]);

        DecimalFormat f = new DecimalFormat("#.##");

        first_lk_p.setText(f.format(mean_lk_all[index_lk[len_index - 1]]));
        second_lk_p.setText((f.format(mean_lk_all[index_lk[len_index - 2]])));
        third_lk_p.setText(f.format(mean_lk_all[index_lk[len_index - 3]]));
        fourth_lk_p.setText(f.format(mean_lk_all[index_lk[len_index - 4]]));
        five_lk_p.setText(f.format(mean_lk_all[index_lk[len_index - 5]]));

    }


}



