package com.iaesteintern;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.Calendar;

public class MainKalendarEvent extends Activity {

    String fraDato;
    String tilDato;
    String eventNavn;
    String eventSted;
    String eventInfo;

    String[] fraDatoArray;
    String[] tilDatoArray;

    TextView textEventNavn;
    TextView textEventSted;
    TextView textEventInfo;
    Typeface iaesteFont;
    Typeface iaesteFontBold;


    CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_kalendar_event);


        getEventInfo();
        setGUI();
        setEventInfo();
        setDateRange();
    }


    private void setEventInfo() {
        textEventNavn.setText(eventNavn);
        textEventSted.setText(eventSted);
        textEventInfo.setText(eventInfo);
    }

    private void setGUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.secondary));
        }

        getActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarTitle(eventNavn);



        iaesteFont = Typeface.createFromAsset(getAssets(), "fonts/iaesteFont.ttf");
        iaesteFontBold = Typeface.createFromAsset(getAssets(), "fonts/iaesteFontBold.ttf");

        textEventNavn = (TextView) findViewById(R.id.event_name);
        textEventSted = (TextView) findViewById(R.id.event_sted);
        textEventInfo = (TextView) findViewById(R.id.event_info);

        textEventNavn.setTypeface(iaesteFontBold);
        textEventSted.setTypeface(iaesteFont);
        textEventInfo.setTypeface(iaesteFont);

        calendarView = (CalendarView) findViewById(R.id.calendarView);


    }

    private void getEventInfo() {
        Intent i = getIntent();
        fraDato = i.getStringExtra("fraDato");
        tilDato = i.getStringExtra("tilDato");
        eventNavn = i.getStringExtra("eventNavn");
        eventSted = i.getStringExtra("eventSted");
        eventInfo = i.getStringExtra("eventInfo");

    }

    private void setDateRange() {

        fraDatoArray = fraDato.split("-");
        tilDatoArray = tilDato.split("-");

        int fraAAr = Integer.parseInt(fraDatoArray[0]);
        int fraManed = Integer.parseInt(fraDatoArray[1]) - 1;
        int fraDag = Integer.parseInt(fraDatoArray[2]);

        System.out.println(fraManed + "/" + fraDag + "/" + fraAAr);

        int tilAAr = Integer.parseInt(tilDatoArray[0]);
        int tilManed = Integer.parseInt(tilDatoArray[1]) - 1;
        int tilDag = Integer.parseInt(tilDatoArray[2]);

        Calendar minCalendar = Calendar.getInstance();
        minCalendar.set(Calendar.YEAR, fraAAr);
        minCalendar.set(Calendar.MONTH, fraManed);
        minCalendar.set(Calendar.DAY_OF_MONTH, fraDag);

        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.set(Calendar.YEAR, tilAAr);
        maxCalendar.set(Calendar.MONTH, tilManed);
        maxCalendar.set(Calendar.DAY_OF_MONTH, tilDag);


        long minMilliTime = minCalendar.getTimeInMillis();
        long maxMilliTime = maxCalendar.getTimeInMillis();
        calendarView.setFirstDayOfWeek(minCalendar.MONDAY);
        //calendarView.setDate(minMilliTime);
        calendarView.setMinDate(minMilliTime);
        calendarView.setMaxDate(maxMilliTime);
    }

    private void setActionBarTitle(String actionBarTitle) {
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainKalendarEvent.this, MainKalender.class);       //Går tilbake til MainHomeNav
        startActivity(intent);
        finish();
    }
}
