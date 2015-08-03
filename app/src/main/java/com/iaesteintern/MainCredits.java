package com.iaesteintern;
//package com.android.HelloWorld;
//TODO: Fikse hele denne screen (men er ikkje kritisk!)

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainCredits extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_credits);
        //getActionBar().setDisplayHomeAsUpEnabled(true);


        Button back_home = (Button) findViewById(R.id.button_credits_back);
        TextView mTextSample = (TextView) findViewById(R.id.text_link_credits_bug);
        mTextSample.setMovementMethod(LinkMovementMethod.getInstance());
        String text = getString(R.string.credits_01) + " <a href ='https://docs.google.com/spreadsheet/ccc?key=0AjghohFDgBf3dDE5d19oQzdoemdhMXdRZmRPRDBOMWc#gid=0'> Google Dokument </a>";
        mTextSample.setText(Html.fromHtml(text));

        back_home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View a) {

                //TODO: fiks problemet her, klager på integer (?)
                String endring = getString(R.string.update_info);

                try {
                    String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                    AlertDialog alertDialog = new AlertDialog.Builder(MainCredits.this).create();
                    alertDialog.setTitle(getString(R.string.version) + versionName);
                    alertDialog.setMessage(endring);
                    alertDialog.setIcon(R.drawable.info);
                    alertDialog.show();
                } catch (PackageManager.NameNotFoundException e) {
                }

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
