package com.iaesteintern;


import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;

import com.squareup.picasso.Picasso;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class MainMedlemslister extends ListActivity implements Runnable {

    private PopupWindow pw;
    Integer lk_active = 0;
    Integer navn_pos = 0;
    Context context;
    String URL_LINK;
    ImageView imgView;
    int i0 = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    int i5 = 0;
    int i6 = 0;

    private ProgressDialog progressDialog;
    ProgressBar mProgress;
    private String[][][] medlem_data = new String[7][5][70];   //PASS PÅ LENGDEN AV DATA DEN FÅR INN, aka 7 LK, 5 DATA INFORMASJON, 100 MEDLEMMER
    private Vector<String> medlemlister_data = new Vector<String>();
    private Vector<String> medlemlister_lk = new Vector<String>();


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
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        getListView().setCacheColorHint(0);
        getListView().setBackgroundResource(R.drawable.back_orange_color);
     //   final SharedPreferences innstillinger = getSharedPreferences("Innstillinger", MODE_PRIVATE);
     //   SharedPreferences.Editor inn = innstillinger.edit();

        class LoadingStream extends AsyncTask<String, Void, Integer> {
            /** The system calls this to perform work in a worker thread and
             * delivers it the parameters given to AsyncTask.execute() */
            protected Integer doInBackground(String... urls) {
                try {
                    String[] file_data = getResources().getStringArray(R.array.filename_list);   //Medlem data filen
                    FileInputStream fis = openFileInput(file_data[0]);
                    BufferedReader r = new BufferedReader(new InputStreamReader(fis));
                    String line;
                    Integer j = 0;

                    //ID til ulike LK-er, generert av databasen
                    //convertstringtohex, takler ikkje ø,æ,å,
                    String oslo_hex = "(.*)4f736c6f";//
                    String bergen_hex = "(.*)42657267656e";//
                    String grimstad_hex = "(.*)4772696d73746164";//
                    String stavanger_hex = "(.*)54726f6e646865696d";//"Dette er egentlig LK Trondheim";
                    String tromso_hex = "(.*)54726f6d73(.*)";//Søker kun med lk-er med Troms i midten
                    String trondheim_hex = "(.*)53746176616e676572";// Dette er egentlig LK Stavanger
                    String aas_hex = "(.*)73";//Søker kun på lk-er med s på slutten

                    //Leser av teskstfilen, og putter data inn i array, og sorteres etter LK, og deretter informasjon om personern, eks.navn,tlf...
                    while ((line = r.readLine()) != null) {
                        medlemlister_data.add(getWord(line, ';', 1));
                        medlemlister_lk.add((getWord(medlemlister_data.get(j), '*', 3)));  //LK informasjonen ligger i 3 kolonne i datafilen
                        // j = data, i = personer
                        Integer words = 5;


                        if (medlemlister_lk.get(j).matches(oslo_hex)) {
                            //Oslo
                            for (int j2 = 0; j2 < words; j2++)
                                medlem_data[2][j2][i2] = convertHexToString(getWord(medlemlister_data.get(j), '*', j2));
                            i2++;
                        }
                        else if (medlemlister_lk.get(j).matches(bergen_hex)) {
                            //Bergen
                            for (int j0 = 0; j0 < words; j0++)
                                medlem_data[0][j0][i0] = convertHexToString(getWord(medlemlister_data.get(j), '*', j0));
                            i0++;
                        }

                        else if (medlemlister_lk.get(j).matches(grimstad_hex)) {
                            //Grimstad
                            for (int j1 = 0; j1 < words; j1++)
                                medlem_data[1][j1][i1] = convertHexToString(getWord(medlemlister_data.get(j), '*', j1));
                            i1++;
                        }

                        else if (medlemlister_lk.get(j).matches(stavanger_hex)) {
                            //Stavanger
                            for (int j5 = 0; j5 < words; j5++)
                                medlem_data[5][j5][i5] = convertHexToString(getWord(medlemlister_data.get(j), '*', j5));
                            i5++;
                        }

                        else if (medlemlister_lk.get(j).matches(tromso_hex)) {
                            //Tromsø
                            for (int j4 = 0; j4 < words; j4++)
                                medlem_data[4][j4][i4] = convertHexToString(getWord(medlemlister_data.get(j), '*', j4));
                            i4++;
                        }

                        else if (medlemlister_lk.get(j).matches(trondheim_hex)) {
                            //Trondheim

                            for (int j3 = 0; j3 < words; j3++)
                                medlem_data[3][j3][i3] = convertHexToString(getWord(medlemlister_data.get(j), '*', j3));
                            i3++;
                        }

                        else if (medlemlister_lk.get(j).matches(aas_hex)) {
                            //Ås
                            for (int j6 = 0; j6 < words; j6++)
                                medlem_data[6][j6][i6] = convertHexToString(getWord(medlemlister_data.get(j), '*', j6));
                            i6++;
                        }
                        j++;
                    }
                    fis.close();


                } catch (FileNotFoundException e) {

                    Toast.makeText(getApplicationContext(), "Vennligst gå tilbake og trykk 'menu' og på oppdater!!", Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    //Teksten er koruppt
                    Toast.makeText(getApplicationContext(), "IO feil! Kode: 01",
                            Toast.LENGTH_SHORT).show();
                }
                return 0;

            }

            /** The system calls this to perform work in the UI thread and delivers
             * the result from doInBackground() */
            protected void onPostExecute() {
            }
        }

        new LoadingStream().execute("http://example.com/image.png");


        //setter lk list som default når du kommer inn
        lk_list();

        ListView lister = getListView();
        lister.setTextFilterEnabled(true);
        registerForContextMenu(lister);

        getListView().setCacheColorHint(0);
        getListView().setBackgroundResource(R.drawable.back_home_02_m);


        lister.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                navn_pos = position;

                final Context context = view.getContext();

                //ImageView imgView = new ImageView(context);

                //Klikker på et objekt kommer et nytt vindu med mer informasjonen om personen
                if (lk_active != -1) {
                    String verv = "medlem";
                    popup_window(medlem_data[lk_active][0][navn_pos], medlem_data[lk_active][2][navn_pos], medlem_data[lk_active][1][navn_pos], medlem_data[lk_active][4][navn_pos], context, medlem_data[lk_active][3][navn_pos], verv);


                } else {

                    if (position == 3) {
                        lk_active(5);
                    } else if (position == 5) {
                        lk_active(3);
                    } else {
                        lk_active(position);
                    }
                }
            }
        });
    }


    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();


        int index_pos = info.position;
        String DisplayName = medlem_data[lk_active][0][index_pos];
        String email = medlem_data[lk_active][1][index_pos];
        String tlf = medlem_data[lk_active][2][index_pos];
        String LK = medlem_data[lk_active][3][index_pos];
        String verv = "medlem"; //ikke aktiv, //TODO: Legge det inn i fremtiden?


        if (item.getTitle() == getString(R.string.medlem_3)) {
            if (tlf != null) {
                try {
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tlf)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (item.getTitle() == getString(R.string.medlem_4)) {
            /*Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setType("vnd.android-dir/mms-sms");
            sendIntent.putExtra("address", tlf);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(sendIntent);*/

            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.setData(Uri.parse("smsto:" + Uri.encode(tlf)));
            startActivity(smsIntent);

        } else if (item.getTitle() == getString(R.string.medlem_5)) {
            Intent i = new Intent(Intent.ACTION_SEND);
            //i.setType("text/plain"); // test med emulator
            i.setType("message/rfc822"); // fra mobilen
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            i.putExtra(Intent.EXTRA_SUBJECT, "");  //Default emne
            i.putExtra(Intent.EXTRA_TEXT, "");       //Default tekst
            startActivity(Intent.createChooser(i, "Velg email app"));
        } else if (item.getTitle() == getString(R.string.medlem_6)) {
            create_contact(DisplayName, email, tlf, LK, verv);
        } else {
            return false;
        }
        return true;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //Genere en meny, hvis du holder inne på et medlem på listen
        menu.setHeaderTitle("Meny");
        menu.add(0, v.getId(), 0, getString(R.string.medlem_3));
        menu.add(0, v.getId(), 0, getString(R.string.medlem_4));
        menu.add(0, v.getId(), 0, getString(R.string.medlem_5));
        menu.add(0, v.getId(), 0, getString(R.string.medlem_6)); //Fungerer ikkje

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_medlemlister, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {


                //Aktivere listene med en string array, må redusere størreslen på array, siden den er definert som 100 elementer
                case R.id.Bergen:
                    lk_active = 0;
                    lk_active(lk_active);
                /*
                String[] navneliste_bergen = new String[i0];
                System.arraycopy(medlem_data[lk_active][0], 0, navneliste_bergen, 0, i0);
                setListAdapter(new ArrayAdapter<String>(this, R.layout.main_medlemslister, R.id.label, navneliste_bergen));
                setTitle("IAESTE intern - LK Bergen " + Integer.toString(navneliste_bergen.length) + " medlemmer");
                 */
                    return true;

                case R.id.Grimstad:
                    lk_active = 1;
                    lk_active(lk_active);
                    return true;

                case R.id.Oslo:
                    //String[] navneliste_oslo = (String[]) medlemlister_navn_oslo.toArray(new String[medlemlister_navn_oslo.size()]);
                    // setListAdapter(new ArrayAdapter<String>(this, R.layout.main_medlemslister, R.id.label, navneliste_oslo));
                    lk_active = 2;
                    lk_active(lk_active);
                    return true;

                case R.id.Trondheim:
                    lk_active = 3;
                    lk_active(lk_active);
                    return true;

                case R.id.Tromso:
                    lk_active = 4;
                    lk_active(lk_active);
                    return true;

                case R.id.Stavanger:
                    lk_active = 5;
                    lk_active(lk_active);
                    return true;

                case R.id.As:
                    lk_active = 6;
                    lk_active(lk_active);
                    return true;

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

        }

    }


    public Object fetch(String address) throws MalformedURLException, IOException {
        URL url = new URL(address);
        Object content = url.getContent();
        return content;
    }

    private Handler handler = new Handler() {
        //Avslutter progressdia.
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
        }
    };

    @Override
    public void onBackPressed() {
        /*
        Sjekk hvis du er inne i "VALG LK"  hvis den er det returnere den til main_home
        eller "MEDLEMLISTER" hvis den er det returnere tilbake til "VALG LK", lk_list()
         */
        if (lk_active > -1) {
            lk_list();      //Går tilbake til LK_LIST
        } else if (lk_active == -1) {
            finish();       //Går tilbake til MainHome
        }
        return;
    }

    public void lk_list() {
        setTitle("LK ");
        //Setter en meny, slik at du kan velge LK
        lk_active = -1;  //Slik at det blir en MENY og ikkje en person
        String[] Start_meny = getResources().getStringArray(R.array.string_LK);
        setListAdapter(new ArrayAdapter<String>(this, R.layout.main_medlemslister_2, R.id.label, Start_meny));
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


    public void lk_active(Integer position) {
        lk_active = position;

        if (lk_active == 0) {
            String[] navneliste_bergen = new String[i0];
            System.arraycopy(medlem_data[lk_active][0], 0, navneliste_bergen, 0, i0);
            setListAdapter(new ArrayAdapter<String>(this, R.layout.main_medlemslister, R.id.label, navneliste_bergen));
            setTitle(getString(R.string.lk_1) + " Bergen " + Integer.toString(navneliste_bergen.length) + " " + getString(R.string.medlem_1));

        } else if (lk_active == 1) {
            String[] navneliste_grimstad = new String[i1];
            System.arraycopy(medlem_data[lk_active][0], 0, navneliste_grimstad, 0, i1);
            setListAdapter(new ArrayAdapter<String>(this, R.layout.main_medlemslister, R.id.label, navneliste_grimstad));
            setTitle(getString(R.string.lk_1) + " Grimstad " + Integer.toString(navneliste_grimstad.length) + " " + getString(R.string.medlem_1));

        } else if (lk_active == 2) {
            String[] navneliste_oslo = new String[i2];
            System.arraycopy(medlem_data[lk_active][0], 0, navneliste_oslo, 0, i2);
            setListAdapter(new ArrayAdapter<String>(MainMedlemslister.this, R.layout.main_medlemslister, R.id.label, navneliste_oslo));
            setTitle(getString(R.string.lk_1) + " Oslo " + Integer.toString(navneliste_oslo.length) + " " + getString(R.string.medlem_1));


        } else if (lk_active == 3) {
            //Dette er egentlig Stavanger, finner ikkje feil
            String[] navneliste_trondheim = new String[i3];
            System.arraycopy(medlem_data[lk_active][0], 0, navneliste_trondheim, 0, i3);
            setListAdapter(new ArrayAdapter<String>(this, R.layout.main_medlemslister, R.id.label, navneliste_trondheim));
            setTitle(getString(R.string.lk_1) + " Stavanger " + Integer.toString(navneliste_trondheim.length) + " " + getString(R.string.medlem_1));

        } else if (lk_active == 4) {
            String[] navneliste_tromso = new String[i4];
            System.arraycopy(medlem_data[lk_active][0], 0, navneliste_tromso, 0, i4);
            setListAdapter(new ArrayAdapter<String>(this, R.layout.main_medlemslister, R.id.label, navneliste_tromso));
            setTitle(getString(R.string.lk_1) + " Tromsø " + Integer.toString(navneliste_tromso.length) + " " + getString(R.string.medlem_1));

        } else if (lk_active == 5) {
            //Dette er egentlig Trondheim, finner ikkje feil
            String[] navneliste_stavanger = new String[i5];
            System.arraycopy(medlem_data[lk_active][0], 0, navneliste_stavanger, 0, i5);
            setListAdapter(new ArrayAdapter<String>(this, R.layout.main_medlemslister, R.id.label, navneliste_stavanger));
            setTitle(getString(R.string.lk_1) + " Trondheim " + Integer.toString(navneliste_stavanger.length) + " " + getString(R.string.medlem_1));

        } else if (lk_active == 6) {
            String[] navneliste_as = new String[i6];
            System.arraycopy(medlem_data[lk_active][0], 0, navneliste_as, 0, i6);
            setListAdapter(new ArrayAdapter<String>(this, R.layout.main_medlemslister, R.id.label, navneliste_as));
            setTitle(getString(R.string.lk_1) + " Ås " + Integer.toString(navneliste_as.length) + " " + getString(R.string.medlem_1));
        }
    }


    public void run() {
    }


    private Drawable ImageOperations(Context ctx, String url, String saveFilename) {

        try {
            InputStream is = (InputStream) this.fetch(url);
            Drawable d = Drawable.createFromStream(is, "src");
            return d;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }


    private class DownloadImageTask extends AsyncTask<String, Void, Drawable> {
        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected Drawable doInBackground(String... urls) {
            Drawable image = ImageOperations(context, urls[0], "image.jpg");
            return image;
        }

        /**
         * The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        protected void onPostExecute(Drawable image) {
            imgView.setImageDrawable(image);
            mProgress.setVisibility(View.GONE);

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


    public void listThumb() {

        /*//System.arraycopy(medlem_data[lk_active][4], 0, bilder_oslo, 0, i2);
        Context mContext;
        String url= "";
        ImageView thumbnailImageView = (ImageView) findViewById(R.id.user);

        URL_LINK = getString(R.string.url_pictures_member) + url;

        if (bilder_oslo.has("cover_i")) {
            mContext = context;
            String imageURL = URL_LINK + url;
            Picasso.with(mContext).load(imageURL).placeholder(R.drawable.people).into(thumbnailImageView);
        } else {

        }*/

    }

    public void popup_window(final String popup_navn, final String popup_tlf, final String popup_email, final String url, Context context, final String popup_lk, final String popup_verv) {


        final Dialog dialog = new Dialog(MainMedlemslister.this);
        dialog.show();
        dialog.setContentView(R.layout.popup_medlem);
        dialog.setTitle(popup_navn);
        dialog.setCancelable(true);
        //TextView popup_navn_func = (TextView) dialog.findViewById(R.id.text_popup_navn);
        TextView popup_tlf_func = (TextView) dialog.findViewById(R.id.text_popup_tlf);
        TextView popup_email_func = (TextView) dialog.findViewById(R.id.text_popup_email);
        TextView popup_info = (TextView) dialog.findViewById(R.id.text_popup_info);
        final Button lukk = (Button) dialog.findViewById(R.id.popup_menu_button1);
        final Button lagre = (Button) dialog.findViewById(R.id.popup_menu_lagre);


        //popup_navn_func.setText("Navn: " + popup_navn);
        popup_tlf_func.setText("Tlf: " + popup_tlf);
        popup_email_func.setText("Email: " + popup_email);
        popup_email_func.setVisibility(View.VISIBLE);


        imgView = new ImageView(context);

        final SharedPreferences innstillinger = getSharedPreferences("Innstillinger", MODE_PRIVATE);
        mProgress = (ProgressBar) dialog.findViewById(R.id.progress_popup);

        //Sjekker hvis du skal laste ned bilde, eller hvis det ikje finnes bilder



        if (url.matches("empty")) {

            imgView = (ImageView) dialog.findViewById(R.id.bilde_popup_kalender);

            int[] bilder = new int[10];
            bilder[0] = R.drawable.uglypictureboy1;
            bilder[1] = R.drawable.uglypictureboy2;
            bilder[2] = R.drawable.uglypictureboy3;
            bilder[3] = R.drawable.uglypictureboy4;
            bilder[4] = R.drawable.uglypictureboy5;
            bilder[5] = R.drawable.uglypicturegirl1;
            bilder[6] = R.drawable.uglypicturegirl2;
            bilder[7] = R.drawable.uglypicturegirl3;
            bilder[8] = R.drawable.uglypicturegirl4;
            bilder[9] = R.drawable.uglypicturegirl5;


            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(10);

            //Sets a random pictures from the vector
            imgView.setImageResource(bilder[randomInt]);
            popup_info.setText(R.string.pic_03);

        } else if (innstillinger.getBoolean("autoBilde", true)) {
            //progressDialog = ProgressDialog.show(MainMedlemslister.this, Integer.toString(R.string.update_text_02), "Henter bildet", true, false);
            //Thread thr = new Thread(this);
            //thr.start();
            mProgress.setVisibility(View.VISIBLE);

            URL_LINK = getString(R.string.url_pictures_member) + url;
            //Drawable image = ImageOperations(context, URL_LINK, "image.jpg");
            imgView = (ImageView) dialog.findViewById(R.id.bilde_popup_kalender);
            //handler.sendEmptyMessage(0); // close progressbar
            new DownloadImageTask().execute(URL_LINK);



        } else {
            imgView = (ImageView) dialog.findViewById(R.id.bilde_popup_kalender);
            imgView.setImageResource(R.drawable.no_droid);
            popup_info.setText(R.string.pic_02);
        }


        lagre.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vv) {
                create_contact(popup_navn, popup_email, popup_tlf, popup_lk, popup_verv);
            }
        });

        lukk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vv) {
                deleteFile("image.jpg");
                // Lukker popup
                dialog.cancel();
            }
        });


    }
}
