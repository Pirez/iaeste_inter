package com.iaesteintern;
//package com.android.HelloWorld;
//TODO: Fikse hele denne screen (men er ikkje kritisk!)

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainCredits extends Activity {
    Typeface iaesteFont;
    Typeface iaesteFontBold;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_credits);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        iaesteFont = Typeface.createFromAsset(getAssets(), "fonts/iaesteFont.ttf");
        iaesteFontBold = Typeface.createFromAsset(getAssets(), "fonts/iaesteFontBold.ttf");

        TextView mTextSample = (TextView) findViewById(R.id.text_link_credits_bug);
        TextView title = (TextView) findViewById(R.id.text_credits_tittel);
        TextView historie = (TextView) findViewById(R.id.text_credits_historie);
        TextView devs = (TextView) findViewById(R.id.text_credits_programmere);
        TextView johan = (TextView) findViewById(R.id.text_credits_johan);
        TextView mailjohan = (TextView) findViewById(R.id.text_credits_mailjohan);
        TextView design = (TextView) findViewById(R.id.text_credits_design);
        TextView edvard = (TextView) findViewById(R.id.text_credits_edvard);
        TextView mailedvard = (TextView) findViewById(R.id.text_credits_mailedvard);
        TextView kontakt = (TextView) findViewById(R.id.text_credits_kontakt);
        Button back_home = (Button) findViewById(R.id.button_credits_back);

        title.setTypeface(iaesteFontBold);
        historie.setTypeface(iaesteFont);
        devs.setTypeface(iaesteFontBold);
        johan.setTypeface(iaesteFont);
        mailjohan.setTypeface(iaesteFont);
        design.setTypeface(iaesteFontBold);
        edvard.setTypeface(iaesteFont);
        mailedvard.setTypeface(iaesteFont);
        kontakt.setTypeface(iaesteFont);
        back_home.setTypeface(iaesteFont);


        SpannableString s = new SpannableString("Om IAESTE appen");
        s.setSpan(new TypefaceSpan(this, "iaesteFontBold.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        /*mTextSample.setMovementMethod(LinkMovementMethod.getInstance());
        String text = getString(R.string.credits_01) + " <a href ='https://docs.google.com/spreadsheet/ccc?key=0AjghohFDgBf3dDE5d19oQzdoemdhMXdRZmRPRDBOMWc#gid=0'> Google Dokument </a>";
        mTextSample.setText(Html.fromHtml(text));*/

        back_home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View a) {

                //TODO: fiks problemet her, klager på integer (?)
                //
                //
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
