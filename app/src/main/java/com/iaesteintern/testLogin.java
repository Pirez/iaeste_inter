package com.iaesteintern;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.*;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import com.squareup.picasso.Picasso;

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

public class testLogin extends Activity implements Runnable {

    Button login, glemt_passord, back_passord, glemsk_passord;
    String name = "", pass = "", mail = "";
    EditText username, password;
    TextView tv, link, appText;
    byte[] data;
    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    InputStream inputStream;
    SharedPreferences app_preferences;
    List<NameValuePair> nameValuePairs;
    CheckBox check;
    ProgressDialog progressDialog;
    String userInfo;
    String[] userInfoArray;
    boolean firstTimeOpen;
    TextView activityTitle;

    Typeface iaesteFont;
    Typeface iaesteFontBold;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(com.iaesteintern.R.layout.test_login);

        //      Toast.makeText(testLogin.this, "Velkommen " + tekst_php, Toast.LENGTH_LONG).show();


        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //appText = (TextView) findViewById(R.id.text);
        username = (EditText) findViewById(com.iaesteintern.R.id.username);
        password = (EditText) findViewById(R.id.password);
        glemt_passord = (Button) findViewById(R.id.glemt_passord);
        back_passord = (Button) findViewById(R.id.back_passord);
        glemsk_passord = (Button) findViewById(R.id.glemsk_passord);
        activityTitle = (TextView) findViewById(R.id.iaesteoverskrift);

        login = (Button) findViewById(R.id.login);
        check = (CheckBox) findViewById(R.id.check);
        link = (TextView) findViewById(R.id.link_iaeste);

        String Str_user = app_preferences.getString("username", "0");
        String Str_pass = app_preferences.getString("password", "0");
        String Str_check = app_preferences.getString("checked", "no");
        Boolean Bol_auth = app_preferences.getBoolean("auth", false);

        firstTimeOpen = app_preferences.getBoolean("firstTime", true);


        //Definerer selve fonten og setter font på der de skal være
        iaesteFont = Typeface.createFromAsset(getAssets(), "fonts/iaesteFont.ttf");
        iaesteFontBold = Typeface.createFromAsset(getAssets(), "fonts/iaesteFontBold.ttf");

        //appText.setTypeface(iaesteFontBold);
        username.setTypeface(iaesteFont);
        password.setTypeface(iaesteFont);
        glemt_passord.setTypeface(iaesteFont);
        back_passord.setTypeface(iaesteFont);
        glemsk_passord.setTypeface(iaesteFont);
        login.setTypeface(iaesteFont);
        check.setTypeface(iaesteFont);
        link.setTypeface(iaesteFont);
        activityTitle.setTypeface(iaesteFont);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
        }

        SpannableString s = new SpannableString("IAESTE Norge");
        s.setSpan(new TypefaceSpan(this, "iaesteFontBold.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        //ActionBar actionBar = getActionBar();
        //actionBar.setTitle(s);
        //setBackgroundImage();

        hasUserOpenedBefore();
        if (Bol_auth) {
            Move_to_next_check();
        }

        if (Str_check.equals("yes")) {
            username.setText(Str_user);
            password.setText(Str_pass);
            check.setChecked(true);
        }
        user_pass_window();


        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                name = username.getText().toString();
                pass = password.getText().toString();
                String Str_check2 = app_preferences.getString("checked", "no");
                if (Str_check2.equals("yes")) {
                    SharedPreferences.Editor editor = app_preferences.edit();
                    editor.putString("username", name);
                    editor.putString("password", pass);
                    editor.apply();
                }
                if (name.equals("") || pass.equals("")) {
                    Toast.makeText(testLogin.this, R.string.login_11, Toast.LENGTH_LONG).show();
                } else if (!pass.matches(".*\\d+.*")) {
                    Toast.makeText(testLogin.this, R.string.login_26, Toast.LENGTH_LONG).show();

                } else {

                    ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
                    Boolean isInternetPresent = cd.isConnectingToInternet(); // true or false

                    if (!isInternetPresent) {
                        Toast.makeText(getApplicationContext(), R.string.errorcode_03, Toast.LENGTH_LONG).show();

                    } else {

                        new StartCheckAsyncTask(testLogin.this).execute((Void[]) null);
                    }

                }
            }
        });

        back_passord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Set the other objeclts GONE
                user_pass_window();
            }
        });

        glemsk_passord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mail = username.getText().toString();
                //Set the other objects GONE
                //Checks if is a email input, if yes, check the server if the user exisist
                if (mail.contains("@") || mail.contains(".")) {

                    ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
                    Boolean isInternetPresent = cd.isConnectingToInternet(); // true or false

                    if (!isInternetPresent) {
                        Toast.makeText(getApplicationContext(), R.string.errorcode_03, Toast.LENGTH_LONG).show();

                    } else {
                        // Log.e("CODE0101","balle");
                        // Toast.makeText(getApplicationContext(), "balle2", Toast.LENGTH_LONG).show();

                        new Startcheckglemtpassord(testLogin.this).execute((Void[]) null);
                    }
                } else {
                    //progressDialog = ProgressDialog.show(testLogin.this, getString(R.string.login_13), getString(R.string.login_14), true, false);
                    Toast.makeText(testLogin.this, R.string.login_16, Toast.LENGTH_LONG).show();

                }
            }
        });

        glemt_passord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lost_pass_window();
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks, depending on whether it's now checked
                SharedPreferences.Editor editor = app_preferences.edit();
                if (((CheckBox) v).isChecked()) {

                    editor.putString("checked", "yes");
                    editor.commit();
                } else {
                    editor.putString("checked", "no");
                    editor.commit();
                }
            }
        });
    }


    public String toHex(String arg) {
        return String.format("%x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }

    public void Move_to_next() {

        progressDialog = ProgressDialog.show(testLogin.this, getString(R.string.update_text_02), getString(R.string.down_01), true, false);
        Thread thr = new Thread(this);
        thr.start();


    }

    public void Move_to_next_check() {
        overridePendingTransition(R.transition.right_to_left, R.transition.left_to_right);
        Intent intent = new Intent(testLogin.this, MainHomeNav.class);
        startActivity(intent);
        finish();

    }

    /**
     * Kan brukes for dynamiske bilder i login screen
     * **/
/*public void setBackgroundImage() {
ImageView layBg = (ImageView) findViewById(R.id.login_bg);
        Picasso.with(this)
                .load(R.drawable.login_bg)
                .error(R.drawable.login_bg)
                .fit()
                .into(layBg);

    }*/
    public void user_pass_window() {

        password.setVisibility(View.VISIBLE);
        login.setVisibility(View.VISIBLE);
        check.setVisibility(View.VISIBLE);
        glemt_passord.setVisibility(View.VISIBLE);
        glemsk_passord.setVisibility(View.GONE);
        back_passord.setVisibility(View.GONE);
    }

    public void lost_pass_window() {
        password.setVisibility(View.GONE);
        login.setVisibility(View.GONE);
        glemt_passord.setVisibility(View.GONE);
        check.setVisibility(View.GONE);
        back_passord.setVisibility(View.VISIBLE);
        glemsk_passord.setVisibility(View.VISIBLE);
    }

    private void hasUserOpenedBefore() {

        if (firstTimeOpen) {
            popup_firstTime();
            SharedPreferences.Editor editor = app_preferences.edit();
            editor.putBoolean("firstTime", false);
            editor.apply();

        }
    }

    public void popup_firstTime() {
        final Dialog dialog = new Dialog(this);
        dialog.show();
        dialog.setContentView(R.layout.pop_up_firsttime);
        dialog.setTitle("Hente passord");
        dialog.setCancelable(true);
        Button btnUnderstood = (Button) dialog.findViewById(R.id.buttonUnderstood);
        TextView textInnhold = (TextView) dialog.findViewById(R.id.textView_forklaring);
        btnUnderstood.setTypeface(iaesteFontBold);
        textInnhold.setTypeface(iaesteFont);
        btnUnderstood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    private class StartCheckAsyncTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog dialog;
        private final Context context;

        public StartCheckAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            // setup your dialog here
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(context.getString(R.string.loginkoble_01));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... ignored) {

            String returnMessage = null;
            //SystemClock.sleep(1200);

            try {

                returnMessage = startCheck();

            } catch (Exception e) {
                //returnMessage = e.getMessage();
            }


            return returnMessage;
        }

        @Override
        protected void onPostExecute(String message) {
            String returnMessage = null;
            String error = "error";
            if (message.matches(error)) {
                Toast.makeText(testLogin.this, R.string.login_03, Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = app_preferences.edit();
                editor.putBoolean("auth", false);
                editor.commit();
            } else {
                SharedPreferences.Editor editor = app_preferences.edit();
                editor.putString("phpOutput", message);
                String hexUserInfo = message;
                Log.d("array:", hexUserInfo);

                //convertHexToString(hexUserInfo);
                if (!hexUserInfo.contains("error")) {
                    Toast.makeText(testLogin.this, "Velkommen!", Toast.LENGTH_LONG).show();
                    userInfoArray = hexUserInfo.split("\\*");

                    editor.putString("name", convertHexToString(userInfoArray[0]));
                    editor.putString("idiaeste", convertHexToString(userInfoArray[1]));
                    editor.putString("picture", convertHexToString(userInfoArray[2]));
                    editor.putBoolean("auth", true);
                    editor.apply();
                    //Go too next screen
                    Move_to_next();
                } else {

                    Toast.makeText(testLogin.this, "Passordet eller mailen din stemmer ikke!", Toast.LENGTH_LONG).show();
                } //Går inn
            }
            dialog.dismiss();
            /*if (message != null) {
                // process the error (show alert etc)
            } else {
                //Log.i("StartPaymentAsyncTask", "No problems");
            }*/
        }
    }


    public String startCheck() throws Exception {


        String tekst_php = "";
        try {
            httpclient = new DefaultHttpClient();
            Log.e("CODE0101", name);
            name = toHex(name);   //Convert to HEX

            httppost = new HttpPost("http://iaeste.no/playground/android_app/portal/check_member_2.php?det1=" + name + "&det2=" + pass);
            // Add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            Log.e("ValuePairs", name);
            nameValuePairs.add(new BasicNameValuePair("det1", name.trim()));
            nameValuePairs.add(new BasicNameValuePair("det2", pass.trim()));
            Log.e("ValuePairs LAGT INN", name + pass);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            Log.e("Vafdsfds", name + pass);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            Log.e("ValuePairs LAGT INN", name);
            tekst_php = httpclient.execute(httppost, responseHandler);
            Log.e("teksphp ok", tekst_php);


            //Return val
            //Confirm the user (//TODO Should change it in the future? Easy to hack!)

            inputStream.close();
        } catch (Exception e) {
            //Toast.makeText(testLogin.this, "error" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return tekst_php;

    }

    private class Startcheckglemtpassord extends AsyncTask<Void, Void, String> {
        private ProgressDialog dialog;
        private final Context context;

        public Startcheckglemtpassord(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            // setup your dialog here
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(context.getString(R.string.loginkoble_01));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... ignored) {

            String returnMessage = null;
            //SystemClock.sleep(1000);
            try {
                sjekkepost();

            } catch (Exception e) {
                //returnMessage = e.getMessage();
            }
            return returnMessage;
        }

        @Override
        protected void onPostExecute(String message) {
            String returnMessage = null;

            try {

            } catch (Exception e) {
                //returnMessage = e.getMessage();
            }
            dialog.dismiss();
            if (message != null) {
                // process the error (show alert etc)
                //Log.e("StartPaymentAsyncTask", String.format("I received an error: %s", message));
            } else {
                //Log.i("StartPaymentAsyncTask", "No problems");
            }
        }
    }

    public void sjekkepost() throws Exception {
        try {

            httpclient = new DefaultHttpClient();
            mail = toHex(mail);   //Convert to HEX
            Log.d("forgot password", mail);
            httppost = new HttpPost("http://iaeste.no/playground/android_app/portal/forgot.php?det1=" + mail);
            // Add your data
            nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("det1", mail.trim()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String tekst_php = httpclient.execute(httppost, responseHandler);
            Log.d("forgot password", "OK!");
            String error = "error"; //Return val
            if (tekst_php.matches(error)) {
                Toast.makeText(testLogin.this, R.string.login_17, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(testLogin.this, R.string.login_18, Toast.LENGTH_LONG).show();
                user_pass_window();
            }
            inputStream.close();
        } catch (Exception e) {
        }
    }

    public void run() {
        httpclient = new DefaultHttpClient();

        String tekst_php = "";
        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String Str_user = app_preferences.getString("username", "0");
        String Str_pass = app_preferences.getString("password", "0");

               /*
        try {
            //name = toHex(Str_user);   //Convert to HEX
            pass = Str_pass;
            httppost = new HttpPost("http://iaeste.no/playground/android_app/portal/check_member.php"); // +
            // Add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            //Send the data as php queries
            nameValuePairs.add(new BasicNameValuePair("det1", name.trim()));
            nameValuePairs.add(new BasicNameValuePair("det2", pass.trim()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            tekst_php = httpclient.execute(httppost, responseHandler);
            //System.out.println("Response : " + response);
            inputStream.close();
        } catch (Exception e) {
            //Toast.makeText(testLogin.this, "error" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        String error = "error"; //Return val
        //setTitle(R.string.login_title);
        if (tekst_php.matches(error)) {
            SharedPreferences.Editor editor = app_preferences.edit();
            //Setter alt til null...
            editor.putBoolean("auth", false);
            editor.putString("checked", "no");
            editor.putString("username", "");
            editor.putString("password", "");
            editor.commit();
            //Toast.makeText(MainHome.this, Str_user, Toast.LENGTH_LONG).show();
            //handler.sendEmptyMessage(0); // close progressbar
            //logout();
        }  else {
                */
        //Integer url_links_len = getResources().getStringArray(R.array.url_list).length;
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


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            progressDialog.dismiss();

            //After download the update, go to the main screen

            Intent intent = new Intent(testLogin.this, MainHomeNav.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.transition.left_to_right, R.transition.right_to_left);
        }
    };

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