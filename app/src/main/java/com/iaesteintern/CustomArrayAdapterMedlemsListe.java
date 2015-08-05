package com.iaesteintern;

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
public class CustomArrayAdapterMedlemsListe extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private String[][][] medlem_data = new String[7][5][70];
    Typeface iaesteFont;
    int i0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    int i5 = 0;
    int i6 = 0;




    public CustomArrayAdapterMedlemsListe(Context context, String[] values) {
        super(context, R.layout.main_medlemslister, values);
        this.context = context;
        this.values = values;
        this.iaesteFont = Typeface.createFromAsset(context.getAssets(), "fonts/iaesteFont.ttf");


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.main_medlemslister, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);

        // henter inn arrayen for å sette elementene i den inn i textviewen

        // Customization to your textView here
        textView.setTextSize(28);
        textView.setTextColor(context.getResources().getColor(R.color.white));

        textView.setTypeface(iaesteFont);
        textView.setText(this.values[position]);



        return rowView;
    }
}