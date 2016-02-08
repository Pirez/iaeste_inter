package com.iaesteintern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "main";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    int lengden_nyheter = 20;
    int kolonne_nyheter = 5;
    String urlLink;
    SwipeRefreshLayout swipeLayout;
    Typeface iaesteFont;
    Typeface iaesteFontBold;

    // ScrollView nyhetsScroll = (ScrollView) getActivity().findViewById(R.id.nyhets_scroll);



    private String[][] nyhet_data = new String[lengden_nyheter][kolonne_nyheter];   //PASS PÅ LENGDEN AV DATA DEN FÅR INN, aka 7 LK, 5 DATA INFORMASJON, 100 MEDLEMMER

    private OnFragmentInteractionListener mListener;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }




    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iaesteFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/iaesteFont.ttf");
        iaesteFontBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/iaesteFontBold.ttf");




        try {
            String[] file_data = getResources().getStringArray(R.array.filename_list);   //Medlem data filen
            FileInputStream fis = getActivity().openFileInput(file_data[4]);
            BufferedReader r = new BufferedReader(new InputStreamReader(fis));
            String line;
            String data;

            int ii = 0;

            //Leser av teskstfilen, og putter data inn i array, og sorteres etter LK, og deretter informasjon om personern, eks.navn,tlf...
            while ((line = r.readLine()) != null) {
                data = (getWord(line, ';', 1));                            //Leser fra første kolonne datasett
                // j = data, i = personer
                ; //Hvor mangen kolonner den skal lese inn

                for (int i = 0; i < kolonne_nyheter ; i++) {
                    nyhet_data[ii][i] = convertHexToString(getWord(data, '*', i));
                }
                ii++;

            }
            fis.close();


        } catch (FileNotFoundException e) {

            Toast.makeText(getActivity(), R.string.update_text_02, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            //Teksten er koruppt
            Toast.makeText(getActivity(), "IO feil! Kode: 01",
                    Toast.LENGTH_SHORT).show();
        }
        // ScrollView nyhetsScroll = (ScrollView) findViewById(R.id.nyhets_scroll);
        LinearLayout nyhetsView = (LinearLayout) getActivity().findViewById(R.id.nyhetsview);
        LinearLayout mainNyhet = (LinearLayout) getActivity().findViewById(R.id.mainnyhetView);


        String tmp_overskrift;
        String tmp_nyhet;
        String tmp_bildelink;
        String tmp_created;
        String tmp_navn;


        for (int i = 0; i < lengden_nyheter; i++) {
            tmp_overskrift = nyhet_data[i][0];
            tmp_nyhet = nyhet_data[i][1];
            tmp_bildelink = nyhet_data[i][2];
            tmp_created = nyhet_data[i][3];
            tmp_navn = nyhet_data[i][4];


            String[] link = tmp_bildelink.split(",");
            Log.d("bildeurl", link + "");

            View inflatedView = getActivity().getLayoutInflater().inflate(R.layout.nyhets_view, nyhetsView, false);

            TextView nyhetOverskrift = (TextView) inflatedView.findViewById(R.id.main_nyhet_overskrift);
            nyhetOverskrift.setText(tmp_overskrift);
            nyhetOverskrift.setTypeface(iaesteFontBold);


            urlLink = getString(R.string.url_pictures_news) + link[0];

            TextView nyhetTekst = (TextView) inflatedView.findViewById(R.id.main_nyhet_innhold);
            nyhetTekst.setText(Html.fromHtml(tmp_nyhet));
            nyhetTekst.setTypeface(iaesteFont);


            TextView nyhetCreated = (TextView) inflatedView.findViewById(R.id.main_nyhet_created);
            nyhetCreated.setText(tmp_created);
            nyhetCreated.setTypeface(iaesteFont);


            TextView nyhetNavn = (TextView) inflatedView.findViewById(R.id.main_nyhet_navn);
            nyhetNavn.setText(tmp_navn);
            nyhetNavn.setTypeface(iaesteFont);


            ImageView nyhetBilde = (ImageView) inflatedView.findViewById(R.id.main_nyhet_bilde);



            //new DownloadImageTask((ImageView) findViewById(R.id.main_nyhet_bilde)).execute(urlLink);
            Picasso.with(getActivity())
                    .load(urlLink)
                    .placeholder(R.drawable.iaesteorange)
                    .error(R.drawable.iaesteorange)
                    .resize(300, nyhetBilde.getMaxHeight())
                    .centerInside()
                    .into(nyhetBilde);

            /*nyhetBilde.setScaleType(ImageView.ScaleType.FIT_XY);
            nyhetBilde.setAdjustViewBounds(true);*/

            mainNyhet.addView(inflatedView);

        }

    }


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





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}