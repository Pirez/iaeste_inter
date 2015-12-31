package com.iaesteintern;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.provider.ContactsContract;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

public class MainPerson extends Activity {

    Typeface iaesteFont;
    //Memberstrings
    String name;
    String telefon;
    String mail;
    String url;
    String lk;
    String verv = "medlem";

    String URL_LINK;

    //views, buttons etc
    ImageView coverImg;
    CircularImageView memberImg;
    TextView textNavn;

    ImageButton callButton;
    TextView tlfNumber;
    ImageButton textingButton;

    ImageButton mailButton;
    TextView mailText;

    Button addContact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_person);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        iaesteFont = Typeface.createFromAsset(getAssets(), "fonts/iaesteFont.ttf");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.secondary));
        }




        coverImg = (ImageView) findViewById(R.id.cover_img);
        memberImg = (CircularImageView) findViewById(R.id.member_img);
        textNavn = (TextView) findViewById(R.id.text_navn);

        callButton = (ImageButton) findViewById(R.id.call_button);
        tlfNumber = (TextView) findViewById(R.id.tele_number);
        textingButton = (ImageButton) findViewById(R.id.texting_button);

        mailButton = (ImageButton) findViewById(R.id.mail_button);
        mailText = (TextView) findViewById(R.id.mail_string);

        addContact = (Button) findViewById(R.id.add_contact);

        getMemberInfo();
        setMemberInfo();

        ////////////////////////////STARTER SMS FRA IKONET/////////////////////////////

        textingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.setData(Uri.parse("smsto:" + Uri.encode(telefon)));
                startActivity(smsIntent);
            }
        });

        ////////////////////////////LEGG TIL KONTAKT/////////////////////////////

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_addcontact(name,  mail, telefon, lk, verv);
            }
        });


    }

    public void getMemberInfo(){
        Intent i = getIntent();

        // Get the name
        name = i.getStringExtra("name");
        telefon = i.getStringExtra("tlf");
        mail = i.getStringExtra("mail");
        url = i.getStringExtra("url");
        lk = i.getStringExtra("lk");
    }

    public void setMemberInfo() {

        URL_LINK = getString(R.string.url_pictures_member) + url;

        List<Transformation> transformations = new ArrayList<>();

        transformations.add(new GrayscaleTransformation(Picasso.with(this)));
        transformations.add(new BlurTransformation(this));

        Picasso.with(this)
                .load(URL_LINK)
                .placeholder(R.drawable.blurry)
                .error(R.drawable.blurry)
                .centerCrop()
                .fit()
                .transform(transformations)
                .into(coverImg);

        Picasso.with(this)
                .load(URL_LINK)
                .placeholder(R.drawable.silhouette)
                .error(R.drawable.silhouette)
                .fit()
                .into(memberImg);

        textNavn.setText(name);
        setActionBarTitle(name);

        tlfNumber.setText("Tlf: " + telefon);
        mailText.setText("Mail: " + mail);

        //setting font
        textNavn.setTypeface(iaesteFont);
        tlfNumber.setTypeface(iaesteFont);
        mailText.setTypeface(iaesteFont);
        addContact.setTypeface(iaesteFont);


    }
//popupen for å godkjenne at man legger til en kontakt
    public void popup_addcontact(final String pop_name, final String pop_mail, final String pop_telefon, final String pop_lk, final String pop_verv){

        final Dialog dialog = new Dialog(MainPerson.this);
        dialog.show();
        dialog.setContentView(R.layout.popup_confirm_contact);
        //dialog.setTitle(popup_navn);
        dialog.setCancelable(true);

        TextView text = (TextView) dialog.findViewById(R.id.dialog_textView);
        Button button = (Button) dialog.findViewById(R.id.add_that_contact_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
        text.setText(pop_name + " " + "(" + pop_telefon + ")" + " vil nå bli lagt til i kontaktlisten.");

        text.setTypeface(iaesteFont);
        button.setTypeface(iaesteFont);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_contact(pop_name, pop_mail, pop_telefon, pop_lk, pop_verv);
                Toast.makeText(getApplicationContext(), pop_name + " " + R.string.medlem_2, Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    private void setActionBarTitle (String actionBarTitle) {
        SpannableString s = new SpannableString(actionBarTitle);
        s.setSpan(new TypefaceSpan(this, "iaesteFontBold.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                onBackPressed();
                return true;


            default:
                return false;

        }
    }

    public void create_contact(String DisplayName, String email, String tlf, String LK, String verv) {


        ArrayList<ContentProviderOperation> ops =
                new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build()
        );

        //------------------------------------------------------ Names
        if (DisplayName != null) {
            ops.add(ContentProviderOperation.newInsert(
                            ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(
                                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                    DisplayName).build()
            );
        }

        //------------------------------------------------------ Mobile Number
        if (tlf != null) {
            ops.add(ContentProviderOperation.
                            newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, tlf)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                            .build()
            );
        }

        if (email != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        if (!LK.equals("") && !verv.equals("")) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, LK)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, verv)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .build());
        }

        //TODO: Få denne å fungere i fremtiden? Må forstå hvordan bitmap er bygget opp og lagre bilde i pictures

            /*
        if(picture != null)
        {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, )
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }
              */


        // Asking the Contact provider to create a new contact
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            //Toast.makeText(getApplicationContext(), R.string.soon, Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), R.string.medlem_2, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), R.string.errorcode_02, Toast.LENGTH_LONG).show();
        }

    }

}

