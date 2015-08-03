package com.iaesteintern;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

//Bruker ListActivity, slik at eg kan lage en meny
public class MainHome extends ListActivity implements Runnable {

    //TEST


    private ProgressDialog progressDialog;
    SharedPreferences app_preferences;

    String name = "", pass = "";
    HttpPost httppost;
    HttpResponse response;
    HttpClient httpclient;
    InputStream inputStream;
    List<NameValuePair> nameValuePairs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setHomeButtonEnabled(false);
        String[] main_menu = getResources().getStringArray(R.array.string_main_intern);
        setListAdapter(new ArrayAdapter<String>(this, R.layout.main_2, R.id.label_main, main_menu));
        ListView mv = getListView();
        mv.setTextFilterEnabled(true);

        getListView().setCacheColorHint(0);
        getListView().setBackgroundResource(R.drawable.back_home_02);

        final SharedPreferences loginSettings = getSharedPreferences("LoginSettings", MODE_PRIVATE);
        final SharedPreferences innstillinger = getSharedPreferences("Innstillinger", MODE_PRIVATE);
        SharedPreferences.Editor inn = innstillinger.edit();
        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);

        setTitle(getString(R.string.title_02) + loginSettings.getString("user", ""));



        //Sjekker hvis knappene blir trykket, og gjør forskjellige ting:
        mv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //BK_knappen
                if (position == 0) {
                    if (loginSettings.getBoolean("login", true)) {
                        Intent in = new Intent(MainHome.this, MainBKTopTen.class);
                        startActivity(in);
                    } else {

                        Toast.makeText(getApplicationContext(), R.string.login_01,
                                Toast.LENGTH_SHORT).show();

                    }

                }
                //IAESTekst sitat


                //Kalender knappen
                else if (position == 1) {

                    final SharedPreferences spinner_memory = getSharedPreferences("Innstillinger", MODE_PRIVATE);
                    SharedPreferences.Editor inn = spinner_memory.edit();
                    inn.putInt("pos", 0);
                    inn.commit();

                    Intent in = new Intent(MainHome.this, MainKalender.class);
                    startActivity(in);

                    //Medlemlister

                } else if (position == 2) {

                    Intent in = new Intent(MainHome.this, MainMedlemslister.class);
                    startActivity(in);

                } else if (position == 3) {

                   /* Intent in = new Intent(MainHome.this, CollectionDemoActivity.class);

                    startActivity(in);*/


                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu
            (Menu
                     menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_1, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected
            (MenuItem
                     item) {
        switch (item.getItemId()) {
            case R.id.innstillinger:
                Intent in2 = new Intent(MainHome.this, MainInnstillinger.class);
                startActivity(in2);
                return true;
            case R.id.credits:
                Intent in3 = new Intent(MainHome.this, MainCredits.class);
                startActivity(in3);
                return true;
            case R.id.refresh:


                //Check if the internet connection is true
                ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
                Boolean isInternetPresent = cd.isConnectingToInternet(); // true or false

                if (!isInternetPresent) {
                    Toast.makeText(getApplicationContext(), R.string.errorcode_03, Toast.LENGTH_LONG).show();

                } else {

                    //Start the update from the IAESTE server, show process dialog
                    progressDialog = ProgressDialog.show(MainHome.this, getString(R.string.update_text_02), getString(R.string.down_01), true, false);
                    Thread thr = new Thread(this);
                    thr.start();

                }
                return true;
            case R.id.loggut:
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
        }
    };


    public String toHex(String arg) {
        return String.format("%x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }

    //Logout, set all the variables null, so the user can access in the app

    public void logout() {
        SharedPreferences.Editor editor = app_preferences.edit();
        //Setter alt til null...
        editor.putBoolean("auth", false);
        editor.putString("checked", "no");
        editor.putString("username", "");
        editor.putString("password", "");
        editor.commit();
        Intent ut = new Intent(MainHome.this, testLogin.class);
        startActivity(ut);
        finish(); //Gjør at ikke brukeren kan gå tilbake i MainHome, dreper hele activityen
    }


    public void run() {

        httpclient = new DefaultHttpClient();

        String tekst_php = "";
        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String Str_user = app_preferences.getString("username", "0");
        String Str_pass = app_preferences.getString("password", "0");

        if (!app_preferences.getBoolean("auth", false)) {
            logout();
        }

        try {
            name = toHex(Str_user);   //Convert to HEX
            pass = Str_pass;
            httppost = new HttpPost(getString(R.string.url_check_member) + "?det1=" + name + "&det2=" + pass);
            // Add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("UserEmail", name.trim()));
            nameValuePairs.add(new BasicNameValuePair("Password", pass.trim()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            tekst_php = httpclient.execute(httppost, responseHandler);
            System.out.println("Response : " + response);

            inputStream.close();
        } catch (Exception e) {
            //Toast.makeText(testLogin.this, "error" + e.toString(), Toast.LENGTH_SHORT).show();
        }

        String error = "error"; //Return val
        //setTitle(R.string.login_title);

        //If the user doesn't exist, set the variables to null, one attempt to use the app again.
        if (tekst_php.matches(error)) {
            SharedPreferences.Editor editor = app_preferences.edit();
            editor.putBoolean("auth", false);
            editor.putString("checked", "no");
            editor.putString("username", "");
            editor.putString("password", "");
            editor.commit();
        }

        String[] url_links = getResources().getStringArray(R.array.url_list);//new String[2]; //HUSK Å FORANDRE LENGDEN PÅ VEKTOREN
        String[] file_data = getResources().getStringArray(R.array.filename_list);//new String[2];  //HUSK Å FORANDRE LENGDEN PÅ VEKTOREN

        for (int i = 0; i < url_links.length; i++) {
            String url = url_links[i];
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet method = new HttpGet(url);
            HttpResponse res = null;
            try {
                res = client.execute(method);
            } catch (ClientProtocolException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                InputStream is = res.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
                String line = null;
                FileOutputStream fos = openFileOutput(file_data[i], Context.MODE_PRIVATE);
                PrintStream ps = new PrintStream(fos);
                Integer a = 0;
                while ((line = reader.readLine()) != null) {
                    ps.print(line);
                    ps.println();
                    a++;
                }
                ps.close();
                fos.close();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        handler.sendEmptyMessage(0); // close progressbar

    }

    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE country (_id integer primary key autoincrement,name, cap, code);";
        db.execSQL(createQuery);
    }

}
