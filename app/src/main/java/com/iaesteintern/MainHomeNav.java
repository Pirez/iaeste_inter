package com.iaesteintern;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
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

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class MainHomeNav extends ActionBarActivity
        implements NavigationDrawerCallbacks, Runnable, NewsFragment.OnFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;

    private ProgressDialog progressDialog;
    SharedPreferences app_preferences;

    String name = "", pass = "";
    String realName, username, localComm, picLink;
    String[] userInfo;

    HttpPost httppost;
    HttpResponse response;
    HttpClient httpclient;
    InputStream inputStream;
    List<NameValuePair> nameValuePairs;

    ScrollView nyhetsScroll;
    SwipeRefreshLayout swipeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home_nav);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        realName = app_preferences.getString("name", "0");
        username = app_preferences.getString("username", "0");
        localComm = app_preferences.getString("idiaeste", "0");
        picLink = app_preferences.getString("picture", "0");
        Log.d(localComm, "IAESTE LK");
        Log.d(realName, "IAESTE NAME");
        Log.d(picLink, "IAESTE bilde");



        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.secondary));
        }

        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);

        mNavigationDrawerFragment.setUserData(realName, username, localComm, picLink);





        final SharedPreferences loginSettings = getSharedPreferences("LoginSettings", MODE_PRIVATE);
        final SharedPreferences innstillinger = getSharedPreferences("Innstillinger", MODE_PRIVATE);
        SharedPreferences.Editor inn = innstillinger.edit();
        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);



       /* swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        nyhetsScroll = (ScrollView) findViewById(R.id.nyhets_scroll);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                Log.d("Swipe", "Refreshing Number");

               refresh();


            }
        });
        int scrollY = nyhetsScroll.getScrollY();
        if (scrollY == 0) swipeLayout.setEnabled(true);
        else swipeLayout.setEnabled(false);


        nyhetsScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                int scrollY = nyhetsScroll.getScrollY();
                if (scrollY == 0) swipeLayout.setEnabled(true);
                else swipeLayout.setEnabled(false);

            }
        });*/


    }



    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
       Fragment fragment;
        Log.d("pos", position + "");
        switch (position){

            case 0:
                fragment = getFragmentManager().findFragmentByTag(NewsFragment.TAG);
                if (fragment == null) {
                    fragment = new NewsFragment();
                }
                getFragmentManager().beginTransaction().replace(R.id.container, fragment, NewsFragment.TAG).commit();
                break;

            case 2: // BK

                Intent i = new Intent(MainHomeNav.this,
                        MainBKTopTen.class);
                startActivity(i);
                finish();
                break;

            case 3: // Kalender

                Intent i2 = new Intent(MainHomeNav.this,
                        MainKalender.class);
                startActivity(i2);
                finish();
                break;

            case 1: // Medlemsliste

                Intent i4 = new Intent(MainHomeNav.this,
                        MainMedlemslister.class);
                startActivity(i4);
                finish();
                break;

            case 4: // Innstillinger

                Intent i5 = new Intent(MainHomeNav.this,
                        MainInnstillinger.class);
                startActivity(i5);
                finish();
                break;

            case 5: //Logg utt
                logout();
                break;


        }
    }


    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main_home_nav, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
                       refresh();


        return super.onOptionsItemSelected(item);
    }


    /*################################### KOPIERT FRA MAINHOME #####################################*/


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
        editor.apply();
        Intent ut = new Intent(MainHomeNav.this, testLogin.class);
        startActivity(ut);
        finish(); //Gjør at ikke brukeren kan gå tilbake i MainHome, dreper hele activityen
    }


    public void refresh() {

        //Check if the internet connection is true
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        Boolean isInternetPresent = cd.isConnectingToInternet(); // true or false


        if (!isInternetPresent) {
            Toast.makeText(getApplicationContext(), R.string.errorcode_03, Toast.LENGTH_LONG).show();

        } else {

            //Start the update from the IAESTE server, show process dialog
            progressDialog = ProgressDialog.show(MainHomeNav.this, getString(R.string.update_text_02), getString(R.string.down_01), true, false);
            Thread thr = new Thread(this);
            thr.start();
            Log.d("Refreshed ", "Refreshing Number");

        }
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
            Log.d(name, pass);
            // Add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("UserEmail", name.trim()));
            nameValuePairs.add(new BasicNameValuePair("Password", pass.trim()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            tekst_php = httpclient.execute(httppost, responseHandler);
            System.out.println("Response : " + tekst_php);

           /* userInfo = tekst_php.split("(\\*)");

            SharedPreferences.Editor editor = app_preferences.edit();
            editor.putString("navn", userInfo[0]);
            editor.putString("idiaeste", userInfo[1]);
            editor.putString("picture", userInfo[2]);
            editor.apply();*/

            System.out.println("VLOSED");
            inputStream.close();
        } catch (Exception e) {
            //Toast.makeText(testLogin.this, "error" + e.toString(), Toast.LENGTH_SHORT).show();
        }

        String error = "error"; //Return val
        Log.d("ERROR IN PHP", "");


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
