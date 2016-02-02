package com.iaesteintern;

import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by Edvard on 05.08.2015.
 */
public class CustomArrayAdapterMedlemsListe extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private String[][][] medlem_data = new String[7][5][70];
    private String[] LKer = new String[]{"Bergen", "Grimstad", "Oslo", "Trondheim", "Tromsø", "Stavanger", "Ås"};
    Typeface iaesteFont;
    String urlLink;
    String[] url;

    int i0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    int i5 = 0;
    int i6 = 0;




    public CustomArrayAdapterMedlemsListe(Context context, String[] values, String[] url) {
        super(context, R.layout.main_medlemslister, values);
        this.context = context;
        this.values = values;
        this.url = url;
        this.iaesteFont = Typeface.createFromAsset(context.getAssets(), "fonts/iaesteFont.ttf");

        this.urlLink = getContext().getString(R.string.url_pictures_news);


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryHolder holder = null;


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.main_medlemslister, parent, false);


        holder = new HistoryHolder();
        holder.textView = (TextView) rowView.findViewById(R.id.label);
        holder.imgUser = (CircularImageView) rowView.findViewById(R.id.user_img);

        // henter inn arrayen for å sette elementene i den inn i textviewen

        // Customization to your textView here
        holder.textView.setTextSize(28);
        holder.textView.setTextColor(context.getResources().getColor(R.color.white));

        holder.textView.setTypeface(iaesteFont);
        holder.textView.setText(this.values[position]);

        urlLink = this.context.getString(R.string.url_pictures_member);
        String jpgUrl = url[position];
        String URL = urlLink + jpgUrl;

        //loko måte å gjøre dette på, men fuckit, finner du en bedre løsning, go for it!
        if (!values[position].equals("Bergen")
                && !values[position].equals("Grimstad")
                && !values[position].equals("Oslo")
                && !values[position].equals("Trondheim")
                && !values[position].equals("Tromsø")
                && !values[position].equals("Stavanger")
                && !values[position].equals("Ås")) {
            Picasso.with(this.context)
                    .load(URL)
                    //.transform(new CircleTransform()) //Fjerna denne, fant en library som lagde en egen view for det! men behold klassen, kan være nyttig senere
                    .placeholder(R.drawable.silhouette)
                    .error(R.drawable.silhouette)
                    .fit()
                    .into(holder.imgUser);
        }

return rowView;

    }
    static class HistoryHolder
    {
        CircularImageView imgUser;
        TextView textView;
;
    }

}