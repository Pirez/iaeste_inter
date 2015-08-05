package com.iaesteintern;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Created by Edvard on 05.08.2015.
 */
public class CustomArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    Activity activity;
    Typeface iaesteFont;



    public CustomArrayAdapter(Context context, String[] values) {
        super(context, R.layout.main_2, values);
        this.context = context;
        this.values = values;
       // iaesteFont = Typeface.createFromAsset(context.getAssets(), "fonts/iaesteFont.ttf");


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.main_2, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label_main);

        // henter inn arrayen for å sette elementene i den inn i textviewen
        String[] main_menu = context.getResources().getStringArray(R.array.string_main_intern);



        // Customization to your textView here
        textView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/iaesteFont.ttf"));
        textView.setTextSize(30);
        textView.setTextColor(context.getResources().getColor(R.color.white));
        textView.setText(main_menu[position]);



        return rowView;
    }
}