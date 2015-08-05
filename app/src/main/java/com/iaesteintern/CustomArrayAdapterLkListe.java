package com.iaesteintern;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Edvard on 05.08.2015.
 */
public class CustomArrayAdapterLkListe extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;




    public CustomArrayAdapterLkListe(Context context, String[] values) {
        super(context, R.layout.main_medlemslister_2, values);
        this.context = context;
        this.values = values;
       // iaesteFont = Typeface.createFromAsset(context.getAssets(), "fonts/iaesteFont.ttf");


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.main_medlemslister_2, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);

        // henter inn arrayen for å sette elementene i den inn i textviewen
        String[] Start_meny = context.getResources().getStringArray(R.array.string_LK);



        // Customization to your textView here
        textView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/iaesteFont.ttf"));
        textView.setTextSize(30);
        textView.setTextColor(context.getResources().getColor(R.color.white));
        textView.setText(Start_meny[position]);



        return rowView;
    }
}